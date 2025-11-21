package com.communityhub.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Feedback model class representing user feedback in the community hub
 * Demonstrates encapsulation and proper data modeling for feedback system
 */
public class Feedback {
    
    private String feedbackId;
    private String userId;
    private String requestId;
    private int rating;
    private String comments;
    private FeedbackType feedbackType;
    private LocalDateTime createdAt;
    private boolean isAnonymous;
    
    /**
     * Default constructor for Feedback
     */
    public Feedback() {
        this.feedbackId = UUID.randomUUID().toString();
        this.feedbackType = FeedbackType.GENERAL;
        this.createdAt = LocalDateTime.now();
        this.rating = 0;
        this.isAnonymous = false;
    }
    
    /**
     * Constructor with basic feedback information
     * @param userId ID of the user providing feedback
     * @param comments Feedback comments
     * @param rating Rating (1-5)
     */
    public Feedback(String userId, String comments, int rating) {
        this();
        this.userId = userId;
        this.comments = comments;
        this.rating = rating;
    }
    
    /**
     * Constructor with all fields (for database loading)
     * @param feedbackId Unique feedback identifier
     * @param userId ID of the user providing feedback
     * @param requestId ID of the related request
     * @param rating Rating (1-5)
     * @param comments Feedback comments
     * @param feedbackType Type of feedback
     * @param createdAt Creation timestamp
     */
    public Feedback(String feedbackId, String userId, String requestId, int rating, 
                   String comments, FeedbackType feedbackType, LocalDateTime createdAt) {
        this.feedbackId = feedbackId;
        this.userId = userId;
        this.requestId = requestId;
        this.rating = rating;
        this.comments = comments;
        this.feedbackType = feedbackType;
        this.createdAt = createdAt;
        this.isAnonymous = false;
    }
    
    // Getters and setters
    public String getFeedbackId() { return feedbackId; }
    public void setFeedbackId(String feedbackId) { this.feedbackId = feedbackId; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = Math.max(1, Math.min(5, rating)); }
    
    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
    
    public FeedbackType getFeedbackType() { return feedbackType; }
    public void setFeedbackType(FeedbackType feedbackType) { 
        this.feedbackType = feedbackType != null ? feedbackType : FeedbackType.GENERAL; 
    }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public boolean isAnonymous() { return isAnonymous; }
    public void setAnonymous(boolean anonymous) { this.isAnonymous = anonymous; }
    
    /**
     * Validates feedback data
     * @return true if feedback data is valid
     */
    public boolean isValid() {
        return feedbackId != null && !feedbackId.isEmpty() &&
               userId != null && !userId.isEmpty() &&
               feedbackType != null &&
               ((rating > 0 && rating <= 5) || (comments != null && !comments.trim().isEmpty()));
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Feedback feedback = (Feedback) obj;
        return Objects.equals(feedbackId, feedback.feedbackId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(feedbackId);
    }
    
    @Override
    public String toString() {
        return String.format("Feedback{feedbackId='%s', userId='%s', rating=%d, type=%s}", 
                           feedbackId, userId, rating, feedbackType);
    }
}