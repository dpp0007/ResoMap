package com.communityhub.ui.controllers;

import com.communityhub.core.BaseController;
import com.communityhub.core.Constants;
import com.communityhub.core.ErrorHandler;
import com.communityhub.exception.AuthenticationException;
import com.communityhub.exception.DatabaseException;
import com.communityhub.model.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Login Controller - Refactored
 * Handles user authentication and navigation to appropriate dashboard
 * 
 * Improvements:
 * - Extends BaseController for common functionality
 * - Uses ErrorHandler for consistent error messages
 * - Removed hardcoded test credentials (security improvement)
 * - Uses Constants for paths and messages
 * - Cleaner async handling with executeAsync
 * 
 * @author ResoMap Team
 * @version 2.0
 */
public class LoginController extends BaseController implements Initializable {
    
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Hyperlink registerLink;
    @FXML private Label errorLabel;
    @FXML private ProgressIndicator loadingIndicator;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            initializeServices();
            setupEventHandlers();
            logger.info("LoginController initialized successfully");
        } catch (DatabaseException e) {
            ErrorHandler.handleDatabaseError(e);
        }
    }
    
    /**
     * Sets up event handlers for the login form
     */
    private void setupEventHandlers() {
        // Enter key navigation
        usernameField.setOnAction(e -> passwordField.requestFocus());
        passwordField.setOnAction(e -> handleLogin(null));
        
        // Clear error when user starts typing
        usernameField.textProperty().addListener((obs, oldVal, newVal) -> clearError());
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> clearError());
    }
    
    /**
     * Handles login button click
     * 
     * @param event Action event (can be null for programmatic calls)
     */
    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        
        // Validate input
        if (!validateInput(username, password)) {
            return;
        }
        
        // Show loading state
        setLoading(true);
        clearError();
        
        // Perform login asynchronously
        javafx.concurrent.Task<User> loginTask = new javafx.concurrent.Task<User>() {
            @Override
            protected User call() throws Exception {
                return performLogin(username, password);
            }
        };
        
        executeAsync(loginTask, this::onLoginSuccess, this::onLoginFailure);
    }
    
    /**
     * Validates login input
     * 
     * @param username Username to validate
     * @param password Password to validate
     * @return true if input is valid
     */
    private boolean validateInput(String username, String password) {
        if (username.isEmpty()) {
            showErrorMessage("Please enter your username.");
            usernameField.requestFocus();
            return false;
        }
        
        if (password.isEmpty()) {
            showErrorMessage("Please enter your password.");
            passwordField.requestFocus();
            return false;
        }
        
        // Note: We don't validate length here for login
        // Length validation is only for registration
        // This allows existing users with any password length to login
        
        return true;
    }
    
    /**
     * Performs login operation
     * Runs in background thread
     * 
     * @param username Username
     * @param password Password
     * @return Authenticated user
     * @throws Exception if login fails
     */
    private User performLogin(String username, String password) throws Exception {
        logger.info("Attempting login for user: " + username);
        
        try {
            // Use AuthenticationService for proper authentication
            User user = authService.login(username, password);
            
            if (user == null) {
                throw new AuthenticationException("Authentication failed");
            }
            
            logger.info("Login successful for user: " + username + " (Role: " + user.getRole() + ")");
            return user;
            
        } catch (AuthenticationException e) {
            logger.warning("Authentication failed for user: " + username);
            throw e;
        } catch (Exception e) {
            logger.severe("Unexpected error during login: " + e.getMessage());
            throw new AuthenticationException("Login failed due to system error");
        }
    }
    
    /**
     * Handles successful login
     * Runs on JavaFX thread
     * 
     * @param user Authenticated user
     */
    private void onLoginSuccess(User user) {
        setLoading(false);
        
        if (user != null) {
            logger.info("Navigating to dashboard for user: " + user.getUsername());
            navigateToDashboard(user);
        } else {
            showErrorMessage(Constants.Messages.LOGIN_FAILED);
        }
    }
    
    /**
     * Handles login failure
     * Runs on JavaFX thread
     * 
     * @param throwable Exception that occurred
     */
    private void onLoginFailure(Throwable throwable) {
        setLoading(false);
        
        logger.warning("Login failed: " + throwable.getMessage());
        
        if (throwable instanceof AuthenticationException) {
            showErrorMessage(throwable.getMessage());
        } else {
            showErrorMessage(Constants.Messages.LOGIN_FAILED);
        }
        
        // Clear password field on error
        passwordField.clear();
        passwordField.requestFocus();
    }
    
    /**
     * Handles register link click
     * 
     * @param event Action event
     */
    @FXML
    private void handleRegister(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.FXML.REGISTER));
            Parent root = loader.load();
            
            Stage stage = (Stage) registerLink.getScene().getWindow();
            Scene scene = new Scene(root, 900, 800);
            scene.getStylesheets().add(getClass().getResource(Constants.CSS.MAIN_STYLESHEET).toExternalForm());
            
            stage.setScene(scene);
            stage.setTitle(Constants.APP_NAME + " - Register");
            
            logger.info("Navigated to registration screen");
            
        } catch (IOException e) {
            ErrorHandler.handleException(e, "Navigation");
        }
    }
    
    /**
     * Navigates to the appropriate dashboard based on user role
     * 
     * @param user Logged in user
     */
    private void navigateToDashboard(User user) {
        try {
            String fxmlPath = getDashboardPath(user);
            String title = getDashboardTitle(user);
            
            // Load dashboard
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            
            // Pass user to dashboard controller
            Object controller = loader.getController();
            if (controller instanceof DashboardController) {
                ((DashboardController) controller).setCurrentUser(user);
            } else if (controller instanceof BaseController) {
                ((BaseController) controller).setCurrentUser(user);
            }
            
            // Setup and show stage
            Stage stage = (Stage) loginButton.getScene().getWindow();
            Scene scene = new Scene(root, Constants.Window.DASHBOARD_WIDTH, Constants.Window.DASHBOARD_HEIGHT);
            scene.getStylesheets().add(getClass().getResource(Constants.CSS.MAIN_STYLESHEET).toExternalForm());
            
            stage.setScene(scene);
            stage.setTitle(title);
            stage.setMaximized(true);
            
            logger.info("Successfully navigated to dashboard for role: " + user.getRole());
            
        } catch (IOException e) {
            logger.severe("Failed to load dashboard: " + e.getMessage());
            ErrorHandler.showError("Navigation Error", 
                "Failed to load dashboard. Please try logging in again.");
            
            // Logout user on dashboard load failure
            try {
                authService.logout();
            } catch (AuthenticationException ex) {
                logger.warning("Failed to logout after dashboard load failure");
            }
        }
    }
    
    /**
     * Gets the dashboard FXML path for a user role
     * 
     * @param user User
     * @return FXML path
     */
    private String getDashboardPath(User user) {
        switch (user.getRole()) {
            case ADMIN:
                return Constants.FXML.ADMIN_DASHBOARD;
            case VOLUNTEER:
                return Constants.FXML.VOLUNTEER_DASHBOARD;
            case REQUESTER:
                return Constants.FXML.REQUESTER_DASHBOARD;
            default:
                throw new IllegalStateException("Unknown user role: " + user.getRole());
        }
    }
    
    /**
     * Gets the dashboard title for a user role
     * 
     * @param user User
     * @return Dashboard title
     */
    private String getDashboardTitle(User user) {
        switch (user.getRole()) {
            case ADMIN:
                return Constants.APP_NAME + " - Admin Dashboard";
            case VOLUNTEER:
                return Constants.APP_NAME + " - Volunteer Dashboard";
            case REQUESTER:
                return Constants.APP_NAME + " - Dashboard";
            default:
                return Constants.APP_NAME + " - Dashboard";
        }
    }
    
    /**
     * Shows an error message in the error label
     * 
     * @param message Error message to display
     */
    private void showErrorMessage(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
    
    /**
     * Clears the error message
     */
    private void clearError() {
        errorLabel.setVisible(false);
        errorLabel.setText("");
    }
    
    /**
     * Sets loading state
     * 
     * @param loading Whether loading is in progress
     */
    private void setLoading(boolean loading) {
        Platform.runLater(() -> {
            loadingIndicator.setVisible(loading);
            loginButton.setDisable(loading);
            usernameField.setDisable(loading);
            passwordField.setDisable(loading);
            registerLink.setDisable(loading);
        });
    }
}
