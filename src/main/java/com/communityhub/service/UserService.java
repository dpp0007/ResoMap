package com.communityhub.service;

import com.communityhub.dao.UserDAO;
import com.communityhub.exception.DatabaseException;
import com.communityhub.exception.InvalidInputException;
import com.communityhub.model.User;
import com.communityhub.model.UserRole;
import com.communityhub.util.ValidationUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Service class for user management operations
 * Handles all user-related business logic
 */
public class UserService {
    
    private static final Logger logger = Logger.getLogger(UserService.class.getName());
    private final UserDAO userDAO;
    
    /**
     * Constructor initializes the user service
     * @throws DatabaseException if DAO initialization fails
     */
    public UserService() throws DatabaseException {
        this.userDAO = new UserDAO();
    }
    
    /**
     * Gets all users (admin only)
     * @return List of all users
     * @throws DatabaseException if database operation fails
     */
    public List<User> getAllUsers() throws DatabaseException {
        List<User> users = userDAO.findAll();
        logger.log(Level.INFO, "Retrieved {0} users", users.size());
        return users;
    }
    
    /**
     * Gets user by ID
     * @param userId User ID
     * @return User if found, null otherwise
     * @throws DatabaseException if database operation fails
     */
    public User getUserById(String userId) throws DatabaseException {
        try {
            ValidationUtils.validateRequired(userId, "user ID");
        } catch (InvalidInputException e) {
            throw new DatabaseException("Invalid user ID: " + e.getMessage());
        }
        
        return userDAO.read(userId);
    }
    
    /**
     * Gets users by role
     * @param role User role
     * @return List of users with the specified role
     * @throws DatabaseException if database operation fails
     */
    public List<User> getUsersByRole(UserRole role) throws DatabaseException {
        try {
            ValidationUtils.validateNotNull(role, "role");
        } catch (InvalidInputException e) {
            throw new DatabaseException("Invalid role: " + e.getMessage());
        }
        
        return userDAO.findByRole(role);
    }
    
    /**
     * Updates user profile
     * @param user User to update
     * @throws DatabaseException if database operation fails
     * @throws InvalidInputException if validation fails
     */
    public void updateUser(User user) throws DatabaseException, InvalidInputException {
        ValidationUtils.validateNotNull(user, "user");
        ValidationUtils.validateRequired(user.getUserId(), "user ID");
        ValidationUtils.validateUsername(user.getUsername());
        ValidationUtils.validateEmail(user.getEmail());
        
        // Check if user exists
        User existingUser = userDAO.read(user.getUserId());
        if (existingUser == null) {
            throw new DatabaseException("User not found: " + user.getUserId());
        }
        
        // Check if username is taken by another user
        User userWithSameUsername = userDAO.findByUsername(user.getUsername());
        if (userWithSameUsername != null && !userWithSameUsername.getUserId().equals(user.getUserId())) {
            throw InvalidInputException.invalidUsername(user.getUsername(), "username already taken");
        }
        
        // Check if email is taken by another user
        User userWithSameEmail = userDAO.findByEmail(user.getEmail());
        if (userWithSameEmail != null && !userWithSameEmail.getUserId().equals(user.getUserId())) {
            throw InvalidInputException.invalidEmail(user.getEmail());
        }
        
        userDAO.update(user);
        logger.log(Level.INFO, "User updated: {0}", user.getUserId());
    }
    
    /**
     * Deletes user (admin only)
     * @param userId User ID to delete
     * @throws DatabaseException if database operation fails
     */
    public void deleteUser(String userId) throws DatabaseException {
        try {
            ValidationUtils.validateRequired(userId, "user ID");
        } catch (InvalidInputException e) {
            throw new DatabaseException("Invalid user ID: " + e.getMessage());
        }
        
        // Check if user exists
        User user = userDAO.read(userId);
        if (user == null) {
            throw new DatabaseException("User not found: " + userId);
        }
        
        userDAO.delete(userId);
        logger.log(Level.INFO, "User deleted: {0}", userId);
    }
    
    /**
     * Searches users by username or email
     * @param query Search query
     * @return List of matching users
     * @throws DatabaseException if database operation fails
     */
    public List<User> searchUsers(String query) throws DatabaseException {
        try {
            ValidationUtils.validateRequired(query, "search query");
        } catch (InvalidInputException e) {
            throw new DatabaseException("Invalid search query: " + e.getMessage());
        }
        
        // Search through all users (DAO search method not yet implemented)
        String lowerQuery = query.toLowerCase();
        return userDAO.findAll().stream()
            .filter(user -> user.getUsername().toLowerCase().contains(lowerQuery) ||
                           user.getEmail().toLowerCase().contains(lowerQuery))
            .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Gets user statistics
     * @return Map with user statistics
     * @throws DatabaseException if database operation fails
     */
    public Map<String, Object> getUserStatistics() throws DatabaseException {
        List<User> allUsers = userDAO.findAll();
        
        Map<UserRole, Long> roleCounts = allUsers.stream()
            .collect(Collectors.groupingBy(User::getRole, Collectors.counting()));
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", allUsers.size());
        stats.put("adminCount", roleCounts.getOrDefault(UserRole.ADMIN, 0L));
        stats.put("volunteerCount", roleCounts.getOrDefault(UserRole.VOLUNTEER, 0L));
        stats.put("requesterCount", roleCounts.getOrDefault(UserRole.REQUESTER, 0L));
        stats.put("roleBreakdown", roleCounts);
        
        return stats;
    }
    
    /**
     * Gets active users count
     * @return Number of active users
     * @throws DatabaseException if database operation fails
     */
    public long getActiveUsersCount() throws DatabaseException {
        return userDAO.findAll().size();
    }
    
    /**
     * Checks if username exists
     * @param username Username to check
     * @return true if username exists
     * @throws DatabaseException if database operation fails
     */
    public boolean usernameExists(String username) throws DatabaseException {
        return userDAO.findByUsername(username) != null;
    }
    
    /**
     * Checks if email exists
     * @param email Email to check
     * @return true if email exists
     * @throws DatabaseException if database operation fails
     */
    public boolean emailExists(String email) throws DatabaseException {
        return userDAO.findByEmail(email) != null;
    }
}
