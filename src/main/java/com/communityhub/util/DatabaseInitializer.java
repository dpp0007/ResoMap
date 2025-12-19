package com.communityhub.util;

import com.communityhub.exception.DatabaseException;

/**
 * Simple database initializer for testing
 */
public class DatabaseInitializer {
    
    public static void main(String[] args) {
        try {
            System.out.println("Initializing database...");
            
            // Initialize database connection and schema
            DBConnection dbConnection = DBConnection.getInstance();
            dbConnection.initializeDatabase();
            
            System.out.println("Database initialized successfully!");
            
            // Initialize sample data
            DataInitializer.initializeSampleData();
            
            System.out.println("Sample data created successfully!");
            System.out.println("Database setup complete. You can now deploy the WAR file to Tomcat.");
            
        } catch (DatabaseException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}