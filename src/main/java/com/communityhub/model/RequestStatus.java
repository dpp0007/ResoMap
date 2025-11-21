package com.communityhub.model;

/**
 * Enumeration for request status in the Community Resource Hub
 * Defines the different states a request can be in
 */
public enum RequestStatus {
    PENDING("Pending", "Request has been submitted and is waiting for assignment"),
    ASSIGNED("Assigned", "Request has been assigned to a volunteer"),
    IN_PROGRESS("In Progress", "Volunteer is actively working on the request"),
    COMPLETED("Completed", "Request has been successfully fulfilled"),
    CANCELLED("Cancelled", "Request has been cancelled by the requester or admin");
    
    private final String displayName;
    private final String description;
    
    RequestStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    /**
     * Gets the display name for the status
     * @return Display name
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Gets the description of the status
     * @return Status description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Checks if this status indicates the request is active
     * @return true if request is active
     */
    public boolean isActive() {
        return this == PENDING || this == ASSIGNED || this == IN_PROGRESS;
    }
    
    /**
     * Checks if this status indicates the request is completed
     * @return true if request is completed
     */
    public boolean isCompleted() {
        return this == COMPLETED;
    }
    
    /**
     * Checks if this status indicates the request is cancelled
     * @return true if request is cancelled
     */
    public boolean isCancelled() {
        return this == CANCELLED;
    }
    
    /**
     * Gets the next possible statuses from current status
     * @return Array of possible next statuses
     */
    public RequestStatus[] getNextPossibleStatuses() {
        switch (this) {
            case PENDING:
                return new RequestStatus[]{ASSIGNED, CANCELLED};
            case ASSIGNED:
                return new RequestStatus[]{IN_PROGRESS, CANCELLED};
            case IN_PROGRESS:
                return new RequestStatus[]{COMPLETED, CANCELLED};
            case COMPLETED:
            case CANCELLED:
                return new RequestStatus[0]; // No transitions from final states
            default:
                return new RequestStatus[0];
        }
    }
    
    /**
     * Checks if transition to another status is valid
     * @param newStatus The status to transition to
     * @return true if transition is valid
     */
    public boolean canTransitionTo(RequestStatus newStatus) {
        if (newStatus == null) return false;
        
        RequestStatus[] possibleStatuses = getNextPossibleStatuses();
        for (RequestStatus status : possibleStatuses) {
            if (status == newStatus) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Gets status from string value
     * @param statusString String representation of status
     * @return RequestStatus enum value
     * @throws IllegalArgumentException if status string is invalid
     */
    public static RequestStatus fromString(String statusString) {
        if (statusString == null) {
            throw new IllegalArgumentException("Status string cannot be null");
        }
        
        try {
            return RequestStatus.valueOf(statusString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + statusString);
        }
    }
}