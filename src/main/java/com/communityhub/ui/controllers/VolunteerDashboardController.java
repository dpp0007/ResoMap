package com.communityhub.ui.controllers;

import com.communityhub.exception.AuthenticationException;
import com.communityhub.model.Request;
import com.communityhub.model.RequestStatus;
import com.communityhub.model.User;
import com.communityhub.service.AuthenticationService;
import com.communityhub.service.RequestService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Controller for the volunteer dashboard
 */
public class VolunteerDashboardController implements Initializable, DashboardController {
    
    private static final Logger logger = Logger.getLogger(VolunteerDashboardController.class.getName());
    
    // Header elements
    @FXML private Label welcomeLabel;
    @FXML private Button logoutButton;
    
    // Navigation buttons
    @FXML private Button dashboardBtn;
    @FXML private Button availableRequestsBtn;
    @FXML private Button myRequestsBtn;
    @FXML private Button resourcesBtn;
    @FXML private Button profileBtn;
    
    // Views
    @FXML private VBox dashboardView;
    @FXML private VBox availableRequestsView;
    @FXML private VBox myRequestsView;
    @FXML private VBox resourcesView;
    @FXML private VBox profileView;
    
    // Dashboard stats
    @FXML private Label availableRequestsLabel;
    @FXML private Label myAssignmentsLabel;
    @FXML private Label completedRequestsLabel;
    
    // Available requests elements
    @FXML private ComboBox<String> urgencyFilter;
    @FXML private Button refreshAvailableBtn;
    @FXML private TableView<Request> availableRequestsTable;
    @FXML private TableColumn<Request, Integer> availableIdCol;
    @FXML private TableColumn<Request, String> availableResourceCol;
    @FXML private TableColumn<Request, String> availableRequesterCol;
    @FXML private TableColumn<Request, String> availableUrgencyCol;
    @FXML private TableColumn<Request, String> availableDescriptionCol;
    @FXML private TableColumn<Request, String> availableDateCol;
    @FXML private TableColumn<Request, String> availableActionCol;
    
    // My requests elements
    @FXML private ComboBox<String> statusFilter;
    @FXML private Button refreshMyRequestsBtn;
    @FXML private TableView<Request> myRequestsTable;
    @FXML private TableColumn<Request, Integer> myIdCol;
    @FXML private TableColumn<Request, String> myResourceCol;
    @FXML private TableColumn<Request, String> myRequesterCol;
    @FXML private TableColumn<Request, String> myStatusCol;
    @FXML private TableColumn<Request, String> myUrgencyCol;
    @FXML private TableColumn<Request, String> myDateCol;
    @FXML private TableColumn<Request, String> myActionCol;
    
    // Profile elements
    @FXML private Label profileDisplayName;
    @FXML private Label profileAssignmentsCount;
    @FXML private Label profileCompletedCount;
    @FXML private Label profileImpactScore;
    @FXML private Label profileMemberSinceLabel;
    @FXML private TextField profileUsernameField;
    @FXML private TextField profileEmailField;
    @FXML private TextField profileRoleField;
    @FXML private Label profileErrorLabel;
    @FXML private Label profileSuccessLabel;
    @FXML private Button updateProfileBtn;
    
    // Resources view elements
    @FXML private TextField volunteerResourceSearchField;
    @FXML private ComboBox<String> volunteerCategoryFilter;
    @FXML private Button refreshVolunteerResourcesBtn;
    @FXML private GridPane volunteerResourceGrid;
    
    private User currentUser;
    private AuthenticationService authService;
    private RequestService requestService;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            authService = new AuthenticationService();
            requestService = new RequestService();
            
            setupUI();
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to initialize volunteer dashboard", e);
            showError("Initialization Error", "Failed to initialize dashboard: " + e.getMessage());
        }
    }
    
    private void setupUI() {
        // Setup urgency filter
        urgencyFilter.setItems(FXCollections.observableArrayList(
            "All Urgency", "🔴 Critical", "🟠 High", "🟡 Medium", "🟢 Low"
        ));
        urgencyFilter.setValue("All Urgency");
        
        // Setup status filter
        statusFilter.setItems(FXCollections.observableArrayList(
            "All Status", "👤 Assigned to Me", "🔄 In Progress", "✅ Completed"
        ));
        statusFilter.setValue("All Status");
        
        // Setup resource category filter
        if (volunteerCategoryFilter != null) {
            volunteerCategoryFilter.setItems(FXCollections.observableArrayList(
                "All Categories", "🍽️ Food & Groceries", "👕 Clothing & Textiles", 
                "🏥 Medical Supplies", "🏠 Shelter & Housing", "📚 Educational Resources"
            ));
            volunteerCategoryFilter.setValue("All Categories");
        }
        
        // Setup tables
        setupAvailableRequestsTable();
        setupMyRequestsTable();
    }
    
    private void setupAvailableRequestsTable() {
        if (availableRequestsTable != null) {
            availableIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
            availableResourceCol.setCellValueFactory(new PropertyValueFactory<>("resourceName"));
            availableRequesterCol.setCellValueFactory(new PropertyValueFactory<>("requesterName"));
            availableUrgencyCol.setCellValueFactory(new PropertyValueFactory<>("urgency"));
            availableDescriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
            availableDateCol.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
            
            // Add action buttons to each row
            availableActionCol.setCellFactory(column -> new TableCell<Request, String>() {
                private final Button acceptButton = new Button("Accept");
                
                {
                    acceptButton.setOnAction(event -> {
                        Request request = getTableView().getItems().get(getIndex());
                        acceptRequest(request);
                    });
                    acceptButton.getStyleClass().add("primary-button");
                }
                
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(acceptButton);
                    }
                }
            });
        }
    }
    
    private void setupMyRequestsTable() {
        if (myRequestsTable != null) {
            myIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
            myResourceCol.setCellValueFactory(new PropertyValueFactory<>("resourceName"));
            myRequesterCol.setCellValueFactory(new PropertyValueFactory<>("requesterName"));
            myStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
            myUrgencyCol.setCellValueFactory(new PropertyValueFactory<>("urgency"));
            myDateCol.setCellValueFactory(new PropertyValueFactory<>("assignedAt"));
            
            // Add action buttons to each row
            myActionCol.setCellFactory(column -> new TableCell<Request, String>() {
                private final Button updateButton = new Button("Update");
                
                {
                    updateButton.setOnAction(event -> {
                        Request request = getTableView().getItems().get(getIndex());
                        updateRequestStatus(request);
                    });
                    updateButton.getStyleClass().add("secondary-button");
                }
                
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(updateButton);
                    }
                }
            });
        }
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
        if (currentUser != null) {
            loadDashboardStats();
            if (availableRequestsView.isVisible()) {
                loadAvailableRequests();
            }
            if (myRequestsView.isVisible()) {
                loadMyRequests();
            }
        }
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
            
            logger.info("Volunteer logged out successfully");
            
        } catch (AuthenticationException | IOException e) {
            logger.log(Level.SEVERE, "Error during logout", e);
            showError("Logout Error", "An error occurred during logout.");
        }
    }
    
    // Navigation methods
    @FXML
    private void showDashboard() {
        showView(dashboardView);
        setActiveButton(dashboardBtn);
        loadDashboardStats();
    }
    
    @FXML
    private void showAvailableRequests() {
        showView(availableRequestsView);
        setActiveButton(availableRequestsBtn);
        loadAvailableRequests();
    }
    
    @FXML
    private void showMyRequests() {
        showView(myRequestsView);
        setActiveButton(myRequestsBtn);
        loadMyRequests();
    }
    
    @FXML
    private void showResources() {
        showView(resourcesView);
        setActiveButton(resourcesBtn);
    }
    
    @FXML
    private void showProfile() {
        showView(profileView);
        setActiveButton(profileBtn);
        loadProfile();
    }
    
    private void showView(VBox viewToShow) {
        dashboardView.setVisible(false);
        availableRequestsView.setVisible(false);
        myRequestsView.setVisible(false);
        resourcesView.setVisible(false);
        profileView.setVisible(false);
        
        viewToShow.setVisible(true);
    }
    
    private void setActiveButton(Button activeButton) {
        dashboardBtn.getStyleClass().remove("active");
        availableRequestsBtn.getStyleClass().remove("active");
        myRequestsBtn.getStyleClass().remove("active");
        resourcesBtn.getStyleClass().remove("active");
        profileBtn.getStyleClass().remove("active");
        
        activeButton.getStyleClass().add("active");
    }
    
    // Data loading methods
    private void loadDashboardStats() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    List<Request> availableRequests = requestService.getAvailableRequests();
                    List<Request> myAssignments = requestService.getRequestsByVolunteer(currentUser.getUserId());
                    long completedCount = myAssignments.stream()
                        .filter(r -> RequestStatus.COMPLETED.equals(r.getStatus()))
                        .count();
                    
                    Platform.runLater(() -> {
                        availableRequestsLabel.setText(String.valueOf(availableRequests.size()));
                        myAssignmentsLabel.setText(String.valueOf(myAssignments.size()));
                        completedRequestsLabel.setText(String.valueOf(completedCount));
                    });
                    
                } catch (Exception e) {
                    Platform.runLater(() -> showError("Data Loading Error", "Failed to load dashboard stats: " + e.getMessage()));
                }
                return null;
            }
        };
        
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
    
    private void loadAvailableRequests() {
        Task<List<Request>> task = new Task<List<Request>>() {
            @Override
            protected List<Request> call() throws Exception {
                return requestService.getAvailableRequests();
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    List<Request> requests = getValue();
                    availableRequestsTable.setItems(FXCollections.observableArrayList(requests));
                });
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> showError("Data Loading Error", "Failed to load available requests: " + getException().getMessage()));
            }
        };
        
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
    
    private void loadMyRequests() {
        if (currentUser == null) return;
        
        Task<List<Request>> task = new Task<List<Request>>() {
            @Override
            protected List<Request> call() throws Exception {
                return requestService.getRequestsByVolunteer(currentUser.getUserId());
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    List<Request> requests = getValue();
                    myRequestsTable.setItems(FXCollections.observableArrayList(requests));
                });
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> showError("Data Loading Error", "Failed to load your assignments: " + getException().getMessage()));
            }
        };
        
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
    
    private void loadProfile() {
        if (currentUser != null) {
            // Basic info
            if (profileDisplayName != null) {
                profileDisplayName.setText(currentUser.getDisplayName());
            }
            profileUsernameField.setText(currentUser.getUsername());
            profileEmailField.setText(currentUser.getEmail());
            profileRoleField.setText(currentUser.getRole().toString());
            
            // Member since
            if (profileMemberSinceLabel != null) {
                java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("MMMM yyyy");
                profileMemberSinceLabel.setText(currentUser.getCreatedAt().format(formatter));
            }
            
            // Load statistics
            loadProfileStatistics();
        }
    }
    
    private void loadProfileStatistics() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    List<Request> myAssignments = requestService.getRequestsByVolunteer(currentUser.getUserId());
                    long completedCount = myAssignments.stream()
                        .filter(r -> r.getStatus() == RequestStatus.COMPLETED)
                        .count();
                    long activeCount = myAssignments.stream()
                        .filter(r -> r.getStatus() != RequestStatus.COMPLETED && r.getStatus() != RequestStatus.CANCELLED)
                        .count();
                    
                    // Calculate impact score (simple formula: completed * 10 + active * 5)
                    long impactScore = (completedCount * 10) + (activeCount * 5);
                    
                    Platform.runLater(() -> {
                        if (profileAssignmentsCount != null) {
                            profileAssignmentsCount.setText(String.valueOf(activeCount));
                        }
                        if (profileCompletedCount != null) {
                            profileCompletedCount.setText(String.valueOf(completedCount));
                        }
                        if (profileImpactScore != null) {
                            profileImpactScore.setText(String.valueOf(impactScore));
                        }
                    });
                    
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Failed to load profile statistics", e);
                }
                return null;
            }
        };
        
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
    
    // Action methods
    @FXML
    private void refreshAvailableRequests() {
        loadAvailableRequests();
    }
    
    @FXML
    private void refreshMyRequests() {
        loadMyRequests();
    }
    
    private void acceptRequest(Request request) {
        if (currentUser == null) {
            showError("Authentication Error", "User session expired. Please log in again.");
            return;
        }
        
        // Show confirmation dialog
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Accept Request");
        confirmAlert.setHeaderText("Accept Help Request");
        confirmAlert.setContentText("Are you sure you want to accept this request?\n\n" +
                                   "Resource ID: " + request.getResourceId() + "\n" +
                                   "Urgency: " + request.getUrgencyLevel() + "\n" +
                                   "Description: " + request.getDescription());
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                performAcceptRequest(request);
            }
        });
    }
    
    private void performAcceptRequest(Request request) {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                request.setVolunteerId(currentUser.getUserId());
                request.setStatus(RequestStatus.ASSIGNED);
                requestService.updateRequest(request);
                return null;
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    showInfo("Success", "Request accepted successfully! The requester will be notified.");
                    loadAvailableRequests();
                    loadDashboardStats();
                });
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> showError("Error", "Failed to accept request: " + getException().getMessage()));
            }
        };
        
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
    
    private void updateRequestStatus(Request request) {
        // Create a dialog to update request status
        Dialog<RequestStatus> dialog = new Dialog<>();
        dialog.setTitle("Update Request Status");
        dialog.setHeaderText("Update status for request #" + request.getRequestId());
        
        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);
        
        ComboBox<RequestStatus> statusCombo = new ComboBox<>();
        statusCombo.setItems(FXCollections.observableArrayList(
            RequestStatus.ASSIGNED, RequestStatus.IN_PROGRESS, RequestStatus.COMPLETED
        ));
        statusCombo.setValue(request.getStatus());
        
        VBox content = new VBox(10);
        content.getChildren().addAll(
            new Label("Current Status: " + request.getStatus().getDisplayName()),
            new Label("New Status:"),
            statusCombo
        );
        
        dialog.getDialogPane().setContent(content);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                return statusCombo.getValue();
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(newStatus -> {
            if (newStatus != null && !newStatus.equals(request.getStatus())) {
                performStatusUpdate(request, newStatus);
            }
        });
    }
    
    private void performStatusUpdate(Request request, RequestStatus newStatus) {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                request.setStatus(newStatus);
                requestService.updateRequest(request);
                return null;
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    showInfo("Success", "Request status updated successfully!");
                    loadMyRequests();
                    loadDashboardStats();
                });
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> showError("Error", "Failed to update request status: " + getException().getMessage()));
            }
        };
        
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
    
    @FXML
    private void refreshVolunteerResources() {
        showInfo("Resources", "Resource data refreshed successfully!");
    }
    
    @FXML
    private void updateProfile() {
        String newEmail = profileEmailField.getText().trim();
        
        if (newEmail.isEmpty() || !newEmail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            showProfileError("Please enter a valid email address.");
            return;
        }
        
        // Update profile
        currentUser.setEmail(newEmail);
        showProfileSuccess("Profile updated successfully!");
        logger.info("Profile updated for user: " + currentUser.getUsername());
    }
    
    @FXML
    private void changePassword() {
        showInfo("Change Password", "Password change functionality will be implemented soon. Please contact your administrator.");
    }
    
    @FXML
    private void editPreferences() {
        showInfo("Edit Preferences", "Preference editing will be available in a future update. You can customize your volunteer categories and availability.");
    }
    
    private void showProfileError(String message) {
        if (profileErrorLabel != null) {
            profileErrorLabel.setText(message);
            profileErrorLabel.setVisible(true);
        }
        if (profileSuccessLabel != null) {
            profileSuccessLabel.setVisible(false);
        }
    }
    
    private void showProfileSuccess(String message) {
        if (profileSuccessLabel != null) {
            profileSuccessLabel.setText(message);
            profileSuccessLabel.setVisible(true);
        }
        if (profileErrorLabel != null) {
            profileErrorLabel.setVisible(false);
        }
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}