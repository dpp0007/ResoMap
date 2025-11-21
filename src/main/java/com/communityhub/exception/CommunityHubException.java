package com.communityhub.exception;

/**
 * Base exception class for Community Hub application
 * Provides error codes and user-friendly messages for all application exceptions
 */
public class CommunityHubException extends Exception {
    
    protected String errorCode;
    protected String userMessage;
    
    /**
     * Creates a new CommunityHubException
     * @param message Technical error message for logging
     * @param errorCode Unique error code for identification
     * @param userMessage User-friendly message for display
     */
    public CommunityHubException(String message, String errorCode, String userMessage) {
        super(message);
        this.errorCode = errorCode;
        this.userMessage = userMessage;
    }
    
    /**
     * Creates a new CommunityHubException with cause
     * @param message Technical error message for logging
     * @param errorCode Unique error code for identification
     * @param userMessage User-friendly message for display
     * @param cause The underlying cause of this exception
     */
    public CommunityHubException(String message, String errorCode, String userMessage, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.userMessage = userMessage;
    }
    
    /**
     * Gets the error code
     * @return Error code string
     */
    public String getErrorCode() {
        return errorCode;
    }
    
    /**
     * Gets the user-friendly message
     * @return User message string
     */
    public String getUserMessage() {
        return userMessage;
    }
    
    /**
     * Sets the user-friendly message
     * @param userMessage New user message
     */
    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }
    
    @Override
    public String toString() {
        return String.format("CommunityHubException[errorCode=%s, userMessage=%s, message=%s]", 
                           errorCode, userMessage, getMessage());
    }
}