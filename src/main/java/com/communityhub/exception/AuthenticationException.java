package com.communityhub.exception;

/**
 * Exception thrown when authentication or authorization fails
 * Provides security-related error handling
 */
public class AuthenticationException extends CommunityHubException {
    
    private String username;
    private String attemptedAction;
    
    /**
     * Creates a new AuthenticationException
     * @param message Technical error message for logging
     */
    public AuthenticationException(String message) {
        super(message, "AUTH_FAILED", 
              "Invalid credentials. Please check your username and password.");
    }
    
    /**
     * Creates a new AuthenticationException with username context
     * @param message Technical error message for logging
     * @param username The username that failed authentication
     */
    public AuthenticationException(String message, String username) {
        super(message, "AUTH_FAILED", 
              "Invalid credentials. Please check your username and password.");
        this.username = username;
    }
    
    /**
     * Creates a new AuthenticationException with custom user message
     * @param message Technical error message for logging
     * @param userMessage Custom user-friendly message
     */
    public AuthenticationException(String message, String userMessage, boolean customMessage) {
        super(message, "AUTH_FAILED", userMessage);
    }
    
    /**
     * Gets the username that failed authentication
     * @return Username
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Gets the action that was attempted
     * @return Attempted action
     */
    public String getAttemptedAction() {
        return attemptedAction;
    }
    
    /**
     * Sets the attempted action
     * @param attemptedAction The action that was attempted
     */
    public void setAttemptedAction(String attemptedAction) {
        this.attemptedAction = attemptedAction;
    }
    
    /**
     * Creates an exception for invalid login credentials
     * @param username The username that failed login
     * @return AuthenticationException for invalid login
     */
    public static AuthenticationException invalidLogin(String username) {
        return new AuthenticationException("Invalid login attempt for username: " + username, username);
    }
    
    /**
     * Creates an exception for expired sessions
     * @return AuthenticationException for expired session
     */
    public static AuthenticationException sessionExpired() {
        return new AuthenticationException("User session has expired", 
                                         "Your session has expired. Please log in again.", true);
    }
    
    /**
     * Creates an exception for insufficient privileges
     * @param requiredRole The role required for the action
     * @param userRole The user's current role
     * @return AuthenticationException for insufficient privileges
     */
    public static AuthenticationException insufficientPrivileges(String requiredRole, String userRole) {
        AuthenticationException ex = new AuthenticationException(
            "User with role " + userRole + " attempted action requiring " + requiredRole,
            "You don't have permission to perform this action.", true);
        ex.errorCode = "INSUFFICIENT_PRIVILEGES";
        return ex;
    }
    
    /**
     * Creates an exception for account lockout
     * @param username The locked username
     * @return AuthenticationException for account lockout
     */
    public static AuthenticationException accountLocked(String username) {
        AuthenticationException ex = new AuthenticationException(
            "Account locked for username: " + username,
            "Your account has been temporarily locked. Please contact an administrator.", true);
        ex.username = username;
        ex.errorCode = "ACCOUNT_LOCKED";
        return ex;
    }
    
    /**
     * Creates an exception for password reset required
     * @param username The username requiring password reset
     * @return AuthenticationException for password reset
     */
    public static AuthenticationException passwordResetRequired(String username) {
        AuthenticationException ex = new AuthenticationException(
            "Password reset required for username: " + username,
            "You must reset your password before continuing.", true);
        ex.username = username;
        ex.errorCode = "PASSWORD_RESET_REQUIRED";
        return ex;
    }
}