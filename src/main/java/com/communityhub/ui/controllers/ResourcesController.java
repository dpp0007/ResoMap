package com.communityhub.ui.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.communityhub.exception.DatabaseException;
import com.communityhub.model.Resource;
import com.communityhub.service.ResourceService;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Controller for the Resources page
 */
public class ResourcesController implements Initializable {
    
    private static final Logger logger = Logger.getLogger(ResourcesController.class.getName());
    
    @FXML private TextField searchField;
    @FXML private ComboBox<String> categoryFilter;
    @FXML @SuppressWarnings("unused") // Used by FXML for action handlers
    private Button createRequestBtn;
    @FXML private GridPane resourcesGrid;
    @FXML @SuppressWarnings("unused") // Used by FXML for layout
    private VBox resourcesContainer;
    
    private ResourceService resourceService;
    private List<Resource> allResources;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            logger.info("Initializing ResourcesController...");
            resourceService = new ResourceService();
            logger.info("ResourceService initialized successfully");
            
            setupUI();
            logger.info("UI setup completed");
            
            loadResources();
            logger.info("Resources loaded successfully");
            
        } catch (DatabaseException e) {
            logger.log(Level.SEVERE, "Failed to initialize resources controller: {0}", e.getMessage());
            // Show error dialog to user
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Initialization Error");
                alert.setHeaderText("Failed to initialize Resources view");
                alert.setContentText("Error: " + e.getMessage());
                alert.showAndWait();
            });
        }
    }
    
    private void setupUI() {
        // Check if UI elements are properly injected
        if (categoryFilter == null || searchField == null) {
            logger.log(Level.SEVERE, "UI elements not properly injected from FXML");
            return;
        }
        
        // Setup category filter
        categoryFilter.setItems(FXCollections.observableArrayList(
            "All Categories", "🍽️ Food & Groceries", "👕 Clothing & Textiles", "🏥 Medical Supplies", 
            "🏠 Shelter & Housing", "🚗 Transportation", "📚 Educational Resources", 
            "🔧 Tools & Equipment", "📱 Technology", "🎯 Other"
        ));
        categoryFilter.setValue("All Categories");
        
        // Add search functionality
        searchField.textProperty().addListener((obs, oldText, newText) -> filterResources());
        categoryFilter.setOnAction(e -> filterResources());
    }
    
    private void loadResources() {
        try {
            allResources = resourceService.getAllResources();
            displayResources(allResources);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to load resources", e);
            showError("Error", "Failed to load resources: " + e.getMessage());
        }
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void displayResources(List<Resource> resources) {
        Platform.runLater(() -> {
            resourcesGrid.getChildren().clear();
            
            if (resources.isEmpty()) {
                showEmptyState();
                return;
            }
            
            int col = 0, row = 0;
            
            for (Resource resource : resources) {
                VBox resourceCard = createResourceCard(resource);
                resourcesGrid.add(resourceCard, col, row);
                
                col++;
                if (col >= 3) {
                    col = 0;
                    row++;
                }
            }
        });
    }
    
    private VBox createResourceCard(Resource resource) {
        VBox card = new VBox(16);
        card.getStyleClass().add("modern-stat-card");
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16px; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 20, 0, 0, 4); " +
                     "-fx-padding: 24px; -fx-cursor: hand;");
        
        // Resource Icon
        ImageView iconView = new ImageView();
        iconView.setFitHeight(48);
        iconView.setFitWidth(48);
        iconView.setPreserveRatio(true);
        
        // Set icon based on category
        String iconPath = getIconPath(resource.getCategory());
        try {
            iconView.setImage(new Image(getClass().getResourceAsStream(iconPath)));
        } catch (Exception e) {
            // Fallback to emoji if image fails
            Label emojiIcon = new Label(getCategoryEmoji(resource.getCategory()));
            emojiIcon.setStyle("-fx-font-size: 48px;");
        }
        
        // Resource Title
        Label titleLabel = new Label(resource.getName());
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0D253F;");
        titleLabel.setFont(Font.font("System Bold", 18));
        
        // Resource Description
        Label descLabel = new Label(resource.getDescription());
        descLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748B; -fx-wrap-text: true;");
        descLabel.setWrapText(true);
        
        // Category Badge
        Label categoryLabel = new Label(resource.getCategory());
        categoryLabel.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: #475569; " +
                              "-fx-padding: 6px 12px; -fx-background-radius: 20px; " +
                              "-fx-font-size: 12px; -fx-font-weight: 600;");
        
        // Request Button
        Button requestBtn = new Button("Request This Resource");
        requestBtn.getStyleClass().add("cta-button");
        requestBtn.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; " +
                           "-fx-font-weight: bold; -fx-padding: 12px 20px; " +
                           "-fx-background-radius: 8px; -fx-cursor: hand;");
        requestBtn.setOnAction(e -> handleRequestResource(resource));
        
        card.getChildren().addAll(iconView, titleLabel, descLabel, categoryLabel, requestBtn);
        
        // Add hover effect
        card.setOnMouseEntered(e -> {
            card.setStyle(card.getStyle() + "; -fx-scale-x: 1.02; -fx-scale-y: 1.02; " +
                         "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 28, 0, 0, 8);");
        });
        
        card.setOnMouseExited(e -> {
            card.setStyle("-fx-background-color: white; -fx-background-radius: 16px; " +
                         "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 20, 0, 0, 4); " +
                         "-fx-padding: 24px; -fx-cursor: hand;");
        });
        
        return card;
    }
    
    private String getIconPath(String category) {
        switch (category.toLowerCase()) {
            case "food & groceries":
                return "/assets/icons/food-assistance.svg";
            case "clothing & textiles":
                return "/assets/icons/clothing.svg";
            case "medical supplies":
                return "/assets/icons/medical-support.svg";
            case "shelter & housing":
                return "/assets/icons/shelter.svg";
            case "transportation":
                return "/assets/icons/transportation.svg";
            case "educational resources":
                return "/assets/icons/education.svg";
            case "tools & equipment":
                return "/assets/icons/tools.svg";
            case "technology":
                return "/assets/icons/technology.svg";
            default:
                return "/assets/icons/resources.png";
        }
    }
    
    private String getCategoryEmoji(String category) {
        switch (category.toLowerCase()) {
            case "food & groceries": return "🍽️";
            case "clothing & textiles": return "👕";
            case "medical supplies": return "🏥";
            case "shelter & housing": return "🏠";
            case "transportation": return "🚗";
            case "educational resources": return "📚";
            case "tools & equipment": return "🔧";
            case "technology": return "📱";
            default: return "📦";
        }
    }
    
    private void filterResources() {
        if (allResources == null) return;
        
        String searchText = searchField.getText().toLowerCase();
        String selectedCategory = categoryFilter.getValue();
        
        List<Resource> filtered = allResources.stream()
            .filter(resource -> {
                boolean matchesSearch = searchText.isEmpty() || 
                    resource.getName().toLowerCase().contains(searchText) ||
                    resource.getDescription().toLowerCase().contains(searchText);
                
                boolean matchesCategory = "All Categories".equals(selectedCategory) ||
                    resource.getCategory().equals(selectedCategory.substring(4)); // Remove emoji
                
                return matchesSearch && matchesCategory;
            })
            .toList();
        
        displayResources(filtered);
    }
    
    private void showEmptyState() {
        resourcesGrid.getChildren().clear();
        
        VBox emptyState = new VBox(16);
        emptyState.setStyle("-fx-alignment: center; -fx-padding: 80px;");
        
        Label emptyIcon = new Label("📦");
        emptyIcon.setStyle("-fx-font-size: 64px;");
        
        Label emptyTitle = new Label("No Resources Found");
        emptyTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #64748B;");
        
        Label emptyDesc = new Label("Try adjusting your search criteria or category filter.");
        emptyDesc.setStyle("-fx-font-size: 16px; -fx-text-fill: #94A3B8;");
        
        emptyState.getChildren().addAll(emptyIcon, emptyTitle, emptyDesc);
        resourcesGrid.add(emptyState, 0, 0, 3, 1);
    }
    
    private void handleRequestResource(Resource resource) {
        try {
            // Load the new request dialog
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/new-request.fxml"));
            Parent root = loader.load();
            
            // Get the controller and pre-select the resource
            NewRequestController controller = loader.getController();
            controller.preSelectResource(resource);
            
            // Create full screen window
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Request: " + resource.getName());
            
            Scene scene = new Scene(root, 1400, 900);
            dialogStage.setScene(scene);
            
            dialogStage.setMinWidth(1200);
            dialogStage.setMinHeight(800);
            dialogStage.setResizable(true);
            dialogStage.centerOnScreen();
            
            dialogStage.show();
            
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to open request dialog", e);
            showError("Error", "Failed to open request dialog: " + e.getMessage());
        }
    }
    
    @FXML @SuppressWarnings("unused") // Used by FXML for button actions
    private void handleCreateNewRequest() {
        try {
            // Load the new request dialog
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/new-request.fxml"));
            Parent root = loader.load();
            
            // Create full screen window
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Create New Request - Community Resource Hub");
            
            Scene scene = new Scene(root, 1400, 900);
            dialogStage.setScene(scene);
            
            dialogStage.setMinWidth(1200);
            dialogStage.setMinHeight(800);
            dialogStage.setResizable(true);
            dialogStage.centerOnScreen();
            
            dialogStage.show();
            
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to open new request dialog", e);
            showError("Error", "Failed to open new request dialog: " + e.getMessage());
        }
    }
}