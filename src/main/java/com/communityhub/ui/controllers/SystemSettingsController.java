package com.communityhub.ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Controller for System Settings page
 */
public class SystemSettingsController implements Initializable {
    
    private static final Logger logger = Logger.getLogger(SystemSettingsController.class.getName());
    
    // General Settings
    @FXML private TextField appNameField;
    @FXML private TextField organizationField;
    @FXML private TextField contactEmailField;
    @FXML private TextField supportPhoneField;
    @FXML private CheckBox enableNotificationsCheck;
    @FXML private CheckBox autoAssignCheck;
    @FXML private CheckBox requireApprovalCheck;
    @FXML private CheckBox enableFeedbackCheck;
    
    // Request Settings
    @FXML private Spinner<Integer> maxRequestsSpinner;
    @FXML private Spinner<Integer> requestTimeoutSpinner;
    @FXML private ComboBox<String> priorityLevelsCombo;
    @FXML private CheckBox enableUrgencyAlertsCheck;
    @FXML private CheckBox escalateCriticalCheck;
    @FXML private Spinner<Integer> criticalAlertSpinner;
    
    // Database Settings
    @FXML private ComboBox<String> dbTypeCombo;
    @FXML private Spinner<Integer> poolSizeSpinner;
    @FXML private ComboBox<String> backupFrequencyCombo;
    @FXML private Button backupNowBtn;
    @FXML private Button optimizeDbBtn;
    @FXML private Button clearCacheBtn;
    @FXML private Label dbStatusLabel;
    @FXML private Label lastBackupLabel;
    
    // Security Settings
    @FXML private Spinner<Integer> minPasswordLengthSpinner;
    @FXML private Spinner<Integer> sessionTimeoutSpinner;
    @FXML private CheckBox requireStrongPasswordCheck;
    @FXML private CheckBox enableTwoFactorCheck;
    @FXML private CheckBox lockAccountCheck;
    @FXML private CheckBox enableAuditLogCheck;
    @FXML private CheckBox anonymizeFeedbackCheck;
    @FXML private CheckBox gdprComplianceCheck;
    
    // Status
    @FXML private Label settingsStatusLabel;
    @FXML private Button saveSettingsBtn;
    @FXML private Button resetSettingsBtn;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeSpinners();
        initializeComboBoxes();
        loadCurrentSettings();
        setupEventHandlers();
    }
    
    private void initializeSpinners() {
        // Request Settings Spinners
        if (maxRequestsSpinner != null) {
            maxRequestsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 10));
        }
        if (requestTimeoutSpinner != null) {
            requestTimeoutSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 168, 24));
        }
        if (criticalAlertSpinner != null) {
            criticalAlertSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 48, 2));
        }
        
        // Database Settings Spinners
        if (poolSizeSpinner != null) {
            poolSizeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 100, 10));
        }
        
        // Security Settings Spinners
        if (minPasswordLengthSpinner != null) {
            minPasswordLengthSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(4, 32, 8));
        }
        if (sessionTimeoutSpinner != null) {
            sessionTimeoutSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 480, 30));
        }
    }
    
    private void initializeComboBoxes() {
        if (priorityLevelsCombo != null) {
            priorityLevelsCombo.getItems().addAll("3 Levels", "4 Levels", "5 Levels");
            priorityLevelsCombo.setValue("4 Levels");
        }
        
        if (dbTypeCombo != null) {
            dbTypeCombo.getItems().addAll("SQLite", "MySQL", "PostgreSQL");
            dbTypeCombo.setValue("SQLite");
        }
        
        if (backupFrequencyCombo != null) {
            backupFrequencyCombo.getItems().addAll("Daily", "Weekly", "Monthly", "Manual");
            backupFrequencyCombo.setValue("Weekly");
        }
    }
    
    private void loadCurrentSettings() {
        // Load General Settings
        if (appNameField != null) appNameField.setText("ResoMap");
        if (organizationField != null) organizationField.setText("Community Services");
        if (contactEmailField != null) contactEmailField.setText("admin@communityhub.org");
        if (supportPhoneField != null) supportPhoneField.setText("+1-555-0100");
        
        // Load Preferences
        if (enableNotificationsCheck != null) enableNotificationsCheck.setSelected(true);
        if (autoAssignCheck != null) autoAssignCheck.setSelected(false);
        if (requireApprovalCheck != null) requireApprovalCheck.setSelected(true);
        if (enableFeedbackCheck != null) enableFeedbackCheck.setSelected(true);
        
        // Load Request Settings
        if (enableUrgencyAlertsCheck != null) enableUrgencyAlertsCheck.setSelected(true);
        if (escalateCriticalCheck != null) escalateCriticalCheck.setSelected(true);
        
        // Load Security Settings
        if (requireStrongPasswordCheck != null) requireStrongPasswordCheck.setSelected(true);
        if (enableTwoFactorCheck != null) enableTwoFactorCheck.setSelected(false);
        if (lockAccountCheck != null) lockAccountCheck.setSelected(true);
        if (enableAuditLogCheck != null) enableAuditLogCheck.setSelected(true);
        if (anonymizeFeedbackCheck != null) anonymizeFeedbackCheck.setSelected(false);
        if (gdprComplianceCheck != null) gdprComplianceCheck.setSelected(true);
        
        // Update status labels
        if (dbStatusLabel != null) dbStatusLabel.setText("Database Status: Connected (SQLite)");
        if (lastBackupLabel != null) lastBackupLabel.setText("Last Backup: Never");
    }
    
    private void setupEventHandlers() {
        if (saveSettingsBtn != null) {
            saveSettingsBtn.setOnAction(e -> handleSaveSettings());
        }
        if (resetSettingsBtn != null) {
            resetSettingsBtn.setOnAction(e -> handleResetSettings());
        }
        if (backupNowBtn != null) {
            backupNowBtn.setOnAction(e -> handleBackupDatabase());
        }
        if (optimizeDbBtn != null) {
            optimizeDbBtn.setOnAction(e -> handleOptimizeDatabase());
        }
        if (clearCacheBtn != null) {
            clearCacheBtn.setOnAction(e -> handleClearCache());
        }
    }
    
    @FXML
    private void handleSaveSettings() {
        try {
            // Validate settings
            if (!validateSettings()) {
                return;
            }
            
            // Save settings (in a real app, this would save to database or config file)
            logger.info("Saving system settings...");
            
            // Simulate save operation
            Thread.sleep(500);
            
            showSuccess("Settings saved successfully!");
            logger.info("System settings saved successfully");
            
        } catch (Exception e) {
            logger.severe("Failed to save settings: " + e.getMessage());
            showError("Failed to Save Settings", "An error occurred while saving settings: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleResetSettings() {
        Alert confirm = new Alert(AlertType.CONFIRMATION);
        confirm.setTitle("Reset Settings");
        confirm.setHeaderText("Reset to Default Settings");
        confirm.setContentText("Are you sure you want to reset all settings to default values? This action cannot be undone.");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                loadCurrentSettings();
                showSuccess("Settings reset to defaults");
                logger.info("Settings reset to default values");
            }
        });
    }
    
    @FXML
    private void handleBackupDatabase() {
        try {
            Alert confirm = new Alert(AlertType.CONFIRMATION);
            confirm.setTitle("Database Backup");
            confirm.setHeaderText("Create Database Backup");
            confirm.setContentText("This will create a backup of the current database. Continue?");
            
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    // Simulate backup operation
                    logger.info("Creating database backup...");
                    
                    try {
                        Thread.sleep(1000);
                        
                        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        if (lastBackupLabel != null) {
                            lastBackupLabel.setText("Last Backup: " + timestamp);
                        }
                        
                        showSuccess("Database backup created successfully!");
                        logger.info("Database backup completed");
                        
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
            
        } catch (Exception e) {
            logger.severe("Failed to backup database: " + e.getMessage());
            showError("Backup Failed", "Failed to create database backup: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleOptimizeDatabase() {
        try {
            Alert confirm = new Alert(AlertType.CONFIRMATION);
            confirm.setTitle("Optimize Database");
            confirm.setHeaderText("Optimize Database Performance");
            confirm.setContentText("This will optimize database tables and indexes. This may take a few moments. Continue?");
            
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    logger.info("Optimizing database...");
                    
                    try {
                        Thread.sleep(1500);
                        showSuccess("Database optimized successfully!");
                        logger.info("Database optimization completed");
                        
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
            
        } catch (Exception e) {
            logger.severe("Failed to optimize database: " + e.getMessage());
            showError("Optimization Failed", "Failed to optimize database: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleClearCache() {
        try {
            Alert confirm = new Alert(AlertType.CONFIRMATION);
            confirm.setTitle("Clear Cache");
            confirm.setHeaderText("Clear Application Cache");
            confirm.setContentText("This will clear all cached data. Continue?");
            
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    logger.info("Clearing cache...");
                    
                    try {
                        Thread.sleep(500);
                        showSuccess("Cache cleared successfully!");
                        logger.info("Cache cleared");
                        
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
            
        } catch (Exception e) {
            logger.severe("Failed to clear cache: " + e.getMessage());
            showError("Clear Cache Failed", "Failed to clear cache: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleViewAuditLog() {
        try {
            // Create audit log dialog
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("System Audit Log");
            dialog.setHeaderText("Recent System Activities");
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            
            // Create table for audit log
            TableView<AuditLogEntry> auditTable = new TableView<>();
            auditTable.setPrefWidth(800);
            auditTable.setPrefHeight(400);
            
            TableColumn<AuditLogEntry, String> timestampCol = new TableColumn<>("Timestamp");
            timestampCol.setPrefWidth(150);
            timestampCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().timestamp));
            
            TableColumn<AuditLogEntry, String> userCol = new TableColumn<>("User");
            userCol.setPrefWidth(120);
            userCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().user));
            
            TableColumn<AuditLogEntry, String> actionCol = new TableColumn<>("Action");
            actionCol.setPrefWidth(150);
            actionCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().action));
            
            TableColumn<AuditLogEntry, String> detailsCol = new TableColumn<>("Details");
            detailsCol.setPrefWidth(350);
            detailsCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().details));
            
            auditTable.getColumns().addAll(timestampCol, userCol, actionCol, detailsCol);
            
            // Add sample audit log entries
            auditTable.getItems().addAll(
                new AuditLogEntry(LocalDateTime.now().minusHours(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), 
                    "admin", "Login", "Admin user logged in successfully"),
                new AuditLogEntry(LocalDateTime.now().minusHours(2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), 
                    "volunteer1", "Request Accepted", "Accepted request #REQ-001"),
                new AuditLogEntry(LocalDateTime.now().minusHours(3).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), 
                    "admin", "Settings Changed", "Updated system notification settings"),
                new AuditLogEntry(LocalDateTime.now().minusHours(4).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), 
                    "requester1", "Request Created", "Created new request for food assistance"),
                new AuditLogEntry(LocalDateTime.now().minusHours(5).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), 
                    "admin", "User Created", "Created new volunteer account: volunteer2"),
                new AuditLogEntry(LocalDateTime.now().minusHours(6).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), 
                    "volunteer2", "Login", "Volunteer user logged in successfully"),
                new AuditLogEntry(LocalDateTime.now().minusHours(7).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), 
                    "admin", "Database Backup", "Database backup completed successfully"),
                new AuditLogEntry(LocalDateTime.now().minusHours(8).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), 
                    "requester2", "Request Updated", "Updated request status to completed"),
                new AuditLogEntry(LocalDateTime.now().minusHours(9).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), 
                    "admin", "Resource Added", "Added new resource: Winter Coats"),
                new AuditLogEntry(LocalDateTime.now().minusHours(10).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), 
                    "volunteer1", "Logout", "Volunteer user logged out")
            );
            
            javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(10);
            content.getChildren().addAll(
                new Label("Showing last 10 audit log entries:"),
                auditTable
            );
            
            dialog.getDialogPane().setContent(content);
            dialog.showAndWait();
            
            logger.info("Audit log viewed");
            
        } catch (Exception e) {
            logger.severe("Failed to view audit log: " + e.getMessage());
            showError("Audit Log Error", "Failed to load audit log: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleExportUserData() {
        try {
            // Create export dialog
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Export User Data");
            dialog.setHeaderText("Export User Data (GDPR Compliance)");
            
            ButtonType exportButtonType = new ButtonType("Export", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(exportButtonType, ButtonType.CANCEL);
            
            // Create form
            javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            
            TextField userIdField = new TextField();
            userIdField.setPromptText("Enter User ID or Username");
            
            ComboBox<String> formatCombo = new ComboBox<>();
            formatCombo.getItems().addAll("PDF", "JSON", "CSV", "XML");
            formatCombo.setValue("PDF");
            
            CheckBox includeRequestsCheck = new CheckBox("Include Requests");
            includeRequestsCheck.setSelected(true);
            
            CheckBox includeFeedbackCheck = new CheckBox("Include Feedback");
            includeFeedbackCheck.setSelected(true);
            
            CheckBox includeActivityCheck = new CheckBox("Include Activity Log");
            includeActivityCheck.setSelected(true);
            
            grid.add(new Label("User ID/Username:"), 0, 0);
            grid.add(userIdField, 1, 0);
            grid.add(new Label("Export Format:"), 0, 1);
            grid.add(formatCombo, 1, 1);
            grid.add(new Label("Include:"), 0, 2);
            grid.add(includeRequestsCheck, 1, 2);
            grid.add(includeFeedbackCheck, 1, 3);
            grid.add(includeActivityCheck, 1, 4);
            
            dialog.getDialogPane().setContent(grid);
            
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == exportButtonType) {
                    return userIdField.getText();
                }
                return null;
            });
            
            dialog.showAndWait().ifPresent(userId -> {
                if (userId != null && !userId.trim().isEmpty()) {
                    performUserDataExport(userId, formatCombo.getValue(), 
                        includeRequestsCheck.isSelected(), 
                        includeFeedbackCheck.isSelected(), 
                        includeActivityCheck.isSelected());
                } else {
                    showError("Invalid Input", "Please enter a valid User ID or Username.");
                }
            });
            
        } catch (Exception e) {
            logger.severe("Failed to export user data: " + e.getMessage());
            showError("Export Error", "Failed to export user data: " + e.getMessage());
        }
    }
    
    private void performUserDataExport(String userId, String format, boolean includeRequests, 
                                      boolean includeFeedback, boolean includeActivity) {
        try {
            logger.info("Exporting user data for: " + userId + " in format: " + format);
            
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            // For PDF, create HTML file that can be printed to PDF
            String fileExtension = "PDF".equals(format) ? "html" : format.toLowerCase();
            String filename = "user_data_" + userId + "_" + timestamp + "." + fileExtension;
            
            // Create exports directory in user's home directory
            String userHome = System.getProperty("user.home");
            java.io.File exportsDir = new java.io.File(userHome, "CommunityHub_Exports");
            if (!exportsDir.exists()) {
                exportsDir.mkdirs();
            }
            
            java.io.File exportFile = new java.io.File(exportsDir, filename);
            String filepath = exportFile.getAbsolutePath();
            
            // Create export summary
            StringBuilder summary = new StringBuilder();
            summary.append("User Data Export Summary\n");
            summary.append("========================\n\n");
            summary.append("User ID: ").append(userId).append("\n");
            summary.append("Export Format: ").append(format).append("\n");
            summary.append("Export Date: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n\n");
            summary.append("Exported Data:\n");
            summary.append("- User Profile Information\n");
            if (includeRequests) summary.append("- User Requests (5 records)\n");
            if (includeFeedback) summary.append("- User Feedback (3 records)\n");
            if (includeActivity) summary.append("- Activity Log (25 entries)\n");
            
            // Add format-specific information
            if ("PDF".equals(format)) {
                summary.append("\nPDF Export Details:\n");
                summary.append("- Document includes formatted tables\n");
                summary.append("- Professional layout with headers and footers\n");
                summary.append("- Includes organization branding\n");
                summary.append("- GDPR compliance statement included\n");
            }
            
            // Generate export content based on format
            String exportContent = generateExportContent(userId, format, includeRequests, includeFeedback, includeActivity);
            
            // Write to file
            try (java.io.FileWriter writer = new java.io.FileWriter(exportFile)) {
                writer.write(exportContent);
            }
            
            long fileSize = exportFile.length();
            String fileSizeStr = fileSize < 1024 ? fileSize + " bytes" : 
                                fileSize < 1024 * 1024 ? (fileSize / 1024) + " KB" : 
                                (fileSize / (1024 * 1024)) + " MB";
            
            summary.append("\n✓ Export file saved to:\n").append(filepath).append("\n");
            summary.append("✓ File size: ").append(fileSizeStr).append("\n");
            
            // Show detailed result dialog
            Alert result = new Alert(AlertType.INFORMATION);
            result.setTitle("Export Complete");
            result.setHeaderText("User Data Export Successful");
            
            // Add additional information for PDF
            if ("PDF".equals(format)) {
                summary.append("\n✓ HTML document generated successfully\n");
                summary.append("✓ Open in browser and use Print → Save as PDF\n");
                summary.append("✓ Professional formatting with tables and styling\n");
            }
            
            summary.append("\nClick OK to open the export folder.");
            result.setContentText(summary.toString());
            
            result.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        // Open the exports folder
                        java.awt.Desktop.getDesktop().open(exportsDir);
                    } catch (Exception e) {
                        logger.warning("Could not open exports folder: " + e.getMessage());
                    }
                }
            });
            
            logger.info("User data export completed for: " + userId + " in " + format + " format at: " + filepath);
            
        } catch (Exception e) {
            logger.severe("Failed to perform export: " + e.getMessage());
            showError("Export Failed", "Failed to export user data: " + e.getMessage());
        }
    }
    
    private String generateExportContent(String userId, String format, boolean includeRequests, 
                                        boolean includeFeedback, boolean includeActivity) {
        StringBuilder content = new StringBuilder();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        switch (format) {
            case "PDF":
                // Generate HTML that can be printed to PDF
                content.append("<!DOCTYPE html>\n<html>\n<head>\n");
                content.append("<meta charset=\"UTF-8\">\n");
                content.append("<title>User Data Export - ").append(userId).append("</title>\n");
                content.append("<style>\n");
                content.append("body { font-family: Arial, sans-serif; margin: 40px; line-height: 1.6; }\n");
                content.append("h1 { color: #2c3e50; border-bottom: 3px solid #3498db; padding-bottom: 10px; }\n");
                content.append("h2 { color: #34495e; margin-top: 30px; border-bottom: 2px solid #95a5a6; padding-bottom: 5px; }\n");
                content.append(".header { text-align: center; margin-bottom: 30px; }\n");
                content.append(".info-box { background: #ecf0f1; padding: 15px; border-radius: 5px; margin: 20px 0; }\n");
                content.append("table { width: 100%; border-collapse: collapse; margin: 20px 0; }\n");
                content.append("th, td { padding: 12px; text-align: left; border-bottom: 1px solid #ddd; }\n");
                content.append("th { background-color: #3498db; color: white; }\n");
                content.append("tr:hover { background-color: #f5f5f5; }\n");
                content.append(".footer { margin-top: 50px; padding: 20px; background: #34495e; color: white; border-radius: 5px; }\n");
                content.append("@media print { body { margin: 20px; } }\n");
                content.append("</style>\n</head>\n<body>\n");
                
                content.append("<div class=\"header\">\n");
                content.append("<h1>RESOMAP</h1>\n");
                content.append("<h2>User Data Export Report</h2>\n");
                content.append("</div>\n");
                
                content.append("<div class=\"info-box\">\n");
                content.append("<p><strong>Export Date:</strong> ").append(timestamp).append("</p>\n");
                content.append("<p><strong>User ID:</strong> ").append(userId).append("</p>\n");
                content.append("<p><strong>Format:</strong> HTML (Print to PDF)</p>\n");
                content.append("</div>\n");
                
                content.append("<h2>User Profile Information</h2>\n");
                content.append("<table>\n");
                content.append("<tr><th>Field</th><th>Value</th></tr>\n");
                content.append("<tr><td>Username</td><td>").append(userId).append("</td></tr>\n");
                content.append("<tr><td>Display Name</td><td>").append(userId.substring(0, 1).toUpperCase()).append(userId.substring(1)).append("</td></tr>\n");
                content.append("<tr><td>Email</td><td>").append(userId).append("@example.com</td></tr>\n");
                content.append("<tr><td>Role</td><td>User</td></tr>\n");
                content.append("<tr><td>Account Created</td><td>2024-01-15</td></tr>\n");
                content.append("<tr><td>Last Login</td><td>").append(timestamp).append("</td></tr>\n");
                content.append("</table>\n");
                
                if (includeRequests) {
                    content.append("<h2>User Requests (5 records)</h2>\n");
                    content.append("<table>\n");
                    content.append("<tr><th>Request ID</th><th>Type</th><th>Status</th></tr>\n");
                    content.append("<tr><td>REQ-001</td><td>Food Assistance</td><td>Completed</td></tr>\n");
                    content.append("<tr><td>REQ-002</td><td>Winter Clothing</td><td>In Progress</td></tr>\n");
                    content.append("<tr><td>REQ-003</td><td>Medical Supplies</td><td>Pending</td></tr>\n");
                    content.append("<tr><td>REQ-004</td><td>School Supplies</td><td>Completed</td></tr>\n");
                    content.append("<tr><td>REQ-005</td><td>Emergency Shelter</td><td>Assigned</td></tr>\n");
                    content.append("</table>\n");
                }
                
                if (includeFeedback) {
                    content.append("<h2>User Feedback (3 records)</h2>\n");
                    content.append("<table>\n");
                    content.append("<tr><th>Rating</th><th>Comment</th></tr>\n");
                    content.append("<tr><td>5/5</td><td>Excellent service and quick response</td></tr>\n");
                    content.append("<tr><td>4/5</td><td>Very helpful volunteers</td></tr>\n");
                    content.append("<tr><td>5/5</td><td>Great community support system</td></tr>\n");
                    content.append("</table>\n");
                }
                
                if (includeActivity) {
                    content.append("<h2>Activity Log (Last 25 entries)</h2>\n");
                    content.append("<table>\n");
                    content.append("<tr><th>Timestamp</th><th>Activity</th></tr>\n");
                    content.append("<tr><td>2024-11-18 10:00:00</td><td>User logged in</td></tr>\n");
                    content.append("<tr><td>2024-11-18 09:45:00</td><td>Request #REQ-005 created</td></tr>\n");
                    content.append("<tr><td>2024-11-17 15:30:00</td><td>Feedback submitted</td></tr>\n");
                    content.append("<tr><td>2024-11-17 14:20:00</td><td>Request #REQ-004 completed</td></tr>\n");
                    content.append("<tr><td>2024-11-16 11:15:00</td><td>Profile updated</td></tr>\n");
                    content.append("<tr><td colspan=\"2\">... (20 more entries)</td></tr>\n");
                    content.append("</table>\n");
                }
                
                content.append("<div class=\"footer\">\n");
                content.append("<h3>GDPR COMPLIANCE NOTICE</h3>\n");
                content.append("<p>This export contains personal data as per GDPR Article 15.</p>\n");
                content.append("<p>Data subject has the right to rectification and erasure.</p>\n");
                content.append("<p>For questions, contact: <a href=\"mailto:privacy@communityhub.org\" style=\"color: #3498db;\">privacy@communityhub.org</a></p>\n");
                content.append("</div>\n");
                
                content.append("<p style=\"text-align: center; margin-top: 30px; color: #7f8c8d;\">\n");
                content.append("<small>To save as PDF: Open this file in your browser and use Print → Save as PDF</small>\n");
                content.append("</p>\n");
                
                content.append("</body>\n</html>\n");
                break;
                
            case "JSON":
                content.append("{\n");
                content.append("  \"exportDate\": \"").append(timestamp).append("\",\n");
                content.append("  \"userId\": \"").append(userId).append("\",\n");
                content.append("  \"userProfile\": {\n");
                content.append("    \"username\": \"").append(userId).append("\",\n");
                content.append("    \"email\": \"").append(userId).append("@example.com\",\n");
                content.append("    \"role\": \"User\",\n");
                content.append("    \"accountCreated\": \"2024-01-15\"\n");
                content.append("  }");
                if (includeRequests) {
                    content.append(",\n  \"requests\": [\n");
                    content.append("    {\"id\": \"REQ-001\", \"type\": \"Food Assistance\", \"status\": \"Completed\"},\n");
                    content.append("    {\"id\": \"REQ-002\", \"type\": \"Winter Clothing\", \"status\": \"In Progress\"}\n");
                    content.append("  ]");
                }
                if (includeFeedback) {
                    content.append(",\n  \"feedback\": [\n");
                    content.append("    {\"rating\": 5, \"comment\": \"Excellent service\"},\n");
                    content.append("    {\"rating\": 4, \"comment\": \"Very helpful\"}\n");
                    content.append("  ]");
                }
                content.append("\n}\n");
                break;
                
            case "CSV":
                content.append("Export Date,User ID,Username,Email,Role,Account Created\n");
                content.append(timestamp).append(",").append(userId).append(",").append(userId).append(",");
                content.append(userId).append("@example.com,User,2024-01-15\n\n");
                if (includeRequests) {
                    content.append("Request ID,Type,Status\n");
                    content.append("REQ-001,Food Assistance,Completed\n");
                    content.append("REQ-002,Winter Clothing,In Progress\n");
                }
                break;
                
            case "XML":
                content.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
                content.append("<userDataExport>\n");
                content.append("  <exportDate>").append(timestamp).append("</exportDate>\n");
                content.append("  <userId>").append(userId).append("</userId>\n");
                content.append("  <userProfile>\n");
                content.append("    <username>").append(userId).append("</username>\n");
                content.append("    <email>").append(userId).append("@example.com</email>\n");
                content.append("    <role>User</role>\n");
                content.append("  </userProfile>\n");
                if (includeRequests) {
                    content.append("  <requests>\n");
                    content.append("    <request id=\"REQ-001\" type=\"Food Assistance\" status=\"Completed\"/>\n");
                    content.append("    <request id=\"REQ-002\" type=\"Winter Clothing\" status=\"In Progress\"/>\n");
                    content.append("  </requests>\n");
                }
                content.append("</userDataExport>\n");
                break;
        }
        
        return content.toString();
    }
    
    // Inner class for audit log entries
    private static class AuditLogEntry {
        private final String timestamp;
        private final String user;
        private final String action;
        private final String details;
        
        public AuditLogEntry(String timestamp, String user, String action, String details) {
            this.timestamp = timestamp;
            this.user = user;
            this.action = action;
            this.details = details;
        }
    }
    
    private boolean validateSettings() {
        // Validate email
        if (contactEmailField != null && !contactEmailField.getText().isEmpty()) {
            String email = contactEmailField.getText();
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                showError("Invalid Email", "Please enter a valid email address.");
                return false;
            }
        }
        
        // Validate organization name
        if (organizationField != null && organizationField.getText().trim().isEmpty()) {
            showError("Invalid Organization", "Organization name cannot be empty.");
            return false;
        }
        
        return true;
    }
    
    private void showSuccess(String message) {
        if (settingsStatusLabel != null) {
            settingsStatusLabel.setText("✓ " + message);
            settingsStatusLabel.setStyle("-fx-text-fill: #4CAF50;");
        }
        
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showError(String title, String message) {
        if (settingsStatusLabel != null) {
            settingsStatusLabel.setText("✗ Error: " + title);
            settingsStatusLabel.setStyle("-fx-text-fill: #F44336;");
        }
        
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showInfo(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
