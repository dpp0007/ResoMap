package com.communityhub.ui.controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.communityhub.exception.DatabaseException;
import com.communityhub.model.Resource;
import com.communityhub.service.DataLoadingService;
import com.communityhub.service.ResourceService;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

/**
 * Controller for resource management interface
 * Demonstrates TableView operations, search/filtering with streams, and background data loading
 */
public class ResourceManagementController implements Initializable {
    
    private static final Logger logger = Logger.getLogger(ResourceManagementController.class.getName());
    
    @FXML private TextField searchField;
    @FXML private ComboBox<String> categoryFilter;
    @FXML private CheckBox availableOnlyFilter;
    @FXML private TableView<Resource> resourceTable;
    @FXML private TableColumn<Resource, String> nameColumn;
    @FXML private TableColumn<Resource, String> categoryColumn;
    @FXML private TableColumn<Resource, String> descriptionColumn;
    @FXML private TableColumn<Resource, Integer> quantityColumn;
    @FXML private TableColumn<Resource, String> locationColumn;
    @FXML private TableColumn<Resource, String> statusColumn;
    @FXML private TableColumn<Resource, Void> actionsColumn;
    @FXML private Button addResourceButton;
    @FXML private Button refreshButton;
    @FXML private Label statusLabel;
    @FXML private Label recordCountLabel;
    @FXML private ProgressIndicator loadingIndicator;
    
    private ResourceService resourceService;
    private DataLoadingService dataLoadingService;
    private ObservableList<Resource> allResources;
    private ObservableList<Resource> filteredResources;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            resourceService = new ResourceService();
            dataLoadingService = new DataLoadingService();
            
            setupTableView();
            setupFilters();
            setupEventHandlers();
            loadResourcesAsync();
            
        } catch (DatabaseException e) {
            logger.log(Level.SEVERE, "Failed to initialize resource management", e);
            showError("Initialization Error", "Failed to initialize resource management: " + e.getMessage());
        }
    }
    
    /**
     * Sets up the TableView with columns and cell factories
     */
    private void setupTableView() {
        allResources = FXCollections.observableArrayList();
        filteredResources = FXCollections.observableArrayList();
        resourceTable.setItems(filteredResources);
        
        // Setup action column with buttons
        actionsColumn.setCellFactory(param -> new TableCell<Resource, Void>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox buttonBox = new HBox(5, editButton, deleteButton);
            
            {
                editButton.getStyleClass().add("secondary-button");
                deleteButton.getStyleClass().add("danger-button");
                
                editButton.setOnAction(e -> {
                    Resource resource = getTableView().getItems().get(getIndex());
                    handleEditResource(resource);
                });
                
                deleteButton.setOnAction(e -> {
                    Resource resource = getTableView().getItems().get(getIndex());
                    handleDeleteResource(resource);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttonBox);
            }
        });
        
        // Setup row selection
        resourceTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                statusLabel.setText("Selected: " + newSelection.getName());
            } else {
                statusLabel.setText("Ready");
            }
        });
    }
    
    /**
     * Sets up search and filter functionality
     */
    private void setupFilters() {
        // Search field listener with debouncing
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(this::applyFilters);
        });
        
        // Category filter listener
        categoryFilter.valueProperty().addListener((obs, oldVal, newVal) -> {
            applyFilters();
        });
        
        // Available only filter listener
        availableOnlyFilter.selectedProperty().addListener((obs, oldVal, newVal) -> {
            applyFilters();
        });
    }
    
    /**
     * Sets up event handlers
     */
    private void setupEventHandlers() {
        // Double-click to edit
        resourceTable.setRowFactory(tv -> {
            TableRow<Resource> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    handleEditResource(row.getItem());
                }
            });
            return row;
        });
    }
    
    /**
     * Loads resources asynchronously with progress indication
     */
    private void loadResourcesAsync() {
        setLoading(true);
        statusLabel.setText("Loading resources...");
        
        dataLoadingService.loadDataAsync(
            "loadResources",
            () -> {
                try {
                    return resourceService.getAllResources();
                } catch (Exception e) {
                    throw new RuntimeException("Failed to load resources", e);
                }
            },
            this::onResourcesLoaded,
            this::onLoadingFailed
        );
    }
    
    /**
     * Callback for successful resource loading
     * @param resources Loaded resources
     */
    private void onResourcesLoaded(List<Resource> resources) {
        allResources.setAll(resources);
        updateCategoryFilter();
        applyFilters();
        setLoading(false);
        statusLabel.setText("Resources loaded successfully");
        
        logger.info("Loaded " + resources.size() + " resources");
    }
    
    /**
     * Callback for failed resource loading
     * @param error Loading error
     */
    private void onLoadingFailed(Throwable error) {
        setLoading(false);
        statusLabel.setText("Failed to load resources");
        logger.log(Level.SEVERE, "Failed to load resources", error);
        showError("Loading Error", "Failed to load resources: " + error.getMessage());
    }
    
    /**
     * Updates the category filter with available categories
     */
    private void updateCategoryFilter() {
        Set<String> categories = allResources.stream()
            .map(Resource::getCategory)
            .collect(Collectors.toSet());
        
        categoryFilter.getItems().clear();
        categoryFilter.getItems().add("All Categories");
        categoryFilter.getItems().addAll(categories.stream().sorted().collect(Collectors.toList()));
        categoryFilter.setValue("All Categories");
    }
    
    /**
     * Applies search and filter criteria using streams
     */
    private void applyFilters() {
        String searchText = searchField.getText();
        String selectedCategory = categoryFilter.getValue();
        boolean availableOnly = availableOnlyFilter.isSelected();
        
        List<Resource> filtered = allResources.stream()
            .filter(resource -> {
                // Search filter
                if (searchText != null && !searchText.trim().isEmpty()) {
                    String lowerSearch = searchText.toLowerCase();
                    if (!resource.getName().toLowerCase().contains(lowerSearch) &&
                        !resource.getDescription().toLowerCase().contains(lowerSearch) &&
                        !resource.getCategory().toLowerCase().contains(lowerSearch)) {
                        return false;
                    }
                }
                
                // Category filter
                if (selectedCategory != null && !"All Categories".equals(selectedCategory)) {
                    if (!resource.getCategory().equals(selectedCategory)) {
                        return false;
                    }
                }
                
                // Available only filter
                if (availableOnly && !resource.isAvailable()) {
                    return false;
                }
                
                return true;
            })
            .sorted((r1, r2) -> {
                // Sort by availability first, then by name
                if (r1.isAvailable() && !r2.isAvailable()) return -1;
                if (r2.isAvailable() && !r1.isAvailable()) return 1;
                return r1.getName().compareToIgnoreCase(r2.getName());
            })
            .collect(Collectors.toList());
        
        filteredResources.setAll(filtered);
        updateRecordCount();
    }
    
    /**
     * Updates the record count label
     */
    private void updateRecordCount() {
        int total = allResources.size();
        int filtered = filteredResources.size();
        
        if (total == filtered) {
            recordCountLabel.setText(total + " resources");
        } else {
            recordCountLabel.setText(filtered + " of " + total + " resources");
        }
    }
    
    /**
     * Handles add resource button click
     * @param event Action event
     */
    @FXML
    private void handleAddResource(ActionEvent event) {
        try {
            // Create a simple input dialog for adding resources
            Dialog<Resource> dialog = new Dialog<>();
            dialog.setTitle("Add New Resource");
            dialog.setHeaderText("Enter resource details");
            
            // Set the button types
            ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);
            
            // Create the form fields
            TextField nameField = new TextField();
            nameField.setPromptText("Resource name");
            ComboBox<String> categoryField = new ComboBox<>();
            categoryField.getItems().addAll("Food", "Medical", "Shelter", "Clothing", "Transportation", "Education");
            categoryField.setPromptText("Select category");
            TextField descriptionField = new TextField();
            descriptionField.setPromptText("Description");
            TextField quantityField = new TextField();
            quantityField.setPromptText("Quantity (number)");
            TextField locationField = new TextField();
            locationField.setPromptText("Location");
            
            // Layout the form
            javafx.scene.layout.VBox vbox = new javafx.scene.layout.VBox(10);
            vbox.getChildren().addAll(
                new Label("Name:"), nameField,
                new Label("Category:"), categoryField,
                new Label("Description:"), descriptionField,
                new Label("Quantity:"), quantityField,
                new Label("Location:"), locationField
            );
            vbox.setPadding(new javafx.geometry.Insets(20));
            
            dialog.getDialogPane().setContent(vbox);
            
            // Enable/Disable add button depending on whether fields are filled
            javafx.scene.Node addButton = dialog.getDialogPane().lookupButton(addButtonType);
            addButton.setDisable(true);
            
            nameField.textProperty().addListener((observable, oldValue, newValue) -> {
                addButton.setDisable(newValue.trim().isEmpty());
            });
            
            // Convert the result when the add button is clicked
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == addButtonType) {
                    try {
                        int quantity = Integer.parseInt(quantityField.getText().trim());
                        Resource resource = new Resource(
                            nameField.getText().trim(),
                            categoryField.getValue() != null ? categoryField.getValue() : "General",
                            descriptionField.getText().trim(),
                            quantity,
                            locationField.getText().trim(),
                            "", // contactInfo
                            "system" // createdBy
                        );
                        return resource;
                    } catch (NumberFormatException e) {
                        showError("Invalid Input", "Quantity must be a valid number.");
                        return null;
                    }
                }
                return null;
            });
            
            java.util.Optional<Resource> result = dialog.showAndWait();
            result.ifPresent(resource -> {
                addResourceAsync(resource);
            });
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error opening add resource dialog", e);
            showError("Error", "Failed to open add resource dialog: " + e.getMessage());
        }
    }
    
    /**
     * Handles refresh button click
     * @param event Action event
     */
    @FXML
    private void handleRefresh(ActionEvent event) {
        loadResourcesAsync();
    }
    
    /**
     * Handles edit resource action
     * @param resource Resource to edit
     */
    private void handleEditResource(Resource resource) {
        if (resource == null) return;
        
        try {
            // Create edit dialog with pre-filled values
            Dialog<Resource> dialog = new Dialog<>();
            dialog.setTitle("Edit Resource");
            dialog.setHeaderText("Modify resource details");
            
            ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
            
            // Create form fields with current values
            TextField nameField = new TextField(resource.getName());
            ComboBox<String> categoryField = new ComboBox<>();
            categoryField.getItems().addAll("Food", "Medical", "Shelter", "Clothing", "Transportation", "Education");
            categoryField.setValue(resource.getCategory());
            TextField descriptionField = new TextField(resource.getDescription());
            TextField quantityField = new TextField(String.valueOf(resource.getQuantity()));
            TextField locationField = new TextField(resource.getLocation());
            
            javafx.scene.layout.VBox vbox = new javafx.scene.layout.VBox(10);
            vbox.getChildren().addAll(
                new Label("Name:"), nameField,
                new Label("Category:"), categoryField,
                new Label("Description:"), descriptionField,
                new Label("Quantity:"), quantityField,
                new Label("Location:"), locationField
            );
            vbox.setPadding(new javafx.geometry.Insets(20));
            
            dialog.getDialogPane().setContent(vbox);
            
            javafx.scene.Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
            nameField.textProperty().addListener((observable, oldValue, newValue) -> {
                saveButton.setDisable(newValue.trim().isEmpty());
            });
            
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == saveButtonType) {
                    try {
                        int quantity = Integer.parseInt(quantityField.getText().trim());
                        // Create updated resource with same ID and creation info
                        Resource updatedResource = new Resource(
                            resource.getResourceId(),
                            nameField.getText().trim(),
                            descriptionField.getText().trim(),
                            categoryField.getValue(),
                            quantity,
                            locationField.getText().trim(),
                            resource.getContactInfo(),
                            resource.getCreatedBy(),
                            resource.getCreatedAt(),
                            java.time.LocalDateTime.now()
                        );
                        return updatedResource;
                    } catch (NumberFormatException e) {
                        showError("Invalid Input", "Quantity must be a valid number.");
                        return null;
                    }
                }
                return null;
            });
            
            java.util.Optional<Resource> result = dialog.showAndWait();
            result.ifPresent(updatedResource -> {
                updateResourceAsync(updatedResource);
            });
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error opening edit resource dialog", e);
            showError("Error", "Failed to open edit resource dialog: " + e.getMessage());
        }
    }
    
    /**
     * Handles delete resource action
     * @param resource Resource to delete
     */
    private void handleDeleteResource(Resource resource) {
        if (resource == null) return;
        
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Delete");
        confirmDialog.setHeaderText("Delete Resource");
        confirmDialog.setContentText("Are you sure you want to delete '" + resource.getName() + "'?\\n\\nThis action cannot be undone.");
        
        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                deleteResourceAsync(resource);
            }
        });
    }
    
    /**
     * Deletes a resource asynchronously
     * @param resource Resource to delete
     */
    private void deleteResourceAsync(Resource resource) {
        setLoading(true);
        statusLabel.setText("Deleting resource...");
        
        dataLoadingService.loadDataAsync(
            "deleteResource",
            () -> {
                try {
                    resourceService.deleteResource(resource.getResourceId());
                    return resource;
                } catch (Exception e) {
                    throw new RuntimeException("Failed to delete resource", e);
                }
            },
            deletedResource -> {
                allResources.remove(deletedResource);
                applyFilters();
                setLoading(false);
                statusLabel.setText("Resource deleted successfully");
                showInfo("Success", "Resource '" + deletedResource.getName() + "' has been deleted.");
            },
            error -> {
                setLoading(false);
                statusLabel.setText("Failed to delete resource");
                logger.log(Level.SEVERE, "Failed to delete resource", error);
                showError("Delete Error", "Failed to delete resource: " + error.getMessage());
            }
        );
    }
    
    /**
     * Sets loading state
     * @param loading Whether loading is in progress
     */
    private void setLoading(boolean loading) {
        loadingIndicator.setVisible(loading);
        addResourceButton.setDisable(loading);
        refreshButton.setDisable(loading);
        resourceTable.setDisable(loading);
    }
    
    /**
     * Shows an error dialog
     * @param title Error title
     * @param message Error message
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Shows an info dialog
     * @param title Info title
     * @param message Info message
     */
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Adds a new resource asynchronously
     * @param resource Resource to add
     */
    private void addResourceAsync(Resource resource) {
        setLoading(true);
        statusLabel.setText("Adding resource...");
        
        dataLoadingService.loadDataAsync(
            "addResource",
            () -> {
                try {
                    resourceService.addResource(resource);
                    return resource;
                } catch (Exception e) {
                    throw new RuntimeException("Failed to add resource", e);
                }
            },
            addedResource -> {
                allResources.add(addedResource);
                updateCategoryFilter();
                applyFilters();
                setLoading(false);
                statusLabel.setText("Resource added successfully");
                showInfo("Success", "Resource '" + addedResource.getName() + "' has been added.");
            },
            error -> {
                setLoading(false);
                statusLabel.setText("Failed to add resource");
                logger.log(Level.SEVERE, "Failed to add resource", error);
                showError("Add Error", "Failed to add resource: " + error.getMessage());
            }
        );
    }
    
    /**
     * Updates a resource asynchronously
     * @param resource Resource to update
     */
    private void updateResourceAsync(Resource resource) {
        setLoading(true);
        statusLabel.setText("Updating resource...");
        
        dataLoadingService.loadDataAsync(
            "updateResource",
            () -> {
                try {
                    resourceService.updateResource(resource);
                    return resource;
                } catch (Exception e) {
                    throw new RuntimeException("Failed to update resource", e);
                }
            },
            updatedResource -> {
                // Update the resource in the list
                for (int i = 0; i < allResources.size(); i++) {
                    if (allResources.get(i).getResourceId().equals(updatedResource.getResourceId())) {
                        allResources.set(i, updatedResource);
                        break;
                    }
                }
                updateCategoryFilter();
                applyFilters();
                setLoading(false);
                statusLabel.setText("Resource updated successfully");
                showInfo("Success", "Resource '" + updatedResource.getName() + "' has been updated.");
            },
            error -> {
                setLoading(false);
                statusLabel.setText("Failed to update resource");
                logger.log(Level.SEVERE, "Failed to update resource", error);
                showError("Update Error", "Failed to update resource: " + error.getMessage());
            }
        );
    }

    /**
     * Cleanup method
     */
    public void cleanup() {
        if (dataLoadingService != null) {
            dataLoadingService.shutdown();
        }
    }
}