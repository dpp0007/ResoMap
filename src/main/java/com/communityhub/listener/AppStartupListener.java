package com.communityhub.listener;

import com.communityhub.exception.DatabaseException;
import com.communityhub.util.DBConnection;
import com.communityhub.util.DataInitializer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Application startup listener that initializes the database on application startup
 */
@WebListener
public class AppStartupListener implements ServletContextListener {
    
    private static final Logger logger = Logger.getLogger(AppStartupListener.class.getName());
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("=== APPLICATION STARTUP ===");
        
        try {
            // Initialize database connection and schema
            logger.info("Initializing database...");
            DBConnection dbConnection = DBConnection.getInstance();
            dbConnection.initializeDatabase();
            logger.info("Database schema initialized successfully");
            
            // Initialize sample data
            logger.info("Initializing sample data...");
            DataInitializer.initializeSampleData();
            logger.info("Sample data initialized successfully");
            
            logger.info("=== APPLICATION STARTUP COMPLETE ===");
            
        } catch (DatabaseException e) {
            logger.log(Level.SEVERE, "Failed to initialize database on startup", e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error during application startup", e);
        }
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("Application shutting down...");
        try {
            DBConnection.getInstance().closeConnection();
            logger.info("Database connection closed");
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error closing database connection on shutdown", e);
        }
    }
}
