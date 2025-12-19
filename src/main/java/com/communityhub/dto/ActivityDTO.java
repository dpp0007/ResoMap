package com.communityhub.dto;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for activity feed items
 * Represents a single activity event in the system
 */
public class ActivityDTO {
    
    private String activityId;
    private String type; // REQUEST_CREATED, REQUEST_ASSIGNED, REQUEST_COMPLETED, RESOURCE_CREATED, FEEDBACK_SUBMITTED
    private String message; // Human-readable message
    private LocalDateTime timestamp;
    private String actorName; // Who performed the action
    private String actorRole; // ADMIN, VOLUNTEER, REQUESTER
    private String relatedResourceName; // Resource involved (if any)
    private String relatedRequestId; // Request involved (if any)
    
    // Constructor
    public ActivityDTO(String activityId, String type, String message, LocalDateTime timestamp, 
                      String actorName, String actorRole, String relatedResourceName, String relatedRequestId) {
        this.activityId = activityId;
        this.type = type;
        this.message = message;
        this.timestamp = timestamp;
        this.actorName = actorName;
        this.actorRole = actorRole;
        this.relatedResourceName = relatedResourceName;
        this.relatedRequestId = relatedRequestId;
    }
    
    // Getters
    public String getActivityId() {
        return activityId;
    }
    
    public String getType() {
        return type;
    }
    
    public String getMessage() {
        return message;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public String getActorName() {
        return actorName;
    }
    
    public String getActorRole() {
        return actorRole;
    }
    
    public String getRelatedResourceName() {
        return relatedResourceName;
    }
    
    public String getRelatedRequestId() {
        return relatedRequestId;
    }
    
    @Override
    public String toString() {
        return "ActivityDTO{" +
                "type='" + type + '\'' +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                ", actorName='" + actorName + '\'' +
                '}';
    }
}
