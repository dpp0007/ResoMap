package com.communityhub.dao;

import com.communityhub.exception.DatabaseException;
import com.communityhub.model.Feedback;
import com.communityhub.model.FeedbackType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Feedback entities
 * Demonstrates concrete DAO implementation with feedback-specific operations
 */
public class FeedbackDAO extends BaseDAO<Feedback> {
    
    public FeedbackDAO() throws DatabaseException {
        super();
    }
    
    @Override
    protected String getTableName() {
        return "feedback";
    }
    
    @Override
    protected String getPrimaryKeyColumn() {
        return "feedback_id";
    }
    
    @Override
    protected String getInsertSQL() {
        return "INSERT INTO feedback (feedback_id, user_id, request_id, rating, comments, feedback_type, created_at) " +
               "VALUES (?, ?, ?, ?, ?, ?, ?)";
    }
    
    @Override
    protected String getUpdateSQL() {
        return "UPDATE feedback SET user_id = ?, request_id = ?, rating = ?, comments = ?, feedback_type = ? " +
               "WHERE feedback_id = ?";
    }
    
    @Override
    protected void setInsertParameters(PreparedStatement stmt, Feedback feedback) throws SQLException {
        stmt.setString(1, feedback.getFeedbackId());
        stmt.setString(2, feedback.getUserId());
        stmt.setString(3, feedback.getRequestId());
        stmt.setInt(4, feedback.getRating());
        stmt.setString(5, feedback.getComments());
        stmt.setString(6, feedback.getFeedbackType().toString());
        stmt.setTimestamp(7, Timestamp.valueOf(feedback.getCreatedAt()));
    }
    
    @Override
    protected void setUpdateParameters(PreparedStatement stmt, Feedback feedback) throws SQLException {
        stmt.setString(1, feedback.getUserId());
        stmt.setString(2, feedback.getRequestId());
        stmt.setInt(3, feedback.getRating());
        stmt.setString(4, feedback.getComments());
        stmt.setString(5, feedback.getFeedbackType().toString());
        stmt.setString(6, feedback.getFeedbackId());
    }
    
    @Override
    protected Feedback mapResultSetToEntity(ResultSet rs) throws SQLException {
        String feedbackId = rs.getString("feedback_id");
        String userId = rs.getString("user_id");
        String requestId = rs.getString("request_id");
        int rating = rs.getInt("rating");
        String comments = rs.getString("comments");
        FeedbackType feedbackType = FeedbackType.fromString(rs.getString("feedback_type"));
        LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
        
        return new Feedback(feedbackId, userId, requestId, rating, comments, feedbackType, createdAt);
    }
    
    @Override
    public void create(Feedback feedback) throws DatabaseException {
        validateEntity(feedback, "create");
        
        if (!feedback.isValid()) {
            throw new DatabaseException("Cannot create feedback with invalid data");
        }
        
        executeInTransaction(() -> {
            PreparedStatement stmt = null;
            try {
                stmt = connection.prepareStatement(getInsertSQL());
                setInsertParameters(stmt, feedback);
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("Creating feedback failed, no rows affected");
                }
                
                logger.info("Feedback created successfully: " + feedback.getFeedbackId());
                
            } catch (SQLException e) {
                throw new DatabaseException("Failed to create feedback", "create feedback", e);
            } finally {
                closeStatement(stmt);
            }
        });
    }
    
    @Override
    public Feedback read(String feedbackId) throws DatabaseException {
        validateId(feedbackId, "read");
        
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            stmt = connection.prepareStatement(getSelectByIdSQL());
            stmt.setString(1, feedbackId);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToEntity(rs);
            }
            return null;
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to read feedback", "read feedback", e);
        } finally {
            closeResources(rs, stmt);
        }
    }
    
    @Override
    public void update(Feedback feedback) throws DatabaseException {
        validateEntity(feedback, "update");
        validateId(feedback.getFeedbackId(), "update");
        
        if (!feedback.isValid()) {
            throw new DatabaseException("Cannot update feedback with invalid data");
        }
        
        executeInTransaction(() -> {
            PreparedStatement stmt = null;
            try {
                stmt = connection.prepareStatement(getUpdateSQL());
                setUpdateParameters(stmt, feedback);
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("Feedback not found for update: " + feedback.getFeedbackId());
                }
                
                logger.info("Feedback updated successfully: " + feedback.getFeedbackId());
                
            } catch (SQLException e) {
                throw new DatabaseException("Failed to update feedback", "update feedback", e);
            } finally {
                closeStatement(stmt);
            }
        });
    }
    
    @Override
    public void delete(String feedbackId) throws DatabaseException {
        validateId(feedbackId, "delete");
        
        executeInTransaction(() -> {
            PreparedStatement stmt = null;
            try {
                stmt = connection.prepareStatement(getDeleteSQL());
                stmt.setString(1, feedbackId);
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("Feedback not found for deletion: " + feedbackId);
                }
                
                logger.info("Feedback deleted successfully: " + feedbackId);
                
            } catch (SQLException e) {
                throw new DatabaseException("Failed to delete feedback", "delete feedback", e);
            } finally {
                closeStatement(stmt);
            }
        });
    }
    
    @Override
    public List<Feedback> findAll() throws DatabaseException {
        List<Feedback> feedbacks = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            stmt = connection.prepareStatement(getSelectAllSQL());
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                feedbacks.add(mapResultSetToEntity(rs));
            }
            
            return feedbacks;
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve all feedback", "find all feedback", e);
        } finally {
            closeResources(rs, stmt);
        }
    }
    
    @Override
    public boolean exists(String feedbackId) throws DatabaseException {
        validateId(feedbackId, "check existence");
        
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            stmt = connection.prepareStatement(getExistsSQL());
            stmt.setString(1, feedbackId);
            rs = stmt.executeQuery();
            
            return rs.next();
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to check feedback existence", "check feedback existence", e);
        } finally {
            closeResources(rs, stmt);
        }
    }
    
    @Override
    public long count() throws DatabaseException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            stmt = connection.prepareStatement(getCountSQL());
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count feedback", "count feedback", e);
        } finally {
            closeResources(rs, stmt);
        }
    }
    
    @Override
    public List<Feedback> findByField(String fieldName, Object value) throws DatabaseException {
        List<Feedback> feedbacks = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            String sql = "SELECT * FROM " + getTableName() + " WHERE " + fieldName + " = ?";
            stmt = connection.prepareStatement(sql);
            stmt.setObject(1, value);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                feedbacks.add(mapResultSetToEntity(rs));
            }
            
            return feedbacks;
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find feedback by " + fieldName, "find feedback", e);
        } finally {
            closeResources(rs, stmt);
        }
    }
    
    /**
     * Finds feedback by user ID
     * @param userId ID of the user
     * @return List of feedback from the user
     * @throws DatabaseException if search fails
     */
    public List<Feedback> findByUserId(String userId) throws DatabaseException {
        if (userId == null || userId.trim().isEmpty()) {
            throw new DatabaseException("User ID cannot be null or empty");
        }
        
        return findByField("user_id", userId);
    }
    
    /**
     * Finds feedback by request ID
     * @param requestId ID of the request
     * @return List of feedback for the request
     * @throws DatabaseException if search fails
     */
    public List<Feedback> findByRequestId(String requestId) throws DatabaseException {
        if (requestId == null || requestId.trim().isEmpty()) {
            throw new DatabaseException("Request ID cannot be null or empty");
        }
        
        return findByField("request_id", requestId);
    }
    
    /**
     * Finds feedback by type
     * @param feedbackType Type of feedback
     * @return List of feedback of the specified type
     * @throws DatabaseException if search fails
     */
    public List<Feedback> findByType(FeedbackType feedbackType) throws DatabaseException {
        if (feedbackType == null) {
            throw new DatabaseException("Feedback type cannot be null");
        }
        
        return findByField("feedback_type", feedbackType.toString());
    }
    
    /**
     * Finds feedback by rating
     * @param rating Rating value (1-5)
     * @return List of feedback with the specified rating
     * @throws DatabaseException if search fails
     */
    public List<Feedback> findByRating(int rating) throws DatabaseException {
        if (rating < 1 || rating > 5) {
            throw new DatabaseException("Rating must be between 1 and 5");
        }
        
        return findByField("rating", rating);
    }
    
    /**
     * Finds positive feedback (rating 4 or 5)
     * @return List of positive feedback
     * @throws DatabaseException if search fails
     */
    public List<Feedback> findPositiveFeedback() throws DatabaseException {
        List<Feedback> positiveFeedback = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            String sql = "SELECT * FROM " + getTableName() + " WHERE rating >= 4 ORDER BY created_at DESC";
            stmt = connection.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                positiveFeedback.add(mapResultSetToEntity(rs));
            }
            
            return positiveFeedback;
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find positive feedback", "find positive feedback", e);
        } finally {
            closeResources(rs, stmt);
        }
    }
    
    /**
     * Finds negative feedback (rating 1 or 2)
     * @return List of negative feedback
     * @throws DatabaseException if search fails
     */
    public List<Feedback> findNegativeFeedback() throws DatabaseException {
        List<Feedback> negativeFeedback = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            String sql = "SELECT * FROM " + getTableName() + " WHERE rating <= 2 ORDER BY created_at DESC";
            stmt = connection.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                negativeFeedback.add(mapResultSetToEntity(rs));
            }
            
            return negativeFeedback;
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find negative feedback", "find negative feedback", e);
        } finally {
            closeResources(rs, stmt);
        }
    }
    
    /**
     * Calculates average rating for all feedback
     * @return Average rating
     * @throws DatabaseException if calculation fails
     */
    public double getAverageRating() throws DatabaseException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            String sql = "SELECT AVG(CAST(rating AS REAL)) FROM " + getTableName() + " WHERE rating > 0";
            stmt = connection.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble(1);
            }
            return 0.0;
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to calculate average rating", "calculate average rating", e);
        } finally {
            closeResources(rs, stmt);
        }
    }
    
    /**
     * Gets recent feedback (within specified days)
     * @param days Number of days to look back
     * @return List of recent feedback
     * @throws DatabaseException if search fails
     */
    public List<Feedback> getRecentFeedback(int days) throws DatabaseException {
        List<Feedback> recentFeedback = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            LocalDateTime cutoffTime = LocalDateTime.now().minusDays(days);
            String sql = "SELECT * FROM " + getTableName() + " WHERE created_at >= ? ORDER BY created_at DESC";
            stmt = connection.prepareStatement(sql);
            stmt.setTimestamp(1, Timestamp.valueOf(cutoffTime));
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                recentFeedback.add(mapResultSetToEntity(rs));
            }
            
            return recentFeedback;
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find recent feedback", "find recent feedback", e);
        } finally {
            closeResources(rs, stmt);
        }
    }
}