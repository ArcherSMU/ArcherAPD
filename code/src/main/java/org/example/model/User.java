package org.example.model;

/**
 * Represents a user with their hashed password and cracking status.
 */
public class User {
    private final String username;
    private final String hashedPassword;
    private boolean isFound;
    private String foundPassword;

    public User(String username, String hashedPassword) {
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.isFound = false;
        this.foundPassword = null;
    }

    public String getUsername() {
        return username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public boolean isFound() {
        return isFound;
    }

    public void setFound(boolean found) {
        isFound = found;
    }

    public String getFoundPassword() {
        return foundPassword;
    }

    public void setFoundPassword(String foundPassword) {
        this.foundPassword = foundPassword;
        this.isFound = true;
    }
}
