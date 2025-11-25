package com.communityhub.core;

import com.communityhub.exception.DatabaseException;
import com.communityhub.model.User;
import com.communityhub.service.*;
import com.communityhub.util.SessionManager;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Base Controller - Abstract base class for all controllers
 * Implements common controller functionality and reduces code duplication
 * 
 * Features:
 * - Service injection via ServiceFactory
 * - Common error handling
 * - Navigation utilities
 * - Loading state management
 * - User session management
 * 
 * @author ResoMap Team
 * @version 2.0
 */
public abstract class BaseController {
    
    protected final Logger logger = Logger.getLogger(getClass().getName());
    protected final ServiceFactory serviceFactory;
    
    // Common services (lazy-loaded)
    protected AuthenticationService authService;
    protected ResourceService resourceService;
    protected RequestService requestService;
    protected UserService userService;
    protected NotificationService notificationService;
    protected final SessionManager sessionManager;
    
    // Current user
    protected User currentUser;
    
    /**
     * Constructor initializes service factory
     */
    protected BaseController() {
        this.serviceFactory = ServiceFactory.getInstance();
        this.sessionManager = serviceFactory.getSessionManager();
    }
    
    /**
     * Initializes services - call this in controller's initialize method
     * 
     * @throws DatabaseException if service initialization fails
     */
    protected void initializeServices() throws DatabaseException {
        this.authService = serviceFactory.getAuthenticationService();
        this.resourceService = serviceFactory.getResourceService();
        this.requestService = serviceFactory.getRequestService();
        this.userService = serviceFactory.getUserService();
        this.notificationService = serviceFactory.getNotificationService();
    }
    
    /**
     * Sets the current user for this controller
     * 
     * @param user Current logged-in user
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {
            logger.info("Current user set: " + user.getUsername() + " (Role: " + user.getRole() + ")");
            onUserSet(user);
        }
    }
    
    /**
     * Gets the current user
     * 
     * @return Current user or null if not set
     */
    protected User getCurrentUser() {
        if (currentUser == null) {
            currentUser = sessionManager.getCurrentUser();
        }
        return currentUser;
    }
    
    /**
     * Hook method called when user is set
     * Override in subclasses to perform actions when user is set
     * 
     * @param user The user that was set
     */
    protected void onUserSet(User user) {
        // Override in subclasses if needed
    }
    
    /**
     * Executes a task in background thread
     * 
     * @param <T> Return type of task
     * @param task Task to execute
     * @param onSuccess Success callback
     * @param onFailure Failure callback
     */
    protected <T> void executeAsync(Task<T> task, SuccessCallback<T> onSuccess, FailureCallback onFailure) {
        task.setOnSucceeded(event -> {
            if (onSuccess != null) {
                Platform.runLater(() -> onSuccess.onSuccess(task.getValue()));
            }
        });
        
        task.setOnFailed(event -> {
            Throwable exception = task.getException();
            logger.log(Level.SEVERE, "Async task failed", exception);
            if (onFailure != null) {
                Platform.runLater(() -> onFailure.onFailure(exception));
            }
        });
        
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
    
    /**
     * Executes a simple background task
     * 
     * @param <T> Return type
     * @param taskLogic Task logic
     * @param onSuccess Success callback
     */
    protected <T> void executeAsync(TaskLogic<T> taskLogic, SuccessCallback<T> onSuccess) {
        Task<T> task = new Task<T>() {
            @Override
            protected T call() throws Exception {
                return taskLogic.execute();
            }
        };
        
        executeAsync(task, onSuccess, throwable -> 
            ErrorHandler.handleException((Exception) throwable, "Background Task"));
    }
    
    /**
     * Shows error dialog
     * 
     * @param title Error title
     * @param message Error message
     */
    protected void showError(String title, String message) {
        ErrorHandler.showError(title, message);
    }
    
    /**
     * Shows warning dialog
     * 
     * @param title Warning title
     * @param message Warning message
     */
    protected void showWarning(String title, String message) {
        ErrorHandler.showWarning(title, message);
    }
    
    /**
     * Shows info dialog
     * 
     * @param title Info title
     * @param message Info message
     */
    protected void showInfo(String title, String message) {
        ErrorHandler.showInfo(title, message);
    }
    
    /**
     * Shows confirmation dialog
     * 
     * @param title Confirmation title
     * @param message Confirmation message
     * @return true if user confirmed
     */
    protected boolean showConfirmation(String title, String message) {
        return ErrorHandler.showConfirmation(title, message);
    }
    
    /**
     * Navigates to a different view
     * 
     * @param fxmlPath Path to FXML file
     * @param title Window title
     * @param button Button to get current stage from
     * @throws IOException if FXML loading fails
     */
    protected void navigateTo(String fxmlPath, String title, Button button) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        
        // Pass current user to new controller if it implements setCurrentUser
        Object controller = loader.getController();
        if (controller instanceof BaseController) {
            ((BaseController) controller).setCurrentUser(currentUser);
        }
        
        Stage stage = (Stage) button.getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        
        stage.setScene(scene);
        stage.setTitle("ResoMap - " + title);
    }
    
    /**
     * Loads a view into a container
     * 
     * @param fxmlPath Path to FXML file
     * @return Loaded parent node
     * @throws IOException if FXML loading fails
     */
    protected Parent loadView(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent view = loader.load();
        
        // Pass current user to loaded controller
        Object controller = loader.getController();
        if (controller instanceof BaseController) {
            ((BaseController) controller).setCurrentUser(currentUser);
        }
        
        return view;
    }
    
    /**
     * Handles logout action
     */
    @FXML
    protected void handleLogout() {
        try {
            if (showConfirmation("Logout", "Are you sure you want to logout?")) {
                authService.logout();
                navigateToLogin();
            }
        } catch (Exception e) {
            ErrorHandler.handleException(e, "Logout");
        }
    }
    
    /**
     * Navigates to login screen
     */
    protected void navigateToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) Stage.getWindows().stream()
                .filter(window -> window instanceof Stage)
                .findFirst()
                .orElse(null);
            
            if (stage != null) {
                Scene scene = new Scene(root, 800, 600);
                scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                
                stage.setScene(scene);
                stage.setTitle("ResoMap - Login");
                stage.setMaximized(false);
                stage.centerOnScreen();
            }
        } catch (IOException e) {
            ErrorHandler.handleException(e, "Navigation");
        }
    }
    
    /**
     * Validates that user is logged in
     * 
     * @return true if user is logged in
     */
    protected boolean validateUserLoggedIn() {
        if (getCurrentUser() == null) {
            showError("Authentication Required", "Please log in to continue.");
            navigateToLogin();
            return false;
        }
        return true;
    }
    
    /**
     * Validates that user has admin privileges
     * 
     * @return true if user is admin
     */
    protected boolean validateAdminAccess() {
        if (!validateUserLoggedIn()) {
            return false;
        }
        
        if (!currentUser.isAdmin()) {
            showError("Access Denied", "This feature requires administrator privileges.");
            return false;
        }
        
        return true;
    }
    
    // Functional interfaces for callbacks
    
    @FunctionalInterface
    protected interface SuccessCallback<T> {
        void onSuccess(T result);
    }
    
    @FunctionalInterface
    protected interface FailureCallback {
        void onFailure(Throwable throwable);
    }
    
    @FunctionalInterface
    protected interface TaskLogic<T> {
        T execute() throws Exception;
    }
}
