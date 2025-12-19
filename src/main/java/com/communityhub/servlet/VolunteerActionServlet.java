package com.communityhub.servlet;

import com.communityhub.exception.DatabaseException;
import com.communityhub.model.Request;
import com.communityhub.model.RequestStatus;
import com.communityhub.model.User;
import com.communityhub.service.RequestService;
import com.communityhub.util.ValidationUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Servlet for volunteer-specific actions on assigned requests
 * 
 * VOLUNTEER ACTIONS:
 * - Accept assigned request (ASSIGNED → IN_PROGRESS)
 * - Reject assigned request (ASSIGNED → CANCELLED with reason)
 * - Mark request as completed (IN_PROGRESS → COMPLETED)
 * - Add progress notes to request
 * 
 * SECURITY:
 * - Enforces VOLUNTEER role in servlet (not just UI)
 * - Validates volunteer owns the request before allowing action
 * - Prevents unauthorized modifications
 * - Logs all volunteer actions for audit trail
 */
@WebServlet("/volunteer-action")
public class VolunteerActionServlet extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(VolunteerActionServlet.class.getName());
    private RequestService requestService;
    
    @Override
    public void init() throws ServletException {
        try {
            this.requestService = new RequestService();
        } catch (DatabaseException e) {
            logger.severe("Failed to initialize RequestService: " + e.getMessage());
            throw new ServletException("Database initialization failed", e);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        // Validate session
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User currentUser = (User) session.getAttribute("user");
        
        // Enforce VOLUNTEER role (backend security check)
        if (!currentUser.getRole().toString().equals("VOLUNTEER")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only volunteers can perform this action");
            logger.warning("Unauthorized volunteer action attempt by user: " + currentUser.getUserId());
            return;
        }
        
        String action = request.getParameter("action");
        String requestId = request.getParameter("requestId");
        String redirectUrl = request.getParameter("redirectUrl");
        
        if (redirectUrl == null || redirectUrl.isEmpty()) {
            redirectUrl = request.getContextPath() + "/requests";
        }
        
        // Extract path from full URL if needed
        if (redirectUrl.contains("://")) {
            try {
                java.net.URL url = new java.net.URL(redirectUrl);
                redirectUrl = url.getPath();
            } catch (Exception e) {
                redirectUrl = request.getContextPath() + "/requests";
            }
        }
        
        try {
            // Validate request ID
            if (requestId == null || requestId.trim().isEmpty()) {
                session.setAttribute("error", "Request ID is required");
                response.sendRedirect(redirectUrl);
                return;
            }
            
            // Fetch the request
            Request targetRequest = requestService.getRequest(requestId);
            if (targetRequest == null) {
                session.setAttribute("error", "Request not found");
                response.sendRedirect(redirectUrl);
                return;
            }
            
            // Validate volunteer owns this request
            if (targetRequest.getVolunteerId() == null || !targetRequest.getVolunteerId().equals(currentUser.getUserId())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, 
                    "You can only modify requests assigned to you");
                logger.warning("Unauthorized request modification attempt by volunteer: " + 
                    currentUser.getUserId() + " on request: " + requestId);
                return;
            }
            
            // Route to appropriate action
            if ("accept-request".equals(action)) {
                handleAcceptRequest(targetRequest, currentUser, request, response, redirectUrl);
            } else if ("reject-request".equals(action)) {
                handleRejectRequest(targetRequest, currentUser, request, response, redirectUrl);
            } else if ("start-work".equals(action)) {
                handleStartWork(targetRequest, currentUser, request, response, redirectUrl);
            } else if ("mark-completed".equals(action)) {
                handleMarkCompleted(targetRequest, currentUser, request, response, redirectUrl);
            } else if ("add-note".equals(action)) {
                handleAddNote(targetRequest, currentUser, request, response, redirectUrl);
            } else {
                session.setAttribute("error", "Unknown action: " + action);
                response.sendRedirect(redirectUrl);
            }
            
        } catch (DatabaseException e) {
            logger.severe("Database error in volunteer action: " + e.getMessage());
            session.setAttribute("error", "Database error: " + e.getMessage());
            response.sendRedirect(redirectUrl);
        }
    }
    
    /**
     * Handle volunteer accepting an assigned request
     * Transition: ASSIGNED → IN_PROGRESS
     */
    private void handleAcceptRequest(Request targetRequest, User volunteer, 
            HttpServletRequest request, HttpServletResponse response, String redirectUrl) 
            throws ServletException, IOException, DatabaseException {
        
        // Validate current status is ASSIGNED
        if (targetRequest.getStatus() != RequestStatus.ASSIGNED) {
            HttpSession session = request.getSession();
            session.setAttribute("error", "Request must be in ASSIGNED status to accept");
            response.sendRedirect(redirectUrl);
            return;
        }
        
        // Update status to IN_PROGRESS
        targetRequest.setStatus(RequestStatus.IN_PROGRESS);
        requestService.updateRequest(targetRequest);
        
        logger.info("Volunteer " + volunteer.getUserId() + " accepted request: " + targetRequest.getRequestId());
        HttpSession session = request.getSession();
        session.setAttribute("success", "Request accepted! You can now start working on it.");
        response.sendRedirect(redirectUrl);
    }
    
    /**
     * Handle volunteer rejecting an assigned request
     * Transition: ASSIGNED → CANCELLED
     */
    private void handleRejectRequest(Request targetRequest, User volunteer, 
            HttpServletRequest request, HttpServletResponse response, String redirectUrl) 
            throws ServletException, IOException, DatabaseException {
        
        // Validate current status is ASSIGNED
        if (targetRequest.getStatus() != RequestStatus.ASSIGNED) {
            HttpSession session = request.getSession();
            session.setAttribute("error", "Request must be in ASSIGNED status to reject");
            response.sendRedirect(redirectUrl);
            return;
        }
        
        String reason = request.getParameter("reason");
        if (reason == null) {
            reason = "Rejected by volunteer";
        }
        
        // Sanitize reason
        reason = ValidationUtils.sanitizeInput(reason);
        
        // Update status to CANCELLED and clear volunteer assignment
        targetRequest.setStatus(RequestStatus.CANCELLED);
        targetRequest.setVolunteerId(null);
        targetRequest.setDescription(targetRequest.getDescription() + " [Rejected: " + reason + "]");
        requestService.updateRequest(targetRequest);
        
        logger.info("Volunteer " + volunteer.getUserId() + " rejected request: " + targetRequest.getRequestId() + 
            " with reason: " + reason);
        HttpSession session = request.getSession();
        session.setAttribute("success", "Request rejected. It has been returned to pending status.");
        response.sendRedirect(redirectUrl);
    }
    
    /**
     * Handle volunteer starting work on a request
     * Transition: ASSIGNED → IN_PROGRESS
     */
    private void handleStartWork(Request targetRequest, User volunteer, 
            HttpServletRequest request, HttpServletResponse response, String redirectUrl) 
            throws ServletException, IOException, DatabaseException {
        
        // Validate current status is ASSIGNED
        if (targetRequest.getStatus() != RequestStatus.ASSIGNED) {
            HttpSession session = request.getSession();
            session.setAttribute("error", "Request must be in ASSIGNED status to start work");
            response.sendRedirect(redirectUrl);
            return;
        }
        
        // Update status to IN_PROGRESS
        targetRequest.setStatus(RequestStatus.IN_PROGRESS);
        requestService.updateRequest(targetRequest);
        
        logger.info("Volunteer " + volunteer.getUserId() + " started work on request: " + targetRequest.getRequestId());
        HttpSession session = request.getSession();
        session.setAttribute("success", "Work started! You can now add progress notes.");
        response.sendRedirect(redirectUrl);
    }
    
    /**
     * Handle volunteer marking request as completed
     * Transition: IN_PROGRESS → COMPLETED
     */
    private void handleMarkCompleted(Request targetRequest, User volunteer, 
            HttpServletRequest request, HttpServletResponse response, String redirectUrl) 
            throws ServletException, IOException, DatabaseException {
        
        // Validate current status is IN_PROGRESS
        if (targetRequest.getStatus() != RequestStatus.IN_PROGRESS) {
            HttpSession session = request.getSession();
            session.setAttribute("error", "Request must be IN_PROGRESS to mark as completed");
            response.sendRedirect(redirectUrl);
            return;
        }
        
        String completionNotes = request.getParameter("completionNotes");
        if (completionNotes != null && !completionNotes.trim().isEmpty()) {
            completionNotes = ValidationUtils.sanitizeInput(completionNotes);
            targetRequest.setDescription(targetRequest.getDescription() + 
                " [Completed by volunteer: " + completionNotes + "]");
        }
        
        // Update status to COMPLETED
        targetRequest.setStatus(RequestStatus.COMPLETED);
        requestService.updateRequest(targetRequest);
        
        logger.info("Volunteer " + volunteer.getUserId() + " marked request as completed: " + targetRequest.getRequestId());
        HttpSession session = request.getSession();
        session.setAttribute("success", "Request marked as completed! Thank you for your work.");
        response.sendRedirect(redirectUrl);
    }
    
    /**
     * Handle volunteer adding progress notes to request
     * Appends notes to request description
     */
    private void handleAddNote(Request targetRequest, User volunteer, 
            HttpServletRequest request, HttpServletResponse response, String redirectUrl) 
            throws ServletException, IOException, DatabaseException {
        
        // Validate request is assigned to volunteer and in progress
        if (targetRequest.getStatus() != RequestStatus.IN_PROGRESS && 
            targetRequest.getStatus() != RequestStatus.ASSIGNED) {
            HttpSession session = request.getSession();
            session.setAttribute("error", "Can only add notes to assigned or in-progress requests");
            response.sendRedirect(redirectUrl);
            return;
        }
        
        String note = request.getParameter("note");
        if (note == null || note.trim().isEmpty()) {
            HttpSession session = request.getSession();
            session.setAttribute("error", "Note cannot be empty");
            response.sendRedirect(redirectUrl);
            return;
        }
        
        // Sanitize note
        note = ValidationUtils.sanitizeInput(note);
        
        // Append note to description
        String updatedDescription = targetRequest.getDescription() + 
            "\n[Note from " + volunteer.getUsername() + "]: " + note;
        targetRequest.setDescription(updatedDescription);
        requestService.updateRequest(targetRequest);
        
        logger.info("Volunteer " + volunteer.getUserId() + " added note to request: " + targetRequest.getRequestId());
        HttpSession session = request.getSession();
        session.setAttribute("success", "Note added successfully!");
        response.sendRedirect(redirectUrl);
    }
}
