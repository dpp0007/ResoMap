package com.communityhub.ui.controllers;

import com.communityhub.exception.AuthenticationException;
import com.communityhub.exception.DatabaseException;
import com.communityhub.model.User;
import com.communityhub.service.AuthenticationService;
import com.communityhub.service.ResourceService;
import com.communityhub.service.RequestService;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Controller for the admin dashboard
 * Demonstrates dashboard functionality with statistics and navigation
 */
public class AdminDashboardController implements Initializable, DashboardController {
    
    private static final Logger logger = Logger.getLogger(AdminDashboardController.class.getName());
    
    @FXML private Label welcomeLabel;
    @FXML private Button logoutButton;
    @FXML private Button resourcesButton;
    @FXML private Button usersButton;
    @FXML private Button requestsButton;
    @FXML private Button reportsButton;
    @FXML private Button settingsButton;
    @FXML private StackPane contentPane;
    
    // Statistics labels
    @FXML private Label totalResourcesLabel;
    @FXML private Label totalUsersLabel;
    @FXML private Label activeRequestsLabel;
    @FXML private Label pendingRequestsLabel;
    @FXML private Label criticalRequestsLabel;
    @FXML private Label availableResourcesLabel;
    
    private User currentUser;
    private AuthenticationService authService;
    private ResourceService resourceService;
    private RequestService requestService;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            authService = new AuthenticationService();
            resourceService = new ResourceService();
            requestService = new RequestService();
            
            setupEventHandlers();
            
        } catch (DatabaseException e) {
            logger.log(Level.SEVERE, "Failed to initialize dashboard services", e);
            showError("Failed to initialize dashboard", "System error occurred during initialization.");
        }
    }
    
    /**
     * Sets up event handlers for dashboard components
     */
    private void setupEventHandlers() {
        // Set initial active button
        resourcesButton.getStyleClass().add("active");
    }
    
    @Override
    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {
            welcomeLabel.setText("Welcome, " + user.getDisplayName());
            refreshData();
        }
    }
    
    @Override
    public void refreshData() {
        if (currentUser == null) return;
        
        // Create background task for loading statistics
        Task<Void> loadStatsTask = new Task<Void>() {
            private long totalResources = 0;
            private long totalUsers = 0;
            private long activeRequests = 0;
            private long pendingRequests = 0;
            private long criticalRequests = 0;
            private long availableResources = 0;
            
            @Override
            protected Void call() throws Exception {
                try {
                    // Load resource statistics
                    totalResources = resourceService.getAllResources().size();
                    availableResources = resourceService.getAvailableResources().size();
                    
                    // Load request statistics
                    Map<String, Object> requestStats = requestService.getRequestStatistics();
                    activeRequests = (Long) requestStats.getOrDefault("totalActiveRequests", 0L);
                    pendingRequests = (Long) requestStats.getOrDefault("pendingRequests", 0L);
                    criticalRequests = (Long) requestStats.getOrDefault("criticalRequests", 0L);
                    
                    // For now, set total users to a placeholder (would need UserService)
                    try {
                        // Get total users from database
                        com.communityhub.dao.UserDAO userDAO = new com.communityhub.dao.UserDAO();
                        totalUsers = (int) userDAO.count();
                    } catch (Exception e) {
                        logger.log(Level.WARNING, "Could not get user count", e);
                        totalUsers = 0;
                    }
                    
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Error loading dashboard statistics", e);
                }
                return null;
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    totalResourcesLabel.setText(String.valueOf(totalResources));
                    totalUsersLabel.setText(String.valueOf(totalUsers));
                    activeRequestsLabel.setText(String.valueOf(activeRequests));
                    pendingRequestsLabel.setText(String.valueOf(pendingRequests));
                    criticalRequestsLabel.setText(String.valueOf(criticalRequests));
                    availableResourcesLabel.setText(String.valueOf(availableResources));
                });
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    logger.log(Level.WARNING, "Failed to load dashboard statistics", getException());
                });
            }
        };
        
        Thread statsThread = new Thread(loadStatsTask);
        statsThread.setDaemon(true);
        statsThread.start();
    }
    
    @Override
    public void handleLogout() {
        try {
            authService.logout();
            
            // Navigate back to login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            
            stage.setScene(scene);
            stage.setTitle("Community Resource Hub - Login");
            stage.setMaximized(false);
            stage.centerOnScreen();
            
            logger.info("User logged out successfully");
            
        } catch (AuthenticationException | IOException e) {
            logger.log(Level.SEVERE, "Error during logout", e);
            showError("Logout Error", "An error occurred during logout.");
        }
    }
    
    /**
     * Shows resource management view
     * @param event Action event
     */
    @FXML
    private void showResourceManagement(ActionEvent event) {
        setActiveButton(resourcesButton);
        loadResourceManagement();
    }
    
    /**
     * Loads the resource management interface
     */
    private void loadResourceManagement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/resource-management.fxml"));
            Parent content = loader.load();
            
            contentPane.getChildren().clear();
            contentPane.getChildren().add(content);
            
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to load resource management", e);
            showPlaceholderContent("Resource Management", 
                "Resource management interface is temporarily unavailable.\n\n" +
                "Please try again later or contact system administrator.");
        }
    }
    
    /**
     * Shows user management view
     * @param event Action event
     */
    @FXML
    private void showUserManagement(ActionEvent event) {
        setActiveButton(usersButton);
        loadView("/fxml/user-management.fxml", "User Management");
    }
    
    /**
     * Shows request overview
     * @param event Action event
     */
    @FXML
    private void showRequestOverview(ActionEvent event) {
        setActiveButton(requestsButton);
        loadView("/fxml/request-overview.fxml", "Request Overview");
    }
    
    /**
     * Shows system reports
     * @param event Action event
     */
    @FXML
    private void showReports(ActionEvent event) {
        setActiveButton(reportsButton);
        loadView("/fxml/system-reports.fxml", "System Reports");
    }
    
    /**
     * Shows system settings
     * @param event Action event
     */
    @FXML
    private void showSettings(ActionEvent event) {
        setActiveButton(settingsButton);
        loadView("/fxml/system-settings.fxml", "System Settings");
    }
    
    /**
     * Sets the active button in the sidebar
     * @param activeButton Button to set as active
     */
    private void setActiveButton(Button activeButton) {
        // Remove active class from all buttons
        resourcesButton.getStyleClass().remove("active");
        usersButton.getStyleClass().remove("active");
        requestsButton.getStyleClass().remove("active");
        reportsButton.getStyleClass().remove("active");
        settingsButton.getStyleClass().remove("active");
        
        // Add active class to selected button
        activeButton.getStyleClass().add("active");
    }
    
    /**
     * Shows placeholder content for unimplemented features
     * @param title Content title
     * @param description Content description
     */
    private void showPlaceholderContent(String title, String description) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/placeholder-content.fxml"));
            Parent content = loader.load();
            
            // Set content in the placeholder controller if it exists
            Object controller = loader.getController();
            if (controller instanceof PlaceholderContentController) {
                ((PlaceholderContentController) controller).setContent(title, description);
            }
            
            contentPane.getChildren().clear();
            contentPane.getChildren().add(content);
            
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to load placeholder content", e);
            // Fallback: show simple label
            Label placeholderLabel = new Label(title + "\n\n" + description);
            placeholderLabel.setWrapText(true);
            placeholderLabel.getStyleClass().add("card-content");
            contentPane.getChildren().clear();
            contentPane.getChildren().add(placeholderLabel);
        }
    }
    
    /**
     * Loads a view into the content pane
     * @param fxmlPath Path to FXML file
     * @param title View title for error messages
     */
    private void loadView(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            contentPane.getChildren().clear();
            contentPane.getChildren().add(view);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load " + title, e);
            showError("Failed to load " + title, "Could not load the view: " + e.getMessage());
        }
    }
    
    /**
     * Shows an error dialog
     * @param title Error title
     * @param message Error message
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}