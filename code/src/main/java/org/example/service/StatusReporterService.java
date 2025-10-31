package org.example.service;

import org.example.model.CrackingStatistics;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Service that provides non-blocking, live status updates in a separate thread.
 * Uses ScheduledExecutorService for periodic updates without blocking main processing.
 * Reports every 1000 users/tasks processed.
 */
public class StatusReporterService {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final int UPDATE_INTERVAL_MS = 100; // Check every 100ms
    private static final int REPORT_INTERVAL = 1000; // Report every 1000 tasks

    private final CrackingStatistics statistics;
    private final ScheduledExecutorService scheduler;
    private volatile boolean running = false;
    private volatile int lastReportedTasks = 0;

    /**
     * Creates a new StatusReporterService.
     *
     * @param statistics the statistics object to read from
     */
    public StatusReporterService(CrackingStatistics statistics) {
        this.statistics = statistics;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "StatusReporter");
            thread.setDaemon(true); // Daemon thread won't prevent JVM shutdown
            return thread;
        });
    }

    /**
     * Starts the status reporter in a separate thread.
     * Updates are checked every 100ms and printed every 1000 tasks.
     */
    public void start() {
        if (running) {
            return;
        }
        running = true;
        lastReportedTasks = 0;

        scheduler.scheduleAtFixedRate(
                this::checkAndPrintStatus,
                0,
                UPDATE_INTERVAL_MS,
                TimeUnit.MILLISECONDS
        );
    }

    /**
     * Stops the status reporter and shuts down the scheduler.
     */
    public void stop() {
        running = false;
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(1, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Checks if we've crossed a 1000-task milestone and prints status if so.
     * This ensures we don't miss milestones even if processing is very fast.
     */
    private void checkAndPrintStatus() {
        int currentTasks = statistics.getTasksProcessed();
        int totalTasks = statistics.getTotalTasks();
        
        // Check if we've crossed a milestone (every 1000 tasks)
        if (totalTasks > 0 && currentTasks >= lastReportedTasks + REPORT_INTERVAL) {
            // Calculate which milestone we should report
            int milestone = (currentTasks / REPORT_INTERVAL) * REPORT_INTERVAL;
            
            if (milestone > lastReportedTasks) {
                String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
                double progressPercent = (milestone * 100.0) / totalTasks;
                int passwordsFound = statistics.getPasswordsFound();

                System.out.printf("[%s] %.2f%% complete | Passwords Found: %d | Users Checked: %d/%d%n",
                        timestamp, progressPercent, passwordsFound, milestone, totalTasks);
                System.out.flush();
                
                lastReportedTasks = milestone;
            }
        }
    }

    /**
     * Prints a final summary after cracking is complete.
     */
    public void printFinalSummary() {
        System.out.println(); // New line after progress updates
        System.out.println("Total passwords found: " + statistics.getPasswordsFound());
        System.out.println("Total hashes computed: " + statistics.getHashesComputed());
        System.out.println("Total time spent (milliseconds): " + statistics.getElapsedTime());
    }
}
