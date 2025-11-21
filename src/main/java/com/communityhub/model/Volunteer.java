package com.communityhub.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Volunteer user class extending User
 * Demonstrates inheritance and polymorphism with volunteer-specific functionality
 */
public class Volunteer extends User {
    
    private List<String> assignedRequestIds;
    private List<String> specializations;
    private int completedRequests;
    private double averageRating;
    private boolean isAvailable;
    private String contactPhone;
    private String preferredLocation;
    
    /**
     * Default constructor for Volunteer
     */
    public Volunteer() {
        super();
        this.assignedRequestIds = new ArrayList<>();
        this.specializations = new ArrayList<>();
        this.isAvailable = true;
        this.averageRating = 0.0;
    }
    
    /**
     * Constructor with basic volunteer information
     * @param username Volunteer's username
     * @param email Volunteer's email address
     * @param passwordHash Hashed password
     */
    public Volunteer(String username, String email, String passwordHash) {
        super(username, email, passwordHash, UserRole.VOLUNTEER);
        this.assignedRequestIds = new ArrayList<>();
        this.specializations = new ArrayList<>();
        this.isAvailable = true;
        this.averageRating = 0.0;
    }
    
    /**
     * Constructor with all fields (for database loading)
     * @param userId Unique user identifier
     * @param username Volunteer's username
     * @param email Volunteer's email address
     * @param passwordHash Hashed password
     * @param createdAt Creation timestamp
     * @param updatedAt Last update timestamp
     */
    public Volunteer(String userId, String username, String email, String passwordHash, 
                    LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(userId, username, email, passwordHash, UserRole.VOLUNTEER, createdAt, updatedAt);
        this.assignedRequestIds = new ArrayList<>();
        this.specializations = new ArrayList<>();
        this.isAvailable = true;
        this.averageRating = 0.0;
    }
    
    @Override
    public void performRoleSpecificAction() {
        // Volunteer-specific request handling actions
        System.out.println("Volunteer " + getUsername() + " is managing help requests and coordinating assistance");
        // This could trigger volunteer dashboard updates, request notifications, etc.
    }
    
    @Override
    public String getDashboardView() {
        return "/fxml/volunteer-dashboard.fxml";
    }
    
    @Override
    public String[] getRoleSpecificMenuItems() {
        return new String[]{
            "My Requests",
            "Available Requests",
            "Request History",
            "My Profile",
            "Availability Settings"
        };
    }
    
    @Override
    public boolean canPerformAction(String action) {
        switch (action.toUpperCase()) {
            case "VIEW_REQUESTS":
            case "ACCEPT_REQUEST":
            case "UPDATE_REQUEST_STATUS":
            case "COMMUNICATE_WITH_REQUESTER":
            case "SUBMIT_COMPLETION_REPORT":
                return true;
            case "CREATE_RESOURCE":
            case "DELETE_RESOURCE":
            case "MANAGE_USERS":
                return false;
            default:
                return false;
        }
    }
    
    /**
     * Assigns a request to this volunteer
     * @param requestId ID of the request to assign
     */
    public void assignRequest(String requestId) {
        if (requestId != null && !requestId.trim().isEmpty() && !assignedRequestIds.contains(requestId)) {
            assignedRequestIds.add(requestId);
            updateTimestamp();
        }
    }
    
    /**
     * Removes a request assignment from this volunteer
     * @param requestId ID of the request to remove
     */
    public void removeRequestAssignment(String requestId) {
        if (requestId != null) {
            assignedRequestIds.remove(requestId);
            updateTimestamp();
        }
    }
    
    /**
     * Marks a request as completed
     * @param requestId ID of the completed request
     */
    public void completeRequest(String requestId) {
        if (assignedRequestIds.contains(requestId)) {
            assignedRequestIds.remove(requestId);
            completedRequests++;
            updateTimestamp();
        }
    }
    
    /**
     * Gets the list of assigned request IDs
     * @return List of assigned request IDs
     */
    public List<String> getAssignedRequestIds() {
        return new ArrayList<>(assignedRequestIds);
    }
    
    /**
     * Sets the assigned request IDs
     * @param assignedRequestIds List of request IDs
     */
    public void setAssignedRequestIds(List<String> assignedRequestIds) {
        this.assignedRequestIds = assignedRequestIds != null ? new ArrayList<>(assignedRequestIds) : new ArrayList<>();
        updateTimestamp();
    }
    
    /**
     * Gets the number of currently assigned requests
     * @return Number of assigned requests
     */
    public int getAssignedRequestCount() {
        return assignedRequestIds.size();
    }
    
    /**
     * Adds a specialization to the volunteer
     * @param specialization Specialization to add
     */
    public void addSpecialization(String specialization) {
        if (specialization != null && !specialization.trim().isEmpty() && !specializations.contains(specialization)) {
            specializations.add(specialization.trim());
            updateTimestamp();
        }
    }
    
    /**
     * Removes a specialization from the volunteer
     * @param specialization Specialization to remove
     */
    public void removeSpecialization(String specialization) {
        if (specialization != null) {
            specializations.remove(specialization.trim());
            updateTimestamp();
        }
    }
    
    /**
     * Gets the list of specializations
     * @return List of specializations
     */
    public List<String> getSpecializations() {
        return new ArrayList<>(specializations);
    }
    
    /**
     * Sets the specializations
     * @param specializations List of specializations
     */
    public void setSpecializations(List<String> specializations) {
        this.specializations = specializations != null ? new ArrayList<>(specializations) : new ArrayList<>();
        updateTimestamp();
    }
    
    /**
     * Checks if volunteer has a specific specialization
     * @param specialization Specialization to check
     * @return true if volunteer has the specialization
     */
    public boolean hasSpecialization(String specialization) {
        return specialization != null && specializations.contains(specialization.trim());
    }
    
    /**
     * Gets the number of completed requests
     * @return Number of completed requests
     */
    public int getCompletedRequests() {
        return completedRequests;
    }
    
    /**
     * Sets the number of completed requests
     * @param completedRequests Number of completed requests
     */
    public void setCompletedRequests(int completedRequests) {
        this.completedRequests = Math.max(0, completedRequests);
        updateTimestamp();
    }
    
    /**
     * Gets the average rating
     * @return Average rating
     */
    public double getAverageRating() {
        return averageRating;
    }
    
    /**
     * Sets the average rating
     * @param averageRating Average rating (0.0 to 5.0)
     */
    public void setAverageRating(double averageRating) {
        this.averageRating = Math.max(0.0, Math.min(5.0, averageRating));
        updateTimestamp();
    }
    
    /**
     * Checks if volunteer is available
     * @return true if volunteer is available
     */
    public boolean isAvailable() {
        return isAvailable;
    }
    
    /**
     * Sets volunteer availability
     * @param available Availability status
     */
    public void setAvailable(boolean available) {
        this.isAvailable = available;
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
     * Gets preferred location
     * @return Preferred location
     */
    public String getPreferredLocation() {
        return preferredLocation;
    }
    
    /**
     * Sets preferred location
     * @param preferredLocation Preferred location
     */
    public void setPreferredLocation(String preferredLocation) {
        this.preferredLocation = preferredLocation;
        updateTimestamp();
    }
    
    /**
     * Gets volunteer statistics summary
     * @return Summary string of volunteer activities
     */
    public String getVolunteerSummary() {
        return String.format("Volunteer %s: %d active requests, %d completed, %.1f rating, %s", 
                           getUsername(), assignedRequestIds.size(), completedRequests, 
                           averageRating, isAvailable ? "Available" : "Unavailable");
    }
    
    @Override
    public String toString() {
        return String.format("Volunteer{userId='%s', username='%s', email='%s', assignedRequests=%d, completedRequests=%d, rating=%.1f}", 
                           getUserId(), getUsername(), getEmail(), assignedRequestIds.size(), completedRequests, averageRating);
    }
}