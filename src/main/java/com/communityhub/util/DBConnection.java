package com.communityhub.util;

import com.communityhub.exception.DatabaseException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Singleton class for managing database connections
 * Provides centralized database connection management with connection pooling
 */
public class DBConnection {
    
    private static final Logger logger = Logger.getLogger(DBConnection.class.getName());
    private static DBConnection instance;
    private Connection connection;
    
    // Database configuration
    private static final String DB_URL = "jdbc:sqlite:community_hub.db";
    private static final String MYSQL_URL = "jdbc:mysql://localhost:3306/community_hub";
    private static final String MYSQL_USER = "root";
    private static final String MYSQL_PASSWORD = "";
    
    // Use SQLite by default for easier setup
    private static final boolean USE_MYSQL = false;
    
    private DBConnection() {
        // Private constructor for singleton pattern
    }
    
    /**
     * Gets the singleton instance of DBConnection
     * @return DBConnection instance
     */
    public static synchronized DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }
    
    /**
     * Gets the database connection, creating it if necessary
     * @return Connection object
     * @throws DatabaseException if connection fails
     */
    public Connection getConnection() throws DatabaseException {
        try {
            if (connection == null || connection.isClosed()) {
                createConnection();
            }
            return connection;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to get database connection", e);
        }
    }
    
    /**
     * Creates a new database connection
     * @throws DatabaseException if connection creation fails
     */
    private void createConnection() throws DatabaseException {
        try {
            if (USE_MYSQL) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD);
            } else {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection(DB_URL);
            }
            
            // CRITICAL: Set autoCommit to FALSE to enable transaction management
            // This allows proper commit/rollback control in DAO operations
            connection.setAutoCommit(false);
            logger.info("Database connection established successfully with transaction support");
            
        } catch (ClassNotFoundException e) {
            throw new DatabaseException("Database driver not found", e);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to create database connection", e);
        }
    }
    
    /**
     * Initializes the database schema
     * @throws DatabaseException if initialization fails
     */
    public void initializeDatabase() throws DatabaseException {
        try (Statement stmt = getConnection().createStatement()) {
            
            // Create users table
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                "user_id TEXT PRIMARY KEY," +
                "username TEXT UNIQUE NOT NULL," +
                "email TEXT UNIQUE NOT NULL," +
                "password_hash TEXT NOT NULL," +
                "role TEXT NOT NULL CHECK (role IN ('ADMIN', 'VOLUNTEER', 'REQUESTER'))," +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ")");
            
            // Create resources table
            stmt.execute("CREATE TABLE IF NOT EXISTS resources (" +
                "resource_id TEXT PRIMARY KEY," +
                "name TEXT NOT NULL," +
                "description TEXT," +
                "category TEXT NOT NULL," +
                "quantity INTEGER DEFAULT 0," +
                "location TEXT," +
                "contact_info TEXT," +
                "created_by TEXT," +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (created_by) REFERENCES users(user_id)" +
                ")");
            
            // Create requests table
            stmt.execute("CREATE TABLE IF NOT EXISTS requests (" +
                "request_id TEXT PRIMARY KEY," +
                "requester_id TEXT NOT NULL," +
                "resource_id TEXT NOT NULL," +
                "volunteer_id TEXT," +
                "status TEXT DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'ASSIGNED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED'))," +
                "description TEXT," +
                "urgency_level TEXT DEFAULT 'MEDIUM' CHECK (urgency_level IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL'))," +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (requester_id) REFERENCES users(user_id)," +
                "FOREIGN KEY (resource_id) REFERENCES resources(resource_id)," +
                "FOREIGN KEY (volunteer_id) REFERENCES users(user_id)" +
                ")");
            
            // Create feedback table
            stmt.execute("CREATE TABLE IF NOT EXISTS feedback (" +
                "feedback_id TEXT PRIMARY KEY," +
                "user_id TEXT NOT NULL," +
                "request_id TEXT," +
                "rating INTEGER CHECK (rating >= 1 AND rating <= 5)," +
                "comments TEXT," +
                "feedback_type TEXT DEFAULT 'GENERAL' CHECK (feedback_type IN ('GENERAL', 'REQUEST_SPECIFIC', 'SYSTEM_IMPROVEMENT'))," +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (user_id) REFERENCES users(user_id)," +
                "FOREIGN KEY (request_id) REFERENCES requests(request_id)" +
                ")");
            
            logger.info("Database schema initialized successfully");
            
            // Insert sample data if tables are empty
            insertSampleDataIfEmpty(stmt);
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to initialize database schema", e);
        }
    }
    
    /**
     * Inserts sample data if the database is empty
     * @param stmt Statement to use for queries
     * @throws SQLException if insertion fails
     */
    private void insertSampleDataIfEmpty(Statement stmt) throws SQLException {
        // Check if users table is empty
        var rs = stmt.executeQuery("SELECT COUNT(*) as count FROM users");
        rs.next();
        int userCount = rs.getInt("count");
        rs.close();
        
        if (userCount == 0) {
            logger.info("Database is empty, inserting sample data...");
            
            // Insert users
            stmt.execute("INSERT INTO users (user_id, username, email, password_hash, role) VALUES " +
                "('admin-001', 'admin', 'admin@communityhub.org', 'salt:hash', 'ADMIN')," +
                "('vol-001', 'volunteer1', 'volunteer1@example.com', 'salt:hash', 'VOLUNTEER')," +
                "('vol-002', 'volunteer2', 'volunteer2@example.com', 'salt:hash', 'VOLUNTEER')," +
                "('req-001', 'requester1', 'requester1@example.com', 'salt:hash', 'REQUESTER')," +
                "('req-002', 'requester2', 'requester2@example.com', 'salt:hash', 'REQUESTER')," +
                "('req-003', 'requester3', 'requester3@example.com', 'salt:hash', 'REQUESTER')");
            
            // Insert resources
            stmt.execute("INSERT INTO resources (resource_id, name, description, category, quantity, location, contact_info, created_by) VALUES " +
                "('res-food-001', 'Canned Vegetables', 'Assorted canned vegetables including corn, peas, and carrots.', 'FOOD', 45, 'Community Center - Storage A', 'food@community.org', 'admin-001')," +
                "('res-food-002', 'Canned Fruits', 'Mixed canned fruits in light syrup.', 'FOOD', 38, 'Community Center - Storage A', 'food@community.org', 'admin-001')," +
                "('res-food-003', 'Pasta & Rice', 'Bulk pasta and rice supplies for meal preparation.', 'FOOD', 60, 'Community Center - Storage B', 'food@community.org', 'admin-001')," +
                "('res-food-004', 'Peanut Butter', 'High-protein peanut butter jars.', 'FOOD', 25, 'Community Center - Storage A', 'food@community.org', 'admin-001')," +
                "('res-food-005', 'Baby Formula', 'Infant formula and baby food supplies.', 'FOOD', 30, 'Family Services - Building C', 'family@community.org', 'admin-001')," +
                "('res-clothing-001', 'Winter Coats', 'Warm winter coats for adults and children.', 'CLOTHING', 28, 'Donation Center - Building B', 'donations@community.org', 'admin-001')," +
                "('res-clothing-002', 'Warm Sweaters', 'Wool and fleece sweaters for cold weather.', 'CLOTHING', 35, 'Donation Center - Building B', 'donations@community.org', 'admin-001')," +
                "('res-clothing-003', 'Thermal Socks', 'Thermal and wool socks for winter warmth.', 'CLOTHING', 100, 'Donation Center - Building B', 'donations@community.org', 'admin-001')," +
                "('res-shelter-001', 'Emergency Blankets', 'Thermal emergency blankets for temporary shelter.', 'SHELTER', 120, 'Emergency Services - Warehouse', 'emergency@community.org', 'admin-001')," +
                "('res-shelter-002', 'Sleeping Bags', 'Warm sleeping bags rated for cold weather.', 'SHELTER', 20, 'Emergency Services - Warehouse', 'emergency@community.org', 'admin-001')," +
                "('res-medical-001', 'First Aid Kits', 'Complete first aid kits with bandages and supplies.', 'MEDICAL', 18, 'Health Center - Supply Room', 'health@community.org', 'admin-001')," +
                "('res-medical-002', 'Medical Masks', 'N95 and surgical masks for health protection.', 'MEDICAL', 500, 'Health Center - Supply Room', 'health@community.org', 'admin-001')," +
                "('res-education-001', 'School Supplies', 'Notebooks, pens, pencils, and basic school supplies.', 'EDUCATION', 80, 'Education Center - Room 101', 'education@community.org', 'admin-001')," +
                "('res-education-002', 'Textbooks', 'Used textbooks for various subjects and grade levels.', 'EDUCATION', 45, 'Education Center - Room 102', 'education@community.org', 'admin-001')," +
                "('res-other-001', 'Hygiene Products', 'Soap, shampoo, toothpaste, and personal hygiene items.', 'OTHER', 60, 'Community Center - Storage C', 'supplies@community.org', 'admin-001')");
            
            // Insert requests
            stmt.execute("INSERT INTO requests (request_id, requester_id, resource_id, description, urgency_level, status) VALUES " +
                "('req-001', 'req-001', 'res-food-001', 'Family of four needs emergency food assistance.', 'HIGH', 'PENDING')," +
                "('req-002', 'req-002', 'res-clothing-001', 'Single mother needs winter coats for children.', 'MEDIUM', 'PENDING')," +
                "('req-003', 'req-001', 'res-medical-001', 'Need first aid kit for elderly parent.', 'LOW', 'PENDING')," +
                "('req-004', 'req-003', 'res-education-001', 'Student needs school supplies for new school year.', 'MEDIUM', 'ASSIGNED')," +
                "('req-005', 'req-002', 'res-shelter-001', 'Temporary shelter needed for family.', 'CRITICAL', 'PENDING')," +
                "('req-006', 'req-001', 'res-food-005', 'New mother needs baby formula.', 'HIGH', 'PENDING')," +
                "('req-007', 'req-003', 'res-clothing-003', 'Need warm socks for homeless outreach program.', 'MEDIUM', 'PENDING')," +
                "('req-008', 'req-002', 'res-other-001', 'Hygiene products needed for family in temporary housing.', 'MEDIUM', 'PENDING')");
            
            connection.commit();
            logger.info("Sample data inserted successfully");
        }
    }
    
    /**
     * Closes the database connection
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                logger.info("Database connection closed");
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error closing database connection", e);
        }
    }
    
    /**
     * Tests the database connection
     * @return true if connection is successful
     */
    public boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Database connection test failed", e);
            return false;
        }
    }
}