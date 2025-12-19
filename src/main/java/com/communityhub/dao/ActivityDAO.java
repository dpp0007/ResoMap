package com.communityhub.dao;

import com.communityhub.dto.ActivityDTO;
import com.communityhub.exception.DatabaseException;
import com.communityhub.model.UserRole;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * DAO for retrieving activity feed data from existing tables
 * Derives activity from requests, resources, and feedback tables
 */
public class ActivityDAO {
    
    private static final Logger logger = Logger.getLogger(ActivityDAO.class.getName());
    private java.sql.Connection connection;
    
    public ActivityDAO() throws DatabaseException {
        this.connection = com.communityhub.util.DBConnection.getInstance().getConnection();
    }
    
    /**
     * Gets recent activity for a user based on their role
     * @param userId User ID
     * @param userRole User's role (ADMIN, VOLUNTEER, REQUESTER)
     * @param limit Maximum number of activities to return
     * @return List of recent activities
     * @throws DatabaseException if database operation fails
     */
    public List<ActivityDTO> getRecentActivityForUser(String userId, UserRole userRole, int limit) throws DatabaseException {
        List<ActivityDTO> activities = new ArrayList<>();
        
        try {
            if (userRole == UserRole.ADMIN) {
                activities.addAll(getAdminActivity(limit));
            } else if (userRole == UserRole.VOLUNTEER) {
                activities.addAll(getVolunteerActivity(userId, limit));
            } else if (userRole == UserRole.REQUESTER) {
                activities.addAll(getRequesterActivity(userId, limit));
            }
            
            // Sort by timestamp descending (most recent first)
            activities.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
            
            // Limit results
            if (activities.size() > limit) {
                activities = activities.subList(0, limit);
            }
            
            logger.info("Retrieved " + activities.size() + " activities for user: " + userId);
            return activities;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error retrieving activity", e);
            throw new DatabaseException("Failed to retrieve activity feed", e);
        }
    }
    
    /**
     * Gets all activities for admin (global view)
     */
    private List<ActivityDTO> getAdminActivity(int limit) throws SQLException {
        List<ActivityDTO> activities = new ArrayList<>();
        
        // Get recent requests (created or updated)
        String requestSQL = "SELECT r.request_id, r.requester_id, r.resource_id, r.status, r.created_at, " +
                           "u.username, u.role, res.name " +
                           "FROM requests r " +
                           "JOIN users u ON r.requester_id = u.user_id " +
                           "JOIN resources res ON r.resource_id = res.resource_id " +
                           "ORDER BY r.created_at DESC LIMIT ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(requestSQL)) {
            stmt.setInt(1, limit * 2); // Get more to filter
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String requestId = rs.getString("request_id");
                String requesterId = rs.getString("requester_id");
                String status = rs.getString("status");
                LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
                String requesterName = rs.getString("username");
                String resourceName = rs.getString("name");
                
                String message = "Request created for " + resourceName;
                String type = "REQUEST_CREATED";
                
                ActivityDTO activity = new ActivityDTO(
                    requestId,
                    type,
                    message,
                    createdAt,
                    requesterName,
                    "REQUESTER",
                    resourceName,
                    requestId
                );
                activities.add(activity);
            }
            rs.close();
        }
        
        // Get recent resources created
        String resourceSQL = "SELECT resource_id, name, created_at, created_by, u.username " +
                            "FROM resources r " +
                            "JOIN users u ON r.created_by = u.user_id " +
                            "ORDER BY r.created_at DESC LIMIT ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(resourceSQL)) {
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String resourceId = rs.getString("resource_id");
                String resourceName = rs.getString("name");
                LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
                String createdBy = rs.getString("username");
                
                String message = "New resource added: " + resourceName;
                String type = "RESOURCE_CREATED";
                
                ActivityDTO activity = new ActivityDTO(
                    resourceId,
                    type,
                    message,
                    createdAt,
                    createdBy,
                    "ADMIN",
                    resourceName,
                    null
                );
                activities.add(activity);
            }
            rs.close();
        }
        
        return activities;
    }
    
    /**
     * Gets activities for a volunteer (assigned requests)
     */
    private List<ActivityDTO> getVolunteerActivity(String volunteerId, int limit) throws SQLException {
        List<ActivityDTO> activities = new ArrayList<>();
        
        // Get requests assigned to this volunteer
        String sql = "SELECT r.request_id, r.requester_id, r.resource_id, r.status, r.created_at, r.updated_at, " +
                    "u.username, res.name, req_user.username as requester_name " +
                    "FROM requests r " +
                    "JOIN users u ON r.volunteer_id = u.user_id " +
                    "JOIN resources res ON r.resource_id = res.resource_id " +
                    "JOIN users req_user ON r.requester_id = req_user.user_id " +
                    "WHERE r.volunteer_id = ? " +
                    "ORDER BY r.updated_at DESC LIMIT ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, volunteerId);
            stmt.setInt(2, limit);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String requestId = rs.getString("request_id");
                String status = rs.getString("status");
                LocalDateTime updatedAt = rs.getTimestamp("updated_at").toLocalDateTime();
                String requesterName = rs.getString("requester_name");
                String resourceName = rs.getString("name");
                
                String message = "";
                String type = "";
                
                switch (status) {
                    case "ASSIGNED":
                        message = "You were assigned to request for " + resourceName;
                        type = "REQUEST_ASSIGNED";
                        break;
                    case "IN_PROGRESS":
                        message = "You started work on " + resourceName + " request";
                        type = "REQUEST_IN_PROGRESS";
                        break;
                    case "COMPLETED":
                        message = "You completed request for " + resourceName;
                        type = "REQUEST_COMPLETED";
                        break;
                    default:
                        message = "Request status: " + status;
                        type = "REQUEST_UPDATED";
                }
                
                ActivityDTO activity = new ActivityDTO(
                    requestId,
                    type,
                    message,
                    updatedAt,
                    requesterName,
                    "REQUESTER",
                    resourceName,
                    requestId
                );
                activities.add(activity);
            }
            rs.close();
        }
        
        return activities;
    }
    
    /**
     * Gets activities for a requester (their own requests and feedback)
     */
    private List<ActivityDTO> getRequesterActivity(String requesterId, int limit) throws SQLException {
        List<ActivityDTO> activities = new ArrayList<>();
        
        // Get requester's requests
        String requestSQL = "SELECT r.request_id, r.status, r.created_at, r.updated_at, " +
                           "res.name, vol.username as volunteer_name " +
                           "FROM requests r " +
                           "JOIN resources res ON r.resource_id = res.resource_id " +
                           "LEFT JOIN users vol ON r.volunteer_id = vol.user_id " +
                           "WHERE r.requester_id = ? " +
                           "ORDER BY r.updated_at DESC LIMIT ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(requestSQL)) {
            stmt.setString(1, requesterId);
            stmt.setInt(2, limit);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String requestId = rs.getString("request_id");
                String status = rs.getString("status");
                LocalDateTime updatedAt = rs.getTimestamp("updated_at").toLocalDateTime();
                String resourceName = rs.getString("name");
                String volunteerName = rs.getString("volunteer_name");
                
                String message = "";
                String type = "";
                
                switch (status) {
                    case "PENDING":
                        message = "Your request for " + resourceName + " is pending";
                        type = "REQUEST_CREATED";
                        break;
                    case "ASSIGNED":
                        message = "Volunteer assigned to your " + resourceName + " request";
                        type = "REQUEST_ASSIGNED";
                        break;
                    case "IN_PROGRESS":
                        message = "Your " + resourceName + " request is in progress";
                        type = "REQUEST_IN_PROGRESS";
                        break;
                    case "COMPLETED":
                        message = "Your " + resourceName + " request is completed";
                        type = "REQUEST_COMPLETED";
                        break;
                    case "CANCELLED":
                        message = "Your " + resourceName + " request was cancelled";
                        type = "REQUEST_CANCELLED";
                        break;
                    default:
                        message = "Request status: " + status;
                        type = "REQUEST_UPDATED";
                }
                
                ActivityDTO activity = new ActivityDTO(
                    requestId,
                    type,
                    message,
                    updatedAt,
                    volunteerName != null ? volunteerName : "System",
                    "SYSTEM",
                    resourceName,
                    requestId
                );
                activities.add(activity);
            }
            rs.close();
        }
        
        // Get feedback submitted by requester
        String feedbackSQL = "SELECT feedback_id, rating, comments, created_at, r.request_id " +
                            "FROM feedback f " +
                            "LEFT JOIN requests r ON f.request_id = r.request_id " +
                            "WHERE f.user_id = ? " +
                            "ORDER BY f.created_at DESC LIMIT ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(feedbackSQL)) {
            stmt.setString(1, requesterId);
            stmt.setInt(2, limit);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String feedbackId = rs.getString("feedback_id");
                int rating = rs.getInt("rating");
                LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
                String requestId = rs.getString("request_id");
                
                String message = "You submitted feedback with " + rating + " star rating";
                String type = "FEEDBACK_SUBMITTED";
                
                ActivityDTO activity = new ActivityDTO(
                    feedbackId,
                    type,
                    message,
                    createdAt,
                    "You",
                    "REQUESTER",
                    null,
                    requestId
                );
                activities.add(activity);
            }
            rs.close();
        }
        
        return activities;
    }
}
