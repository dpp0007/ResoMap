package com.communityhub.core;

import com.communityhub.exception.*;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Centralized Error Handler
 * Provides consistent error handling and user-friendly error messages
 * 
 * Features:
 * - Standardized error dialogs
 * - Logging integration
 * - User-friendly error messages
 * - Stack trace display for debugging
 * 
 * @author ResoMap Team
 * @version 2.0
 */
public final class ErrorHandler {
    
    private static final Logger logger = Logger.getLogger(ErrorHandler.class.getName());
    private static final String APP_NAME = "ResoMap";
    
    // Private constructor prevents instantiation
    private ErrorHandler() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    /**
     * Handles an exception and shows appropriate error dialog
     * 
     * @param exception Exception to handle
     * @param context Context where error occurred (e.g., "Login", "Save Resource")
     */
    public static void handleException(Exception exception, String context) {
        logger.log(Level.SEVERE, "Error in " + context, exception);
        
        final String title = (context != null && !context.isEmpty()) ? context + " Error" : "Error";
        final String message = getUserFriendlyMessage(exception);
        
        Platform.runLater(() -> showErrorDialog(title, message, exception));
    }
    
    /**
     * Handles an exception without showing dialog (logs only)
     * 
     * @param exception Exception to handle
     * @param context Context where error occurred
     */
    public static void handleExceptionSilently(Exception exception, String context) {
        logger.log(Level.SEVERE, "Silent error in " + context, exception);
    }
    
    /**
     * Shows a simple error dialog
     * 
     * @param title Dialog title
     * @param message Error message
     */
    public static void showError(String title, String message) {
        logger.warning(title + ": " + message);
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle(APP_NAME + " - " + title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
    
    /**
     * Shows a warning dialog
     * 
     * @param title Dialog title
     * @param message Warning message
     */
    public static void showWarning(String title, String message) {
        logger.warning(title + ": " + message);
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle(APP_NAME + " - " + title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
    
    /**
     * Shows an information dialog
     * 
     * @param title Dialog title
     * @param message Information message
     */
    public static void showInfo(String title, String message) {
        logger.info(title + ": " + message);
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle(APP_NAME + " - " + title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
    
    /**
     * Shows a confirmation dialog
     * 
     * @param title Dialog title
     * @param message Confirmation message
     * @return true if user confirmed
     */
    public static boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(APP_NAME + " - " + title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
    
    /**
     * Shows an error dialog with expandable stack trace
     * 
     * @param title Dialog title
     * @param message Error message
     * @param exception Exception with stack trace
     */
    private static void showErrorDialog(String title, String message, Exception exception) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(APP_NAME + " - " + title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        // Add expandable stack trace
        if (exception != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            exception.printStackTrace(pw);
            String stackTrace = sw.toString();
            
            TextArea textArea = new TextArea(stackTrace);
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);
            
            GridPane expContent = new GridPane();
            expContent.setMaxWidth(Double.MAX_VALUE);
            expContent.add(textArea, 0, 0);
            
            alert.getDialogPane().setExpandableContent(expContent);
        }
        
        alert.showAndWait();
    }
    
    /**
     * Converts exception to user-friendly message
     * 
     * @param exception Exception to convert
     * @return User-friendly error message
     */
    private static String getUserFriendlyMessage(Exception exception) {
        if (exception instanceof AuthenticationException) {
            return exception.getMessage();
        } else if (exception instanceof InvalidInputException) {
            return "Invalid input: " + exception.getMessage();
        } else if (exception instanceof DatabaseException) {
            return "Database error: Unable to complete the operation. Please try again.";
        } else if (exception instanceof CommunityHubException) {
            return exception.getMessage();
        } else if (exception instanceof NullPointerException) {
            return "An unexpected error occurred. Some required data is missing.";
        } else if (exception instanceof IllegalArgumentException) {
            return "Invalid data provided: " + exception.getMessage();
        } else if (exception instanceof IllegalStateException) {
            return "Operation cannot be performed in current state: " + exception.getMessage();
        } else {
            return "An unexpected error occurred: " + exception.getMessage();
        }
    }
    
    /**
     * Handles database connection errors specifically
     * 
     * @param exception Database exception
     */
    public static void handleDatabaseError(DatabaseException exception) {
        logger.log(Level.SEVERE, "Database error", exception);
        showError("Database Error", 
            "Unable to connect to the database.\n\n" +
            "Please ensure:\n" +
            "• Database server is running\n" +
            "• Connection settings are correct\n" +
            "• Network connection is available\n\n" +
            "Error: " + exception.getMessage());
    }
    
    /**
     * Handles authentication errors specifically
     * 
     * @param exception Authentication exception
     */
    public static void handleAuthenticationError(AuthenticationException exception) {
        logger.log(Level.WARNING, "Authentication error", exception);
        showError("Authentication Error", exception.getMessage());
    }
    
    /**
     * Handles validation errors specifically
     * 
     * @param exception Validation exception
     */
    public static void handleValidationError(InvalidInputException exception) {
        logger.log(Level.INFO, "Validation error", exception);
        showWarning("Validation Error", exception.getMessage());
    }
}
