package com.communityhub.exception;

/**
 * Exception thrown when user input validation fails
 * Provides specific field-level error information
 */
public class InvalidInputException extends CommunityHubException {
    
    private String fieldName;
    private String fieldValue;
    
    /**
     * Creates a new InvalidInputException for a specific field
     * @param field The field name that failed validation
     * @param value The invalid value that was provided
     */
    public InvalidInputException(String field, String value) {
        super("Invalid input for field: " + field + " with value: " + value, 
              "INVALID_INPUT", 
              "Please check your " + field + " and try again.");
        this.fieldName = field;
        this.fieldValue = value;
    }
    
    /**
     * Creates a new InvalidInputException with custom message
     * @param field The field name that failed validation
     * @param value The invalid value that was provided
     * @param customMessage Custom user-friendly message
     */
    public InvalidInputException(String field, String value, String customMessage) {
        super("Invalid input for field: " + field + " with value: " + value, 
              "INVALID_INPUT", 
              customMessage);
        this.fieldName = field;
        this.fieldValue = value;
    }
    
    /**
     * Creates a new InvalidInputException for general validation failure
     * @param message The validation error message
     */
    public InvalidInputException(String message) {
        super(message, "INVALID_INPUT", message);
    }
    
    /**
     * Gets the field name that failed validation
     * @return Field name
     */
    public String getFieldName() {
        return fieldName;
    }
    
    /**
     * Gets the invalid field value
     * @return Field value
     */
    public String getFieldValue() {
        return fieldValue;
    }
    
    /**
     * Creates an exception for required field validation
     * @param fieldName Name of the required field
     * @return InvalidInputException for required field
     */
    public static InvalidInputException requiredField(String fieldName) {
        return new InvalidInputException(fieldName, "", 
            fieldName + " is required and cannot be empty.");
    }
    
    /**
     * Creates an exception for email format validation
     * @param email The invalid email address
     * @return InvalidInputException for email format
     */
    public static InvalidInputException invalidEmail(String email) {
        return new InvalidInputException("email", email, 
            "Please enter a valid email address.");
    }
    
    /**
     * Creates an exception for password strength validation
     * @param reason The specific reason password is weak
     * @return InvalidInputException for password strength
     */
    public static InvalidInputException weakPassword(String reason) {
        return new InvalidInputException("password", "[hidden]", 
            "Password is too weak: " + reason);
    }
    
    /**
     * Creates an exception for username validation
     * @param username The invalid username
     * @param reason The reason username is invalid
     * @return InvalidInputException for username
     */
    public static InvalidInputException invalidUsername(String username, String reason) {
        return new InvalidInputException("username", username, 
            "Username is invalid: " + reason);
    }
}