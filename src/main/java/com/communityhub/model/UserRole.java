package com.communityhub.model;

/**
 * Enumeration for user roles in the Community Resource Hub
 * Defines the different types of users and their access levels
 */
public enum UserRole {
    ADMIN("Administrator", "Full system access with resource and user management capabilities"),
    VOLUNTEER("Volunteer", "Can manage requests and coordinate resource distribution"),
    REQUESTER("Requester", "Can view resources and submit help requests");
    
    private final String displayName;
    private final String description;
    
    UserRole(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    /**
     * Gets the display name for the role
     * @return Display name
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Gets the description of the role
     * @return Role description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Checks if this role has admin privileges
     * @return true if this is an admin role
     */
    public boolean isAdmin() {
        return this == ADMIN;
    }
    
    /**
     * Checks if this role can manage requests
     * @return true if this role can manage requests
     */
    public boolean canManageRequests() {
        return this == ADMIN || this == VOLUNTEER;
    }
    
    /**
     * Checks if this role can manage resources
     * @return true if this role can manage resources
     */
    public boolean canManageResources() {
        return this == ADMIN;
    }
    
    /**
     * Gets role from string value
     * @param roleString String representation of role
     * @return UserRole enum value
     * @throws IllegalArgumentException if role string is invalid
     */
    public static UserRole fromString(String roleString) {
        if (roleString == null) {
            throw new IllegalArgumentException("Role string cannot be null");
        }
        
        try {
            return UserRole.valueOf(roleString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + roleString);
        }
    }
}