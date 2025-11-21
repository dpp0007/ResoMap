package com.communityhub.model;

/**
 * Enumeration for urgency levels in the Community Resource Hub
 * Defines the priority levels for requests
 */
public enum UrgencyLevel {
    LOW("Low", "Non-urgent request that can be handled when convenient", 1),
    MEDIUM("Medium", "Standard request with normal priority", 2),
    HIGH("High", "Important request that should be prioritized", 3),
    CRITICAL("Critical", "Urgent request requiring immediate attention", 4);
    
    private final String displayName;
    private final String description;
    private final int priority;
    
    UrgencyLevel(String displayName, String description, int priority) {
        this.displayName = displayName;
        this.description = description;
        this.priority = priority;
    }
    
    /**
     * Gets the display name for the urgency level
     * @return Display name
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Gets the description of the urgency level
     * @return Urgency description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Gets the priority value (higher number = higher priority)
     * @return Priority value
     */
    public int getPriority() {
        return priority;
    }
    
    /**
     * Checks if this urgency level is higher than another
     * @param other Other urgency level to compare
     * @return true if this level is higher priority
     */
    public boolean isHigherThan(UrgencyLevel other) {
        return other != null && this.priority > other.priority;
    }
    
    /**
     * Checks if this urgency level is critical
     * @return true if this is critical urgency
     */
    public boolean isCritical() {
        return this == CRITICAL;
    }
    
    /**
     * Checks if this urgency level requires immediate attention
     * @return true if high or critical urgency
     */
    public boolean requiresImmediateAttention() {
        return this == HIGH || this == CRITICAL;
    }
    
    /**
     * Gets the color code for UI display
     * @return Color code string
     */
    public String getColorCode() {
        switch (this) {
            case LOW:
                return "#4CAF50"; // Green
            case MEDIUM:
                return "#FF9800"; // Orange
            case HIGH:
                return "#F44336"; // Red
            case CRITICAL:
                return "#9C27B0"; // Purple
            default:
                return "#757575"; // Gray
        }
    }
    
    /**
     * Gets urgency level from string value
     * @param urgencyString String representation of urgency
     * @return UrgencyLevel enum value
     * @throws IllegalArgumentException if urgency string is invalid
     */
    public static UrgencyLevel fromString(String urgencyString) {
        if (urgencyString == null) {
            throw new IllegalArgumentException("Urgency string cannot be null");
        }
        
        try {
            return UrgencyLevel.valueOf(urgencyString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid urgency level: " + urgencyString);
        }
    }
    
    /**
     * Gets urgency level from priority value
     * @param priority Priority value (1-4)
     * @return UrgencyLevel enum value
     * @throws IllegalArgumentException if priority is invalid
     */
    public static UrgencyLevel fromPriority(int priority) {
        for (UrgencyLevel level : values()) {
            if (level.priority == priority) {
                return level;
            }
        }
        throw new IllegalArgumentException("Invalid priority value: " + priority);
    }
}