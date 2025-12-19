package com.communityhub.servlet;

import com.communityhub.exception.DatabaseException;
import com.communityhub.model.User;
import com.communityhub.model.UserRole;
import com.communityhub.service.RequestService;
import com.communityhub.service.ResourceService;
import com.communityhub.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Admin Action Servlet
 * Handles all admin operations (user management, resource moderation, request control)
 * All operations require ADMIN role and are logged for audit purposes
 */
@WebServlet(name = "AdminActionServlet", urlPatterns = {"/admin-action"})
public class AdminActionServlet extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(AdminActionServlet.class.getName());
    private UserService userService;
    private ResourceService resourceService;
    private RequestService requestService;
    
    @Override
    public void init() throws ServletException {
        try {
            userService = new UserService();
            resourceService = new ResourceService();
            requestService = new RequestService();
            logger.info("AdminActionServlet initialized successfully");
        } catch (DatabaseException e) {
            logger.log(Level.SEVERE, "Failed to initialize services", e);
            throw new ServletException("Service initialization failed", e);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User currentUser = (User) session.getAttribute("user");
        
        // Verify admin role
        if (!currentUser.isAdmin()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, 
                "Only administrators can perform this action");
            return;
        }
        
        String action = request.getParameter("action");
        String targetId = request.getParameter("targetId");
        String redirectUrl = request.getParameter("redirectUrl");
        
        try {
            switch (action) {
                // USER MANAGEMENT ACTIONS
                case "deactivate-user":
                    handleDeactivateUser(targetId, currentUser);
                    break;
                case "activate-user":
                    handleActivateUser(targetId, currentUser);
                    break;
                case "reset-lockout":
                    handleResetLockout(targetId, currentUser);
                    break;
                case "change-role":
                    handleChangeRole(request, targetId, currentUser);
                    break;
                
                // RESOURCE MANAGEMENT ACTIONS
                case "update-quantity":
                    handleUpdateQuantity(request, targetId, currentUser);
                    break;
                case "update-category":
                    handleUpdateCategory(request, targetId, currentUser);
                    break;
                
                // REQUEST CONTROL ACTIONS
                case "escalate-request":
                    handleEscalateRequest(targetId, currentUser);
                    break;
                case "force-close-request":
                    handleForceCloseRequest(targetId, currentUser);
                    break;
                case "reject-request":
                    handleRejectRequest(targetId, currentUser);
                    break;
                
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
                    return;
            }
            
            // Set success message
            session.setAttribute("success", "Action completed successfully!");
            
            // Redirect back to admin panel or specified URL
            String redirectTo = redirectUrl != null ? redirectUrl : request.getContextPath() + "/admin";
            response.sendRedirect(redirectTo);
            
        } catch (DatabaseException e) {
            logger.log(Level.SEVERE, "Database error performing admin action: " + action, e);
            session.setAttribute("error", "Failed to perform action: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error performing admin action: " + action, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "An unexpected error occurred");
        }
    }
    
    // USER MANAGEMENT HANDLERS
    
    private void handleDeactivateUser(String userId, User admin) throws DatabaseException {
        userService.deactivateUser(userId);
        logger.info("Admin " + admin.getUsername() + " deactivated user: " + userId);
    }
    
    private void handleActivateUser(String userId, User admin) throws DatabaseException {
        userService.activateUser(userId);
        logger.info("Admin " + admin.getUsername() + " activated user: " + userId);
    }
    
    private void handleResetLockout(String userId, User admin) throws DatabaseException {
        userService.resetUserLockout(userId);
        logger.info("Admin " + admin.getUsername() + " reset lockout for user: " + userId);
    }
    
    private void handleChangeRole(HttpServletRequest request, String userId, User admin) 
            throws DatabaseException {
        String newRoleStr = request.getParameter("newRole");
        UserRole newRole = UserRole.valueOf(newRoleStr);
        userService.changeUserRole(userId, newRole);
        logger.info("Admin " + admin.getUsername() + " changed role for user " + userId + " to " + newRole);
    }
    
    // RESOURCE MANAGEMENT HANDLERS
    
    private void handleUpdateQuantity(HttpServletRequest request, String resourceId, User admin) 
            throws DatabaseException {
        int newQuantity = Integer.parseInt(request.getParameter("quantity"));
        resourceService.updateResourceQuantity(resourceId, newQuantity);
        logger.info("Admin " + admin.getUsername() + " updated quantity for resource: " + resourceId);
    }
    
    private void handleUpdateCategory(HttpServletRequest request, String resourceId, User admin) 
            throws DatabaseException {
        String newCategory = request.getParameter("category");
        resourceService.updateResourceCategory(resourceId, newCategory);
        logger.info("Admin " + admin.getUsername() + " updated category for resource: " + resourceId);
    }
    
    // REQUEST CONTROL HANDLERS
    
    private void handleEscalateRequest(String requestId, User admin) throws DatabaseException {
        requestService.escalateRequest(requestId);
        logger.info("Admin " + admin.getUsername() + " escalated request: " + requestId);
    }
    
    private void handleForceCloseRequest(String requestId, User admin) throws DatabaseException {
        requestService.forceCloseRequest(requestId);
        logger.info("Admin " + admin.getUsername() + " force-closed request: " + requestId);
    }
    
    private void handleRejectRequest(String requestId, User admin) throws DatabaseException {
        requestService.rejectRequest(requestId);
        logger.info("Admin " + admin.getUsername() + " rejected request: " + requestId);
    }
}
