package com.communityhub.ui.util;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Utility class for common UI operations
 * Provides reusable dialog methods and UI helpers
 */
public class UIUtils {
    
    /**
     * Shows an error dialog with detailed information
     * @param title Dialog title
     * @param header Header text
     * @param content Content text
     * @param owner Owner stage
     */
    public static void showErrorDialog(String title, String header, String content, Stage owner) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
            
            if (owner != null) {
                alert.initOwner(owner);
            }
            
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();
        });
    }
    
    /**
     * Shows a success dialog
     * @param title Dialog title
     * @param header Header text
     * @param content Content text
     * @param owner Owner stage
     */
    public static void showSuccessDialog(String title, String header, String content, Stage owner) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
            
            if (owner != null) {
                alert.initOwner(owner);
            }
            
            alert.showAndWait();
        });
    }
    
    /**
     * Shows a confirmation dialog
     * @param title Dialog title
     * @param header Header text
     * @param content Content text
     * @param owner Owner stage
     * @return CompletableFuture with user's choice
     */
    public static CompletableFuture<Boolean> showConfirmationDialog(String title, String header, String content, Stage owner) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
            
            if (owner != null) {
                alert.initOwner(owner);
            }
            
            Optional<ButtonType> result = alert.showAndWait();
            future.complete(result.isPresent() && result.get() == ButtonType.OK);
        });
        
        return future;
    }
    
    /**
     * Shows a warning dialog
     * @param title Dialog title
     * @param header Header text
     * @param content Content text
     * @param owner Owner stage
     */
    public static void showWarningDialog(String title, String header, String content, Stage owner) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
            
            if (owner != null) {
                alert.initOwner(owner);
            }
            
            alert.showAndWait();
        });
    }
    
    /**
     * Shows a progress dialog for long-running operations
     * @param title Dialog title
     * @param header Header text
     * @param owner Owner stage
     * @return ProgressDialog instance
     */
    public static ProgressDialog showProgressDialog(String title, String header, Stage owner) {
        ProgressDialog dialog = new ProgressDialog(title, header);
        if (owner != null) {
            dialog.initOwner(owner);
        }
        
        Platform.runLater(() -> dialog.show());
        return dialog;
    }
    
    /**
     * Validates required text field
     * @param field Text field to validate
     * @param fieldName Field name for error message
     * @return true if valid
     */
    public static boolean validateRequiredField(TextField field, String fieldName) {
        if (field.getText() == null || field.getText().trim().isEmpty()) {
            field.getStyleClass().add("error");
            showValidationTooltip(field, fieldName + " is required");
            return false;
        } else {
            field.getStyleClass().remove("error");
            return true;
        }
    }
    
    /**
     * Validates email format
     * @param field Email field to validate
     * @return true if valid
     */
    public static boolean validateEmailField(TextField field) {
        String email = field.getText();
        if (email == null || email.trim().isEmpty()) {
            field.getStyleClass().add("error");
            showValidationTooltip(field, "Email is required");
            return false;
        }
        
        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            field.getStyleClass().add("error");
            showValidationTooltip(field, "Please enter a valid email address");
            return false;
        }
        
        field.getStyleClass().remove("error");
        return true;
    }
    
    /**
     * Validates numeric field
     * @param field Numeric field to validate
     * @param fieldName Field name for error message
     * @param min Minimum value
     * @param max Maximum value
     * @return true if valid
     */
    public static boolean validateNumericField(TextField field, String fieldName, int min, int max) {
        try {
            int value = Integer.parseInt(field.getText());
            if (value < min || value > max) {
                field.getStyleClass().add("error");
                showValidationTooltip(field, fieldName + " must be between " + min + " and " + max);
                return false;
            }
            field.getStyleClass().remove("error");
            return true;
        } catch (NumberFormatException e) {
            field.getStyleClass().add("error");
            showValidationTooltip(field, fieldName + " must be a valid number");
            return false;
        }
    }
    
    /**
     * Shows validation tooltip
     * @param control Control to show tooltip on
     * @param message Tooltip message
     */
    private static void showValidationTooltip(Control control, String message) {
        Tooltip tooltip = new Tooltip(message);
        tooltip.getStyleClass().add("error-tooltip");
        control.setTooltip(tooltip);
        
        // Auto-hide tooltip after 3 seconds
        Platform.runLater(() -> {
            try {
                Thread.sleep(3000);
                control.setTooltip(null);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }
    
    /**
     * Clears validation errors from a control
     * @param control Control to clear errors from
     */
    public static void clearValidationError(Control control) {
        control.getStyleClass().remove("error");
        control.setTooltip(null);
    }
    
    /**
     * Sets up real-time validation for a text field
     * @param field Text field to validate
     * @param validator Validation function
     */
    public static void setupRealTimeValidation(TextField field, java.util.function.Function<String, String> validator) {
        field.textProperty().addListener((obs, oldVal, newVal) -> {
            String errorMessage = validator.apply(newVal);
            if (errorMessage != null) {
                field.getStyleClass().add("error");
                showValidationTooltip(field, errorMessage);
            } else {
                clearValidationError(field);
            }
        });
    }
    
    /**
     * Custom progress dialog class
     */
    public static class ProgressDialog extends Dialog<Void> {
        private final ProgressIndicator progressIndicator;
        private final Label messageLabel;
        
        public ProgressDialog(String title, String header) {
            setTitle(title);
            setHeaderText(header);
            
            progressIndicator = new ProgressIndicator();
            messageLabel = new Label("Please wait...");
            
            getDialogPane().setContent(new javafx.scene.layout.VBox(10, progressIndicator, messageLabel));
            getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
            
            setResizable(false);
        }
        
        public void updateMessage(String message) {
            Platform.runLater(() -> messageLabel.setText(message));
        }
        
        public void updateProgress(double progress) {
            Platform.runLater(() -> progressIndicator.setProgress(progress));
        }
        
        public void setIndeterminate() {
            Platform.runLater(() -> progressIndicator.setProgress(-1));
        }
    }
}