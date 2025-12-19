package com.communityhub.service;

import com.communityhub.dao.UserDAO;
import com.communityhub.exception.AuthenticationException;
import com.communityhub.exception.DatabaseException;
import com.communityhub.exception.InvalidInputException;
import com.communityhub.model.User;
import com.communityhub.model.UserRole;
import com.communityhub.model.Admin;
import com.communityhub.model.Volunteer;
import com.communityhub.model.Requester;
import com.communityhub.util.SessionManager;
import com.communityhub.util.ValidationUtils;
import com.communityhub.util.PasswordUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Service class for user authentication and session management
 * Demonstrates service layer pattern with security features and collections usage
 * 
 * MULTITHREADING IMPLEMENTATION (Review-1 Requirement):
 * - REASON: Multiple concurrent users may attempt login simultaneously
 * - SYNCHRONIZATION: login() method is synchronized to prevent race conditions
 *   when checking/updating login attempts and activeUsers map
 * - THREAD-SAFE COLLECTIONS: ConcurrentHashMap used for activeUsers, loginAttempts,
 *   and lastLoginAttempt to handle concurrent access without explicit locking
 * - CRITICAL SECTION: Account lockout check and login attempt recording must be atomic
 *   to prevent multiple threads from bypassing lockout protection
 */
public class AuthenticationService {
    
    private static final Logger logger = Logger.getLogger(AuthenticationService.class.getName());
    
    private final UserDAO userDAO;
    private final SessionManager sessionManager;
    
    // Thread-safe collections for session management
    // ConcurrentHashMap allows multiple threads to read/write without blocking
    private final Map<String, User> activeUsers;
    private final Map<String, Integer> loginAttempts;
    private final Map<String, Long> lastLoginAttempt;
    
    // Security configuration
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final long LOCKOUT_DURATION_MS = 15 * 60 * 1000; // 15 minutes
    
    /**
     * Constructor initializes the authentication service
     * @throws DatabaseException if DAO initialization fails
     */
    public AuthenticationService() throws DatabaseException {
        this.userDAO = new UserDAO();
        this.sessionManager = SessionManager.getInstance();
        this.activeUsers = new ConcurrentHashMap<>();
        this.loginAttempts = new ConcurrentHashMap<>();
        this.lastLoginAttempt = new ConcurrentHashMap<>();
    }
    
    /**
     * Authenticates a user with username and password
     * @param username User's username
     * @param password User's plain text password
     * @return Authenticated user
     * @throws AuthenticationException if authentication fails
     * @throws InvalidInputException if input validation fails
     * @throws DatabaseException if database operation fails
     */
    public synchronized User login(String username, String password) throws AuthenticationException, InvalidInputException, DatabaseException {
        // Validate input
        ValidationUtils.validateRequired(username, "username");
        ValidationUtils.validateRequired(password, "password");
        
        // Check if account is locked
        if (isAccountLocked(username)) {
            throw AuthenticationException.accountLocked(username);
        }
        
        try {
            // Find user by username
            User user = userDAO.findByUsername(username);
            if (user == null) {
                recordFailedLogin(username);
                throw AuthenticationException.invalidLogin(username);
            }
            
            // Verify password using PasswordUtils
            logger.info("Verifying password for user: " + username);
            logger.fine("Stored hash: " + user.getPasswordHash());
            
            if (!PasswordUtils.verifyPassword(password, user.getPasswordHash())) {
                recordFailedLogin(username);
                logger.warning("Password verification failed for user: " + username);
                throw AuthenticationException.invalidLogin(username);
            }
            
            // Successful login
            clearLoginAttempts(username);
            sessionManager.login(user);
            activeUsers.put(user.getUserId(), user);
            
            logger.info("User logged in successfully: " + username + " (Role: " + user.getRole() + ")");
            return user;
            
        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error during login", e);
            throw new AuthenticationException("Login failed due to system error");
        }
    }
    
    /**
     * Logs out the current user
     * @throws AuthenticationException if no user is logged in
     */
    public synchronized void logout() throws AuthenticationException {
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser == null) {
            throw new AuthenticationException("No user is currently logged in");
        }
        
        activeUsers.remove(currentUser.getUserId());
        sessionManager.logout();
        
        logger.info("User logged out: " + currentUser.getUsername());
    }
    
    /**
     * Registers a new user
     * @param username User's username
     * @param email User's email
     * @param password User's plain text password
     * @param confirmPassword Password confirmation
     * @param role User's role
     * @return Created user
     * @throws InvalidInputException if input validation fails
     * @throws DatabaseException if database operation fails
     */
    public User register(String username, String email, String password, String confirmPassword, UserRole role) 
            throws InvalidInputException, DatabaseException {
        
        // Validate input
        ValidationUtils.validateUsername(username);
        ValidationUtils.validateEmail(email);
        ValidationUtils.validatePassword(password);
        ValidationUtils.validatePasswordMatch(password, confirmPassword);
        ValidationUtils.validateNotNull(role, "role");
        
        // Check if username or email already exists
        if (userDAO.findByUsername(username) != null) {
            throw InvalidInputException.invalidUsername(username, "username already exists");
        }
        
        if (userDAO.findByEmail(email) != null) {
            throw InvalidInputException.invalidEmail(email);
        }
        
        // Hash password using PasswordUtils
        String hashedPassword = PasswordUtils.hashPassword(password);
        
        // Create user based on role (polymorphism)
        User newUser;
        switch (role) {
            case ADMIN:
                newUser = new Admin(username, email, hashedPassword);
                break;
            case VOLUNTEER:
                newUser = new Volunteer(username, email, hashedPassword);
                break;
            case REQUESTER:
                newUser = new Requester(username, email, hashedPassword);
                break;
            default:
                throw new InvalidInputException("Invalid role: " + role);
        }
        
        // Save to database
        userDAO.create(newUser);
        
        logger.info("User registered successfully: " + username + " (Role: " + role + ")");
        return newUser;
    }
    
    /**
     * Changes a user's password
     * @param userId User's ID
     * @param currentPassword Current password
     * @param newPassword New password
     * @param confirmPassword New password confirmation
     * @throws AuthenticationException if current password is incorrect
     * @throws InvalidInputException if input validation fails
     * @throws DatabaseException if database operation fails
     */
    public void changePassword(String userId, String currentPassword, String newPassword, String confirmPassword) 
            throws AuthenticationException, InvalidInputException, DatabaseException {
        
        ValidationUtils.validateRequired(userId, "user ID");
        ValidationUtils.validateRequired(currentPassword, "current password");
        ValidationUtils.validatePassword(newPassword);
        ValidationUtils.validatePasswordMatch(newPassword, confirmPassword);
        
        // Get user
        User user = userDAO.read(userId);
        if (user == null) {
            throw new AuthenticationException("User not found");
        }
        
        // Verify current password using PasswordUtils
        if (!PasswordUtils.verifyPassword(currentPassword, user.getPasswordHash())) {
            throw new AuthenticationException("Current password is incorrect");
        }
        
        // Update password using PasswordUtils
        String hashedNewPassword = PasswordUtils.hashPassword(newPassword);
        user.setPasswordHash(hashedNewPassword);
        userDAO.update(user);
        
        logger.info("Password changed for user: " + user.getUsername());
    }
    
    /**
     * Gets the currently logged-in user
     * @return Current user or null if not logged in
     */
    public User getCurrentUser() {
        return sessionManager.getCurrentUser();
    }
    
    /**
     * Checks if a user is currently logged in
     * @return true if user is logged in
     */
    public boolean isLoggedIn() {
        return sessionManager.isLoggedIn();
    }
    
    /**
     * Checks if the current user has admin privileges
     * @return true if current user is admin
     */
    public boolean isCurrentUserAdmin() {
        return sessionManager.isCurrentUserAdmin();
    }
    
    /**
     * Checks if the current user is a volunteer
     * @return true if current user is volunteer
     */
    public boolean isCurrentUserVolunteer() {
        return sessionManager.isCurrentUserVolunteer();
    }
    
    /**
     * Validates user session and refreshes if valid
     * @return true if session is valid
     */
    public boolean validateSession() {
        if (sessionManager.isSessionValid()) {
            sessionManager.refreshSession();
            return true;
        }
        return false;
    }
    
    /**
     * Gets all active user sessions
     * @return Map of active sessions (for admin use)
     */
    public Map<String, User> getActiveSessions() {
        User currentUser = getCurrentUser();
        if (currentUser == null || !currentUser.isAdmin()) {
            return new ConcurrentHashMap<>(); // Return empty map for non-admins
        }
        return new ConcurrentHashMap<>(activeUsers);
    }
    
    /**
     * Records a failed login attempt
     * @param username Username that failed login
     */
    private void recordFailedLogin(String username) {
        int attempts = loginAttempts.getOrDefault(username, 0) + 1;
        loginAttempts.put(username, attempts);
        lastLoginAttempt.put(username, System.currentTimeMillis());
        
        logger.warning("Failed login attempt for user: " + username + " (Attempt " + attempts + ")");
    }
    
    /**
     * Clears login attempts for a user
     * @param username Username to clear attempts for
     */
    private void clearLoginAttempts(String username) {
        loginAttempts.remove(username);
        lastLoginAttempt.remove(username);
    }
    
    /**
     * Checks if an account is locked due to too many failed attempts
     * @param username Username to check
     * @return true if account is locked
     */
    private boolean isAccountLocked(String username) {
        int attempts = loginAttempts.getOrDefault(username, 0);
        if (attempts < MAX_LOGIN_ATTEMPTS) {
            return false;
        }
        
        Long lastAttempt = lastLoginAttempt.get(username);
        if (lastAttempt == null) {
            return false;
        }
        
        long timeSinceLastAttempt = System.currentTimeMillis() - lastAttempt;
        if (timeSinceLastAttempt > LOCKOUT_DURATION_MS) {
            clearLoginAttempts(username);
            return false;
        }
        
        return true;
    }
    
}