package com.communityhub.model;

/**
 * Enumeration for feedback types in the Community Resource Hub
 * Defines the different categories of feedback users can provide
 */
public enum FeedbackType {
    GENERAL("General", "General feedback about the system or service"),
    REQUEST_SPECIFIC("Request Specific", "Feedback related to a specific help request"),
    SYSTEM_IMPROVEMENT("System Improvement", "Suggestions for improving the system");
    
    private final String displayName;
    private final String description;
    
    FeedbackType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    /**
     * Gets the display name for the feedback type
     * @return Display name
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Gets the description of the feedback type
     * @return Feedback type description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Checks if this feedback type requires a request ID
     * @return true if request ID is required
     */
    public boolean requiresRequestId() {
        return this == REQUEST_SPECIFIC;
    }
    
    /**
     * Gets feedback type from string value
     * @param typeString String representation of feedback type
     * @return FeedbackType enum value
     * @throws IllegalArgumentException if type string is invalid
     */
    public static FeedbackType fromString(String typeString) {
        if (typeString == null) {
            throw new IllegalArgumentException("Feedback type string cannot be null");
        }
        
        try {
            return FeedbackType.valueOf(typeString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid feedback type: " + typeString);
        }
    }
}