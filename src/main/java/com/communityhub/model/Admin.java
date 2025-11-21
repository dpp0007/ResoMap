package com.communityhub.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Admin user class extending User
 * Demonstrates inheritance and polymorphism with admin-specific functionality
 */
public class Admin extends User {
    
    private Set<String> permissions;
    private int resourcesManaged;
    private int usersManaged;
    
    /**
     * Default constructor for Admin
     */
    public Admin() {
        super();
        this.permissions = new HashSet<>();
        initializeAdminPermissions();
    }
    
    /**
     * Constructor with basic admin information
     * @param username Admin's username
     * @param email Admin's email address
     * @param passwordHash Hashed password
     */
    public Admin(String username, String email, String passwordHash) {
        super(username, email, passwordHash, UserRole.ADMIN);
        this.permissions = new HashSet<>();
        initializeAdminPermissions();
    }
    
    /**
     * Constructor with all fields (for database loading)
     * @param userId Unique user identifier
     * @param username Admin's username
     * @param email Admin's email address
     * @param passwordHash Hashed password
     * @param createdAt Creation timestamp
     * @param updatedAt Last update timestamp
     */
    public Admin(String userId, String username, String email, String passwordHash, 
                LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(userId, username, email, passwordHash, UserRole.ADMIN, createdAt, updatedAt);
        this.permissions = new HashSet<>();
        initializeAdminPermissions();
    }
    
    /**
     * Initializes default admin permissions
     */
    private void initializeAdminPermissions() {
        permissions.add("CREATE_RESOURCE");
        permissions.add("UPDATE_RESOURCE");
        permissions.add("DELETE_RESOURCE");
        permissions.add("VIEW_ALL_RESOURCES");
        permissions.add("MANAGE_USERS");
        permissions.add("VIEW_ALL_REQUESTS");
        permissions.add("ASSIGN_VOLUNTEERS");
        permissions.add("GENERATE_REPORTS");
        permissions.add("SYSTEM_CONFIGURATION");
    }
    
    @Override
    public void performRoleSpecificAction() {
        // Admin-specific resource management actions
        System.out.println("Admin " + getUsername() + " is managing community resources and users");
        // This could trigger admin dashboard updates, resource management, etc.
    }
    
    @Override
    public String getDashboardView() {
        return "/fxml/admin-dashboard.fxml";
    }
    
    @Override
    public String[] getRoleSpecificMenuItems() {
        return new String[]{
            "Resource Management",
            "User Management", 
            "Request Overview",
            "System Reports",
            "Settings"
        };
    }
    
    @Override
    public boolean canPerformAction(String action) {
        return permissions.contains(action.toUpperCase());
    }
    
    /**
     * Adds a permission to the admin
     * @param permission Permission to add
     */
    public void addPermission(String permission) {
        if (permission != null && !permission.trim().isEmpty()) {
            permissions.add(permission.toUpperCase());
            updateTimestamp();
        }
    }
    
    /**
     * Removes a permission from the admin
     * @param permission Permission to remove
     */
    public void removePermission(String permission) {
        if (permission != null) {
            permissions.remove(permission.toUpperCase());
            updateTimestamp();
        }
    }
    
    /**
     * Checks if admin has a specific permission
     * @param permission Permission to check
     * @return true if admin has the permission
     */
    public boolean hasPermission(String permission) {
        return permission != null && permissions.contains(permission.toUpperCase());
    }
    
    /**
     * Gets all permissions for this admin
     * @return Set of permissions
     */
    public Set<String> getPermissions() {
        return new HashSet<>(permissions);
    }
    
    /**
     * Sets permissions for this admin
     * @param permissions Set of permissions
     */
    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions != null ? new HashSet<>(permissions) : new HashSet<>();
        updateTimestamp();
    }
    
    /**
     * Gets the number of resources managed by this admin
     * @return Number of resources managed
     */
    public int getResourcesManaged() {
        return resourcesManaged;
    }
    
    /**
     * Sets the number of resources managed
     * @param resourcesManaged Number of resources managed
     */
    public void setResourcesManaged(int resourcesManaged) {
        this.resourcesManaged = Math.max(0, resourcesManaged);
        updateTimestamp();
    }
    
    /**
     * Increments the count of resources managed
     */
    public void incrementResourcesManaged() {
        this.resourcesManaged++;
        updateTimestamp();
    }
    
    /**
     * Gets the number of users managed by this admin
     * @return Number of users managed
     */
    public int getUsersManaged() {
        return usersManaged;
    }
    
    /**
     * Sets the number of users managed
     * @param usersManaged Number of users managed
     */
    public void setUsersManaged(int usersManaged) {
        this.usersManaged = Math.max(0, usersManaged);
        updateTimestamp();
    }
    
    /**
     * Increments the count of users managed
     */
    public void incrementUsersManaged() {
        this.usersManaged++;
        updateTimestamp();
    }
    
    /**
     * Checks if this admin can manage a specific resource type
     * @param resourceType Type of resource
     * @return true if admin can manage this resource type
     */
    public boolean canManageResourceType(String resourceType) {
        // Admins can manage all resource types by default
        return hasPermission("CREATE_RESOURCE") || hasPermission("UPDATE_RESOURCE");
    }
    
    /**
     * Gets admin statistics summary
     * @return Summary string of admin activities
     */
    public String getAdminSummary() {
        return String.format("Admin %s: %d resources managed, %d users managed, %d permissions", 
                           getUsername(), resourcesManaged, usersManaged, permissions.size());
    }
    
    @Override
    public String toString() {
        return String.format("Admin{userId='%s', username='%s', email='%s', permissions=%d, resourcesManaged=%d}", 
                           getUserId(), getUsername(), getEmail(), permissions.size(), resourcesManaged);
    }
}