package com.communityhub.service;

import com.communityhub.dao.FeedbackDAO;
import com.communityhub.exception.DatabaseException;
import com.communityhub.model.Feedback;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Service class for feedback management
 * Handles feedback submission, retrieval, and rating aggregation
 * 
 * MULTITHREADING NOTE:
 * - FeedbackService uses synchronized methods for thread-safe feedback operations
 * - Multiple threads may submit feedback concurrently; synchronization ensures
 *   data consistency when accessing shared FeedbackDAO instance
 * - Rating aggregation queries are thread-safe due to database-level locking
 */
public class FeedbackService {
    
    private static final Logger logger = Logger.getLogger(FeedbackService.class.getName());
    private final FeedbackDAO feedbackDAO;
    
    /**
     * Constructor initializes the feedback service
     * @throws DatabaseException if DAO initialization fails
     */
    public FeedbackService() throws DatabaseException {
        this.feedbackDAO = new FeedbackDAO();
    }
    
    /**
     * Submits new feedback
     * Thread-safe: synchronized to prevent concurrent modification issues
     * 
     * @param feedback Feedback object to submit
     * @throws DatabaseException if database operation fails
     */
    public synchronized void submitFeedback(Feedback feedback) throws DatabaseException {
        try {
            feedbackDAO.create(feedback);
            logger.info("Feedback submitted successfully by user: " + feedback.getUserId());
        } catch (DatabaseException e) {
            logger.log(Level.SEVERE, "Error submitting feedback", e);
            throw e;
        }
    }
    
    /**
     * Retrieves all feedback (admin only)
     * 
     * @return List of all feedback entries
     * @throws DatabaseException if database operation fails
     */
    public List<Feedback> getAllFeedback() throws DatabaseException {
        try {
            List<Feedback> feedbackList = feedbackDAO.findAll();
            logger.info("Retrieved " + feedbackList.size() + " feedback entries");
            return feedbackList;
        } catch (DatabaseException e) {
            logger.log(Level.SEVERE, "Error retrieving all feedback", e);
            throw e;
        }
    }
    
    /**
     * Retrieves feedback for a specific request
     * 
     * @param requestId Request ID
     * @return List of feedback for the request
     * @throws DatabaseException if database operation fails
     */
    public List<Feedback> getFeedbackByRequest(String requestId) throws DatabaseException {
        try {
            List<Feedback> feedbackList = feedbackDAO.findByRequestId(requestId);
            logger.info("Retrieved " + feedbackList.size() + " feedback entries for request: " + requestId);
            return feedbackList;
        } catch (DatabaseException e) {
            logger.log(Level.SEVERE, "Error retrieving feedback for request", e);
            throw e;
        }
    }
    
    /**
     * Retrieves feedback from a specific user
     * 
     * @param userId User ID
     * @return List of feedback from the user
     * @throws DatabaseException if database operation fails
     */
    public List<Feedback> getFeedbackByUser(String userId) throws DatabaseException {
        try {
            List<Feedback> feedbackList = feedbackDAO.findByUserId(userId);
            logger.info("Retrieved " + feedbackList.size() + " feedback entries from user: " + userId);
            return feedbackList;
        } catch (DatabaseException e) {
            logger.log(Level.SEVERE, "Error retrieving feedback by user", e);
            throw e;
        }
    }
    
    /**
     * Calculates average rating for a request
     * 
     * @param requestId Request ID
     * @return Average rating (0.0 if no ratings)
     * @throws DatabaseException if database operation fails
     */
    public double getAverageRating(String requestId) throws DatabaseException {
        try {
            List<Feedback> feedbackList = feedbackDAO.findByRequestId(requestId);
            
            if (feedbackList.isEmpty()) {
                return 0.0;
            }
            
            double sum = 0;
            int count = 0;
            
            for (Feedback feedback : feedbackList) {
                if (feedback.getRating() > 0) {
                    sum += feedback.getRating();
                    count++;
                }
            }
            
            double average = count > 0 ? sum / count : 0.0;
            logger.fine("Average rating for request " + requestId + ": " + average);
            return average;
            
        } catch (DatabaseException e) {
            logger.log(Level.SEVERE, "Error calculating average rating", e);
            throw e;
        }
    }
    
    /**
     * Gets rating distribution for a request
     * 
     * @param requestId Request ID
     * @return Array where index is rating (1-5) and value is count
     * @throws DatabaseException if database operation fails
     */
    public int[] getRatingDistribution(String requestId) throws DatabaseException {
        try {
            List<Feedback> feedbackList = feedbackDAO.findByRequestId(requestId);
            int[] distribution = new int[6]; // Index 0 unused, 1-5 for ratings
            
            for (Feedback feedback : feedbackList) {
                if (feedback.getRating() > 0 && feedback.getRating() <= 5) {
                    distribution[feedback.getRating()]++;
                }
            }
            
            logger.fine("Rating distribution calculated for request: " + requestId);
            return distribution;
            
        } catch (DatabaseException e) {
            logger.log(Level.SEVERE, "Error calculating rating distribution", e);
            throw e;
        }
    }
    
    /**
     * Deletes feedback (admin only)
     * 
     * @param feedbackId Feedback ID to delete
     * @throws DatabaseException if database operation fails
     */
    public synchronized void deleteFeedback(String feedbackId) throws DatabaseException {
        try {
            feedbackDAO.delete(feedbackId);
            logger.info("Feedback deleted: " + feedbackId);
        } catch (DatabaseException e) {
            logger.log(Level.SEVERE, "Error deleting feedback", e);
            throw e;
        }
    }
}
