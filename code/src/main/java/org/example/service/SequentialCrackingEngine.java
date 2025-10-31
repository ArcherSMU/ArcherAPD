package org.example.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Sequential (single-threaded) implementation of the cracking engine.
 * Used as baseline for performance comparison or when concurrency is not needed.
 */
public class SequentialCrackingEngine implements CrackingEngine {
    
    private final HashService hashService;
    private final ProgressTracker progressTracker;
    
    public SequentialCrackingEngine(HashService hashService, ProgressTracker progressTracker) {
        this.hashService = hashService;
        this.progressTracker = progressTracker;
    }
    
    @Override
    public Map<String, String> buildHashLookupTable(List<String> dictionary) {
        // Pre-size HashMap to avoid resizing - use load factor of 0.75
        int initialCapacity = (int) Math.ceil(dictionary.size() / 0.75);
        Map<String, String> hashToPassword = new HashMap<>(initialCapacity);
        int hashesComputed = 0;
        
        for (String password : dictionary) {
            String hash = hashService.computeHash(password);
            hashesComputed++;
            hashToPassword.put(hash, password);
            
            if (hashesComputed % 10000 == 0) {
                progressTracker.reportHashingProgress(hashesComputed);
            }
        }
        
        return hashToPassword;
    }
    
    @Override
    public String getEngineName() {
        return "Sequential";
    }
}
