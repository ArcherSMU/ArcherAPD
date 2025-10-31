package org.example.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * Parallel Streams implementation of the cracking engine.
 * Uses Java's parallel streams for optimal multi-core utilization.
 * Automatically adapts to available CPU cores (scales from 1-16+ cores).
 * Benchmarked as the fastest approach for this workload.
 */
public class ParallelStreamsCrackingEngine implements CrackingEngine {
    
    private final HashService hashService;
    private final ProgressTracker progressTracker;
    
    public ParallelStreamsCrackingEngine(HashService hashService, ProgressTracker progressTracker) {
        this.hashService = hashService;
        this.progressTracker = progressTracker;
    }
    
    @Override
    public Map<String, String> buildHashLookupTable(List<String> dictionary) {
        int initialCapacity = (int) Math.ceil(dictionary.size() / 0.75);
        Map<String, String> hashToPassword = new ConcurrentHashMap<>(initialCapacity);
        
        // Report available parallelism (number of cores available to JVM)
        int availableParallelism = ForkJoinPool.commonPool().getParallelism();
        System.out.println("Available CPU cores for parallel processing: " + availableParallelism);
        
        // Use AtomicInteger for thread-safe progress tracking
        AtomicInteger hashesComputed = new AtomicInteger(0);
        
        // Parallel stream leverages ForkJoinPool.commonPool() - automatically scales to available cores
        // Works efficiently on 4-core, 8-core, 16-core systems without code changes
        IntStream.range(0, dictionary.size())
            .parallel()
            .forEach(i -> {
                String password = dictionary.get(i);
                String hash = hashService.computeHash(password);
                hashToPassword.put(hash, password);
                
                int count = hashesComputed.incrementAndGet();
                if (count % 10000 == 0) {
                    progressTracker.reportHashingProgress(count);
                }
            });
        
        return hashToPassword;
    }
    
    @Override
    public String getEngineName() {
        int cores = ForkJoinPool.commonPool().getParallelism();
        return "Parallel Streams (" + cores + " cores)";
    }
}
