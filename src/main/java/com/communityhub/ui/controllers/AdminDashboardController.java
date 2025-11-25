package com.communityhub.ui.controllers;

import com.communityhub.core.BaseController;
import com.communityhub.core.Constants;
import com.communityhub.core.ErrorHandler;
import com.communityhub.exception.DatabaseException;
import com.communityhub.model.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Admin Dashboard Controller - Refactored
 * Main controller for admin dashboard with multi-view navigation
 * 
 * Improvements:
 * - Extends BaseController for common functionality
 * - Uses service injection via ServiceFactory
 * - Cleaner async handling with executeAsync
 * - Uses ErrorHandler for consistent error messages
 * - Uses Constants for paths
 * - Reduced code duplication
 * 
 * @author ResoMap Team
 * @version 2.0
 */
public class AdminDashboardController extends BaseController implements Initializable {
    
    @FXML private Label welcomeLabel;
    @FXML private Button logoutButton;
    @FXML private Button dashboardBtn;
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
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            initializeServices();
            setupNavigation();
            logger.info("AdminDashboardController initialized successfully");
        } catch (DatabaseException e) {
            ErrorHandler.handleDatabaseError(e);
        }
    }
    
    /**
     * Sets up navigation
     */
    private void setupNavigation() {
        // Set initial active button
        dashboardBtn.getStyleClass().add("active");
    }
    
    @Override
    protected void onUserSet(User user) {
        if (user != null) {
            welcomeLabel.setText("Welcome, " + user.getDisplayName());
            refreshData();
        }
    }
    
    /**
     * Refreshes dashboard data
     */
    public void refreshData() {
        if (!validateUserLoggedIn()) {
            return;
        }
        
        loadDashboardStatistics();
    }
    
    /**
     * Loads dashboard statistics asynchronously
     */
    private void loadDashboardStatistics() {
        executeAsync(
            () -> {
                // Load all statistics
                long totalResources = resourceService.getAllResources().size();
                long availableResources = resourceService.getAvailableResources().size();
                
                Map<String, Object> requestStats = requestService.getRequestStatistics();
                long activeRequests = (Long) requestStats.getOrDefault("totalActiveRequests", 0L);
                long pendingRequests = (Long) requestStats.getOrDefault("pendingRequests", 0L);
                long criticalRequests = (Long) requestStats.getOrDefault("criticalRequests", 0L);
                
                long totalUsers = userService.getAllUsers().size();
                
                return new DashboardStats(
                    totalResources,
                    totalUsers,
                    activeRequests,
                    pendingRequests,
                    criticalRequests,
                    availableResources
                );
            },
            this::updateDashboardUI
        );
    }
    
    /**
     * Updates dashboard UI with statistics
     * 
     * @param stats Dashboard statistics
     */
    private void updateDashboardUI(DashboardStats stats) {
        Platform.runLater(() -> {
            totalResourcesLabel.setText(String.valueOf(stats.totalResources));
            totalUsersLabel.setText(String.valueOf(stats.totalUsers));
            activeRequestsLabel.setText(String.valueOf(stats.activeRequests));
            pendingRequestsLabel.setText(String.valueOf(stats.pendingRequests));
            criticalRequestsLabel.setText(String.valueOf(stats.criticalRequests));
            availableResourcesLabel.setText(String.valueOf(stats.availableResources));
        });
    }
    
    // Navigation Methods
    
    /**
     * Shows the main dashboard view
     */
    @FXML
    private void showDashboard() {
        setActiveButton(dashboardBtn);
        refreshData();
        logger.info("Showing dashboard view");
    }
    
    /**
     * Shows resource management view
     * 
     * @param event Action event
     */
    @FXML
    private void showResourceManagement(ActionEvent event) {
        setActiveButton(resourcesButton);
        loadViewIntoPane(Constants.FXML.RESOURCE_MANAGEMENT, "Resource Management");
    }
    
    /**
     * Shows user management view
     * 
     * @param event Action event
     */
    @FXML
    private void showUserManagement(ActionEvent event) {
        setActiveButton(usersButton);
        loadViewIntoPane(Constants.FXML.USER_MANAGEMENT, "User Management");
    }
    
    /**
     * Shows request overview
     * 
     * @param event Action event
     */
    @FXML
    private void showRequestOverview(ActionEvent event) {
        setActiveButton(requestsButton);
        loadViewIntoPane(Constants.FXML.REQUEST_OVERVIEW, "Request Overview");
    }
    
    /**
     * Shows system reports
     * 
     * @param event Action event
     */
    @FXML
    private void showReports(ActionEvent event) {
        setActiveButton(reportsButton);
        loadViewIntoPane(Constants.FXML.SYSTEM_REPORTS, "System Reports");
    }
    
    /**
     * Shows system settings
     * 
     * @param event Action event
     */
    @FXML
    private void showSettings(ActionEvent event) {
        setActiveButton(settingsButton);
        loadViewIntoPane(Constants.FXML.SYSTEM_SETTINGS, "System Settings");
    }
    
    /**
     * Loads a view into the content pane
     * 
     * @param fxmlPath Path to FXML file
     * @param viewName Name of view for logging
     */
    private void loadViewIntoPane(String fxmlPath, String viewName) {
        try {
            Parent view = loadView(fxmlPath);
            
            contentPane.getChildren().clear();
            contentPane.getChildren().add(view);
            
            logger.info("Loaded view: " + viewName);
            
        } catch (IOException e) {
            logger.severe("Failed to load view: " + viewName);
            ErrorHandler.handleException(e, "Load View");
            showPlaceholderContent(viewName, "This view is temporarily unavailable.");
        }
    }
    
    /**
     * Shows placeholder content when view fails to load
     * 
     * @param title Content title
     * @param message Content message
     */
    private void showPlaceholderContent(String title, String message) {
        try {
            Parent placeholder = loadView("/fxml/placeholder-content.fxml");
            
            // Set content in placeholder controller if possible
            Object controller = placeholder.getUserData();
            if (controller instanceof PlaceholderContentController) {
                ((PlaceholderContentController) controller).setContent(title, message);
            }
            
            contentPane.getChildren().clear();
            contentPane.getChildren().add(placeholder);
            
        } catch (IOException e) {
            logger.severe("Failed to load placeholder content");
            // Fallback: show simple label
            Label fallbackLabel = new Label(title + "\n\n" + message);
            fallbackLabel.setWrapText(true);
            fallbackLabel.getStyleClass().add("card-content");
            contentPane.getChildren().clear();
            contentPane.getChildren().add(fallbackLabel);
        }
    }
    
    /**
     * Sets the active navigation button
     * 
     * @param activeButton Button to set as active
     */
    private void setActiveButton(Button activeButton) {
        // Remove active class from all buttons
        dashboardBtn.getStyleClass().remove("active");
        resourcesButton.getStyleClass().remove("active");
        usersButton.getStyleClass().remove("active");
        requestsButton.getStyleClass().remove("active");
        reportsButton.getStyleClass().remove("active");
        settingsButton.getStyleClass().remove("active");
        
        // Add active class to selected button
        if (activeButton != null) {
            activeButton.getStyleClass().add("active");
        }
    }
    
    /**
     * Inner class to hold dashboard statistics
     */
    private static class DashboardStats {
        final long totalResources;
        final long totalUsers;
        final long activeRequests;
        final long pendingRequests;
        final long criticalRequests;
        final long availableResources;
        
        DashboardStats(long totalResources, long totalUsers, long activeRequests,
                      long pendingRequests, long criticalRequests, long availableResources) {
            this.totalResources = totalResources;
            this.totalUsers = totalUsers;
            this.activeRequests = activeRequests;
            this.pendingRequests = pendingRequests;
            this.criticalRequests = criticalRequests;
            this.availableResources = availableResources;
        }
    }
}
