package com.communityhub.util;

import com.communityhub.exception.AuthenticationException;
import com.communityhub.model.User;
import com.communityhub.model.UserRole;

/**
 * Role-based access control utility
 * Provides authorization checks for different user roles
 */
public class RoleBasedAccessControl {
    
    /**
     * Requires admin role
     * @param user User to check
     * @throws AuthenticationException if user is not admin
     */
    public static void requireAdmin(User user) throws AuthenticationException {
        if (user == null) {
            throw new AuthenticationException("User not authenticated");
        }
        if (user.getRole() != UserRole.ADMIN) {
            throw new AuthenticationException("Admin access required. Current role: " + user.getRole());
        }
    }
    
    /**
     * Requires volunteer role
     * @param user User to check
     * @throws AuthenticationException if user is not volunteer
     */
    public static void requireVolunteer(User user) throws AuthenticationException {
        if (user == null) {
            throw new AuthenticationException("User not authenticated");
        }
        if (user.getRole() != UserRole.VOLUNTEER) {
            throw new AuthenticationException("Volunteer access required. Current role: " + user.getRole());
        }
    }
    
    /**
     * Requires requester role
     * @param user User to check
     * @throws AuthenticationException if user is not requester
     */
    public static void requireRequester(User user) throws AuthenticationException {
        if (user == null) {
            throw new AuthenticationException("User not authenticated");
        }
        if (user.getRole() != UserRole.REQUESTER) {
            throw new AuthenticationException("Requester access required. Current role: " + user.getRole());
        }
    }
    
    /**
     * Requires any authenticated user
     * @param user User to check
     * @throws AuthenticationException if user is not authenticated
     */
    public static void requireAuthenticated(User user) throws AuthenticationException {
        if (user == null) {
            throw new AuthenticationException("Authentication required");
        }
    }
    
    /**
     * Checks if user owns resource
     * @param user User to check
     * @param ownerId Owner ID of the resource
     * @throws AuthenticationException if user doesn't own resource and is not admin
     */
    public static void requireOwnership(User user, String ownerId) throws AuthenticationException {
        if (user == null) {
            throw new AuthenticationException("User not authenticated");
        }
        if (!user.getUserId().equals(ownerId) && user.getRole() != UserRole.ADMIN) {
            throw new AuthenticationException("Access denied: not resource owner");
        }
    }
    
    /**
     * Checks if user has admin or volunteer role
     * @param user User to check
     * @throws AuthenticationException if user is neither admin nor volunteer
     */
    public static void requireAdminOrVolunteer(User user) throws AuthenticationException {
        if (user == null) {
            throw new AuthenticationException("User not authenticated");
        }
        if (user.getRole() != UserRole.ADMIN && user.getRole() != UserRole.VOLUNTEER) {
            throw new AuthenticationException("Admin or Volunteer access required. Current role: " + user.getRole());
        }
    }
    
    /**
     * Checks if user has admin or requester role
     * @param user User to check
     * @throws AuthenticationException if user is neither admin nor requester
     */
    public static void requireAdminOrRequester(User user) throws AuthenticationException {
        if (user == null) {
            throw new AuthenticationException("User not authenticated");
        }
        if (user.getRole() != UserRole.ADMIN && user.getRole() != UserRole.REQUESTER) {
            throw new AuthenticationException("Admin or Requester access required. Current role: " + user.getRole());
        }
    }
    
    /**
     * Checks if user is admin
     * @param user User to check
     * @return true if user is admin
     */
    public static boolean isAdmin(User user) {
        return user != null && user.getRole() == UserRole.ADMIN;
    }
    
    /**
     * Checks if user is volunteer
     * @param user User to check
     * @return true if user is volunteer
     */
    public static boolean isVolunteer(User user) {
        return user != null && user.getRole() == UserRole.VOLUNTEER;
    }
    
    /**
     * Checks if user is requester
     * @param user User to check
     * @return true if user is requester
     */
    public static boolean isRequester(User user) {
        return user != null && user.getRole() == UserRole.REQUESTER;
    }
    
    /**
     * Checks if user owns resource or is admin
     * @param user User to check
     * @param ownerId Owner ID of the resource
     * @return true if user owns resource or is admin
     */
    public static boolean canAccess(User user, String ownerId) {
        if (user == null) {
            return false;
        }
        return user.getUserId().equals(ownerId) || user.getRole() == UserRole.ADMIN;
    }
}
