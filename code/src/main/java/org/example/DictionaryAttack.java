package org.example;

import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.nio.charset.StandardCharsets;

public class DictionaryAttack {

    static HashMap<String, User> users = new HashMap<>();
    static int passwordsFound = 0;
    static int hashesComputed = 0;

    public static void main(String[] args) throws Exception {

        // Check if both file paths are provided as command-line arguments
        if (args.length < 2) {
            System.out.println("Usage: java -jar <jar-file-name>.jar <input_file> <dictionary_file> <output_file>");
            System.exit(1);
        }

        String usersPath = args[0];
        String dictionaryPath = args[1];
        String passwordsPath = args[2];

//        String datasetPath = "/Users/kasung/Projects/se301/project/code/se301/src/datasets/large/";
//
//        String dictionaryPath = datasetPath + "dictionary.txt";
//        String usersPath = datasetPath + "in.txt";
//        String passwordsPath = datasetPath + "out.txt";

        long start = System.currentTimeMillis();
        List<String> allPasswords = loadDictionary(dictionaryPath);
        loadUsers(usersPath);

        // Build reverse lookup map: hash -> password (O(M) time, O(M) space)
        System.out.println("Building hash lookup table from dictionary...");
        HashMap<String, String> hashToPassword = new HashMap<>();
        for (String password : allPasswords) {
            String hash = sha256(password);
            hashesComputed++;
            hashToPassword.put(hash, password);
            
            if (hashesComputed % 10000 == 0) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String timestamp = LocalDateTime.now().format(formatter);
                System.out.printf("\r[%s] Hashed %d passwords...", timestamp, hashesComputed);
            }
        }
        System.out.println("");
        System.out.println("Hash lookup table built with " + hashToPassword.size() + " entries.");

        // Lookup each user's hash in the map (O(N) time with O(1) lookups)
        System.out.println("Looking up user password hashes...");
        int usersChecked = 0;
        for (User user : users.values()) {
            usersChecked++;
            String crackedPassword = hashToPassword.get(user.hashedPassword);
            
            if (crackedPassword != null) {
                user.isFound = true;
                user.foundPassword = crackedPassword;
                passwordsFound++;
            }
            
            if (usersChecked % 100 == 0) {
                double progressPercent = (double) usersChecked / users.size() * 100;
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String timestamp = LocalDateTime.now().format(formatter);
                System.out.printf("\r[%s] %.2f%% complete | Passwords Found: %d | Users Checked: %d/%d",
                        timestamp, progressPercent, passwordsFound, usersChecked, users.size());
            }
        }

        System.out.println("");
        System.out.println("Total passwords found: " + passwordsFound);
        System.out.println("Total hashes computed: " + hashesComputed);
        System.out.println("Total time spent (milliseconds): " + (System.currentTimeMillis() - start));

        if (passwordsFound > 0) {
            writeCrackedPasswordsToCSV(passwordsPath);
        }
    }


    /**
     * Writes the successfully cracked user credentials to a CSV file.
     * @param filePath The path of the CSV file to write to.
     */
    static void writeCrackedPasswordsToCSV(String filePath) {
        File file = new File(filePath);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // Write the CSV header
            writer.write("user_name,hashed_password,plain_password\n");

            // Iterate through all users and write the details of the cracked ones
            for (User user : users.values()) {
                if (user.isFound) {
                    String line = String.format("%s,%s,%s\n",
                            user.username,
                            user.hashedPassword,
                            user.foundPassword);
                    writer.write(line);
                }
            }
            System.out.println("\nCracked password details have been written to " + filePath);
        } catch (IOException e) {
            System.err.println("Error: Could not write to CSV file: " + e.getMessage());
        }
    }

    static List<String> loadDictionary(String filePath) throws IOException {
        List<String> allWords = new ArrayList<>();
        try {
            allWords.addAll(Files.readAllLines(Paths.get(filePath)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return allWords;
    }

    static void loadUsers(String filename) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filename));
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length >= 2) {
                String username = parts[0];
                String hashedPassword = parts[1];
                users.put(username, new User(username, hashedPassword));
            }
        }
    }

    static String sha256(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder hex = new StringBuilder();
        for (byte b : hash) {
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }

    static class User {
        String username;
        String hashedPassword;
        boolean isFound = false;
        String foundPassword = null;

        public User(String username, String hashedPassword) {
            this.username = username;
            this.hashedPassword = hashedPassword;
        }
    }
}

