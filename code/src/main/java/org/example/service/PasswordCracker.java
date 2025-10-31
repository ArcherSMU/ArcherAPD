package org.example.service;

import org.example.model.CrackResult;
import org.example.model.User;

import java.util.List;
import java.util.Map;

/**
 * Core service responsible for orchestrating the password cracking algorithm.
 * Uses pluggable CrackingEngine for flexible concurrency strategies.
 * Implements O(N+M) algorithm: hash dictionary once, lookup user hashes.
 */
public class PasswordCracker {
    
    private final CrackingEngine crackingEngine;
    private final ProgressTracker progressTracker;
    
    /**
     * Creates a PasswordCracker with a specific cracking engine.
     * 
     * @param crackingEngine The engine to use for hash computation
     * @param progressTracker Progress reporting component
     */
    public PasswordCracker(CrackingEngine crackingEngine, ProgressTracker progressTracker) {
        this.crackingEngine = crackingEngine;
        this.progressTracker = progressTracker;
    }
    
    /**
     * Executes the password cracking operation using the configured engine.
     * 
     * @param users List of users to crack passwords for
     * @param dictionary List of potential passwords
     * @return CrackResult containing statistics about the operation
     */
    public CrackResult crack(List<User> users, List<String> dictionary) {
        long startTime = System.currentTimeMillis();
        
        // Phase 1: Build hash lookup table using the cracking engine
        System.out.println("Building hash lookup table from dictionary...");
        System.out.println("Dictionary size: " + dictionary.size() + " passwords");
        System.out.println("Using cracking engine: " + crackingEngine.getEngineName());
        
        Map<String, String> hashToPassword = crackingEngine.buildHashLookupTable(dictionary);
            
        System.out.println("");
        System.out.println("Hash lookup table built with " + hashToPassword.size() + " unique entries.");
        
        // Phase 2: Lookup user passwords
        System.out.println("Looking up user password hashes...");
        int passwordsFound = lookupUserPasswords(users, hashToPassword);
        
        long duration = System.currentTimeMillis() - startTime;
        return new CrackResult(passwordsFound, dictionary.size(), duration);
    }
    
    
    /**
     * Looks up passwords for all users using the hash table.
     * 
     * @param users List of users
     * @param hashToPassword Hash lookup table
     * @return Number of passwords found
     */
    private int lookupUserPasswords(List<User> users, Map<String, String> hashToPassword) {
        int passwordsFound = 0;
        int usersChecked = 0;
        int totalUsers = users.size();
        
        for (User user : users) {
            usersChecked++;
            String crackedPassword = hashToPassword.get(user.getHashedPassword());
            
            if (crackedPassword != null) {
                user.setFoundPassword(crackedPassword);
                passwordsFound++;
            }
            
            if (usersChecked % 100 == 0) {
                progressTracker.reportLookupProgress(usersChecked, totalUsers, passwordsFound);
            }
        }
        
        return passwordsFound;
    }
}
