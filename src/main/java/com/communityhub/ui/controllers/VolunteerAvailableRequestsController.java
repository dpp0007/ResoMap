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

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for Available Requests view in Volunteer Dashboard
 * 
 * @author ResoMap Team
 * @version 2.0
 */
public class VolunteerAvailableRequestsController extends BaseController implements Initializable {
    
    @FXML private ComboBox<String> urgencyFilter;
    @FXML private Button refreshBtn;
    @FXML private TableView<Request> requestsTable;
    @FXML private TableColumn<Request, String> idCol;
    @FXML private TableColumn<Request, String> resourceCol;
    @FXML private TableColumn<Request, String> requesterCol;
    @FXML private TableColumn<Request, String> urgencyCol;
    @FXML private TableColumn<Request, String> descriptionCol;
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
        // Setup urgency filter
        urgencyFilter.setItems(FXCollections.observableArrayList(
            "All Urgency", "🔴 Critical", "🟠 High", "🟡 Medium", "🟢 Low"
        ));
        urgencyFilter.setValue("All Urgency");
        urgencyFilter.setOnAction(e -> loadData());
        
        // Setup table columns
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        resourceCol.setCellValueFactory(new PropertyValueFactory<>("resourceName"));
        requesterCol.setCellValueFactory(new PropertyValueFactory<>("requesterName"));
        urgencyCol.setCellValueFactory(new PropertyValueFactory<>("urgency"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        
        // Setup action column with Accept button
        actionCol.setCellFactory(column -> new TableCell<>() {
            private final Button acceptBtn = new Button("Accept");
            
            {
                acceptBtn.getStyleClass().add("primary-button");
                acceptBtn.setOnAction(event -> {
                    Request request = getTableView().getItems().get(getIndex());
                    handleAcceptRequest(request);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : acceptBtn);
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
            () -> requestService.getAvailableRequests(),
            this::updateTable
        );
    }
    
    private void updateTable(List<Request> requests) {
        Platform.runLater(() -> {
            requestsTable.setItems(FXCollections.observableArrayList(requests));
        });
    }
    
    private void handleAcceptRequest(Request request) {
        if (!validateUserLoggedIn()) {
            return;
        }
        
        boolean confirmed = showConfirmation(
            "Accept Request",
            "Accept this request?\n\n" +
            "Resource: " + request.getResourceName() + "\n" +
            "Urgency: " + request.getUrgency()
        );
        
        if (confirmed) {
            executeAsync(
                () -> {
                    request.setVolunteerId(currentUser.getUserId());
                    request.setStatus(RequestStatus.ASSIGNED);
                    requestService.updateRequest(request);
                    return null;
                },
                result -> {
                    showInfo("Success", "Request accepted successfully!");
                    loadData(); // Refresh list
                }
            );
        }
    }
}
