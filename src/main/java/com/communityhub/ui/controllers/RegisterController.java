package com.communityhub.ui.controllers;

import com.communityhub.exception.DatabaseException;
import com.communityhub.exception.InvalidInputException;
import com.communityhub.model.User;
import com.communityhub.model.UserRole;
import com.communityhub.service.AuthenticationService;
import javafx.application.Platform;
import javafx.concurrent.Task;
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
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Controller for the registration screen
 */
public class RegisterController implements Initializable {
    
    private static final Logger logger = Logger.getLogger(RegisterController.class.getName());
    
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private Button registerButton;
    @FXML private Hyperlink loginLink;
    @FXML private Label errorLabel;
    @FXML private Label successLabel;
    @FXML private ProgressIndicator loadingIndicator;
    
    private AuthenticationService authService;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            authService = new AuthenticationService();
            setupComponents();
            setupEventHandlers();
            setupValidation();
        } catch (DatabaseException e) {
            logger.log(Level.SEVERE, "Failed to initialize authentication service", e);
            showError("System initialization failed. Please restart the application.");
        }
    }
    
    /**
     * Sets up UI components
     */
    private void setupComponents() {
        // Populate role combo box with user-friendly descriptions (exclude ADMIN for security)
        roleComboBox.getItems().addAll(
            "Help others in my community (Volunteer)",
            "Find help and resources (Requester)"
        );
        roleComboBox.setValue("Find help and resources (Requester)"); // Default selection
    }
    
    /**
     * Sets up event handlers
     */
    private void setupEventHandlers() {
        // Enter key navigation
        usernameField.setOnAction(e -> emailField.requestFocus());
        emailField.setOnAction(e -> passwordField.requestFocus());
        passwordField.setOnAction(e -> confirmPasswordField.requestFocus());
        confirmPasswordField.setOnAction(e -> handleRegister(null));
        
        // Clear messages when user starts typing
        usernameField.textProperty().addListener((obs, oldVal, newVal) -> clearMessages());
        emailField.textProperty().addListener((obs, oldVal, newVal) -> clearMessages());
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> clearMessages());
        confirmPasswordField.textProperty().addListener((obs, oldVal, newVal) -> clearMessages());
    }
    
    /**
     * Sets up input validation
     */
    private void setupValidation() {
        // We'll handle button state manually in setLoading method
        // to avoid binding conflicts
    }
    
    /**
     * Handles registration button click
     * @param event Action event
     */
    @FXML
    private void handleRegister(ActionEvent event) {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String roleSelection = roleComboBox.getValue();
        
        // Basic validation
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || 
            confirmPassword.isEmpty() || roleSelection == null) {
            showError("Please fill in all fields.");
            return;
        }
        
        // Map user-friendly role description to UserRole enum
        UserRole role = null;
        if ("Help others in my community (Volunteer)".equals(roleSelection)) {
            role = UserRole.VOLUNTEER;
        } else if ("Find help and resources (Requester)".equals(roleSelection)) {
            role = UserRole.REQUESTER;
        }
        
        if (role == null) {
            showError("Please select a valid role.");
            return;
        }
        
        // Make role effectively final for use in inner class
        final UserRole finalRole = role;
        
        // Show loading indicator
        setLoading(true);
        clearMessages();
        
        // Create background task for registration
        Task<User> registerTask = new Task<User>() {
            @Override
            protected User call() throws Exception {
                return authService.register(username, email, password, confirmPassword, finalRole);
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    setLoading(false);
                    User user = getValue();
                    if (user != null) {
                        logger.info("Registration successful for user: " + user.getUsername());
                        showSuccess("Account created successfully! You can now log in with your credentials.");
                        
                        // Clear form
                        clearForm();
                        
                        // Auto-navigate to login after 2 seconds
                        Platform.runLater(() -> {
                            try {
                                Thread.sleep(2000);
                                handleBackToLogin(null);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        });
                    }
                });
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    setLoading(false);
                    Throwable exception = getException();
                    
                    if (exception instanceof InvalidInputException) {
                        showError(((InvalidInputException) exception).getUserMessage());
                    } else if (exception instanceof DatabaseException) {
                        showError("System error occurred. Please try again later.");
                        logger.log(Level.SEVERE, "Database error during registration", exception);
                    } else {
                        showError("Registration failed. Please try again.");
                        logger.log(Level.SEVERE, "Unexpected error during registration", exception);
                    }
                });
            }
        };
        
        // Run registration task in background thread
        Thread registerThread = new Thread(registerTask);
        registerThread.setDaemon(true);
        registerThread.start();
    }
    
    /**
     * Handles back to login link click
     * @param event Action event
     */
    @FXML
    private void handleBackToLogin(ActionEvent event) {
        try {
            // Load login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) loginLink.getScene().getWindow();
            Scene scene = new Scene(root, 900, 800);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            
            stage.setScene(scene);
            stage.setTitle("Community Resource Hub - Login");
            
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load login screen", e);
            showError("Failed to open login screen.");
        }
    }
    
    /**
     * Shows an error message
     * @param message Error message to display
     */
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        successLabel.setVisible(false);
    }
    
    /**
     * Shows a success message
     * @param message Success message to display
     */
    private void showSuccess(String message) {
        successLabel.setText(message);
        successLabel.setVisible(true);
        errorLabel.setVisible(false);
    }
    
    /**
     * Clears all messages
     */
    private void clearMessages() {
        errorLabel.setVisible(false);
        successLabel.setVisible(false);
    }
    
    /**
     * Clears the registration form
     */
    private void clearForm() {
        usernameField.clear();
        emailField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        roleComboBox.setValue("Find help and resources (Requester)");
    }
    
    /**
     * Sets loading state
     * @param loading Whether loading is in progress
     */
    private void setLoading(boolean loading) {
        loadingIndicator.setVisible(loading);
        
        // Only disable controls during loading, not based on field validation
        // (We'll handle validation in the handleRegister method)
        
        registerButton.setDisable(loading);
        usernameField.setDisable(loading);
        emailField.setDisable(loading);
        passwordField.setDisable(loading);
        confirmPasswordField.setDisable(loading);
        roleComboBox.setDisable(loading);
        loginLink.setDisable(loading);
    }
}