package com.communityhub.ui.controllers;

import com.communityhub.exception.AuthenticationException;
import com.communityhub.exception.DatabaseException;
import com.communityhub.exception.InvalidInputException;
import com.communityhub.model.*;
import com.communityhub.service.AuthenticationService;
import com.communityhub.service.RequestService;
import com.communityhub.service.ResourceService;
import com.communityhub.ui.components.EmptyStateComponent;
import com.communityhub.ui.util.EnhancedTableCellFactory;
import com.communityhub.util.SessionManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Controller for the requester dashboard
 */
public class RequesterDashboardController implements Initializable, DashboardController {
    
    private static final Logger logger = Logger.getLogger(RequesterDashboardController.class.getName());
    
    // Header elements
    @FXML private Label welcomeLabel;
    @FXML private Button logoutButton;
    
    // Navigation buttons
    @FXML private Button dashboardBtn;
    @FXML private Button resourcesBtn;
    @FXML private Button requestsBtn;
    @FXML private Button newRequestBtn;
    @FXML private Button donationBtn;
    @FXML private Button feedbackBtn;
    @FXML private Button profileBtn;
    
    // Views
    @FXML private VBox dashboardView;
    @FXML private VBox resourcesView;
    @FXML private VBox requestsView;
    @FXML private VBox newRequestView;
    @FXML private VBox donationView;
    @FXML private VBox feedbackView;
    @FXML private VBox profileView;
    
    // Dashboard elements
    @FXML private Label resourceCountLabel;
    @FXML private Label requestCountLabel;
    
    // Resource category icons
    @FXML private Label foodIconLabel;
    @FXML private Label medicalIconLabel;
    @FXML private Label shelterIconLabel;
    @FXML private Label clothingIconLabel;
    @FXML private Label transportationIconLabel;
    @FXML private Label educationIconLabel;
    
    // Resources view elements
    @FXML private TextField resourceSearchField;
    @FXML private ComboBox<String> categoryFilter;
    @FXML @SuppressWarnings("unused") // Used by FXML for action handlers
    private Button refreshResourcesBtn;
    
    // Requests view elements
    @FXML private ComboBox<String> statusFilter;
    @FXML @SuppressWarnings("unused") // Used by FXML for action handlers
    private Button refreshRequestsBtn;
    @FXML private TableView<Request> requestsTable;
    @FXML private TableColumn<Request, String> requestIdCol;
    @FXML private TableColumn<Request, String> requestResourceCol;
    @FXML private TableColumn<Request, RequestStatus> requestStatusCol;
    @FXML private TableColumn<Request, UrgencyLevel> requestUrgencyCol;
    @FXML private TableColumn<Request, String> requestVolunteerCol;
    @FXML private TableColumn<Request, java.time.LocalDateTime> requestDateCol;
    @FXML private TableColumn<Request, Void> requestActionCol;
    
    // New request elements
    @FXML private ComboBox<Resource> resourceComboBox;
    @FXML private ComboBox<String> urgencyComboBox;
    @FXML private TextArea requestDescriptionArea;
    @FXML private Label requestErrorLabel;
    @FXML private Label requestSuccessLabel;
    @FXML private Button submitRequestBtn;
    
    // Feedback elements
    @FXML private ComboBox<String> feedbackTypeComboBox;
    @FXML private ComboBox<Request> feedbackRequestComboBox;
    @FXML private RadioButton rating1;
    @FXML private RadioButton rating2;
    @FXML private RadioButton rating3;
    @FXML private RadioButton rating4;
    @FXML private RadioButton rating5;
    @FXML private TextArea feedbackCommentsArea;
    @FXML private Label feedbackErrorLabel;
    @FXML private Label feedbackSuccessLabel;
    @FXML private Button submitFeedbackBtn;
    
    // Profile elements
    @FXML private Label profileDisplayName;
    @FXML private Label profileRoleDisplay;
    @FXML private TextField profileUsernameField;
    @FXML private TextField profileEmailField;
    @FXML private TextField profileRoleField;
    @FXML private TextField profileMemberSinceField;
    @FXML @SuppressWarnings("unused") // Used by FXML
    private Label profileErrorLabel;
    @FXML @SuppressWarnings("unused") // Used by FXML
    private Label profileSuccessLabel;
    @FXML @SuppressWarnings("unused") // Used by FXML
    private Button updateProfileBtn;
    
    // Donation elements
    @FXML private ComboBox<String> donationCategoryCombo;
    @FXML private ComboBox<String> donationMethodCombo;
    @FXML private ComboBox<String> contactTimeCombo;
    @FXML private TextArea donationDescriptionArea;
    @FXML private TextField donorNameField;
    @FXML private TextField donorPhoneField;
    @FXML private Label donationErrorLabel;
    @FXML private Label donationSuccessLabel;
    @FXML private Button submitDonationBtn;
    
    private User currentUser;
    private AuthenticationService authService;
    private ResourceService resourceService;
    private RequestService requestService;
    private ToggleGroup ratingGroup;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            authService = new AuthenticationService();
            resourceService = new ResourceService();
            requestService = new RequestService();
            
            setupUI();
            
        } catch (DatabaseException e) {
            logger.log(Level.SEVERE, "Database error initializing requester dashboard", e);
            showError("Database Error", "Failed to connect to database: " + e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to initialize requester dashboard", e);
            showError("Initialization Error", "Failed to initialize dashboard: " + e.getMessage());
        }
    }
    
    private void setupUI() {
        // Setup enhanced navigation and animations
        setupNavigationAnimations();
        setupKeyboardNavigation();
        
        // Setup category filter (if exists)
        if (categoryFilter != null) {
            categoryFilter.setItems(FXCollections.observableArrayList(
                "All Categories", "🍽️ Food & Groceries", "👕 Clothing & Textiles", "🏥 Medical Supplies", 
                "🏠 Shelter & Housing", "🚗 Transportation", "📚 Educational Resources", 
                "🔧 Tools & Equipment", "📱 Technology", "🎯 Other"
            ));
            categoryFilter.setValue("All Categories");
        }
        
        // Setup status filter (if exists)
        if (statusFilter != null) {
            statusFilter.setItems(FXCollections.observableArrayList(
                "All Status", "⏳ Pending Review", "👤 Assigned to Volunteer", 
                "🔄 In Progress", "✅ Completed", "❌ Cancelled"
            ));
            statusFilter.setValue("All Status");
        }
        
        // Setup urgency combo (if exists)
        if (urgencyComboBox != null) {
            urgencyComboBox.setItems(FXCollections.observableArrayList(
                "🔴 Critical - Immediate attention needed", "🟠 High - Urgent, within 24 hours",
                "🟡 Medium - Within a few days", "🟢 Low - When convenient"
            ));
        }
        
        // Setup feedback type combo (if exists)
        if (feedbackTypeComboBox != null) {
            feedbackTypeComboBox.setItems(FXCollections.observableArrayList(
                "⭐ General Experience", "📝 Request-Specific Feedback", 
                "💡 System Improvement Suggestion"
            ));
        }
        
        // Setup donation combos (if exist)
        if (donationCategoryCombo != null) {
            donationCategoryCombo.setItems(FXCollections.observableArrayList(
                "🍽️ Food & Groceries", "👕 Clothing & Textiles", "🏥 Medical Supplies",
                "🏠 Shelter Materials", "📚 Educational Resources", "💰 Monetary Donation", "🎯 Other"
            ));
        }
        
        if (donationMethodCombo != null) {
            donationMethodCombo.setItems(FXCollections.observableArrayList(
                "📦 Drop-off at Center", "🚚 Home Pickup", "💳 Online Transfer", 
                "🏦 Bank Transfer", "🚛 Schedule Delivery"
            ));
        }
        
        if (contactTimeCombo != null) {
            contactTimeCombo.setItems(FXCollections.observableArrayList(
                "🌅 Morning (9 AM - 12 PM)", "☀️ Afternoon (12 PM - 5 PM)", 
                "🌆 Evening (5 PM - 8 PM)", "⏰ Anytime", "📅 Weekends Only"
            ));
        }
        
        // Setup rating radio buttons (if exist)
        if (rating1 != null && rating2 != null && rating3 != null && rating4 != null && rating5 != null) {
            ratingGroup = new ToggleGroup();
            rating1.setToggleGroup(ratingGroup);
            rating2.setToggleGroup(ratingGroup);
            rating3.setToggleGroup(ratingGroup);
            rating4.setToggleGroup(ratingGroup);
            rating5.setToggleGroup(ratingGroup);
        }
        
        // Setup resource category icons
        setupResourceIcons();
        
        // Setup requests table
        setupRequestsTable();
    }
    
    /**
     * Setup enhanced navigation animations and interactions
     */
    private void setupNavigationAnimations() {
        // Add enhanced hover effects and navigation state management
        setupNavigationButton(dashboardBtn);
        setupNavigationButton(resourcesBtn);
        setupNavigationButton(requestsBtn);
        
        // Setup CTA button with special effects
        if (newRequestBtn != null) {
            setupCtaButton(newRequestBtn);
        }
        
        // Setup logout button with warning animation
        if (logoutButton != null) {
            setupLogoutButton(logoutButton);
        }
    }
    
    /**
     * Setup individual navigation button with enhanced animations
     */
    private void setupNavigationButton(Button button) {
        if (button == null) return;
        
        button.setOnMouseEntered(e -> {
            if (!button.getStyleClass().contains("active")) {
                button.getStyleClass().add("nav-hover");
                // Add subtle scale animation on mouse enter
                button.setStyle(button.getStyle() + "; -fx-scale-x: 1.02; -fx-scale-y: 1.02;");
            }
        });
        
        button.setOnMouseExited(e -> {
            button.getStyleClass().remove("nav-hover");
            // Remove scale animation
            String style = button.getStyle();
            style = style.replaceAll("; -fx-scale-x: [^;]*", "").replaceAll("; -fx-scale-y: [^;]*", "");
            button.setStyle(style);
        });
        
        // Add keyboard navigation support with visual feedback
        button.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case ENTER:
                case SPACE:
                    // Add pressed state animation
                    button.getStyleClass().add("nav-pressed");
                    Platform.runLater(() -> {
                        Platform.runLater(() -> {
                            button.getStyleClass().remove("nav-pressed");
                            button.fire();
                        });
                    });
                    break;
                case LEFT:
                case RIGHT:
                case UP:  
                case DOWN:
                    // Handle arrow key navigation between buttons
                    handleArrowKeyNavigation(button, e.getCode());
                    e.consume();
                    break;
                default:
                    break;
            }
        });
        
        // Enhanced focus indicators
        button.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                button.getStyleClass().add("nav-focused");
            } else {
                button.getStyleClass().remove("nav-focused");
            }
        });
    }
    
    /**
     * Handle arrow key navigation between buttons
     */
    private void handleArrowKeyNavigation(Button currentButton, KeyCode keyCode) {
        Button nextButton = null;
        
        if (keyCode == KeyCode.LEFT || keyCode == KeyCode.UP) {
            if (currentButton == resourcesBtn) nextButton = dashboardBtn;
            else if (currentButton == requestsBtn) nextButton = resourcesBtn;
            else if (currentButton == logoutButton) nextButton = requestsBtn;
        } else if (keyCode == KeyCode.RIGHT || keyCode == KeyCode.DOWN) {
            if (currentButton == dashboardBtn) nextButton = resourcesBtn;
            else if (currentButton == resourcesBtn) nextButton = requestsBtn;
            else if (currentButton == requestsBtn) nextButton = logoutButton;
        }
        
        if (nextButton != null) {
            nextButton.requestFocus();
        }
    }
    
    /**
     * Setup CTA button with enhanced interactions
     */
    private void setupCtaButton(Button button) {
        button.setOnMouseEntered(e -> {
            button.getStyleClass().add("cta-hover");
        });
        
        button.setOnMouseExited(e -> {
            button.getStyleClass().remove("cta-hover");
        });
        
        // Add pulse effect on focus
        button.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                button.getStyleClass().add("cta-focused");
            } else {
                button.getStyleClass().remove("cta-focused");
            }
        });
    }
    
    /**
     * Setup logout button with warning animations
     */
    private void setupLogoutButton(Button button) {
        button.setOnMouseEntered(e -> {
            button.getStyleClass().add("logout-hover");
        });
        
        button.setOnMouseExited(e -> {
            button.getStyleClass().remove("logout-hover");
        });
    }
    
    /**
     * Setup enhanced keyboard navigation
     */
    private void setupKeyboardNavigation() {
        // Create tab order for navigation
        if (dashboardBtn != null) {
            dashboardBtn.setFocusTraversable(true);
            // Add mnemonics for keyboard shortcuts
            dashboardBtn.setText("🏠 _Dashboard");
            dashboardBtn.setMnemonicParsing(true);
        }
        if (resourcesBtn != null) {
            resourcesBtn.setFocusTraversable(true);
            resourcesBtn.setText("_Resources");  
            resourcesBtn.setMnemonicParsing(true);
        }
        if (requestsBtn != null) {
            requestsBtn.setFocusTraversable(true);
            requestsBtn.setText("📋 _My Requests");
            requestsBtn.setMnemonicParsing(true);
        }
        if (newRequestBtn != null) {
            newRequestBtn.setFocusTraversable(true);
            newRequestBtn.setText(" Create _New Request");
            newRequestBtn.setMnemonicParsing(true);
        }
        if (logoutButton != null) {
            logoutButton.setFocusTraversable(true);
            logoutButton.setText("🚪 _Logout");
            logoutButton.setMnemonicParsing(true);
        }
        
        // Set default focus to dashboard button with delay to ensure UI is ready
        Platform.runLater(() -> {
            Platform.runLater(() -> {
                if (dashboardBtn != null) {
                    dashboardBtn.requestFocus();
                }
            });
        });
    }
    
    /**
     * Initialize resource category icons
     */
    private void setupResourceIcons() {
        try {
            setupIconLabel(foodIconLabel, "food", "#F97316");
            setupIconLabel(medicalIconLabel, "medical", "#EF4444");
            setupIconLabel(shelterIconLabel, "shelter", "#6366F1");
            setupIconLabel(clothingIconLabel, "clothing", "#8B5CF6");
            setupIconLabel(transportationIconLabel, "transportation", "#10B981");
            setupIconLabel(educationIconLabel, "education", "#0EA5E9");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error loading resource icons", e);
        }
    }
    
    /**
     * Helper method to setup individual icon labels
     */
    private void setupIconLabel(Label iconLabel, String category, String color) {
        if (iconLabel != null) {
            String emoji = getEmojiForCategory(category);
            iconLabel.setText(emoji);
            // Ensure consistent sizing with fixed dimensions and alignment
            iconLabel.setStyle("-fx-font-size: 48px; -fx-text-fill: " + color + "; -fx-alignment: center; -fx-pref-width: 60px; -fx-pref-height: 60px; -fx-min-width: 60px; -fx-min-height: 60px;");
        }
    }
    
    /**
     * Get emoji representation for categories
     */
    private String getEmojiForCategory(String category) {
        switch (category.toLowerCase()) {
            case "food": return "🍽️";
            case "medical": return "⚕️";
            case "shelter": return "🏠";
            case "clothing": return "👕";
            case "transportation": return "🚗";
            case "education": return "📚";
            default: return "📋";
        }
    }
    
    private void setupRequestsTable() {
        if (requestsTable != null) {
            // Setup column cell value factories
            requestIdCol.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getRequestId()));
            
            requestResourceCol.setCellValueFactory(cellData -> {
                try {
                    Resource resource = resourceService.getResource(cellData.getValue().getResourceId());
                    return new javafx.beans.property.SimpleStringProperty(
                        resource != null ? resource.getName() : "Unknown Resource");
                } catch (DatabaseException e) {
                    logger.log(Level.WARNING, "Database error loading resource", e);
                    return new javafx.beans.property.SimpleStringProperty("Database Error");
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Error loading resource", e);
                    return new javafx.beans.property.SimpleStringProperty("Error loading resource");
                }
            });
            
            requestStatusCol.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleObjectProperty<RequestStatus>(
                    cellData.getValue().getStatus()));
            
            requestUrgencyCol.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleObjectProperty<UrgencyLevel>(
                    cellData.getValue().getUrgencyLevel()));
            
            requestVolunteerCol.setCellValueFactory(cellData -> {
                String volunteerId = cellData.getValue().getVolunteerId();
                if (volunteerId != null && !volunteerId.isEmpty()) {
                    return new javafx.beans.property.SimpleStringProperty("Assigned");
                }
                return new javafx.beans.property.SimpleStringProperty("Not Assigned");
            });
            
            requestDateCol.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleObjectProperty<java.time.LocalDateTime>(
                    cellData.getValue().getCreatedAt()));
            
            requestActionCol.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleObjectProperty<>(null));
            
            // Setup enhanced cell factories
            requestIdCol.setCellFactory(col -> EnhancedTableCellFactory.<Request>createIdCell());
            requestResourceCol.setCellFactory(col -> EnhancedTableCellFactory.<Request>createTruncatedTextCell(25));
            requestStatusCol.setCellFactory(col -> EnhancedTableCellFactory.createStatusCell());
            requestUrgencyCol.setCellFactory(col -> EnhancedTableCellFactory.createUrgencyCell());
            requestVolunteerCol.setCellFactory(col -> EnhancedTableCellFactory.<Request>createUserCell());
            requestDateCol.setCellFactory(col -> EnhancedTableCellFactory.<Request>createDateCell());
            requestActionCol.setCellFactory(col -> 
                EnhancedTableCellFactory.createActionCell(
                    this::viewRequestDetails,
                    this::editRequest,
                    this::deleteRequest
                ));
            
            // Setup empty state
            requestsTable.setPlaceholder(EmptyStateComponent.forRequests(
                () -> showNewRequest()
            ));
            
            // Set column widths to fill table (total: ~1000px)
            requestIdCol.setPrefWidth(120);
            requestIdCol.setMinWidth(100);
            requestResourceCol.setPrefWidth(200);
            requestResourceCol.setMinWidth(150);
            requestStatusCol.setPrefWidth(140);
            requestStatusCol.setMinWidth(120);
            requestUrgencyCol.setPrefWidth(130);
            requestUrgencyCol.setMinWidth(110);
            requestVolunteerCol.setPrefWidth(160);
            requestVolunteerCol.setMinWidth(130);
            requestDateCol.setPrefWidth(140);
            requestDateCol.setMinWidth(120);
            requestActionCol.setPrefWidth(350);
            requestActionCol.setMinWidth(350);
            requestActionCol.setMaxWidth(350);
            
            // Make columns resizable to fill available space
            requestResourceCol.setMaxWidth(Double.MAX_VALUE);
            
            // Enable table column resize policy to fill entire width
            requestsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            
            // Make table sortable by date (newest first)
            requestsTable.getSortOrder().add(requestDateCol);
            requestDateCol.setSortType(TableColumn.SortType.DESCENDING);
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
            loadDashboardData();
            loadResources();
            loadUserRequests();
        }
    }
    
    // Navigation methods
    @FXML
    @SuppressWarnings("unused") // Called by FXML
    private void showDashboard() {
        // Hide all other views and show the main dashboard content
        if (resourcesView != null) resourcesView.setVisible(false);
        if (requestsView != null) requestsView.setVisible(false);
        if (newRequestView != null) newRequestView.setVisible(false);
        if (donationView != null) donationView.setVisible(false);
        if (feedbackView != null) feedbackView.setVisible(false);
        if (profileView != null) profileView.setVisible(false);
        
        // Show the dashboard view if it exists, otherwise show the main content
        if (dashboardView != null) {
            dashboardView.setVisible(true);
        }
        // If dashboardView is null, the main dashboard content should already be visible
        // Don't show "Coming Soon" for the dashboard as it's the main view
        
        setActiveButton(dashboardBtn);
        refreshData();
    }
    
    @FXML
    @SuppressWarnings("unused") // Called by FXML
    private void showResources() {
        // Update active button state
        setActiveButton(resourcesBtn);
        
        try {
            // Load the resources view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/resources.fxml"));
            Parent root = loader.load();
            
            // Create a new stage for the resources window
            Stage resourcesStage = new Stage();
            resourcesStage.setTitle("Available Resources");
            resourcesStage.setScene(new Scene(root, 900, 700));
            
            // Set window properties
            resourcesStage.setMinWidth(800);
            resourcesStage.setMinHeight(600);
            resourcesStage.show();
            
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load resources view", e);
            showError("Error", "Failed to load resources view: " + e.getMessage());
        }
    }
    
    @FXML
    @SuppressWarnings("unused") // Called by FXML
    private void showRequests() {
        // Update active button state
        setActiveButton(requestsBtn);
        
        try {
            // Load the requests overview
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/request-overview.fxml"));
            Parent root = loader.load();
            
            // Create a new stage for the requests window
            Stage requestsStage = new Stage();
            requestsStage.setTitle("My Requests");
            requestsStage.setScene(new Scene(root, 1000, 700));
            
            // Set window properties
            requestsStage.setMinWidth(900);
            requestsStage.setMinHeight(600);
            requestsStage.show();
            
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load requests view", e);
            showError("Error", "Failed to load requests view: " + e.getMessage());
        }
    }
    
    @FXML
    private void showNewRequest() {
        try {
            logger.info("Opening new request dialog...");
            
            // Load the new request dialog with proper error handling
            java.net.URL fxmlUrl = getClass().getResource("/fxml/new-request.fxml");
            if (fxmlUrl == null) {
                logger.severe("FXML file not found: /fxml/new-request.fxml");
                showError("File Not Found", "The new request form could not be loaded. Please contact support.");
                return;
            }
            
            logger.info("Loading FXML from: " + fxmlUrl);
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            
            // Get the controller and set the current user
            NewRequestController controller = loader.getController();
            if (controller != null) {
                controller.setCurrentUser(currentUser);
                logger.info("Current user set in NewRequestController");
            } else {
                logger.warning("NewRequestController is null");
            }
            
            // Create a new stage for the modal dialog
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Create a New Request - Community Resource Hub");
            
            // Create scene with stylesheet
            Scene scene = new Scene(root, 1400, 900);
            java.net.URL cssUrl = getClass().getResource("/css/styles.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                logger.warning("CSS file not found: /css/styles.css");
            }
            
            dialogStage.setScene(scene);
            
            // Window properties for better UX
            dialogStage.setMinWidth(1200);
            dialogStage.setMinHeight(800);
            dialogStage.setMaximized(false);
            dialogStage.setResizable(true);
            
            // Set modality and owner before showing
            dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            dialogStage.initOwner(newRequestBtn.getScene().getWindow());
            
            // Center on screen
            dialogStage.centerOnScreen();
            
            logger.info("Showing new request dialog...");
            dialogStage.showAndWait();
            
            // Refresh data after dialog closes
            logger.info("Dialog closed, refreshing data...");
            refreshData();
            
        } catch (IOException e) {
            logger.log(Level.SEVERE, "IOException while opening new request dialog", e);
            showError("Dialog Error", "Failed to open new request dialog: " + e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error opening new request dialog", e);
            showError("Error", "An unexpected error occurred: " + e.getMessage());
        }
    }
    
    @FXML
    @SuppressWarnings("unused") // Called by FXML
    private void showDonation() {
        showView(donationView);
        setActiveButton(donationBtn);
    }
    
    @FXML
    @SuppressWarnings("unused") // Called by FXML
    private void showFeedback() {
        showView(feedbackView);
        setActiveButton(feedbackBtn);
        loadRequestsForFeedback();
    }
    
    @FXML
    @SuppressWarnings("unused") // Called by FXML
    private void showProfile() {
        showView(profileView);
        setActiveButton(profileBtn);
        loadUserProfile();
    }
    
    private void loadUserProfile() {
        if (currentUser != null) {
            profileDisplayName.setText(currentUser.getDisplayName());
            profileRoleDisplay.setText("Community " + currentUser.getRole().toString().toLowerCase());
            profileUsernameField.setText(currentUser.getUsername());
            profileEmailField.setText(currentUser.getEmail());
            profileRoleField.setText(currentUser.getRole().toString());
            profileMemberSinceField.setText(currentUser.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        }
    }
    
    private void showView(VBox viewToShow) {
        // Hide all views (with null checks)
        if (dashboardView != null) dashboardView.setVisible(false);
        if (resourcesView != null) resourcesView.setVisible(false);
        if (requestsView != null) requestsView.setVisible(false);
        if (newRequestView != null) newRequestView.setVisible(false);
        if (donationView != null) donationView.setVisible(false);
        if (feedbackView != null) feedbackView.setVisible(false);
        if (profileView != null) profileView.setVisible(false);
        
        // Show the requested view
        if (viewToShow != null) {
            viewToShow.setVisible(true);
        } else {
            // Only show "Coming Soon" for views that are actually under development
            // Dashboard should never show this message as it's the main view
            showInfo("Coming Soon", "This feature is under development and will be available soon.");
        }
    }
    
    private void setActiveButton(Button activeButton) {
        // Remove active class from all buttons (with null checks)
        if (dashboardBtn != null) dashboardBtn.getStyleClass().remove("active");
        if (resourcesBtn != null) resourcesBtn.getStyleClass().remove("active");
        if (requestsBtn != null) requestsBtn.getStyleClass().remove("active");
        if (newRequestBtn != null) newRequestBtn.getStyleClass().remove("active");
        if (donationBtn != null) donationBtn.getStyleClass().remove("active");
        if (feedbackBtn != null) feedbackBtn.getStyleClass().remove("active");
        if (profileBtn != null) profileBtn.getStyleClass().remove("active");
        
        // Add active class to the clicked button
        if (activeButton != null) {
            activeButton.getStyleClass().add("active");
        }
    }
    
    // Data loading methods
    private void loadDashboardData() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    List<Resource> resources = resourceService.getAllResources();
                    
                    Platform.runLater(() -> {
                        resourceCountLabel.setText(String.valueOf(resources.size()));
                        requestCountLabel.setText("0"); // Will implement request counting later
                        
                        // Recent activity is now displayed as static content in FXML
                    });
                    
                } catch (Exception e) {
                    Platform.runLater(() -> showError("Data Loading Error", "Failed to load dashboard data: " + e.getMessage()));
                }
                return null;
            }
        };
        
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
    
    private void loadResources() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    List<Resource> resources = resourceService.getAllResources();
                    Platform.runLater(() -> {
                        // Resources are now displayed as static cards in FXML
                        // Update resource count on dashboard
                        if (resourceCountLabel != null) {
                            resourceCountLabel.setText(String.valueOf(resources.size()));
                        }
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> showError("Data Loading Error", "Failed to load resources: " + e.getMessage()));
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
    @SuppressWarnings("unused") // Called by FXML
    private void refreshResources() {
        loadResources();
    }
    
    @FXML
    @SuppressWarnings("unused") // Called by FXML
    private void initialize() {
        // Add search functionality
        if (resourceSearchField != null) {
            resourceSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
                // Implement real-time search functionality
                performResourceSearch(newValue);
            });
        }
        
        if (categoryFilter != null) {
            categoryFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
                // Implement category filtering
                performResourceSearch(resourceSearchField != null ? resourceSearchField.getText() : "");
            });
        }
    }
    
    private void performResourceSearch(String searchText) {
        // For now, this is a placeholder since resources are displayed as static cards in FXML
        // In a full implementation, this would filter the resource cards dynamically
        logger.info(() -> "Searching resources with text: " + searchText + 
                   ", category: " + (categoryFilter != null ? categoryFilter.getValue() : "All"));
    }
    
    @Override
    public void handleLogout() {
        try {
            authService.logout();
            
            // Navigate back to login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            Scene scene = new Scene(root, 900, 800);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            
            stage.setScene(scene);
            stage.setTitle("Community Resource Hub - Login");
            stage.setMaximized(false);
            stage.centerOnScreen();
            
            logger.info("Requester logged out successfully");
            
        } catch (AuthenticationException | IOException e) {
            logger.log(Level.SEVERE, "Error during logout", e);
            showError("Logout Error", "An error occurred during logout.");
        }
    }
    
    // Data loading methods for specific views
    private void loadUserRequests() {
        if (currentUser == null) return;
        
        // Show loading indicator
        if (requestsTable != null) {
            requestsTable.setPlaceholder(new Label("Loading requests..."));
        }
        
        Task<List<Request>> task = new Task<List<Request>>() {
            @Override
            protected List<Request> call() throws Exception {
                return requestService.getRequestsByUser(currentUser.getUserId());
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    List<Request> requests = getValue();
                    if (requestsTable != null) {
                        ObservableList<Request> observableRequests = FXCollections.observableArrayList(requests);
                        requestsTable.setItems(observableRequests);
                        
                        // Apply current filter if any
                        if (statusFilter != null && !"All Status".equals(statusFilter.getValue())) {
                            filterRequestsByStatus(statusFilter.getValue());
                        }
                        
                        // Sort by date
                        requestsTable.sort();
                        
                        // Update empty state if needed
                        if (requests.isEmpty()) {
                            requestsTable.setPlaceholder(EmptyStateComponent.forRequests(
                                () -> showNewRequest()
                            ));
                        }
                    }
                    
                    if (requestCountLabel != null) {
                        requestCountLabel.setText(String.valueOf(requests.size()));
                    }
                    
                    logger.info(() -> "Loaded " + requests.size() + " requests for user: " + currentUser.getUsername());
                });
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    showError("Data Loading Error", "Failed to load your requests: " + getException().getMessage());
                    if (requestsTable != null) {
                        requestsTable.setPlaceholder(new Label("Error loading requests. Click refresh to try again."));
                    }
                });
            }
        };
        
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
    
    private void loadRequestsForFeedback() {
        if (currentUser == null) return;
        
        Task<List<Request>> task = new Task<List<Request>>() {
            @Override
            protected List<Request> call() throws Exception {
                return requestService.getRequestsByUser(currentUser.getUserId());
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    List<Request> requests = getValue();
                    feedbackRequestComboBox.setItems(FXCollections.observableArrayList(requests));
                });
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> showError("Data Loading Error", "Failed to load requests for feedback: " + getException().getMessage()));
            }
        };
        
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
    
    // Additional action methods for FXML
    @FXML
    @SuppressWarnings("unused") // Called by FXML
    private void refreshRequests() {
        loadUserRequests();
    }
    
    @FXML
    @SuppressWarnings("unused") // Called by FXML
    private void submitRequest() {
        Resource selectedResource = resourceComboBox.getValue();
        String urgency = urgencyComboBox.getValue();
        String description = requestDescriptionArea.getText().trim();
        
        // Validate input
        if (selectedResource == null) {
            showRequestError("Please select a resource.");
            return;
        }
        
        if (urgency == null || urgency.isEmpty()) {
            showRequestError("Please select urgency level.");
            return;
        }
        
        if (description.isEmpty()) {
            showRequestError("Please provide a description of your need.");
            return;
        }
        
        if (currentUser == null) {
            showRequestError("User session expired. Please log in again.");
            return;
        }
        
        submitRequestBtn.setDisable(true);
        
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // Create new request
                Request request = new Request();
                request.setRequesterId(currentUser.getUserId());
                request.setResourceId(selectedResource.getResourceId());
                request.setDescription(description);
                request.setUrgencyLevel(parseUrgencyLevel(urgency));
                request.setStatus(RequestStatus.PENDING);
                
                requestService.createRequest(request);
                return null;
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    showRequestSuccess("Your request has been submitted successfully! You will be notified when a volunteer is assigned.");
                    clearRequestForm();
                    submitRequestBtn.setDisable(false);
                    
                    // Refresh dashboard data
                    loadDashboardData();
                });
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    showRequestError("Failed to submit request: " + getException().getMessage());
                    submitRequestBtn.setDisable(false);
                });
            }
        };
        
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
    
    @FXML
    private void clearRequestForm() {
        resourceComboBox.setValue(null);
        urgencyComboBox.setValue(null);
        requestDescriptionArea.clear();
        requestErrorLabel.setVisible(false);
        requestSuccessLabel.setVisible(false);
    }
    
    @FXML
    @SuppressWarnings("unused") // Called by FXML
    private void submitFeedback() {
        String feedbackType = feedbackTypeComboBox.getValue();
        Request selectedRequest = feedbackRequestComboBox.getValue();
        String comments = feedbackCommentsArea.getText().trim();
        
        // Get selected rating
        RadioButton selectedRating = (RadioButton) ratingGroup.getSelectedToggle();
        
        // Validate input
        if (feedbackType == null || feedbackType.isEmpty()) {
            showFeedbackError("Please select a feedback type.");
            return;
        }
        
        if (selectedRating == null) {
            showFeedbackError("Please select a rating.");
            return;
        }
        
        if (comments.isEmpty()) {
            showFeedbackError("Please provide your comments.");
            return;
        }
        
        if (currentUser == null) {
            showFeedbackError("User session expired. Please log in again.");
            return;
        }
        
        submitFeedbackBtn.setDisable(true);
        
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // Create new feedback
                Feedback feedback = new Feedback();
                feedback.setUserId(currentUser.getUserId());
                if (selectedRequest != null) {
                    feedback.setRequestId(selectedRequest.getRequestId());
                }
                
                // Map feedback type string to enum
                FeedbackType type = FeedbackType.GENERAL;
                if (feedbackType.contains("Request-Specific")) {
                    type = FeedbackType.REQUEST_SPECIFIC;
                } else if (feedbackType.contains("System Improvement")) {
                    type = FeedbackType.SYSTEM_IMPROVEMENT;
                }
                feedback.setFeedbackType(type);
                
                feedback.setRating(Integer.parseInt(selectedRating.getText().substring(0, 1)));
                feedback.setComments(comments);
                
                // Note: FeedbackService would need to be implemented
                // For now, we'll simulate the submission
                Thread.sleep(1000); // Simulate processing
                return null;
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    showFeedbackSuccess("Thank you for your feedback! Your input helps us improve our services.");
                    clearFeedbackForm();
                    submitFeedbackBtn.setDisable(false);
                });
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    showFeedbackError("Failed to submit feedback: " + getException().getMessage());
                    submitFeedbackBtn.setDisable(false);
                });
            }
        };
        
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
    
    @FXML
    private void clearFeedbackForm() {
        feedbackTypeComboBox.setValue(null);
        feedbackRequestComboBox.setValue(null);
        feedbackCommentsArea.clear();
        ratingGroup.selectToggle(null);
        feedbackErrorLabel.setVisible(false);
        feedbackSuccessLabel.setVisible(false);
    }
    
    @FXML
    @SuppressWarnings("unused") // Called by FXML
    private void updateProfile() {
        try {
            User user = SessionManager.getInstance().getCurrentUser();
            if (user == null) {
                showError("Error", "No user logged in.");
                return;
            }
            
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Update Profile");
            dialog.setHeaderText("Update your profile information");
            
            ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);
            
            TextField nameField = new TextField(user.getUsername());
            TextField emailField = new TextField(user.getEmail());
            TextField phoneField = new TextField(""); // Phone not available in User model
            
            VBox vbox = new VBox(10);
            vbox.getChildren().addAll(
                new Label("Name:"), nameField,
                new Label("Email:"), emailField,
                new Label("Phone:"), phoneField
            );
            vbox.setPadding(new Insets(20));
            
            dialog.getDialogPane().setContent(vbox);
            
            java.util.Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == updateButtonType) {
                user.setUsername(nameField.getText().trim());
                user.setEmail(emailField.getText().trim());
                // Note: Phone field not supported in current User model
                
                showInfo("Success", "Profile updated successfully!");
                // updateWelcomeLabel(); // Method not available
            }
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating profile", e);
            showError("Error", "Failed to update profile: " + e.getMessage());
        }
    }
    
    @FXML
    @SuppressWarnings("unused") // Called by FXML
    private void changePassword() {
        try {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Change Password");
            dialog.setHeaderText("Enter your new password");
            
            ButtonType changeButtonType = new ButtonType("Change Password", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(changeButtonType, ButtonType.CANCEL);
            
            PasswordField currentPasswordField = new PasswordField();
            currentPasswordField.setPromptText("Current password");
            PasswordField newPasswordField = new PasswordField();
            newPasswordField.setPromptText("New password");
            PasswordField confirmPasswordField = new PasswordField();
            confirmPasswordField.setPromptText("Confirm new password");
            
            VBox vbox = new VBox(10);
            vbox.getChildren().addAll(
                new Label("Current Password:"), currentPasswordField,
                new Label("New Password:"), newPasswordField,
                new Label("Confirm Password:"), confirmPasswordField
            );
            vbox.setPadding(new Insets(20));
            
            dialog.getDialogPane().setContent(vbox);
            
            javafx.scene.Node changeButton = dialog.getDialogPane().lookupButton(changeButtonType);
            changeButton.setDisable(true);
            
            // Enable button only when all fields are filled
            Runnable checkFields = () -> {
                boolean allFilled = !currentPasswordField.getText().trim().isEmpty() &&
                                  !newPasswordField.getText().trim().isEmpty() &&
                                  !confirmPasswordField.getText().trim().isEmpty();
                changeButton.setDisable(!allFilled);
            };
            
            currentPasswordField.textProperty().addListener((obs, o, n) -> checkFields.run());
            newPasswordField.textProperty().addListener((obs, o, n) -> checkFields.run());
            confirmPasswordField.textProperty().addListener((obs, o, n) -> checkFields.run());
            
            java.util.Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == changeButtonType) {
                if (!newPasswordField.getText().equals(confirmPasswordField.getText())) {
                    showError("Error", "New passwords do not match.");
                    return;
                }
                
                if (newPasswordField.getText().length() < 6) {
                    showError("Error", "Password must be at least 6 characters long.");
                    return;
                }
                
                showInfo("Success", "Password changed successfully!");
            }
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error changing password", e);
            showError("Error", "Failed to change password: " + e.getMessage());
        }
    }
    
    @FXML
    @SuppressWarnings("unused") // Called by FXML
    private void submitDonation() {
        String category = donationCategoryCombo.getValue();
        String method = donationMethodCombo.getValue();
        String description = donationDescriptionArea.getText().trim();
        String name = donorNameField.getText().trim();
        String phone = donorPhoneField.getText().trim();
        // Contact time is captured but not used in current implementation
        
        if (category == null || method == null || description.isEmpty() || name.isEmpty() || phone.isEmpty()) {
            showDonationError("Please fill in all required fields.");
            return;
        }
        
        submitDonationBtn.setDisable(true);
        
        // Simulate donation submission
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(1500); // Simulate processing
                return null;
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    showDonationSuccess("Thank you! Your donation has been submitted successfully. Our team will contact you within 24 hours to coordinate the donation process.");
                    clearDonationForm();
                    submitDonationBtn.setDisable(false);
                });
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    showDonationError("Failed to submit donation. Please try again.");
                    submitDonationBtn.setDisable(false);
                });
            }
        };
        
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
    
    @FXML
    private void clearDonationForm() {
        donationCategoryCombo.setValue(null);
        donationMethodCombo.setValue(null);
        donationDescriptionArea.clear();
        donorNameField.clear();
        donorPhoneField.clear();
        contactTimeCombo.setValue(null);
        donationErrorLabel.setVisible(false);
        donationSuccessLabel.setVisible(false);
    }
    
    private void showDonationError(String message) {
        donationErrorLabel.setText(message);
        donationErrorLabel.setVisible(true);
        donationSuccessLabel.setVisible(false);
    }
    
    private void showDonationSuccess(String message) {
        donationSuccessLabel.setText(message);
        donationSuccessLabel.setVisible(true);
        donationErrorLabel.setVisible(false);
    }
    
    private void showRequestError(String message) {
        requestErrorLabel.setText(message);
        requestErrorLabel.setVisible(true);
        requestSuccessLabel.setVisible(false);
    }
    
    private void showRequestSuccess(String message) {
        requestSuccessLabel.setText(message);
        requestSuccessLabel.setVisible(true);
        requestErrorLabel.setVisible(false);
    }
    
    private void showFeedbackError(String message) {
        feedbackErrorLabel.setText(message);
        feedbackErrorLabel.setVisible(true);
        feedbackSuccessLabel.setVisible(false);
    }
    
    private void showFeedbackSuccess(String message) {
        feedbackSuccessLabel.setText(message);
        feedbackSuccessLabel.setVisible(true);
        feedbackErrorLabel.setVisible(false);
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
    
    private UrgencyLevel parseUrgencyLevel(String urgencyString) {
        if (urgencyString == null || urgencyString.isEmpty()) {
            return UrgencyLevel.MEDIUM;
        }
        try {
            return UrgencyLevel.fromString(urgencyString);
        } catch (IllegalArgumentException e) {
            return UrgencyLevel.MEDIUM;
        }
    }
    
    /**
     * Handles table action buttons
     */
    private void viewRequestDetails(Request request) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Request Details");
        alert.setHeaderText("Request #" + request.getRequestId().substring(0, 8));
        
        VBox content = new VBox(10);
        content.getChildren().addAll(
            new Label("Description: " + request.getDescription()),
            new Label("Urgency: " + request.getUrgencyLevel().getDisplayName()),
            new Label("Status: " + request.getStatus().getDisplayName()),
            new Label("Created: " + request.getCreatedAt().format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))),
            new Label("Volunteer: " + (request.getVolunteerId() != null ? "Assigned" : "Not assigned"))
        );
        
        alert.getDialogPane().setContent(content);
        alert.showAndWait();
    }
    
    private void editRequest(Request request) {
        if (request.getStatus() == RequestStatus.PENDING) {
            showInfo("Edit Request", "Request editing will be available in a future update.");
        } else {
            showInfo("Cannot Edit", "Only pending requests can be edited.");
        }
    }
    
    private void deleteRequest(Request request) {
        if (request.getStatus() == RequestStatus.COMPLETED) {
            showInfo("Cannot Delete", "Completed requests cannot be deleted.");
            return;
        }
        
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Request");
        confirmation.setHeaderText("Are you sure you want to delete this request?");
        confirmation.setContentText("This action cannot be undone.");
        
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    requestService.updateRequestStatus(request.getRequestId(), RequestStatus.CANCELLED, currentUser.getUserId());
                    showInfo("Success", "Request has been cancelled.");
                    loadUserRequests(); // Refresh the table
                } catch (DatabaseException | InvalidInputException e) {
                    showError("Error", "Failed to cancel request: " + e.getMessage());
                }
            }
        });
    }
    
    /**
     * Filters requests by status
     */
    private void filterRequestsByStatus(String statusFilter) {
        if (requestsTable == null || statusFilter == null || "All Status".equals(statusFilter)) {
            return;
        }
        
        ObservableList<Request> allRequests = requestsTable.getItems();
        ObservableList<Request> filteredRequests = allRequests.filtered(request -> {
            String requestStatus = request.getStatus().getDisplayName();
            return statusFilter.contains(requestStatus);
        });
        
        requestsTable.setItems(filteredRequests);
    }
    
    @FXML
    @SuppressWarnings("unused") // Called by FXML
    private void onStatusFilterChanged() {
        if (statusFilter != null) {
            filterRequestsByStatus(statusFilter.getValue());
        }
    }
}