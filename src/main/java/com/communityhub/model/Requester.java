package com.communityhub.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Requester user class extending User
 * Demonstrates inheritance and polymorphism with requester-specific functionality
 */
public class Requester extends User {
    
    private List<String> submittedRequestIds;
    private List<String> favoriteResourceIds;
    private String contactPhone;
    private String address;
    private String emergencyContact;
    private int totalRequestsSubmitted;
    private int completedRequests;
    private boolean needsAssistance;
    
    /**
     * Default constructor for Requester
     */
    public Requester() {
        super();
        this.submittedRequestIds = new ArrayList<>();
        this.favoriteResourceIds = new ArrayList<>();
        this.needsAssistance = false;
    }
    
    /**
     * Constructor with basic requester information
     * @param username Requester's username
     * @param email Requester's email address
     * @param passwordHash Hashed password
     */
    public Requester(String username, String email, String passwordHash) {
        super(username, email, passwordHash, UserRole.REQUESTER);
        this.submittedRequestIds = new ArrayList<>();
        this.favoriteResourceIds = new ArrayList<>();
        this.needsAssistance = false;
    }
    
    /**
     * Constructor with all fields (for database loading)
     * @param userId Unique user identifier
     * @param username Requester's username
     * @param email Requester's email address
     * @param passwordHash Hashed password
     * @param createdAt Creation timestamp
     * @param updatedAt Last update timestamp
     */
    public Requester(String userId, String username, String email, String passwordHash, 
                    LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(userId, username, email, passwordHash, UserRole.REQUESTER, createdAt, updatedAt);
        this.submittedRequestIds = new ArrayList<>();
        this.favoriteResourceIds = new ArrayList<>();
        this.needsAssistance = false;
    }
    
    @Override
    public void performRoleSpecificAction() {
        // Requester-specific resource browsing and request submission actions
        System.out.println("Requester " + getUsername() + " is browsing resources and managing help requests");
        // This could trigger resource search, request submission, etc.
    }
    
    @Override
    public String getDashboardView() {
        return "/fxml/requester-dashboard.fxml";
    }
    
    @Override
    public String[] getRoleSpecificMenuItems() {
        return new String[]{
            "Browse Resources",
            "My Requests",
            "Submit Request",
            "Favorites",
            "My Profile",
            "Submit Feedback"
        };
    }
    
    @Override
    public boolean canPerformAction(String action) {
        switch (action.toUpperCase()) {
            case "VIEW_RESOURCES":
            case "SUBMIT_REQUEST":
            case "VIEW_OWN_REQUESTS":
            case "SUBMIT_FEEDBACK":
            case "UPDATE_PROFILE":
                return true;
            case "CREATE_RESOURCE":
            case "DELETE_RESOURCE":
            case "MANAGE_USERS":
            case "VIEW_ALL_REQUESTS":
                return false;
            default:
                return false;
        }
    }
    
    /**
     * Submits a new request
     * @param requestId ID of the submitted request
     */
    public void submitRequest(String requestId) {
        if (requestId != null && !requestId.trim().isEmpty() && !submittedRequestIds.contains(requestId)) {
            submittedRequestIds.add(requestId);
            totalRequestsSubmitted++;
            updateTimestamp();
        }
    }
    
    /**
     * Removes a submitted request
     * @param requestId ID of the request to remove
     */
    public void removeSubmittedRequest(String requestId) {
        if (requestId != null) {
            submittedRequestIds.remove(requestId);
            updateTimestamp();
        }
    }
    
    /**
     * Marks a request as completed
     * @param requestId ID of the completed request
     */
    public void markRequestCompleted(String requestId) {
        if (submittedRequestIds.contains(requestId)) {
            submittedRequestIds.remove(requestId);
            completedRequests++;
            updateTimestamp();
        }
    }
    
    /**
     * Gets the list of submitted request IDs
     * @return List of submitted request IDs
     */
    public List<String> getSubmittedRequestIds() {
        return new ArrayList<>(submittedRequestIds);
    }
    
    /**
     * Sets the submitted request IDs
     * @param submittedRequestIds List of request IDs
     */
    public void setSubmittedRequestIds(List<String> submittedRequestIds) {
        this.submittedRequestIds = submittedRequestIds != null ? new ArrayList<>(submittedRequestIds) : new ArrayList<>();
        updateTimestamp();
    }
    
    /**
     * Gets the number of currently active requests
     * @return Number of active requests
     */
    public int getActiveRequestCount() {
        return submittedRequestIds.size();
    }
    
    /**
     * Adds a resource to favorites
     * @param resourceId ID of the resource to favorite
     */
    public void addFavoriteResource(String resourceId) {
        if (resourceId != null && !resourceId.trim().isEmpty() && !favoriteResourceIds.contains(resourceId)) {
            favoriteResourceIds.add(resourceId);
            updateTimestamp();
        }
    }
    
    /**
     * Removes a resource from favorites
     * @param resourceId ID of the resource to remove from favorites
     */
    public void removeFavoriteResource(String resourceId) {
        if (resourceId != null) {
            favoriteResourceIds.remove(resourceId);
            updateTimestamp();
        }
    }
    
    /**
     * Checks if a resource is in favorites
     * @param resourceId ID of the resource to check
     * @return true if resource is in favorites
     */
    public boolean isFavoriteResource(String resourceId) {
        return resourceId != null && favoriteResourceIds.contains(resourceId);
    }
    
    /**
     * Gets the list of favorite resource IDs
     * @return List of favorite resource IDs
     */
    public List<String> getFavoriteResourceIds() {
        return new ArrayList<>(favoriteResourceIds);
    }
    
    /**
     * Sets the favorite resource IDs
     * @param favoriteResourceIds List of favorite resource IDs
     */
    public void setFavoriteResourceIds(List<String> favoriteResourceIds) {
        this.favoriteResourceIds = favoriteResourceIds != null ? new ArrayList<>(favoriteResourceIds) : new ArrayList<>();
        updateTimestamp();
    }
    
    /**
     * Gets contact phone number
     * @return Contact phone number
     */
    public String getContactPhone() {
        return contactPhone;
    }
    
    /**
     * Sets contact phone number
     * @param contactPhone Contact phone number
     */
    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
        updateTimestamp();
    }
    
    /**
     * Gets address
     * @return Address
     */
    public String getAddress() {
        return address;
    }
    
    /**
     * Sets address
     * @param address Address
     */
    public void setAddress(String address) {
        this.address = address;
        updateTimestamp();
    }
    
    /**
     * Gets emergency contact
     * @return Emergency contact
     */
    public String getEmergencyContact() {
        return emergencyContact;
    }
    
    /**
     * Sets emergency contact
     * @param emergencyContact Emergency contact
     */
    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
        updateTimestamp();
    }
    
    /**
     * Gets total number of requests submitted
     * @return Total requests submitted
     */
    public int getTotalRequestsSubmitted() {
        return totalRequestsSubmitted;
    }
    
    /**
     * Sets total number of requests submitted
     * @param totalRequestsSubmitted Total requests submitted
     */
    public void setTotalRequestsSubmitted(int totalRequestsSubmitted) {
        this.totalRequestsSubmitted = Math.max(0, totalRequestsSubmitted);
        updateTimestamp();
    }
    
    /**
     * Gets number of completed requests
     * @return Number of completed requests
     */
    public int getCompletedRequests() {
        return completedRequests;
    }
    
    /**
     * Sets number of completed requests
     * @param completedRequests Number of completed requests
     */
    public void setCompletedRequests(int completedRequests) {
        this.completedRequests = Math.max(0, completedRequests);
        updateTimestamp();
    }
    
    /**
     * Checks if requester needs assistance
     * @return true if needs assistance
     */
    public boolean needsAssistance() {
        return needsAssistance;
    }
    
    /**
     * Sets whether requester needs assistance
     * @param needsAssistance Assistance needed status
     */
    public void setNeedsAssistance(boolean needsAssistance) {
        this.needsAssistance = needsAssistance;
        updateTimestamp();
    }
    
    /**
     * Calculates completion rate for requests
     * @return Completion rate as percentage (0.0 to 100.0)
     */
    public double getCompletionRate() {
        if (totalRequestsSubmitted == 0) {
            return 0.0;
        }
        return (double) completedRequests / totalRequestsSubmitted * 100.0;
    }
    
    /**
     * Gets requester statistics summary
     * @return Summary string of requester activities
     */
    public String getRequesterSummary() {
        return String.format("Requester %s: %d active requests, %d completed (%.1f%% completion rate), %d favorites", 
                           getUsername(), submittedRequestIds.size(), completedRequests, 
                           getCompletionRate(), favoriteResourceIds.size());
    }
    
    @Override
    public String toString() {
        return String.format("Requester{userId='%s', username='%s', email='%s', activeRequests=%d, completedRequests=%d, favorites=%d}", 
                           getUserId(), getUsername(), getEmail(), submittedRequestIds.size(), completedRequests, favoriteResourceIds.size());
    }
}