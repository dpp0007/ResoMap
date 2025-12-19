package com.communityhub.servlet.listener;

import com.communityhub.util.DataInitializer;
import com.communityhub.util.DBConnection;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Application Context Listener
 * Initializes database and sample data on application startup
 */
@WebListener
public class AppContextListener implements ServletContextListener {
    
    private static final Logger logger = Logger.getLogger(AppContextListener.class.getName());
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("=== APPLICATION STARTUP ===");
        
        try {
            // Initialize database connection and schema
            DBConnection dbConnection = DBConnection.getInstance();
            dbConnection.initializeDatabase();
            logger.info("Database schema initialized");
            
            // Initialize sample data
            DataInitializer.initializeSampleData();
            logger.info("Sample data initialized");
            
            logger.info("=== APPLICATION READY ===");
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to initialize application", e);
        }
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("=== APPLICATION SHUTDOWN ===");
        
        try {
            DBConnection dbConnection = DBConnection.getInstance();
            dbConnection.closeConnection();
            logger.info("Database connection closed");
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error during application shutdown", e);
        }
    }
}
