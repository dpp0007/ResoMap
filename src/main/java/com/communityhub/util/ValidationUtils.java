package com.communityhub.util;

import com.communityhub.exception.InvalidInputException;

import java.util.regex.Pattern;

/**
 * Utility class for input validation
 * Provides comprehensive validation methods for user inputs
 * 
 * DESIGN DECISION: Centralized validation ensures consistent error messages
 * across all servlets and prevents validation logic duplication. This improves
 * maintainability and makes it easier to update validation rules globally.
 */
public class ValidationUtils {
    
    // Centralized validation error messages (constants for consistency)
    private static final String MSG_REQUIRED = "%s is required";
    private static final String MSG_INVALID_EMAIL = "Please enter a valid email address";
    private static final String MSG_INVALID_USERNAME_LENGTH = "Username must be between 3 and 20 characters";
    private static final String MSG_INVALID_USERNAME_FORMAT = "Username can only contain letters, numbers, underscores, and hyphens";
    private static final String MSG_PASSWORD_TOO_SHORT = "Password must be at least %d characters long";
    private static final String MSG_PASSWORD_UPPERCASE = "Password must contain at least one uppercase letter";
    private static final String MSG_PASSWORD_LOWERCASE = "Password must contain at least one lowercase letter";
    private static final String MSG_PASSWORD_DIGIT = "Password must contain at least one number";
    private static final String MSG_PASSWORD_SPECIAL = "Password must contain at least one special character";
    private static final String MSG_PASSWORD_MISMATCH = "Passwords do not match";
    private static final String MSG_INVALID_RANGE = "%s must be between %d and %d";
    private static final String MSG_INVALID_LENGTH = "%s must be between %d and %d characters";
    private static final String MSG_INVALID_PHONE = "Please enter a valid phone number (10-15 digits)";
    private static final String MSG_INVALID_RATING = "Rating must be between 1 and 5";
    
    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    
    // Username validation pattern (alphanumeric, underscore, hyphen, 3-20 chars)
    private static final Pattern USERNAME_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_-]{3,20}$"
    );
    
    // Password strength requirements
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final Pattern PASSWORD_UPPERCASE = Pattern.compile(".*[A-Z].*");
    private static final Pattern PASSWORD_LOWERCASE = Pattern.compile(".*[a-z].*");
    private static final Pattern PASSWORD_DIGIT = Pattern.compile(".*\\d.*");
    private static final Pattern PASSWORD_SPECIAL = Pattern.compile(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");
    
    private ValidationUtils() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Validates that a string is not null or empty
     * WHY: Defensive null checks prevent NullPointerException in downstream code
     * @param value The value to validate
     * @param fieldName The name of the field being validated
     * @throws InvalidInputException if value is null or empty
     */
    public static void validateRequired(String value, String fieldName) throws InvalidInputException {
        // Defensive null check: value could be null from request parameter
        if (value == null || value.trim().isEmpty()) {
            throw InvalidInputException.requiredField(fieldName);
        }
    }
    
    /**
     * Validates email format
     * WHY: Email validation prevents invalid data from entering the system
     * @param email The email address to validate
     * @throws InvalidInputException if email format is invalid
     */
    public static void validateEmail(String email) throws InvalidInputException {
        validateRequired(email, "email");
        
        // Normalize: trim whitespace before validation
        String normalizedEmail = email.trim().toLowerCase();
        
        if (!EMAIL_PATTERN.matcher(normalizedEmail).matches()) {
            throw InvalidInputException.invalidEmail(email);
        }
    }
    
    /**
     * Validates username format and requirements
     * WHY: Username validation ensures consistent user identification and prevents injection attacks
     * @param username The username to validate
     * @throws InvalidInputException if username is invalid
     */
    public static void validateUsername(String username) throws InvalidInputException {
        validateRequired(username, "username");
        
        // Normalize: trim and convert to lowercase for consistency
        String normalized = username.trim().toLowerCase();
        
        if (!USERNAME_PATTERN.matcher(normalized).matches()) {
            if (normalized.length() < 3) {
                throw InvalidInputException.invalidUsername(username, MSG_INVALID_USERNAME_LENGTH);
            } else if (normalized.length() > 20) {
                throw InvalidInputException.invalidUsername(username, MSG_INVALID_USERNAME_LENGTH);
            } else {
                throw InvalidInputException.invalidUsername(username, MSG_INVALID_USERNAME_FORMAT);
            }
        }
    }
    
    /**
     * Validates password strength
     * @param password The password to validate
     * @throws InvalidInputException if password is too weak
     */
    public static void validatePassword(String password) throws InvalidInputException {
        validateRequired(password, "password");
        
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw InvalidInputException.weakPassword("must be at least " + MIN_PASSWORD_LENGTH + " characters long");
        }
        
        if (!PASSWORD_UPPERCASE.matcher(password).matches()) {
            throw InvalidInputException.weakPassword("must contain at least one uppercase letter");
        }
        
        if (!PASSWORD_LOWERCASE.matcher(password).matches()) {
            throw InvalidInputException.weakPassword("must contain at least one lowercase letter");
        }
        
        if (!PASSWORD_DIGIT.matcher(password).matches()) {
            throw InvalidInputException.weakPassword("must contain at least one number");
        }
        
        if (!PASSWORD_SPECIAL.matcher(password).matches()) {
            throw InvalidInputException.weakPassword("must contain at least one special character");
        }
    }
    
    /**
     * Validates that passwords match
     * @param password The original password
     * @param confirmPassword The confirmation password
     * @throws InvalidInputException if passwords don't match
     */
    public static void validatePasswordMatch(String password, String confirmPassword) throws InvalidInputException {
        if (password == null || confirmPassword == null || !password.equals(confirmPassword)) {
            throw new InvalidInputException("password confirmation", "[hidden]", "Passwords do not match");
        }
    }
    
    /**
     * Validates a numeric value within a range
     * @param value The value to validate
     * @param fieldName The name of the field
     * @param min Minimum allowed value
     * @param max Maximum allowed value
     * @throws InvalidInputException if value is out of range
     */
    public static void validateNumericRange(int value, String fieldName, int min, int max) throws InvalidInputException {
        if (value < min || value > max) {
            throw new InvalidInputException(fieldName, String.valueOf(value), 
                fieldName + " must be between " + min + " and " + max);
        }
    }
    
    /**
     * Validates string length
     * @param value The string to validate
     * @param fieldName The name of the field
     * @param minLength Minimum length
     * @param maxLength Maximum length
     * @throws InvalidInputException if length is invalid
     */
    public static void validateStringLength(String value, String fieldName, int minLength, int maxLength) throws InvalidInputException {
        validateRequired(value, fieldName);
        
        int length = value.trim().length();
        if (length < minLength || length > maxLength) {
            throw new InvalidInputException(fieldName, value, 
                fieldName + " must be between " + minLength + " and " + maxLength + " characters long");
        }
    }
    
    /**
     * Validates that a value is not null
     * @param value The value to validate
     * @param fieldName The name of the field
     * @throws InvalidInputException if value is null
     */
    public static void validateNotNull(Object value, String fieldName) throws InvalidInputException {
        if (value == null) {
            throw InvalidInputException.requiredField(fieldName);
        }
    }
    
    /**
     * Validates phone number format (basic validation)
     * @param phoneNumber The phone number to validate
     * @throws InvalidInputException if phone number format is invalid
     */
    public static void validatePhoneNumber(String phoneNumber) throws InvalidInputException {
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            String cleaned = phoneNumber.replaceAll("[\\s\\-\\(\\)\\+]", "");
            if (!cleaned.matches("\\d{10,15}")) {
                throw new InvalidInputException("phone number", phoneNumber, 
                    "Please enter a valid phone number (10-15 digits)");
            }
        }
    }
    
    /**
     * Validates rating value (1-5 scale)
     * @param rating The rating to validate
     * @throws InvalidInputException if rating is out of range
     */
    public static void validateRating(int rating) throws InvalidInputException {
        validateNumericRange(rating, "rating", 1, 5);
    }
    
    /**
     * Sanitizes input string by trimming and removing potentially harmful characters
     * WHY: XSS prevention - escapes HTML characters to prevent script injection
     * @param input The input string to sanitize
     * @return Sanitized string (null-safe)
     */
    public static String sanitizeInput(String input) {
        // Defensive null check: input could be null from request
        if (input == null) {
            return "";  // Return empty string instead of null for safer downstream handling
        }
        
        // Normalize: trim whitespace first
        String trimmed = input.trim();
        
        // Escape HTML characters to prevent XSS attacks
        // Order matters: escape & first to avoid double-escaping
        return trimmed.replaceAll("&", "&amp;")
                      .replaceAll("<", "&lt;")
                      .replaceAll(">", "&gt;")
                      .replaceAll("\"", "&quot;")
                      .replaceAll("'", "&#x27;");
    }
}