package com.communityhub.ui.controllers;

import com.communityhub.core.BaseController;
import com.communityhub.core.Constants;
import com.communityhub.core.ErrorHandler;
import com.communityhub.exception.DatabaseException;
import com.communityhub.model.Request;
import com.communityhub.model.RequestStatus;
import com.communityhub.model.User;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Volunteer Dashboard Controller - Refactored
 * Main controller for volunteer dashboard with multi-view navigation
 * 
 * Improvements:
 * - Extends BaseController for common functionality
 * - Multi-view pattern (consistent with Admin dashboard)
 * - Uses service injection via ServiceFactory
 * - Cleaner navigation with view loading
 * - Reduced code duplication
 * 
 * @author ResoMap Team
 * @version 2.0
 */
public class VolunteerDashboardController extends BaseController implements Initializable {
    
    // Header elements
    @FXML private Label welcomeLabel;
    @FXML private Button logoutButton;
    
    // Navigation buttons
    @FXML private Button dashboardBtn;
    @FXML private Button availableRequestsBtn;
    @FXML private Button myAssignmentsBtn;
    
    // Content pane for loading views
    @FXML private StackPane contentPane;
    
    // Dashboard stats (for main dashboard view)
    @FXML private Label availableRequestsLabel;
    @FXML private Label myAssignmentsLabel;
    @FXML private Label completedRequestsLabel;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            initializeServices();
            setupNavigation();
            logger.info("VolunteerDashboardController initialized successfully");
        } catch (DatabaseException e) {
            ErrorHandler.handleDatabaseError(e);
        }
    }
    
    /**
     * Sets up navigation button handlers
     */
    private void setupNavigation() {
        // Set initial active button
        setActiveButton(dashboardBtn);
    }
    
    @Override
    protected void onUserSet(User user) {
        if (user != null) {
            welcomeLabel.setText("Welcome, " + user.getDisplayName());
            // Load dashboard data when user is set
            refreshData();
        }
    }
    
    public void refreshData() {
        if (!validateUserLoggedIn()) {
            return;
        }
        
        loadDashboardStats();
    }
    
    /**
     * Loads dashboard statistics
     */
    private void loadDashboardStats() {
        executeAsync(
            () -> {
                List<Request> availableRequests = requestService.getAvailableRequests();
                List<Request> myAssignments = requestService.getRequestsByVolunteer(currentUser.getUserId());
                long completedCount = myAssignments.stream()
                    .filter(r -> RequestStatus.COMPLETED.equals(r.getStatus()))
                    .count();
                
                return new DashboardStats(
                    availableRequests.size(),
                    myAssignments.size(),
                    completedCount
                );
            },
            this::updateDashboardStats
        );
    }
    
    /**
     * Updates dashboard statistics in UI
     * 
     * @param stats Dashboard statistics
     */
    private void updateDashboardStats(DashboardStats stats) {
        Platform.runLater(() -> {
            if (availableRequestsLabel != null) {
                availableRequestsLabel.setText(String.valueOf(stats.availableRequests));
            }
            if (myAssignmentsLabel != null) {
                myAssignmentsLabel.setText(String.valueOf(stats.myAssignments));
            }
            if (completedRequestsLabel != null) {
                completedRequestsLabel.setText(String.valueOf(stats.completedRequests));
            }
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
     * Shows available requests view
     */
    @FXML
    private void showAvailableRequests() {
        setActiveButton(availableRequestsBtn);
        loadView("/fxml/volunteer-available-requests.fxml", "Available Requests");
    }
    
    /**
     * Shows my assignments view
     */
    @FXML
    private void showMyAssignments() {
        setActiveButton(myAssignmentsBtn);
        loadView("/fxml/volunteer-my-assignments.fxml", "My Assignments");
    }
    
    /**
     * Loads a view into the content pane
     * 
     * @param fxmlPath Path to FXML file
     * @param viewName Name of view for logging
     */
    private void loadView(String fxmlPath, String viewName) {
        try {
            Parent view = loadView(fxmlPath);
            
            // Pass current user to loaded controller
            // (already done in BaseController.loadView)
            
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
        availableRequestsBtn.getStyleClass().remove("active");
        myAssignmentsBtn.getStyleClass().remove("active");
        
        // Add active class to selected button
        if (activeButton != null) {
            activeButton.getStyleClass().add("active");
        }
    }
    
    /**
     * Inner class to hold dashboard statistics
     */
    private static class DashboardStats {
        final int availableRequests;
        final int myAssignments;
        final long completedRequests;
        
        DashboardStats(int availableRequests, int myAssignments, long completedRequests) {
            this.availableRequests = availableRequests;
            this.myAssignments = myAssignments;
            this.completedRequests = completedRequests;
        }
    }
}

    
    // DashboardController interface methods (BaseController already implements handleLogout)
    // No need to re-implement, just use the inherited version
