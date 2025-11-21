package com.communityhub.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Resource model class representing community resources
 * Demonstrates encapsulation and proper data modeling
 */
public class Resource {
    
    private String resourceId;
    private String name;
    private String description;
    private String category;
    private int quantity;
    private String location;
    private String contactInfo;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isAvailable;
    
    /**
     * Default constructor for Resource
     */
    public Resource() {
        this.resourceId = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isAvailable = true;
        this.quantity = 0;
    }
    
    /**
     * Constructor with basic resource information
     * @param name Resource name
     * @param category Resource category
     * @param description Resource description
     */
    public Resource(String name, String category, String description) {
        this();
        this.name = name;
        this.category = category;
        this.description = description;
    }
    
    /**
     * Constructor with detailed resource information
     * @param name Resource name
     * @param category Resource category
     * @param description Resource description
     * @param quantity Available quantity
     * @param location Resource location
     * @param contactInfo Contact information
     * @param createdBy User ID who created the resource
     */
    public Resource(String name, String category, String description, int quantity, 
                   String location, String contactInfo, String createdBy) {
        this();
        this.name = name;
        this.category = category;
        this.description = description;
        this.quantity = quantity;
        this.location = location;
        this.contactInfo = contactInfo;
        this.createdBy = createdBy;
    }
    
    /**
     * Constructor with all fields (for database loading)
     * @param resourceId Unique resource identifier
     * @param name Resource name
     * @param description Resource description
     * @param category Resource category
     * @param quantity Available quantity
     * @param location Resource location
     * @param contactInfo Contact information
     * @param createdBy User ID who created the resource
     * @param createdAt Creation timestamp
     * @param updatedAt Last update timestamp
     */
    public Resource(String resourceId, String name, String description, String category, 
                   int quantity, String location, String contactInfo, String createdBy,
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.resourceId = resourceId;
        this.name = name;
        this.description = description;
        this.category = category;
        this.quantity = quantity;
        this.location = location;
        this.contactInfo = contactInfo;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isAvailable = quantity > 0;
    }
    
    // Getters and setters with proper encapsulation
    
    public String getResourceId() {
        return resourceId;
    }
    
    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
        updateTimestamp();
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
        updateTimestamp();
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
        updateTimestamp();
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
        updateTimestamp();
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = Math.max(0, quantity);
        this.isAvailable = this.quantity > 0;
        updateTimestamp();
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
        updateTimestamp();
    }
    
    public String getContactInfo() {
        return contactInfo;
    }
    
    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
        updateTimestamp();
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
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
    
    public boolean isAvailable() {
        return isAvailable && quantity > 0;
    }
    
    public void setAvailable(boolean available) {
        this.isAvailable = available;
        updateTimestamp();
    }
    
    /**
     * Updates the timestamp when resource data is modified
     */
    private void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Decreases the quantity by the specified amount
     * @param amount Amount to decrease
     * @return true if successful, false if insufficient quantity
     */
    public boolean decreaseQuantity(int amount) {
        if (amount > 0 && quantity >= amount) {
            quantity -= amount;
            isAvailable = quantity > 0;
            updateTimestamp();
            return true;
        }
        return false;
    }
    
    /**
     * Increases the quantity by the specified amount
     * @param amount Amount to increase
     */
    public void increaseQuantity(int amount) {
        if (amount > 0) {
            quantity += amount;
            isAvailable = true;
            updateTimestamp();
        }
    }
    
    /**
     * Validates resource data
     * @return true if resource data is valid
     */
    public boolean isValid() {
        return resourceId != null && !resourceId.isEmpty() &&
               name != null && !name.trim().isEmpty() &&
               category != null && !category.trim().isEmpty() &&
               quantity >= 0;
    }
    
    /**
     * Gets a short display name for the resource
     * @return Display name
     */
    public String getDisplayName() {
        return name + " (" + category + ")";
    }
    
    /**
     * Gets the availability status as a string
     * @return Availability status
     */
    public String getAvailabilityStatus() {
        if (!isAvailable) {
            return "Unavailable";
        } else if (quantity == 0) {
            return "Out of Stock";
        } else if (quantity < 5) {
            return "Low Stock (" + quantity + ")";
        } else {
            return "Available (" + quantity + ")";
        }
    }
    
    /**
     * Checks if the resource matches a search query
     * @param query Search query
     * @return true if resource matches the query
     */
    public boolean matchesSearch(String query) {
        if (query == null || query.trim().isEmpty()) {
            return true;
        }
        
        String lowerQuery = query.toLowerCase();
        return (name != null && name.toLowerCase().contains(lowerQuery)) ||
               (category != null && category.toLowerCase().contains(lowerQuery)) ||
               (description != null && description.toLowerCase().contains(lowerQuery)) ||
               (location != null && location.toLowerCase().contains(lowerQuery));
    }
    
    /**
     * Gets resource summary information
     * @return Summary string
     */
    public String getSummary() {
        return String.format("%s - %s (Qty: %d) at %s", 
                           name, category, quantity, location != null ? location : "Unknown location");
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Resource resource = (Resource) obj;
        return Objects.equals(resourceId, resource.resourceId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(resourceId);
    }
    
    @Override
    public String toString() {
        return String.format("Resource{resourceId='%s', name='%s', category='%s', quantity=%d, location='%s', available=%s}", 
                           resourceId, name, category, quantity, location, isAvailable);
    }
}