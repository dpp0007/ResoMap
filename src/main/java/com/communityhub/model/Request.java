package com.communityhub.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Request model class representing help requests in the community hub
 * Demonstrates encapsulation and proper data modeling with status management
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
    private LocalDateTime assignedAt;
    private LocalDateTime completedAt;
    private String notes;
    private int quantityRequested;
    
    // Display properties (not persisted to database)
    private transient String resourceName;
    private transient String requesterName;
    
    /**
     * Default constructor for Request
     */
    public Request() {
        this.requestId = UUID.randomUUID().toString();
        this.status = RequestStatus.PENDING;
        this.urgencyLevel = UrgencyLevel.MEDIUM;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.quantityRequested = 1;
    }
    
    /**
     * Constructor with basic request information
     * @param requesterId ID of the user making the request
     * @param resourceId ID of the requested resource
     * @param description Description of the request
     */
    public Request(String requesterId, String resourceId, String description) {
        this();
        this.requesterId = requesterId;
        this.resourceId = resourceId;
        this.description = description;
    }
    
    /**
     * Constructor with detailed request information
     * @param requesterId ID of the user making the request
     * @param resourceId ID of the requested resource
     * @param description Description of the request
     * @param urgencyLevel Urgency level of the request
     * @param quantityRequested Quantity requested
     */
    public Request(String requesterId, String resourceId, String description, 
                  UrgencyLevel urgencyLevel, int quantityRequested) {
        this();
        this.requesterId = requesterId;
        this.resourceId = resourceId;
        this.description = description;
        this.urgencyLevel = urgencyLevel;
        this.quantityRequested = quantityRequested;
    }
    
    /**
     * Constructor with all fields (for database loading)
     * @param requestId Unique request identifier
     * @param requesterId ID of the requester
     * @param resourceId ID of the requested resource
     * @param volunteerId ID of the assigned volunteer
     * @param status Request status
     * @param description Request description
     * @param urgencyLevel Urgency level
     * @param createdAt Creation timestamp
     * @param updatedAt Last update timestamp
     */
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
        this.quantityRequested = 1;
    }
    
    // Getters and setters with proper encapsulation
    
    public String getRequestId() {
        return requestId;
    }
    
    public void setRequestId(String requestId) {
        this.requestId = requestId;
        updateTimestamp();
    }
    
    public String getRequesterId() {
        return requesterId;
    }
    
    public void setRequesterId(String requesterId) {
        this.requesterId = requesterId;
        updateTimestamp();
    }
    
    public String getResourceId() {
        return resourceId;
    }
    
    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
        updateTimestamp();
    }
    
    public String getVolunteerId() {
        return volunteerId;
    }
    
    public void setVolunteerId(String volunteerId) {
        this.volunteerId = volunteerId;
        if (volunteerId != null && status == RequestStatus.PENDING) {
            setStatus(RequestStatus.ASSIGNED);
            this.assignedAt = LocalDateTime.now();
        }
        updateTimestamp();
    }
    
    public RequestStatus getStatus() {
        return status;
    }
    
    public void setStatus(RequestStatus status) {
        if (status != null && (this.status == null || this.status.canTransitionTo(status))) {
            this.status = status;
            if (status == RequestStatus.COMPLETED) {
                this.completedAt = LocalDateTime.now();
            }
            updateTimestamp();
        }
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
        updateTimestamp();
    }
    
    public UrgencyLevel getUrgencyLevel() {
        return urgencyLevel;
    }
    
    public void setUrgencyLevel(UrgencyLevel urgencyLevel) {
        this.urgencyLevel = urgencyLevel != null ? urgencyLevel : UrgencyLevel.MEDIUM;
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
    
    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }
    
    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }
    
    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
    
    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
        updateTimestamp();
    }
    
    public int getQuantityRequested() {
        return quantityRequested;
    }
    
    public void setQuantityRequested(int quantityRequested) {
        this.quantityRequested = Math.max(1, quantityRequested);
        updateTimestamp();
    }
    
    // Display property getters and setters
    
    /**
     * Gets the ID for display purposes (alias for requestId)
     * @return Request ID
     */
    public String getId() {
        return requestId;
    }
    
    /**
     * Gets the resource name for display
     * @return Resource name
     */
    public String getResourceName() {
        return resourceName;
    }
    
    /**
     * Sets the resource name for display
     * @param resourceName Resource name
     */
    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }
    
    /**
     * Gets the requester name for display
     * @return Requester name
     */
    public String getRequesterName() {
        return requesterName;
    }
    
    /**
     * Sets the requester name for display
     * @param requesterName Requester name
     */
    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }
    
    /**
     * Gets the urgency for display (alias for urgencyLevel)
     * @return Urgency level
     */
    public String getUrgency() {
        return urgencyLevel != null ? urgencyLevel.getDisplayName() : "Unknown";
    }
    
    /**
     * Updates the timestamp when request data is modified
     */
    private void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Assigns a volunteer to this request
     * @param volunteerId ID of the volunteer to assign
     * @return true if assignment was successful
     */
    public boolean assignVolunteer(String volunteerId) {
        if (volunteerId != null && status == RequestStatus.PENDING) {
            this.volunteerId = volunteerId;
            this.status = RequestStatus.ASSIGNED;
            this.assignedAt = LocalDateTime.now();
            updateTimestamp();
            return true;
        }
        return false;
    }
    
    /**
     * Unassigns the volunteer from this request
     * @return true if unassignment was successful
     */
    public boolean unassignVolunteer() {
        if (status == RequestStatus.ASSIGNED) {
            this.volunteerId = null;
            this.status = RequestStatus.PENDING;
            this.assignedAt = null;
            updateTimestamp();
            return true;
        }
        return false;
    }
    
    /**
     * Marks the request as in progress
     * @return true if status change was successful
     */
    public boolean markInProgress() {
        if (status == RequestStatus.ASSIGNED) {
            this.status = RequestStatus.IN_PROGRESS;
            updateTimestamp();
            return true;
        }
        return false;
    }
    
    /**
     * Marks the request as completed
     * @return true if status change was successful
     */
    public boolean markCompleted() {
        if (status == RequestStatus.IN_PROGRESS) {
            this.status = RequestStatus.COMPLETED;
            this.completedAt = LocalDateTime.now();
            updateTimestamp();
            return true;
        }
        return false;
    }
    
    /**
     * Cancels the request
     * @return true if cancellation was successful
     */
    public boolean cancel() {
        if (status.isActive()) {
            this.status = RequestStatus.CANCELLED;
            updateTimestamp();
            return true;
        }
        return false;
    }
    
    /**
     * Checks if the request is assigned to a volunteer
     * @return true if request is assigned
     */
    public boolean isAssigned() {
        return volunteerId != null && status != RequestStatus.PENDING;
    }
    
    /**
     * Checks if the request is active (not completed or cancelled)
     * @return true if request is active
     */
    public boolean isActive() {
        return status != null && status.isActive();
    }
    
    /**
     * Checks if the request is overdue (created more than specified hours ago and still active)
     * @param hours Number of hours to consider overdue
     * @return true if request is overdue
     */
    public boolean isOverdue(int hours) {
        if (!isActive()) {
            return false;
        }
        return createdAt != null && createdAt.isBefore(LocalDateTime.now().minusHours(hours));
    }
    
    /**
     * Gets the age of the request in hours
     * @return Age in hours
     */
    public long getAgeInHours() {
        if (createdAt == null) {
            return 0;
        }
        return java.time.Duration.between(createdAt, LocalDateTime.now()).toHours();
    }
    
    /**
     * Validates request data
     * @return true if request data is valid
     */
    public boolean isValid() {
        return requestId != null && !requestId.isEmpty() &&
               requesterId != null && !requesterId.isEmpty() &&
               resourceId != null && !resourceId.isEmpty() &&
               status != null &&
               urgencyLevel != null &&
               quantityRequested > 0;
    }
    
    /**
     * Gets a display summary of the request
     * @return Summary string
     */
    public String getSummary() {
        return String.format("Request #%s - %s (%s) - %s", 
                           requestId.substring(0, 8), 
                           urgencyLevel.getDisplayName(),
                           status.getDisplayName(),
                           description != null && description.length() > 50 ? 
                               description.substring(0, 47) + "..." : description);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Request request = (Request) obj;
        return Objects.equals(requestId, request.requestId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(requestId);
    }
    
    @Override
    public String toString() {
        return String.format("Request{requestId='%s', requesterId='%s', resourceId='%s', volunteerId='%s', status=%s, urgency=%s, created=%s}", 
                           requestId, requesterId, resourceId, volunteerId, status, urgencyLevel, createdAt);
    }
}