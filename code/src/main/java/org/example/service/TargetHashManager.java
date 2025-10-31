package org.example.service;

import org.example.model.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Component responsible for loading and managing target hashes.
 * Handles reading user credentials from input files.
 */
public class TargetHashManager {
    
    private static final int INITIAL_CAPACITY = 10000;
    
    /**
     * Loads target users with their hashed passwords from a file.
     * Expected format: username,hashed_password (CSV)
     * 
     * @param filePath Path to the input file containing users
     * @return List of User objects with target hashes
     * @throws IOException if file cannot be read
     */
    public List<User> loadTargetHashes(String filePath) throws IOException {
        List<User> users = new ArrayList<>(INITIAL_CAPACITY);
        
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath), StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 2); // Limit splits to 2 for efficiency
                if (parts.length >= 2) {
                    users.add(new User(parts[0].trim(), parts[1].trim()));
                }
            }
        }
        
        return users;
    }
    
    /**
     * Gets statistics about the loaded target hashes.
     * 
     * @param users List of users
     * @return Statistics string
     */
    public String getTargetStats(List<User> users) {
        return String.format("Loaded %d target hashes", users.size());
    }
}
