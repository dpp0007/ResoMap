package com.communityhub.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Abstract base class for all users in the Community Resource Hub
 * Demonstrates inheritance, encapsulation, and polymorphism principles
 * Provides common user properties and abstract methods for role-specific behavior
 */
public abstract class User {
    
    // Private fields demonstrating encapsulation
    private String userId;
    private String username;
    private String email;
    private String passwordHash;
    private UserRole role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean active = true; // For admin to activate/deactivate users
    private int failedLoginAttempts = 0; // For account lockout tracking
    
    /**
     * Default constructor for User
     */
    protected User() {
        this.userId = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Constructor with basic user information
     * @param username User's username
     * @param email User's email address
     * @param passwordHash Hashed password
     * @param role User's role
     */
    protected User(String username, String email, String passwordHash, UserRole role) {
        this();
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }
    
    /**
     * Constructor with all fields (for database loading)
     * @param userId Unique user identifier
     * @param username User's username
     * @param email User's email address
     * @param passwordHash Hashed password
     * @param role User's role
     * @param createdAt Creation timestamp
     * @param updatedAt Last update timestamp
     */
    protected User(String userId, String username, String email, String passwordHash, 
                  UserRole role, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Abstract methods for polymorphism - must be implemented by subclasses
    
    /**
     * Performs role-specific actions
     * This method demonstrates polymorphism - each subclass implements different behavior
     */
    public abstract void performRoleSpecificAction();
    
    /**
     * Gets the dashboard view appropriate for this user role
     * @return Dashboard view name/path
     */
    public abstract String getDashboardView();
    
    /**
     * Gets role-specific menu items
     * @return Array of menu item names
     */
    public abstract String[] getRoleSpecificMenuItems();
    
    /**
     * Checks if user can perform a specific action
     * @param action The action to check
     * @return true if user can perform the action
     */
    public abstract boolean canPerformAction(String action);
    
    // Getters and setters demonstrating encapsulation
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
        updateTimestamp();
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
        updateTimestamp();
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
        updateTimestamp();
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
        updateTimestamp();
    }
    
    public UserRole getRole() {
        return role;
    }
    
    public void setRole(UserRole role) {
        this.role = role;
        updateTimestamp();
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
        updateTimestamp();
    }
    
    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }
    
    public void setFailedLoginAttempts(int failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
        updateTimestamp();
    }
    
    /**
     * Resets failed login attempts (used by admin to unlock accounts)
     */
    public void resetFailedLoginAttempts() {
        this.failedLoginAttempts = 0;
        updateTimestamp();
    }
    
    /**
     * Updates the timestamp when user data is modified
     */
    protected void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Validates user data
     * @return true if user data is valid
     */
    public boolean isValid() {
        return userId != null && !userId.isEmpty() &&
               username != null && !username.trim().isEmpty() &&
               email != null && !email.trim().isEmpty() &&
               passwordHash != null && !passwordHash.isEmpty() &&
               role != null;
    }
    
    /**
     * Gets the display name for the user (username or email)
     * @return Display name
     */
    public String getDisplayName() {
        return username != null && !username.trim().isEmpty() ? username : email;
    }
    
    /**
     * Checks if this user has admin privileges
     * @return true if user is an admin
     */
    public boolean isAdmin() {
        return role != null && role.isAdmin();
    }
    
    /**
     * Checks if this user can manage requests
     * @return true if user can manage requests
     */
    public boolean canManageRequests() {
        return role != null && role.canManageRequests();
    }
    
    /**
     * Checks if this user can manage resources
     * @return true if user can manage resources
     */
    public boolean canManageResources() {
        return role != null && role.canManageResources();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        User user = (User) obj;
        return Objects.equals(userId, user.userId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
    
    @Override
    public String toString() {
        return String.format("%s{userId='%s', username='%s', email='%s', role=%s, createdAt=%s}", 
                           getClass().getSimpleName(), userId, username, email, role, createdAt);
    }
}