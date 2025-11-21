package com.communityhub.ui.util;

import com.communityhub.model.Request;
import com.communityhub.model.RequestStatus;
import com.communityhub.model.UrgencyLevel;
import com.communityhub.util.IconManager;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

/**
 * Enhanced table cell factories for better table rendering
 * Provides custom cell renderers with icons, badges, and improved styling
 */
public class EnhancedTableCellFactory {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    @SuppressWarnings("unused") // May be used for future date formatting needs
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, HH:mm");
    
    /**
     * Creates a status cell with colored badges and icons
     */
    public static TableCell<Request, RequestStatus> createStatusCell() {
        return new TableCell<Request, RequestStatus>() {
            @Override
            protected void updateItem(RequestStatus status, boolean empty) {
                super.updateItem(status, empty);
                
                if (empty || status == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    HBox container = new HBox(8);
                    container.setAlignment(Pos.CENTER_LEFT);
                    
                    // Add status icon
                    IconManager iconManager = IconManager.getInstance();
                    var statusIcon = iconManager.getStatusIcon(status.name(), 16);
                    if (statusIcon != null) {
                        container.getChildren().add(statusIcon);
                    }
                    
                    // Add status badge
                    Label statusLabel = new Label(status.getDisplayName());
                    statusLabel.getStyleClass().addAll("badge", getStatusStyleClass(status));
                    statusLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
                    container.getChildren().add(statusLabel);
                    
                    setGraphic(container);
                    setText(null);
                }
            }
            
            private String getStatusStyleClass(RequestStatus status) {
                switch (status) {
                    case PENDING: return "badge-warning";
                    case ASSIGNED: return "badge-info";
                    case IN_PROGRESS: return "badge-primary";
                    case COMPLETED: return "badge-success";
                    case CANCELLED: return "badge-danger";
                    default: return "badge-info";
                }
            }
        };
    }
    
    /**
     * Creates an urgency cell with colored indicators
     */
    public static TableCell<Request, UrgencyLevel> createUrgencyCell() {
        return new TableCell<Request, UrgencyLevel>() {
            @Override
            protected void updateItem(UrgencyLevel urgency, boolean empty) {
                super.updateItem(urgency, empty);
                
                if (empty || urgency == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    HBox container = new HBox(8);
                    container.setAlignment(Pos.CENTER_LEFT);
                    
                    // Add urgency icon/indicator
                    Label indicator = new Label();
                    indicator.setPrefSize(12, 12);
                    indicator.setStyle(
                        "-fx-background-radius: 6px; " +
                        "-fx-background-color: " + getUrgencyColor(urgency) + ";"
                    );
                    container.getChildren().add(indicator);
                    
                    // Add urgency text
                    Label urgencyLabel = new Label(urgency.getDisplayName());
                    urgencyLabel.setTextFill(Color.web(getUrgencyTextColor(urgency)));
                    urgencyLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
                    container.getChildren().add(urgencyLabel);
                    
                    setGraphic(container);
                    setText(null);
                }
            }
            
            private String getUrgencyColor(UrgencyLevel urgency) {
                switch (urgency) {
                    case LOW: return "#10B981";
                    case MEDIUM: return "#F59E0B";
                    case HIGH: return "#F97316";
                    case CRITICAL: return "#EF4444";
                    default: return "#6B7280";
                }
            }
            
            private String getUrgencyTextColor(UrgencyLevel urgency) {
                switch (urgency) {
                    case LOW: return "#065F46";
                    case MEDIUM: return "#92400E";
                    case HIGH: return "#9A3412";
                    case CRITICAL: return "#991B1B";
                    default: return "#374151";
                }
            }
        };
    }
    
    /**
     * Creates a date cell with formatted dates
     */
    public static <T> TableCell<T, java.time.LocalDateTime> createDateCell() {
        return new TableCell<T, java.time.LocalDateTime>() {
            @Override
            protected void updateItem(java.time.LocalDateTime date, boolean empty) {
                super.updateItem(date, empty);
                
                if (empty || date == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    VBox container = new VBox(2);
                    container.setAlignment(Pos.CENTER_LEFT);
                    
                    // Main date
                    Label dateLabel = new Label(date.format(DATE_FORMATTER));
                    dateLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
                    container.getChildren().add(dateLabel);
                    
                    // Time
                    Label timeLabel = new Label(date.format(DateTimeFormatter.ofPattern("HH:mm")));
                    timeLabel.setTextFill(Color.web("#64748B"));
                    timeLabel.setFont(Font.font("System", FontWeight.NORMAL, 11));
                    container.getChildren().add(timeLabel);
                    
                    setGraphic(container);
                    setText(null);
                }
            }
        };
    }
    
    /**
     * Creates an action cell with buttons or helpful message
     */
    public static <T> TableCell<T, Void> createActionCell(
            Consumer<T> onView, 
            Consumer<T> onEdit, 
            Consumer<T> onDelete) {
        
        return new TableCell<T, Void>() {
            private final Button viewBtn = new Button();
            private final Button editBtn = new Button();
            private final Button deleteBtn = new Button();
            private final HBox buttonContainer = new HBox(6);
            private final Label messageLabel = new Label();
            
            {
                // Setup view button
                viewBtn.setText("View");
                viewBtn.getStyleClass().addAll("table-action-button", "view");
                viewBtn.setTooltip(new Tooltip("View Details"));
                viewBtn.setPrefSize(45, 26);
                viewBtn.setMinSize(45, 26);
                viewBtn.setMaxSize(45, 26);
                
                // Setup edit button
                editBtn.setText("Edit");
                editBtn.getStyleClass().addAll("table-action-button", "edit");
                editBtn.setTooltip(new Tooltip("Edit Request"));
                editBtn.setPrefSize(45, 26);
                editBtn.setMinSize(45, 26);
                editBtn.setMaxSize(45, 26);
                
                // Setup delete button
                deleteBtn.setText("Cancel");
                deleteBtn.getStyleClass().addAll("table-action-button", "delete");
                deleteBtn.setTooltip(new Tooltip("Cancel Request"));
                deleteBtn.setPrefSize(50, 26);
                deleteBtn.setMinSize(50, 26);
                deleteBtn.setMaxSize(50, 26);
                
                buttonContainer.setAlignment(Pos.CENTER);
                buttonContainer.setSpacing(3);
                buttonContainer.setPrefHeight(30);
                buttonContainer.setMinHeight(30);
                buttonContainer.setMaxHeight(30);
                buttonContainer.getStyleClass().add("action-cell-container");
                buttonContainer.getChildren().addAll(viewBtn, editBtn, deleteBtn);
                
                // Setup helpful message
                messageLabel.setText("No actions available");
                messageLabel.setStyle("-fx-text-fill: #9CA3AF; -fx-font-style: italic; -fx-font-size: 12px;");
                messageLabel.setAlignment(Pos.CENTER);
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                
                if (empty || getTableView() == null || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    T rowData = getTableView().getItems().get(getIndex());
                    
                    if (rowData != null) {
                        viewBtn.setOnAction(e -> {
                            if (onView != null) onView.accept(rowData);
                        });
                        
                        editBtn.setOnAction(e -> {
                            if (onEdit != null) onEdit.accept(rowData);
                        });
                        
                        deleteBtn.setOnAction(e -> {
                            if (onDelete != null) onDelete.accept(rowData);
                        });
                        
                        setGraphic(buttonContainer);
                    } else {
                        setGraphic(messageLabel);
                    }
                }
            }
        };
    }
    
    /**
     * Creates a truncated text cell with tooltip
     */
    public static <T> TableCell<T, String> createTruncatedTextCell(int maxLength) {
        return new TableCell<T, String>() {
            @Override
            protected void updateItem(String text, boolean empty) {
                super.updateItem(text, empty);
                
                if (empty || text == null) {
                    setText(null);
                    setTooltip(null);
                } else {
                    if (text.length() > maxLength) {
                        setText(text.substring(0, maxLength) + "...");
                        setTooltip(new Tooltip(text));
                    } else {
                        setText(text);
                        setTooltip(null);
                    }
                }
            }
        };
    }
    
    /**
     * Creates a user name cell with avatar placeholder
     */
    public static <T> TableCell<T, String> createUserCell() {
        return new TableCell<T, String>() {
            @Override
            protected void updateItem(String userName, boolean empty) {
                super.updateItem(userName, empty);
                
                if (empty || userName == null || userName.trim().isEmpty()) {
                    setGraphic(null);
                    setText(null);
                } else {
                    HBox container = new HBox(8);
                    container.setAlignment(Pos.CENTER_LEFT);
                    
                    // Avatar placeholder
                    Label avatar = new Label(String.valueOf(userName.charAt(0)).toUpperCase());
                    avatar.setPrefSize(24, 24);
                    avatar.setAlignment(Pos.CENTER);
                    avatar.setStyle(
                        "-fx-background-color: #0096D6; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 12px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-font-size: 11px;"
                    );
                    container.getChildren().add(avatar);
                    
                    // User name
                    Label nameLabel = new Label(userName);
                    nameLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
                    container.getChildren().add(nameLabel);
                    
                    setGraphic(container);
                    setText(null);
                }
            }
        };
    }
    
    /**
     * Creates an ID cell with monospace font
     */
    public static <T> TableCell<T, String> createIdCell() {
        return new TableCell<T, String>() {
            @Override
            protected void updateItem(String id, boolean empty) {
                super.updateItem(id, empty);
                
                if (empty || id == null) {
                    setText(null);
                } else {
                    // Show only first 8 characters of ID
                    String displayId = id.length() > 8 ? id.substring(0, 8) : id;
                    setText(displayId);
                    setFont(Font.font("Consolas", FontWeight.NORMAL, 13));
                    setTextFill(Color.web("#64748B"));
                    setTooltip(new Tooltip("Full ID: " + id));
                }
            }
        };
    }
    
    /**
     * Creates a message cell for empty actions column
     */
    public static <T> TableCell<T, Void> createMessageCell(String message, String actionText, Runnable action) {
        return new TableCell<T, Void>() {
            private final VBox container = new VBox(8);
            private final Label messageLabel = new Label();
            private final Button actionButton = new Button();
            
            {
                messageLabel.setText(message);
                messageLabel.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 13px; -fx-text-alignment: center;");
                messageLabel.setWrapText(true);
                messageLabel.setMaxWidth(200);
                
                actionButton.setText(actionText);
                actionButton.getStyleClass().add("primary-button");
                actionButton.setStyle("-fx-font-size: 12px; -fx-padding: 8px 16px;");
                actionButton.setOnAction(e -> {
                    if (action != null) action.run();
                });
                
                container.setAlignment(Pos.CENTER);
                container.getChildren().addAll(messageLabel, actionButton);
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(container);
            }
        };
    }
}