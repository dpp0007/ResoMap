package com.communityhub.ui.controllers;

import com.communityhub.model.User;

/**
 * Interface for dashboard controllers
 * Provides common dashboard functionality
 */
public interface DashboardController {
    
    /**
     * Sets the current user for the dashboard
     * @param user Current logged-in user
     */
    void setCurrentUser(User user);
    
    /**
     * Refreshes dashboard data
     */
    void refreshData();
    
    /**
     * Handles logout action
     */
    void handleLogout();
}