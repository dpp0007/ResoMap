package com.communityhub.ui.controllers;

import com.communityhub.model.*;
import com.communityhub.service.RequestService;
import com.communityhub.service.ResourceService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for the New Request modal dialog
 */
public class NewRequestController implements Initializable {
    
    private static final Logger logger = Logger.getLogger(NewRequestController.class.getName());
    
    @FXML private Button closeButton;
    @FXML private ComboBox<Resource> resourceComboBox;
    @FXML private TextArea descriptionTextArea;
    @FXML private ToggleButton urgencyLowBtn;
    @FXML private ToggleButton urgencyMediumBtn;
    @FXML private ToggleButton urgencyHighBtn;
    @FXML private ToggleButton urgencyCriticalBtn;
    @FXML private ToggleGroup urgencyGroup;
    @FXML private TextField quantityField;
    @FXML @SuppressWarnings("unused") // Used by FXML for action handlers
    private Button decrementBtn;
    @FXML @SuppressWarnings("unused") // Used by FXML for action handlers
    private Button incrementBtn;
    @FXML private Button submitBtn;
    @FXML @SuppressWarnings("unused") // Used by FXML for action handlers
    private Button clearButton;
    @FXML private Label statusLabel;
    
    private User currentUser;
    private ResourceService resourceService;
    private RequestService requestService;
    private int quantity = 1;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            resourceService = new ResourceService();
            requestService = new RequestService();
            
            // Set initial quantity
            quantityField.setText("1");
            
            // Select Low urgency by default
            urgencyLowBtn.setSelected(true);
            
            // Load resources
            loadResources();
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to initialize new request dialog", e);
            showError("Failed to initialize: " + e.getMessage());
        }
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    private void loadResources() {
        Task<List<Resource>> task = new Task<List<Resource>>() {
            @Override
            protected List<Resource> call() throws Exception {
                return resourceService.getAllResources();
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    List<Resource> resources = getValue();
                    resourceComboBox.setItems(FXCollections.observableArrayList(resources));
                });
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> showError("Failed to load resources: " + getException().getMessage()));
            }
        };
        
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
    
    @FXML @SuppressWarnings("unused") // Used by FXML for button actions
    private void handleBack() {
        closeDialog();
    }
    
    @FXML @SuppressWarnings("unused") // Used by FXML for button actions
    private void handleDecrement() {
        if (quantity > 1) {
            quantity--;
            quantityField.setText(String.valueOf(quantity));
        }
    }
    
    @FXML @SuppressWarnings("unused") // Used by FXML for button actions
    private void handleIncrement() {
        if (quantity < 100) {
            quantity++;
            quantityField.setText(String.valueOf(quantity));
        }
    }
    
    @FXML @SuppressWarnings("unused") // Used by FXML for button actions
    private void handleSubmit() {
        Resource selectedResource = resourceComboBox.getValue();
        String description = descriptionTextArea.getText().trim();
        
        // Validate input
        if (selectedResource == null) {
            showError("Please select a resource category.");
            return;
        }
        
        if (description.isEmpty()) {
            showError("Please provide a description of your need.");
            return;
        }
        
        if (urgencyGroup.getSelectedToggle() == null) {
            showError("Please select an urgency level.");
            return;
        }
        
        if (currentUser == null) {
            showError("User session expired. Please log in again.");
            return;
        }
        
        submitBtn.setDisable(true);
        
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // Create new request
                Request request = new Request();
                request.setRequesterId(currentUser.getUserId());
                request.setResourceId(selectedResource.getResourceId());
                request.setDescription(description);
                request.setQuantityRequested(quantity);
                request.setUrgencyLevel(getSelectedUrgencyLevel());
                request.setStatus(RequestStatus.PENDING);
                
                requestService.createRequest(request);
                return null;
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    showSuccess("Your request has been submitted successfully! You will be notified when a volunteer is assigned.");
                    
                    // Close dialog after 2 seconds
                    new Thread(() -> {
                        try {
                            Thread.sleep(2000);
                            Platform.runLater(() -> closeDialog());
                        } catch (InterruptedException e) {
                            logger.log(Level.WARNING, "Sleep interrupted", e);
                        }
                    }).start();
                });
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    showError("Failed to submit request: " + getException().getMessage());
                    submitBtn.setDisable(false);
                });
            }
        };
        
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
    
    @FXML @SuppressWarnings("unused") // Used by FXML for button actions
    private void handleClear() {
        resourceComboBox.setValue(null);
        descriptionTextArea.clear();
        urgencyLowBtn.setSelected(true);
        quantity = 1;
        quantityField.setText("1");
        statusLabel.setVisible(false);
    }
    
    private UrgencyLevel getSelectedUrgencyLevel() {
        Toggle selected = urgencyGroup.getSelectedToggle();
        if (selected == urgencyLowBtn) {
            return UrgencyLevel.LOW;
        } else if (selected == urgencyMediumBtn) {
            return UrgencyLevel.MEDIUM;
        } else if (selected == urgencyHighBtn) {
            return UrgencyLevel.HIGH;
        } else if (selected == urgencyCriticalBtn) {
            return UrgencyLevel.CRITICAL;
        }
        return UrgencyLevel.MEDIUM;
    }
    
    private void showError(String message) {
        statusLabel.setText("❌ " + message);
        statusLabel.setStyle("-fx-background-color: #FFE5E5; -fx-text-fill: #E74C3C;");
        statusLabel.setVisible(true);
        statusLabel.setManaged(true);
    }
    
    private void showSuccess(String message) {
        statusLabel.setText("✅ " + message);
        statusLabel.setStyle("-fx-background-color: #E8F8F0; -fx-text-fill: #2ECC71;");
        statusLabel.setVisible(true);
        statusLabel.setManaged(true);
    }
    
    /**
     * Pre-select a specific resource when opening the dialog
     */
    public void preSelectResource(Resource resource) {
        if (resource != null && resourceComboBox != null) {
            Platform.runLater(() -> {
                resourceComboBox.setValue(resource);
                // Optionally focus on description field
                if (descriptionTextArea != null) {
                    descriptionTextArea.requestFocus();
                }
            });
        }
    }
    
    private void closeDialog() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}
