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
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to initialize database schema", e);
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