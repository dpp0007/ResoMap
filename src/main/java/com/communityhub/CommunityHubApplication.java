package com.communityhub;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.communityhub.util.DBConnection;
import com.communityhub.util.SessionManager;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Main application class for Community Resource Hub
 * Initializes the JavaFX application and manages application lifecycle
 */
public class CommunityHubApplication extends Application {
    
    private static final Logger logger = Logger.getLogger(CommunityHubApplication.class.getName());
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialize logging system
            com.communityhub.util.LoggingConfig.initialize();
            
            // Initialize database connection
            DBConnection.getInstance().initializeDatabase();
            
            // Initialize session manager
            SessionManager.getInstance();
            
            // Initialize sample data
            com.communityhub.util.DataInitializer.initializeSampleData();
            
            // Run login diagnostics (helps troubleshoot login issues)
            com.communityhub.util.LoginDiagnostic.runDiagnostics();
            
            // Load login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Scene scene = new Scene(loader.load(), 900, 800);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            
            primaryStage.setTitle("ResoMap");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
            
            logger.info("ResoMap application started successfully");
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to start application", e);
            System.exit(1);
        }
    }
    
    @Override
    public void stop() {
        try {
            // Cleanup resources
            DBConnection.getInstance().closeConnection();
            SessionManager.getInstance().clearSession();
            logger.info("Application stopped gracefully");
            
            // Shutdown logging system
            com.communityhub.util.LoggingConfig.shutdown();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error during application shutdown", e);
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}