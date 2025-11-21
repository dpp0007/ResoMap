package com.communityhub.ui.components;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Custom empty state component for tables
 * Provides a better user experience when tables have no data
 */
public class EmptyStateComponent extends VBox {
    
    private final Label iconLabel;
    private final Label titleLabel;
    private final Label messageLabel;
    private final Button actionButton;
    
    public EmptyStateComponent() {
        this(null, null, null, null, null);
    }
    
    public EmptyStateComponent(String icon, String title, String message, String buttonText, Runnable buttonAction) {
        super(15);
        setAlignment(Pos.CENTER);
        setPrefSize(500, 300);
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        setStyle("-fx-background-color: transparent; -fx-padding: 40px;");
        
        // Icon
        iconLabel = new Label(icon != null ? icon : "📋");
        iconLabel.setStyle("-fx-font-size: 42px; -fx-opacity: 0.7;");
        iconLabel.setAlignment(Pos.CENTER);
        
        // Title
        titleLabel = new Label(title != null ? title : "No Data Available");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        titleLabel.setStyle("-fx-text-fill: #374151;");
        
        // Message
        messageLabel = new Label(message != null ? message : "There are currently no items to display.");
        messageLabel.setFont(Font.font("System", FontWeight.NORMAL, 14));
        messageLabel.setStyle("-fx-text-fill: #6B7280;");
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(300);
        
        // Action button
        actionButton = new Button(buttonText != null ? buttonText : "Refresh");
        actionButton.getStyleClass().add("primary-button");
        actionButton.setStyle("-fx-padding: 12px 24px; -fx-font-size: 14px;");
        if (buttonAction != null) {
            actionButton.setOnAction(e -> buttonAction.run());
        }
        
        getChildren().addAll(iconLabel, titleLabel, messageLabel, actionButton);
    }
    
    public static EmptyStateComponent forRequests(Runnable refreshAction) {
        return new EmptyStateComponent(
            "📋",
            "No Requests Found",
            "You don't have any requests yet. Make a new request to get started!",
            "Create New Request",
            refreshAction
        );
    }
    
    public static EmptyStateComponent forResources(Runnable refreshAction) {
        return new EmptyStateComponent(
            "📦",
            "No Resources Available",
            "There are currently no resources available. Please check back later or contact support.",
            "Refresh",
            refreshAction
        );
    }
    
    public static EmptyStateComponent forUsers(Runnable refreshAction) {
        return new EmptyStateComponent(
            "👥",
            "No Users Found",
            "No users match your current filter criteria. Try adjusting your search or filters.",
            "Reset Filters",
            refreshAction
        );
    }
    
    public static EmptyStateComponent forSearchResults(Runnable clearSearchAction) {
        return new EmptyStateComponent(
            "🔍",
            "No Results Found",
            "Your search didn't return any results. Try different keywords or clear your search.",
            "Clear Search",
            clearSearchAction
        );
    }
    
    public void setIcon(String icon) {
        iconLabel.setText(icon);
    }
    
    public void setTitle(String title) {
        titleLabel.setText(title);
    }
    
    public void setMessage(String message) {
        messageLabel.setText(message);
    }
    
    public void setButtonText(String text) {
        actionButton.setText(text);
    }
    
    public void setButtonAction(Runnable action) {
        actionButton.setOnAction(e -> action.run());
    }
    
    public void hideButton() {
        actionButton.setVisible(false);
        actionButton.setManaged(false);
    }
    
    public void showButton() {
        actionButton.setVisible(true);
        actionButton.setManaged(true);
    }
}