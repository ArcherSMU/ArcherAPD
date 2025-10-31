package org.example.model;

/**
 * Represents the result of a password cracking operation.
 */
public class CrackResult {
    private final int passwordsFound;
    private final int hashesComputed;
    private final long durationMillis;

    public CrackResult(int passwordsFound, int hashesComputed, long durationMillis) {
        this.passwordsFound = passwordsFound;
        this.hashesComputed = hashesComputed;
        this.durationMillis = durationMillis;
    }

    public int getPasswordsFound() {
        return passwordsFound;
    }

    public int getHashesComputed() {
        return hashesComputed;
    }

    public long getDurationMillis() {
        return durationMillis;
    }
}
