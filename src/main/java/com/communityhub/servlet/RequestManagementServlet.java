package com.communityhub.servlet;

import com.communityhub.exception.DatabaseException;
import com.communityhub.model.Request;
import com.communityhub.model.RequestStatus;
import com.communityhub.model.UrgencyLevel;
import com.communityhub.model.User;
import com.communityhub.service.RequestService;
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
 * Request Management Servlet
 * Handles admin operations on requests (assign, reassign, status change, urgency change)
 * All operations require ADMIN role and are logged for audit purposes
 */
@WebServlet(name = "RequestManagementServlet", urlPatterns = {"/request-management"})
public class RequestManagementServlet extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(RequestManagementServlet.class.getName());
    private RequestService requestService;
    private UserService userService;
    
    @Override
    public void init() throws ServletException {
        try {
            requestService = new RequestService();
            userService = new UserService();
            logger.info("RequestManagementServlet initialized successfully");
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
        String requestId = request.getParameter("requestId");
        String redirectUrl = request.getParameter("redirectUrl");
        
        try {
            switch (action) {
                case "assign-volunteer":
                    handleAssignVolunteer(request, requestId, currentUser);
                    break;
                case "reassign-volunteer":
                    handleReassignVolunteer(request, requestId, currentUser);
                    break;
                case "unassign-volunteer":
                    handleUnassignVolunteer(requestId, currentUser);
                    break;
                case "change-status":
                    handleChangeStatus(request, requestId, currentUser);
                    break;
                case "change-urgency":
                    handleChangeUrgency(request, requestId, currentUser);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
                    return;
            }
            
            // Set success message
            session.setAttribute("success", "Request updated successfully!");
            
            // Redirect back to requests page or specified URL
            String redirectTo = redirectUrl != null ? redirectUrl : request.getContextPath() + "/requests";
            response.sendRedirect(redirectTo);
            
        } catch (DatabaseException e) {
            logger.log(Level.SEVERE, "Database error performing request management action: " + action, e);
            session.setAttribute("error", "Failed to perform action: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/requests");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error performing request management action: " + action, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "An unexpected error occurred");
        }
    }
    
    // REQUEST MANAGEMENT HANDLERS
    
    private void handleAssignVolunteer(HttpServletRequest request, String requestId, User admin) 
            throws DatabaseException {
        String volunteerId = request.getParameter("volunteerId");
        
        // Validate volunteer exists and has VOLUNTEER role
        User volunteer = userService.getUser(volunteerId);
        if (volunteer == null || !volunteer.getRole().toString().equals("VOLUNTEER")) {
            throw new DatabaseException("Invalid volunteer selected");
        }
        
        requestService.assignVolunteer(requestId, volunteerId);
        logger.info("Admin " + admin.getUsername() + " assigned volunteer " + volunteerId + " to request: " + requestId);
    }
    
    private void handleReassignVolunteer(HttpServletRequest request, String requestId, User admin) 
            throws DatabaseException {
        String newVolunteerId = request.getParameter("volunteerId");
        
        // Validate volunteer exists and has VOLUNTEER role
        User volunteer = userService.getUser(newVolunteerId);
        if (volunteer == null || !volunteer.getRole().toString().equals("VOLUNTEER")) {
            throw new DatabaseException("Invalid volunteer selected");
        }
        
        requestService.assignVolunteer(requestId, newVolunteerId);
        logger.info("Admin " + admin.getUsername() + " reassigned volunteer to " + newVolunteerId + " for request: " + requestId);
    }
    
    private void handleUnassignVolunteer(String requestId, User admin) throws DatabaseException {
        requestService.unassignVolunteer(requestId);
        logger.info("Admin " + admin.getUsername() + " unassigned volunteer from request: " + requestId);
    }
    
    private void handleChangeStatus(HttpServletRequest request, String requestId, User admin) 
            throws DatabaseException {
        String statusStr = request.getParameter("status");
        RequestStatus newStatus = RequestStatus.valueOf(statusStr);
        
        requestService.changeRequestStatus(requestId, newStatus);
        logger.info("Admin " + admin.getUsername() + " changed request status: " + requestId + " -> " + newStatus);
    }
    
    private void handleChangeUrgency(HttpServletRequest request, String requestId, User admin) 
            throws DatabaseException {
        String urgencyStr = request.getParameter("urgency");
        UrgencyLevel newUrgency = UrgencyLevel.valueOf(urgencyStr);
        
        requestService.changeRequestUrgency(requestId, newUrgency);
        logger.info("Admin " + admin.getUsername() + " changed request urgency: " + requestId + " -> " + newUrgency);
    }
}
