package org.example.service;

import org.example.model.User;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Service responsible for file I/O operations.
 * Optimized with buffering and capacity hints.
 */
public class FileService {
    
    private static final int INITIAL_USER_CAPACITY = 10000;
    private final DictionaryLoader dictionaryLoader;
    
    public FileService() {
        this.dictionaryLoader = new DictionaryLoader(true); // Enable deduplication
    }
    
    public FileService(boolean deduplicate) {
        this.dictionaryLoader = new DictionaryLoader(deduplicate);
    }
    
    /**
     * Loads dictionary passwords from a file.
     * 
     * @param filePath Path to the dictionary file
     * @return List of passwords
     * @throws IOException if file cannot be read
     */
    public List<String> loadDictionary(String filePath) throws IOException {
        return dictionaryLoader.loadDictionary(filePath);
    }
    
    /**
     * Loads users from a CSV file with optimized buffering.
     * 
     * @param filePath Path to the users file
     * @return List of User objects
     * @throws IOException if file cannot be read
     */
    public List<User> loadUsers(String filePath) throws IOException {
        List<User> users = new ArrayList<>(INITIAL_USER_CAPACITY);
        
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath), StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 2); // Limit splits to 2 for efficiency
                if (parts.length >= 2) {
                    users.add(new User(parts[0], parts[1]));
                }
            }
        }
        
        return users;
    }
    
    /**
     * Writes cracked passwords to a CSV file with optimized buffering.
     * 
     * @param filePath Path to the output file
     * @param users Collection of users with potential cracked passwords
     * @throws IOException if file cannot be written
     */
    public void writeCrackedPasswords(String filePath, Collection<User> users) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath), 8192)) {
            writer.write("user_name,hashed_password,plain_password\n");
            
            for (User user : users) {
                if (user.isFound()) {
                    // More efficient than String.format
                    writer.write(user.getUsername());
                    writer.write(',');
                    writer.write(user.getHashedPassword());
                    writer.write(',');
                    writer.write(user.getFoundPassword());
                    writer.write('\n');
                }
            }
        }
    }
}
