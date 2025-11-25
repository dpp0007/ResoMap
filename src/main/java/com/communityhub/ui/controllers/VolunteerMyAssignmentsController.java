package com.communityhub.ui.controllers;

import com.communityhub.core.BaseController;
import com.communityhub.core.ErrorHandler;
import com.communityhub.exception.DatabaseException;
import com.communityhub.model.Request;
import com.communityhub.model.RequestStatus;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for My Assignments view in Volunteer Dashboard
 * 
 * @author ResoMap Team
 * @version 2.0
 */
public class VolunteerMyAssignmentsController extends BaseController implements Initializable {
    
    @FXML private ComboBox<String> statusFilter;
    @FXML private Button refreshBtn;
    @FXML private TableView<Request> assignmentsTable;
    @FXML private TableColumn<Request, String> idCol;
    @FXML private TableColumn<Request, String> resourceCol;
    @FXML private TableColumn<Request, String> requesterCol;
    @FXML private TableColumn<Request, String> statusCol;
    @FXML private TableColumn<Request, String> urgencyCol;
    @FXML private TableColumn<Request, String> dateCol;
    @FXML private TableColumn<Request, Void> actionCol;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            initializeServices();
            setupUI();
            loadData();
        } catch (DatabaseException e) {
            ErrorHandler.handleDatabaseError(e);
        }
    }
    
    private void setupUI() {
        // Setup status filter
        statusFilter.setItems(FXCollections.observableArrayList(
            "All Status", "👤 Assigned", "🔄 In Progress", "✅ Completed"
        ));
        statusFilter.setValue("All Status");
        statusFilter.setOnAction(e -> loadData());
        
        // Setup table columns
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        resourceCol.setCellValueFactory(new PropertyValueFactory<>("resourceName"));
        requesterCol.setCellValueFactory(new PropertyValueFactory<>("requesterName"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        urgencyCol.setCellValueFactory(new PropertyValueFactory<>("urgency"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("assignedAt"));
        
        // Setup action column with Update Status button
        actionCol.setCellFactory(column -> new TableCell<>() {
            private final Button updateBtn = new Button("Update Status");
            
            {
                updateBtn.getStyleClass().add("secondary-button");
                updateBtn.setOnAction(event -> {
                    Request request = getTableView().getItems().get(getIndex());
                    handleUpdateStatus(request);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : updateBtn);
            }
        });
    }
    
    @FXML
    private void handleRefresh() {
        loadData();
    }
    
    private void loadData() {
        if (!validateUserLoggedIn()) {
            return;
        }
        
        executeAsync(
            () -> requestService.getRequestsByVolunteer(currentUser.getUserId()),
            this::updateTable
        );
    }
    
    private void updateTable(List<Request> assignments) {
        Platform.runLater(() -> {
            assignmentsTable.setItems(FXCollections.observableArrayList(assignments));
        });
    }
    
    private void handleUpdateStatus(Request request) {
        // Create status update dialog
        Dialog<RequestStatus> dialog = new Dialog<>();
        dialog.setTitle("Update Request Status");
        dialog.setHeaderText("Update status for request #" + request.getId());
        
        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);
        
        ComboBox<RequestStatus> statusCombo = new ComboBox<>();
        statusCombo.setItems(FXCollections.observableArrayList(
            RequestStatus.ASSIGNED,
            RequestStatus.IN_PROGRESS,
            RequestStatus.COMPLETED
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
        executeAsync(
            () -> {
                request.setStatus(newStatus);
                requestService.updateRequest(request);
                return null;
            },
            result -> {
                showInfo("Success", "Request status updated successfully!");
                loadData(); // Refresh list
            }
        );
    }
}
