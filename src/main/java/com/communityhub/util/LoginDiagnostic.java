package com.communityhub.util;

import com.communityhub.dao.UserDAO;
import com.communityhub.exception.DatabaseException;
import com.communityhub.model.User;

import java.util.logging.Logger;

/**
 * Diagnostic utility to help troubleshoot login issues
 * 
 * @author ResoMap Team
 * @version 2.0
 */
public class LoginDiagnostic {
    
    private static final Logger logger = Logger.getLogger(LoginDiagnostic.class.getName());
    
    /**
     * Runs diagnostic checks for login system
     */
    public static void runDiagnostics() {
        logger.info("=== LOGIN DIAGNOSTIC START ===");
        
        try {
            UserDAO userDAO = new UserDAO();
            
            // Check if users exist
            logger.info("Checking for test users...");
            
            String[] testUsers = {"admin", "volunteer1", "user1"};
            for (String username : testUsers) {
                User user = userDAO.findByUsername(username);
                if (user != null) {
                    logger.info("✓ User found: " + username + " (Role: " + user.getRole() + ")");
                    logger.info("  Password hash format: " + (user.getPasswordHash().contains(":") ? "SALTED" : "PLAIN"));
                    logger.info("  Password hash length: " + user.getPasswordHash().length());
                } else {
                    logger.warning("✗ User NOT found: " + username);
                }
            }
            
            // Check total user count
            long userCount = userDAO.count();
            logger.info("Total users in database: " + userCount);
            
        } catch (DatabaseException e) {
            logger.severe("Database error during diagnostics: " + e.getMessage());
        }
        
        logger.info("=== LOGIN DIAGNOSTIC END ===");
    }
}
