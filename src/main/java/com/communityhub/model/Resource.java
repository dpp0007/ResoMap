package com.communityhub.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Resource model class representing community resources
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
    
    // Default constructor
    public Resource() {
        this.resourceId = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Constructor for new resource
    public Resource(String name, String description, String category, int quantity, 
                   String location, String contactInfo, String createdBy) {
        this();
        this.name = name;
        this.description = description;
        this.category = category;
        this.quantity = quantity;
        this.location = location;
        this.contactInfo = contactInfo;
        this.createdBy = createdBy;
    }
    
    // Constructor for existing resource (from database)
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
    }
    
    // Getters and Setters
    public String getResourceId() {
        return resourceId;
    }
    
    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
        this.updatedAt = LocalDateTime.now();
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getContactInfo() {
        return contactInfo;
    }
    
    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
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
    
    // Validation method
    public boolean isValid() {
        return name != null && !name.trim().isEmpty() &&
               category != null && !category.trim().isEmpty() &&
               quantity >= 0;
    }
    
    @Override
    public String toString() {
        return "Resource{" +
                "resourceId='" + resourceId + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", quantity=" + quantity +
                ", location='" + location + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Resource resource = (Resource) obj;
        return resourceId != null ? resourceId.equals(resource.resourceId) : resource.resourceId == null;
    }
    
    @Override
    public int hashCode() {
        return resourceId != null ? resourceId.hashCode() : 0;
    }
}