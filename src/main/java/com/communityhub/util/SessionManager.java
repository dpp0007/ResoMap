package com.communityhub.util;

import com.communityhub.model.User;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Singleton class for managing user sessions
 * Provides thread-safe session management with automatic cleanup
 */
public class SessionManager {
    
    private static final Logger logger = Logger.getLogger(SessionManager.class.getName());
    private static SessionManager instance;
    
    private User currentUser;
    private LocalDateTime loginTime;
    private final Map<String, User> activeSessions;
    private static final int SESSION_TIMEOUT_HOURS = 8;
    
    private SessionManager() {
        this.activeSessions = new ConcurrentHashMap<>();
    }
    
    /**
     * Gets the singleton instance of SessionManager
     * @return SessionManager instance
     */
    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    /**
     * Logs in a user and creates a session
     * @param user The user to log in
     */
    public synchronized void login(User user) {
        this.currentUser = user;
        this.loginTime = LocalDateTime.now();
        this.activeSessions.put(user.getUserId(), user);
        
        logger.info("User logged in: " + user.getUsername() + " (Role: " + user.getRole() + ")");
    }
    
    /**
     * Logs out the current user and clears the session
     */
    public synchronized void logout() {
        if (currentUser != null) {
            activeSessions.remove(currentUser.getUserId());
            logger.info("User logged out: " + currentUser.getUsername());
            currentUser = null;
            loginTime = null;
        }
    }
    
    /**
     * Gets the currently logged-in user
     * @return Current user or null if not logged in
     */
    public User getCurrentUser() {
        if (isSessionValid()) {
            return currentUser;
        }
        return null;
    }
    
    /**
     * Checks if the current session is valid
     * @return true if session is valid and not expired
     */
    public boolean isSessionValid() {
        if (currentUser == null || loginTime == null) {
            return false;
        }
        
        LocalDateTime expiryTime = loginTime.plusHours(SESSION_TIMEOUT_HOURS);
        if (LocalDateTime.now().isAfter(expiryTime)) {
            logout();
            return false;
        }
        
        return true;
    }
    
    /**
     * Checks if a user is currently logged in
     * @return true if a user is logged in
     */
    public boolean isLoggedIn() {
        return getCurrentUser() != null;
    }
    
    /**
     * Gets the current user's role
     * @return User role or null if not logged in
     */
    public String getCurrentUserRole() {
        User user = getCurrentUser();
        return user != null ? user.getRole().toString() : null;
    }
    
    /**
     * Checks if the current user has admin privileges
     * @return true if current user is an admin
     */
    public boolean isCurrentUserAdmin() {
        return "ADMIN".equals(getCurrentUserRole());
    }
    
    /**
     * Checks if the current user is a volunteer
     * @return true if current user is a volunteer
     */
    public boolean isCurrentUserVolunteer() {
        return "VOLUNTEER".equals(getCurrentUserRole());
    }
    
    /**
     * Gets all active sessions
     * @return Map of active sessions
     */
    public Map<String, User> getActiveSessions() {
        return new ConcurrentHashMap<>(activeSessions);
    }
    
    /**
     * Clears all sessions
     */
    public synchronized void clearSession() {
        currentUser = null;
        loginTime = null;
        activeSessions.clear();
        logger.info("All sessions cleared");
    }
    
    /**
     * Refreshes the current session timestamp
     */
    public synchronized void refreshSession() {
        if (currentUser != null) {
            this.loginTime = LocalDateTime.now();
        }
    }
    
    /**
     * Gets the login time of the current session
     * @return Login time or null if not logged in
     */
    public LocalDateTime getLoginTime() {
        return loginTime;
    }
}