package com.communityhub.service;

import com.communityhub.dao.ActivityDAO;
import com.communityhub.dto.ActivityDTO;
import com.communityhub.exception.DatabaseException;
import com.communityhub.model.User;
import com.communityhub.model.UserRole;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Service class for activity feed operations
 * Provides business logic for retrieving and processing activity data
 */
public class ActivityService {
    
    private static final Logger logger = Logger.getLogger(ActivityService.class.getName());
    private final ActivityDAO activityDAO;
    private static final int DEFAULT_LIMIT = 10;
    private static final int MAX_LIMIT = 50;
    
    public ActivityService() throws DatabaseException {
        this.activityDAO = new ActivityDAO();
    }
    
    /**
     * Gets recent activity for a user based on their role
     * @param user Current user
     * @param limit Maximum number of activities to return (capped at MAX_LIMIT)
     * @return List of recent activities
     */
    public List<ActivityDTO> getRecentActivity(User user, int limit) {
        // Validate and cap limit
        if (limit <= 0) {
            limit = DEFAULT_LIMIT;
        }
        if (limit > MAX_LIMIT) {
            limit = MAX_LIMIT;
        }
        
        try {
            if (user == null || user.getUserId() == null) {
                logger.warning("Invalid user provided to getRecentActivity");
                return new ArrayList<>();
            }
            
            List<ActivityDTO> activities = activityDAO.getRecentActivityForUser(
                user.getUserId(),
                user.getRole(),
                limit
            );
            
            logger.info("Retrieved " + activities.size() + " activities for user: " + user.getUsername());
            return activities;
            
        } catch (DatabaseException e) {
            logger.log(Level.WARNING, "Failed to retrieve recent activity for user: " + user.getUsername(), e);
            // Return empty list instead of crashing
            return new ArrayList<>();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error retrieving recent activity", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Gets recent activity with default limit
     * @param user Current user
     * @return List of recent activities (default limit)
     */
    public List<ActivityDTO> getRecentActivity(User user) {
        return getRecentActivity(user, DEFAULT_LIMIT);
    }
    
    /**
     * Formats activity timestamp for display
     * @param activity Activity to format
     * @return Formatted timestamp string
     */
    public String formatActivityTimestamp(ActivityDTO activity) {
        if (activity == null || activity.getTimestamp() == null) {
            return "Unknown";
        }
        
        return activity.getTimestamp()
            .format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"));
    }
    
    /**
     * Gets activity type badge for UI display
     * @param activity Activity to get badge for
     * @return Badge text
     */
    public String getActivityBadge(ActivityDTO activity) {
        if (activity == null || activity.getType() == null) {
            return "Activity";
        }
        
        switch (activity.getType()) {
            case "REQUEST_CREATED":
                return "📋 New Request";
            case "REQUEST_ASSIGNED":
                return "👤 Assigned";
            case "REQUEST_IN_PROGRESS":
                return "⚡ In Progress";
            case "REQUEST_COMPLETED":
                return "✅ Completed";
            case "REQUEST_CANCELLED":
                return "❌ Cancelled";
            case "RESOURCE_CREATED":
                return "📦 New Resource";
            case "FEEDBACK_SUBMITTED":
                return "⭐ Feedback";
            default:
                return "📌 Update";
        }
    }
}
