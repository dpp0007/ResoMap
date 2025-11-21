package com.communityhub.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Controller for placeholder content screens
 */
public class PlaceholderContentController {
    
    @FXML private Label titleLabel;
    @FXML private Label descriptionLabel;
    
    /**
     * Sets the content for the placeholder
     * @param title Content title
     * @param description Content description
     */
    public void setContent(String title, String description) {
        if (titleLabel != null) {
            titleLabel.setText(title);
        }
        if (descriptionLabel != null) {
            descriptionLabel.setText(description);
        }
    }
}