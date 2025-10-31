package org.example.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Service responsible for tracking and displaying progress.
 */
public class ProgressTracker {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Reports progress for hash building phase.
     */
    public void reportHashingProgress(int hashesComputed) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        System.out.printf("\r[%s] Hashed %d passwords...", timestamp, hashesComputed);
    }
    
    /**
     * Reports progress for user lookup phase.
     */
    public void reportLookupProgress(int usersChecked, int totalUsers, int passwordsFound) {
        double progressPercent = (double) usersChecked / totalUsers * 100;
        String timestamp = LocalDateTime.now().format(FORMATTER);
        System.out.printf("\r[%s] %.2f%% complete | Passwords Found: %d | Users Checked: %d/%d",
                timestamp, progressPercent, passwordsFound, usersChecked, totalUsers);
    }
    
    /**
     * Reports final statistics.
     */
    public void reportFinalStats(int passwordsFound, int hashesComputed, long durationMillis) {
        System.out.println("");
        System.out.println("Total passwords found: " + passwordsFound);
        System.out.println("Total hashes computed: " + hashesComputed);
        System.out.println("Total time spent (milliseconds): " + durationMillis);
    }
}
