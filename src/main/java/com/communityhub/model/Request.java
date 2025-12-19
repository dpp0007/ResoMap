package com.communityhub.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Request model class representing resource requests
 */
public class Request {
    
    private String requestId;
    private String requesterId;
    private String resourceId;
    private String volunteerId;
    private RequestStatus status;
    private String description;
    private UrgencyLevel urgencyLevel;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Default constructor
    public Request() {
        this.requestId = UUID.randomUUID().toString();
        this.status = RequestStatus.PENDING;
        this.urgencyLevel = UrgencyLevel.MEDIUM;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Constructor for new request
    public Request(String requesterId, String resourceId, String description, UrgencyLevel urgencyLevel) {
        this();
        this.requesterId = requesterId;
        this.resourceId = resourceId;
        this.description = description;
        this.urgencyLevel = urgencyLevel;
    }
    
    // Constructor for existing request (from database)
    public Request(String requestId, String requesterId, String resourceId, String volunteerId,
                  RequestStatus status, String description, UrgencyLevel urgencyLevel,
                  LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.requestId = requestId;
        this.requesterId = requesterId;
        this.resourceId = resourceId;
        this.volunteerId = volunteerId;
        this.status = status;
        this.description = description;
        this.urgencyLevel = urgencyLevel;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
    public String getRequestId() {
        return requestId;
    }
    
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    
    public String getRequesterId() {
        return requesterId;
    }
    
    public void setRequesterId(String requesterId) {
        this.requesterId = requesterId;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getResourceId() {
        return resourceId;
    }
    
    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getVolunteerId() {
        return volunteerId;
    }
    
    public void setVolunteerId(String volunteerId) {
        this.volunteerId = volunteerId;
        this.updatedAt = LocalDateTime.now();
    }
    
    public RequestStatus getStatus() {
        return status;
    }
    
    public void setStatus(RequestStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }
    
    public UrgencyLevel getUrgencyLevel() {
        return urgencyLevel;
    }
    
    public void setUrgencyLevel(UrgencyLevel urgencyLevel) {
        this.urgencyLevel = urgencyLevel;
        this.updatedAt = LocalDateTime.now();
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
        return requesterId != null && !requesterId.trim().isEmpty() &&
               resourceId != null && !resourceId.trim().isEmpty() &&
               status != null &&
               urgencyLevel != null;
    }
    
    @Override
    public String toString() {
        return "Request{" +
                "requestId='" + requestId + '\'' +
                ", requesterId='" + requesterId + '\'' +
                ", resourceId='" + resourceId + '\'' +
                ", volunteerId='" + volunteerId + '\'' +
                ", status=" + status +
                ", urgencyLevel=" + urgencyLevel +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Request request = (Request) obj;
        return requestId != null ? requestId.equals(request.requestId) : request.requestId == null;
    }
    
    @Override
    public int hashCode() {
        return requestId != null ? requestId.hashCode() : 0;
    }
}