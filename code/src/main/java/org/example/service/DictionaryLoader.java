package org.example.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Optimized dictionary loader with buffering, capacity hints, and deduplication.
 */
public class DictionaryLoader {
    
    private static final int INITIAL_CAPACITY = 10000; // Hint for common dictionary sizes
    private final boolean deduplicate;
    
    public DictionaryLoader(boolean deduplicate) {
        this.deduplicate = deduplicate;
    }
    
    /**
     * Loads dictionary with optimized buffering and optional deduplication.
     * 
     * @param filePath Path to dictionary file
     * @return List of passwords (deduplicated if enabled)
     * @throws IOException if file cannot be read
     */
    public List<String> loadDictionary(String filePath) throws IOException {
        if (deduplicate) {
            return loadDictionaryWithDeduplication(filePath);
        } else {
            return loadDictionaryBasic(filePath);
        }
    }
    
    /**
     * Loads dictionary without deduplication (preserves all entries).
     */
    private List<String> loadDictionaryBasic(String filePath) throws IOException {
        List<String> passwords = new ArrayList<>(INITIAL_CAPACITY);
        
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath), StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isEmpty()) { // Skip empty lines
                    passwords.add(line);
                }
            }
        }
        
        return passwords;
    }
    
    /**
     * Loads dictionary with deduplication using LinkedHashSet to preserve order.
     * This eliminates duplicate passwords before hashing, saving CPU cycles.
     */
    private List<String> loadDictionaryWithDeduplication(String filePath) throws IOException {
        Set<String> uniquePasswords = new LinkedHashSet<>(INITIAL_CAPACITY);
        int totalLines = 0;
        
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath), StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isEmpty()) {
                    totalLines++;
                    uniquePasswords.add(line); // Set automatically deduplicates
                }
            }
        }
        
        System.out.println("Loaded " + uniquePasswords.size() + " unique passwords (deduplicated from " + totalLines + ")");
        return new ArrayList<>(uniquePasswords);
    }
}
