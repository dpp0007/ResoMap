package com.communityhub.ui.controllers;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.communityhub.model.Request;
import com.communityhub.model.RequestStatus;
import com.communityhub.model.Resource;
import com.communityhub.model.UrgencyLevel;
import com.communityhub.model.User;
import com.communityhub.service.AuthenticationService;
import com.communityhub.service.RequestService;
import com.communityhub.service.ResourceService;
import com.communityhub.util.SessionManager;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * Controller for the Request Overview page
 */
public class RequestOverviewController implements Initializable {
    
    private static final Logger logger = Logger.getLogger(RequestOverviewController.class.getName());
    
    @FXML private ComboBox<String> statusFilter;
    @FXML private ComboBox<String> urgencyFilter;
    @FXML private Button refreshBtn;
    @FXML private TableView<Request> requestsTable;
    @FXML private TableColumn<Request, String> requestIdCol;
    @FXML private TableColumn<Request, String> requesterCol;
    @FXML private TableColumn<Request, String> resourceCol;
    @FXML private TableColumn<Request, String> descriptionCol;
    @FXML private TableColumn<Request, String> urgencyCol;
    @FXML private TableColumn<Request, String> statusCol;
    @FXML private TableColumn<Request, String> volunteerCol;
    @FXML private TableColumn<Request, String> createdCol;
    @FXML private TableColumn<Request, Void> actionsCol;
    
    // Statistics labels
    @FXML private Label totalRequestsLabel;
    @FXML private Label pendingRequestsLabel;
    @FXML private Label assignedRequestsLabel;
    @FXML private Label completedRequestsLabel;
    
    private RequestService requestService;
    private ResourceService resourceService;
    @SuppressWarnings("unused") // May be used for future authentication features
    private AuthenticationService authService;
    private User currentUser;
    private ObservableList<Request> allRequests;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            authService = new AuthenticationService();
            requestService = new RequestService();
            resourceService = new ResourceService();
            
            // Get current user from session
            SessionManager sessionManager = SessionManager.getInstance();
            currentUser = sessionManager.getCurrentUser();
            
            if (currentUser == null) {
                logger.log(Level.SEVERE, "No current user found in session");
                showError("Authentication Error", "User session not found. Please login again.");
                return;
            }
            
            setupUI();
            setupTableColumns();
            loadUserRequests();
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to initialize request overview controller", e);
            showError("Initialization Error", "Failed to initialize request overview: " + e.getMessage());
        }
    }
    
    private void setupUI() {
        // Check if required UI elements are loaded from FXML
        if (statusFilter == null || urgencyFilter == null) {
            logger.log(Level.SEVERE, "Filter UI elements failed to load from FXML");
            showError("UI Error", "Failed to initialize filter controls. Please check the FXML file.");
            return;
        }
        
        // Setup status filter
        statusFilter.setItems(FXCollections.observableArrayList(
            "All Status", "Pending", "Assigned", "In Progress", "Completed", "Cancelled"
        ));
        statusFilter.setValue("All Status");
        statusFilter.setOnAction(e -> filterRequests());
        
        // Setup urgency filter
        urgencyFilter.setItems(FXCollections.observableArrayList(
            "All Urgency", "Critical", "High", "Medium", "Low"
        ));
        urgencyFilter.setValue("All Urgency");
        urgencyFilter.setOnAction(e -> filterRequests());
    }
    
    private void setupTableColumns() {
        // Check if all required table columns are loaded from FXML
        if (requestsTable == null || requestIdCol == null || requesterCol == null || 
            resourceCol == null || descriptionCol == null || urgencyCol == null || 
            statusCol == null || volunteerCol == null || createdCol == null || actionsCol == null) {
            logger.log(Level.SEVERE, "One or more table columns failed to load from FXML");
            showError("UI Error", "Failed to initialize table columns. Please check the FXML file.");
            return;
        }
        
        // Request ID column
        requestIdCol.setCellValueFactory(new PropertyValueFactory<>("requestId"));
        requestIdCol.setCellFactory(col -> new TableCell<Request, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.substring(0, Math.min(8, item.length()))); // Short ID
                }
            }
        });
        
        // Requester column
        requesterCol.setCellValueFactory(data -> {
            Request request = data.getValue();
            String requesterName = getRequesterName(request.getRequesterId());
            return new SimpleStringProperty(requesterName);
        });
        
        // Resource column
        resourceCol.setCellValueFactory(data -> {
            Request request = data.getValue();
            String resourceName = getResourceName(request.getResourceId());
            return new SimpleStringProperty(resourceName);
        });
        
        // Description column
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionCol.setCellFactory(col -> new TableCell<Request, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String truncated = item.length() > 50 ? item.substring(0, 50) + "..." : item;
                    setText(truncated);
                    setTooltip(new Tooltip(item));
                }
            }
        });
        
        // Urgency column
        urgencyCol.setCellValueFactory(data -> {
            UrgencyLevel urgency = data.getValue().getUrgencyLevel();
            return new SimpleStringProperty(formatUrgency(urgency));
        });
        
        // Status column
        statusCol.setCellValueFactory(data -> {
            RequestStatus status = data.getValue().getStatus();
            return new SimpleStringProperty(formatStatus(status));
        });
        
        // Volunteer column
        volunteerCol.setCellValueFactory(data -> {
            Request request = data.getValue();
            String volunteerName = getVolunteerName(request.getVolunteerId());
            return new SimpleStringProperty(volunteerName);
        });
        
        // Created date column
        createdCol.setCellValueFactory(data -> {
            LocalDateTime created = data.getValue().getCreatedAt();
            String formatted = created.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
            return new SimpleStringProperty(formatted);
        });
        
        // Actions column
        actionsCol.setCellFactory(col -> new TableCell<Request, Void>() {
            private final Button cancelBtn = new Button("Cancel");
            
            {
                cancelBtn.getStyleClass().add("danger-button");
                cancelBtn.setOnAction(e -> {
                    Request request = getTableView().getItems().get(getIndex());
                    handleCancelRequest(request);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Request request = getTableView().getItems().get(getIndex());
                    if (request.getStatus() != RequestStatus.CANCELLED && 
                        request.getStatus() != RequestStatus.COMPLETED) {
                        setGraphic(cancelBtn);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });
    }
    
    private void loadUserRequests() {
        try {
            List<Request> userRequests = requestService.getRequestsByUser(currentUser.getUserId());
            allRequests = FXCollections.observableArrayList(userRequests);
            requestsTable.setItems(allRequests);
            updateStatistics();
            
            logger.log(Level.INFO, "Loaded {0} requests for user: {1}", new Object[]{userRequests.size(), currentUser.getUsername()});
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to load user requests", e);
            showError("Error", "Failed to load requests: " + e.getMessage());
        }
    }
    
    private void filterRequests() {
        if (allRequests == null) return;
        
        String statusFilterValue = this.statusFilter.getValue();
        String urgencyFilterValue = this.urgencyFilter.getValue();
        
        List<Request> filtered = allRequests.stream()
            .filter(request -> {
                // Filter by status
                if (!"All Status".equals(statusFilterValue)) {
                    boolean statusMatches = false;
                    switch (statusFilterValue) {
                        case "Pending":
                            statusMatches = request.getStatus() == RequestStatus.PENDING;
                            break;
                        case "Assigned":
                            statusMatches = request.getStatus() == RequestStatus.ASSIGNED;
                            break;
                        case "In Progress":
                            statusMatches = request.getStatus() == RequestStatus.IN_PROGRESS;
                            break;
                        case "Completed":
                            statusMatches = request.getStatus() == RequestStatus.COMPLETED;
                            break;
                        case "Cancelled":
                            statusMatches = request.getStatus() == RequestStatus.CANCELLED;
                            break;
                    }
                    if (!statusMatches) return false;
                }
                
                // Filter by urgency
                if (!"All Urgency".equals(urgencyFilterValue)) {
                    boolean urgencyMatches = false;
                    switch (urgencyFilterValue) {
                        case "Critical":
                            urgencyMatches = request.getUrgencyLevel() == UrgencyLevel.CRITICAL;
                            break;
                        case "High":
                            urgencyMatches = request.getUrgencyLevel() == UrgencyLevel.HIGH;
                            break;
                        case "Medium":
                            urgencyMatches = request.getUrgencyLevel() == UrgencyLevel.MEDIUM;
                            break;
                        case "Low":
                            urgencyMatches = request.getUrgencyLevel() == UrgencyLevel.LOW;
                            break;
                    }
                    if (!urgencyMatches) return false;
                }
                
                return true;
            })
            .collect(Collectors.toList());
        
        requestsTable.setItems(FXCollections.observableArrayList(filtered));
        updateStatistics();
    }
    
    private void updateStatistics() {
        if (allRequests == null) {
            totalRequestsLabel.setText("0");
            pendingRequestsLabel.setText("0");
            assignedRequestsLabel.setText("0");
            completedRequestsLabel.setText("0");
            return;
        }
        
        long total = allRequests.size();
        long pending = allRequests.stream().filter(r -> r.getStatus() == RequestStatus.PENDING).count();
        long assigned = allRequests.stream().filter(r -> r.getStatus() == RequestStatus.ASSIGNED).count();
        long completed = allRequests.stream().filter(r -> r.getStatus() == RequestStatus.COMPLETED).count();
        
        totalRequestsLabel.setText(String.valueOf(total));
        pendingRequestsLabel.setText(String.valueOf(pending));
        assignedRequestsLabel.setText(String.valueOf(assigned));
        completedRequestsLabel.setText(String.valueOf(completed));
    }
    
    private String formatUrgency(UrgencyLevel urgency) {
        switch (urgency) {
            case CRITICAL:
                return "🔴 Critical";
            case HIGH:
                return "🟠 High";
            case MEDIUM:
                return "🟡 Medium";
            case LOW:
                return "🟢 Low";
            default:
                return urgency.name();
        }
    }
    
    private String formatStatus(RequestStatus status) {
        switch (status) {
            case PENDING:
                return "⏳ Pending";
            case ASSIGNED:
                return "👤 Assigned";
            case IN_PROGRESS:
                return "🔄 In Progress";
            case COMPLETED:
                return "✅ Completed";
            case CANCELLED:
                return "❌ Cancelled";
            default:
                return status.name();
        }
    }
    
    private String getRequesterName(String requesterId) {
        try {
            // Use DAO directly since AuthenticationService doesn't have findUserById
            com.communityhub.dao.UserDAO userDAO = new com.communityhub.dao.UserDAO();
            User user = userDAO.read(requesterId);
            return user != null ? user.getUsername() : "Unknown User";
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to get requester name for ID: " + requesterId, e);
            return "Unknown User";
        }
    }
    
    private String getResourceName(String resourceId) {
        try {
            // Check if ResourceService has the correct method
            Resource resource = resourceService.getResource(resourceId);
            return resource != null ? resource.getName() : "Unknown Resource";
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to get resource name for ID: " + resourceId, e);
            return "Unknown Resource";
        }
    }
    
    private String getVolunteerName(String volunteerId) {
        if (volunteerId == null) {
            return "Not Assigned";
        }
        try {
            com.communityhub.dao.UserDAO userDAO = new com.communityhub.dao.UserDAO();
            User volunteer = userDAO.read(volunteerId);
            return volunteer != null ? volunteer.getUsername() : "Unknown Volunteer";
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to get volunteer name for ID: " + volunteerId, e);
            return "Unknown Volunteer";
        }
    }
    
    private void handleCancelRequest(Request request) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Cancel Request");
        confirmation.setHeaderText("Are you sure you want to cancel this request?");
        confirmation.setContentText("Request ID: " + request.getRequestId() + "\n" +
                                   "Resource: " + getResourceName(request.getResourceId()));
        
        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Update request status to cancelled
                request.setStatus(RequestStatus.CANCELLED);
                requestService.updateRequest(request);
                
                // Refresh the table
                loadUserRequests();
                
                showInfo("Success", "Request cancelled successfully.");
                
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Failed to cancel request", e);
                showError("Error", "Failed to cancel request: " + e.getMessage());
            }
        }
    }
    
    @FXML @SuppressWarnings("unused") // Used by FXML for button actions
    private void handleRefresh() {
        loadUserRequests();
        showInfo("Refreshed", "Request data has been refreshed.");
    }
    
    @FXML @SuppressWarnings("unused") // Used by FXML for button actions
    private void handleCreateNewRequest() {
        try {
            // Load the enhanced new request dialog
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/new-request-enhanced.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Create New Request");
            stage.setScene(new Scene(root));
            stage.setResizable(true);
            stage.setMaximized(true); // Full screen
            
            // Set the stage to be modal
            Stage currentStage = (Stage) refreshBtn.getScene().getWindow();
            stage.initOwner(currentStage);
            stage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            
            stage.showAndWait();
            
            // Refresh requests after dialog closes
            loadUserRequests();
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to open new request dialog", e);
            showError("Error", "Failed to open new request dialog: " + e.getMessage());
        }
    }
    
    private void showError(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
    
    private void showInfo(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}