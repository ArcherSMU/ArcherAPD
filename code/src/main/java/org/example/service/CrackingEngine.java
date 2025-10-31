package org.example.service;

import java.util.List;
import java.util.Map;

/**
 * Interface for the core concurrent cracking engine.
 * Defines the contract for building hash lookup tables using different concurrency strategies.
 */
public interface CrackingEngine {
    
    /**
     * Builds a hash lookup table from the dictionary using the engine's concurrency strategy.
     * 
     * @param dictionary List of passwords to hash
     * @return Thread-safe map from hash to password
     */
    Map<String, String> buildHashLookupTable(List<String> dictionary);
    
    /**
     * Gets the name of this cracking engine implementation.
     * 
     * @return Engine name (e.g., "Parallel Streams", "Sequential")
     */
    String getEngineName();
}
