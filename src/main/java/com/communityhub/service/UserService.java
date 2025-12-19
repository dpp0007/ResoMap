package com.communityhub.service;

import com.communityhub.dao.UserDAO;
import com.communityhub.exception.DatabaseException;
import com.communityhub.model.User;
import com.communityhub.model.UserRole;

import java.util.List;
import java.util.logging.Logger;

/**
 * Service class for user management operations
 */
public class UserService {
    
    private static final Logger logger = Logger.getLogger(UserService.class.getName());
    private final UserDAO userDAO;
    
    public UserService() throws DatabaseException {
        this.userDAO = new UserDAO();
    }
    
    /**
     * Gets all users
     * @return List of all users
     * @throws DatabaseException if database operation fails
     */
    public List<User> getAllUsers() throws DatabaseException {
        return userDAO.findAll();
    }
    
    /**
     * Gets a user by ID
     * @param userId User ID
     * @return User if found, null otherwise
     * @throws DatabaseException if database operation fails
     */
    public User getUser(String userId) throws DatabaseException {
        return userDAO.read(userId);
    }
    
    /**
     * Gets a user by username
     * @param username Username
     * @return User if found, null otherwise
     * @throws DatabaseException if database operation fails
     */
    public User getUserByUsername(String username) throws DatabaseException {
        return userDAO.findByUsername(username);
    }
    
    /**
     * Gets a user by email
     * @param email Email address
     * @return User if found, null otherwise
     * @throws DatabaseException if database operation fails
     */
    public User getUserByEmail(String email) throws DatabaseException {
        return userDAO.findByEmail(email);
    }
    
    /**
     * Updates an existing user
     * @param user User to update
     * @throws DatabaseException if database operation fails
     */
    public void updateUser(User user) throws DatabaseException {
        userDAO.update(user);
        logger.info("User updated: " + user.getUsername());
    }
    
    /**
     * Deletes a user
     * @param userId User ID to delete
     * @throws DatabaseException if database operation fails
     */
    public void deleteUser(String userId) throws DatabaseException {
        userDAO.delete(userId);
        logger.info("User deleted: " + userId);
    }
    
    /**
     * Gets users by role
     * @param role User role
     * @return List of users with the specified role
     * @throws DatabaseException if database operation fails
     */
    public List<User> getUsersByRole(UserRole role) throws DatabaseException {
        return userDAO.findByRole(role);
    }
    
    /**
     * Gets count of volunteers
     * @return Count of volunteer users
     * @throws DatabaseException if database operation fails
     */
    public long getVolunteerCount() throws DatabaseException {
        List<User> volunteers = getUsersByRole(UserRole.VOLUNTEER);
        return volunteers.size();
    }
    
    /**
     * Gets count of requesters
     * @return Count of requester users
     * @throws DatabaseException if database operation fails
     */
    public long getRequesterCount() throws DatabaseException {
        List<User> requesters = getUsersByRole(UserRole.REQUESTER);
        return requesters.size();
    }
    
    /**
     * Gets count of admins
     * @return Count of admin users
     * @throws DatabaseException if database operation fails
     */
    public long getAdminCount() throws DatabaseException {
        List<User> admins = getUsersByRole(UserRole.ADMIN);
        return admins.size();
    }
    
    /**
     * Gets the total count of users
     * @return Total user count
     * @throws DatabaseException if database operation fails
     */
    public long getUserCount() throws DatabaseException {
        return userDAO.count();
    }
    
    /**
     * Admin action: Deactivate a user
     * @param userId User ID to deactivate
     * @throws DatabaseException if database operation fails
     */
    public void deactivateUser(String userId) throws DatabaseException {
        User user = userDAO.read(userId);
        if (user != null) {
            user.setActive(false);
            userDAO.update(user);
            logger.info("User deactivated by admin: " + userId);
        }
    }
    
    /**
     * Admin action: Activate a user
     * @param userId User ID to activate
     * @throws DatabaseException if database operation fails
     */
    public void activateUser(String userId) throws DatabaseException {
        User user = userDAO.read(userId);
        if (user != null) {
            user.setActive(true);
            userDAO.update(user);
            logger.info("User activated by admin: " + userId);
        }
    }
    
    /**
     * Admin action: Reset user lockout (failed login attempts)
     * @param userId User ID to unlock
     * @throws DatabaseException if database operation fails
     */
    public void resetUserLockout(String userId) throws DatabaseException {
        User user = userDAO.read(userId);
        if (user != null) {
            user.resetFailedLoginAttempts();
            userDAO.update(user);
            logger.info("User lockout reset by admin: " + userId);
        }
    }
    
    /**
     * Admin action: Change user role
     * @param userId User ID
     * @param newRole New role for the user
     * @throws DatabaseException if database operation fails
     */
    public void changeUserRole(String userId, UserRole newRole) throws DatabaseException {
        User user = userDAO.read(userId);
        if (user != null && !user.isAdmin()) { // Prevent changing admin roles
            user.setRole(newRole);
            userDAO.update(user);
            logger.info("User role changed by admin: " + userId + " -> " + newRole);
        }
    }
}