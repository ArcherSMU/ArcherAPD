package org.example.model;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Thread-safe statistics tracker for password cracking operations.
 * Uses atomic types for lock-free updates from main thread and reads from reporter thread.
 */
public class CrackingStatistics {
    
    private final AtomicInteger tasksProcessed = new AtomicInteger(0);
    private final AtomicInteger passwordsFound = new AtomicInteger(0);
    private final AtomicInteger totalTasks = new AtomicInteger(0);
    private final AtomicInteger hashesComputed = new AtomicInteger(0);
    private final AtomicLong startTime = new AtomicLong(0);
    
    /**
     * Sets the total number of tasks (users) to process.
     */
    public void setTotalTasks(int total) {
        this.totalTasks.set(total);
    }
    
    /**
     * Increments the tasks processed counter.
     */
    public void incrementTasksProcessed() {
        tasksProcessed.incrementAndGet();
    }
    
    /**
     * Increments the passwords found counter.
     */
    public void incrementPasswordsFound() {
        passwordsFound.incrementAndGet();
    }
    
    /**
     * Sets the number of hashes computed.
     */
    public void setHashesComputed(int hashes) {
        this.hashesComputed.set(hashes);
    }
    
    /**
     * Records the start time of the operation.
     */
    public void recordStartTime() {
        this.startTime.set(System.currentTimeMillis());
    }
    
    /**
     * Gets the current number of tasks processed.
     */
    public int getTasksProcessed() {
        return tasksProcessed.get();
    }
    
    /**
     * Gets the total number of tasks.
     */
    public int getTotalTasks() {
        return totalTasks.get();
    }
    
    /**
     * Gets the number of passwords found.
     */
    public int getPasswordsFound() {
        return passwordsFound.get();
    }
    
    /**
     * Gets the number of hashes computed.
     */
    public int getHashesComputed() {
        return hashesComputed.get();
    }
    
    /**
     * Calculates progress percentage.
     */
    public double getProgressPercent() {
        int total = totalTasks.get();
        if (total == 0) return 0.0;
        return (tasksProcessed.get() * 100.0) / total;
    }
    
    /**
     * Gets remaining tasks.
     */
    public int getRemainingTasks() {
        return totalTasks.get() - tasksProcessed.get();
    }
    
    /**
     * Gets elapsed time in milliseconds.
     */
    public long getElapsedTime() {
        long start = startTime.get();
        if (start == 0) return 0;
        return System.currentTimeMillis() - start;
    }
}
