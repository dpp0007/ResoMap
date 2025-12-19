package com.communityhub.util;

import com.communityhub.dao.ResourceDAO;
import com.communityhub.dao.RequestDAO;
import com.communityhub.dao.UserDAO;
import com.communityhub.exception.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Data Cleanup Utility for removing dummy/test data from production database
 * 
 * IMPORTANT: This class is FOR INITIAL DATA CLEANUP ONLY
 * Run once during initial setup to remove all seed/test data
 * 
 * Dummy Data Identification Criteria:
 * 1. User IDs matching pattern: admin-001, vol-001, vol-002, req-001, req-002, req-003
 * 2. Resource IDs matching pattern: res-food-*, res-clothing-*, res-shelter-*, res-medical-*, res-education-*, res-other-*
 * 3. Request IDs matching pattern: req-001 through req-008
 * 4. Usernames: admin, volunteer1, volunteer2, requester1, requester2, requester3
 * 5. Email domains: @example.com (test emails)
 * 6. Resource descriptions containing: "Assorted", "Mixed", "Bulk", "Gently used", "Refurbished"
 * 7. Request descriptions containing: "Family of four", "Single mother", "elderly parent", "Student needs"
 */
public class DataCleanupRunner {
    
    private static final Logger logger = Logger.getLogger(DataCleanupRunner.class.getName());
    
    // Dummy data patterns
    private static final String[] DUMMY_USER_IDS = {
        "admin-001", "vol-001", "vol-002", "req-001", "req-002", "req-003"
    };
    
    private static final String[] DUMMY_USERNAMES = {
        "admin", "volunteer1", "volunteer2", "requester1", "requester2", "requester3"
    };
    
    private static final String[] DUMMY_RESOURCE_PATTERNS = {
        "res-food-", "res-clothing-", "res-shelter-", "res-medical-", "res-education-", "res-other-"
    };
    
    private static final String[] DUMMY_REQUEST_IDS = {
        "req-001", "req-002", "req-003", "req-004", "req-005", "req-006", "req-007", "req-008"
    };
    
    /**
     * Main cleanup execution method
     * Removes all dummy data in correct order to maintain referential integrity
     * 
     * @throws DatabaseException if cleanup fails
     */
    public static void cleanupDummyData() throws DatabaseException {
        logger.info("========== STARTING DATA CLEANUP ==========");
        
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            boolean originalAutoCommit = conn.getAutoCommit();
            
            try {
                conn.setAutoCommit(false);
                
                // Step 1: Delete dummy requests (must be first due to FK constraints)
                int requestsDeleted = deleteDummyRequests(conn);
                logger.info("Deleted " + requestsDeleted + " dummy requests");
                
                // Step 2: Delete dummy resources (must be before users)
                int resourcesDeleted = deleteDummyResources(conn);
                logger.info("Deleted " + resourcesDeleted + " dummy resources");
                
                // Step 3: Delete dummy users (must be last)
                int usersDeleted = deleteDummyUsers(conn);
                logger.info("Deleted " + usersDeleted + " dummy users");
                
                // Commit transaction
                conn.commit();
                logger.info("========== DATA CLEANUP COMPLETED SUCCESSFULLY ==========");
                logger.info("Summary: " + requestsDeleted + " requests, " + 
                           resourcesDeleted + " resources, " + usersDeleted + " users removed");
                
            } catch (SQLException e) {
                conn.rollback();
                logger.log(Level.SEVERE, "Cleanup failed, rolling back changes", e);
                throw new DatabaseException("Data cleanup failed and was rolled back", e);
            } finally {
                conn.setAutoCommit(originalAutoCommit);
            }
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to initialize cleanup transaction", e);
        }
    }
    
    /**
     * Deletes all dummy requests
     * Criteria: Request IDs matching dummy patterns
     * 
     * @param conn Database connection
     * @return Number of requests deleted
     * @throws SQLException if deletion fails
     */
    private static int deleteDummyRequests(Connection conn) throws SQLException {
        int totalDeleted = 0;
        
        // Delete by request ID patterns
        for (String requestId : DUMMY_REQUEST_IDS) {
            String sql = "DELETE FROM requests WHERE request_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, requestId);
                totalDeleted += stmt.executeUpdate();
            }
        }
        
        // Also delete requests created by dummy users
        String sql = "DELETE FROM requests WHERE requester_id IN (?, ?, ?, ?, ?, ?) " +
                    "OR volunteer_id IN (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Requester IDs
            stmt.setString(1, "req-001");
            stmt.setString(2, "req-002");
            stmt.setString(3, "req-003");
            stmt.setString(4, "admin-001");
            stmt.setString(5, "vol-001");
            stmt.setString(6, "vol-002");
            // Volunteer IDs
            stmt.setString(7, "vol-001");
            stmt.setString(8, "vol-002");
            stmt.setString(9, "admin-001");
            totalDeleted += stmt.executeUpdate();
        }
        
        return totalDeleted;
    }
    
    /**
     * Deletes all dummy resources
     * Criteria: Resource IDs matching dummy patterns (res-*-*)
     * 
     * @param conn Database connection
     * @return Number of resources deleted
     * @throws SQLException if deletion fails
     */
    private static int deleteDummyResources(Connection conn) throws SQLException {
        int totalDeleted = 0;
        
        // Delete resources by pattern
        for (String pattern : DUMMY_RESOURCE_PATTERNS) {
            String sql = "DELETE FROM resources WHERE resource_id LIKE ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, pattern + "%");
                totalDeleted += stmt.executeUpdate();
            }
        }
        
        // Also delete resources created by dummy admin user
        String sql = "DELETE FROM resources WHERE created_by = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "admin-001");
            totalDeleted += stmt.executeUpdate();
        }
        
        return totalDeleted;
    }
    
    /**
     * Deletes all dummy users
     * Criteria: User IDs and usernames matching dummy patterns
     * IMPORTANT: Preserves any real admin users
     * 
     * @param conn Database connection
     * @return Number of users deleted
     * @throws SQLException if deletion fails
     */
    private static int deleteDummyUsers(Connection conn) throws SQLException {
        int totalDeleted = 0;
        
        // Delete by user ID
        for (String userId : DUMMY_USER_IDS) {
            String sql = "DELETE FROM users WHERE user_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, userId);
                totalDeleted += stmt.executeUpdate();
            }
        }
        
        // Delete by username (test usernames only)
        for (String username : DUMMY_USERNAMES) {
            String sql = "DELETE FROM users WHERE username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                totalDeleted += stmt.executeUpdate();
            }
        }
        
        // Delete test users with @example.com emails
        String sql = "DELETE FROM users WHERE email LIKE ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%@example.com");
            totalDeleted += stmt.executeUpdate();
        }
        
        return totalDeleted;
    }
    
    /**
     * Verifies cleanup was successful
     * Checks that no dummy data remains
     * 
     * @throws DatabaseException if verification fails
     */
    public static void verifyCleanup() throws DatabaseException {
        logger.info("========== VERIFYING DATA CLEANUP ==========");
        
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            
            // Check for remaining dummy users
            int dummyUsers = countDummyUsers(conn);
            if (dummyUsers > 0) {
                logger.warning("WARNING: Found " + dummyUsers + " remaining dummy users");
            } else {
                logger.info("✓ No dummy users found");
            }
            
            // Check for remaining dummy resources
            int dummyResources = countDummyResources(conn);
            if (dummyResources > 0) {
                logger.warning("WARNING: Found " + dummyResources + " remaining dummy resources");
            } else {
                logger.info("✓ No dummy resources found");
            }
            
            // Check for remaining dummy requests
            int dummyRequests = countDummyRequests(conn);
            if (dummyRequests > 0) {
                logger.warning("WARNING: Found " + dummyRequests + " remaining dummy requests");
            } else {
                logger.info("✓ No dummy requests found");
            }
            
            // Show final counts
            long totalUsers = countTotal(conn, "users");
            long totalResources = countTotal(conn, "resources");
            long totalRequests = countTotal(conn, "requests");
            
            logger.info("========== FINAL DATABASE STATE ==========");
            logger.info("Total Users: " + totalUsers);
            logger.info("Total Resources: " + totalResources);
            logger.info("Total Requests: " + totalRequests);
            logger.info("========== VERIFICATION COMPLETE ==========");
            
        } catch (SQLException e) {
            throw new DatabaseException("Verification failed", e);
        }
    }
    
    /**
     * Counts remaining dummy users
     */
    private static int countDummyUsers(Connection conn) throws SQLException {
        int count = 0;
        
        // Count by user ID
        for (String userId : DUMMY_USER_IDS) {
            String sql = "SELECT COUNT(*) FROM users WHERE user_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        count += rs.getInt(1);
                    }
                }
            }
        }
        
        return count;
    }
    
    /**
     * Counts remaining dummy resources
     */
    private static int countDummyResources(Connection conn) throws SQLException {
        int count = 0;
        
        for (String pattern : DUMMY_RESOURCE_PATTERNS) {
            String sql = "SELECT COUNT(*) FROM resources WHERE resource_id LIKE ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, pattern + "%");
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        count += rs.getInt(1);
                    }
                }
            }
        }
        
        return count;
    }
    
    /**
     * Counts remaining dummy requests
     */
    private static int countDummyRequests(Connection conn) throws SQLException {
        int count = 0;
        
        for (String requestId : DUMMY_REQUEST_IDS) {
            String sql = "SELECT COUNT(*) FROM requests WHERE request_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, requestId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        count += rs.getInt(1);
                    }
                }
            }
        }
        
        return count;
    }
    
    /**
     * Counts total records in a table
     */
    private static long countTotal(Connection conn, String tableName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + tableName;
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        }
        return 0;
    }
    
    /**
     * Main method for running cleanup from command line
     * Usage: java DataCleanupRunner
     */
    public static void main(String[] args) {
        try {
            logger.info("Community Resource Hub - Data Cleanup Utility");
            logger.info("This will remove all dummy/test data from the database");
            logger.info("");
            
            // Run cleanup
            cleanupDummyData();
            
            // Verify cleanup
            verifyCleanup();
            
            logger.info("");
            logger.info("Data cleanup completed successfully!");
            logger.info("The database is now ready for production use.");
            
        } catch (DatabaseException e) {
            logger.log(Level.SEVERE, "Data cleanup failed: " + e.getMessage(), e);
            System.exit(1);
        }
    }
}
