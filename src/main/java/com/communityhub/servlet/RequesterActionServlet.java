package com.communityhub.servlet;

import com.communityhub.exception.DatabaseException;
import com.communityhub.model.Request;
import com.communityhub.model.RequestStatus;
import com.communityhub.model.User;
import com.communityhub.service.RequestService;
import com.communityhub.service.UserService;
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
 * Servlet for requester-specific actions on their own requests
 * 
 * REQUESTER ACTIONS:
 * - Edit request (only when status = PENDING)
 * - Cancel request (only when status = PENDING or ASSIGNED)
 * - Add notes to request
 * - Submit feedback on completed requests
 * 
 * SECURITY:
 * - Enforces REQUESTER role in servlet (not just UI)
 * - Validates requester owns the request before allowing action
 * - Prevents unauthorized modifications
 * - Logs all requester actions for audit trail
 */
@WebServlet("/requester-action")
public class RequesterActionServlet extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(RequesterActionServlet.class.getName());
    private RequestService requestService;
    private UserService userService;
    
    @Override
    public void init() throws ServletException {
        try {
            this.requestService = new RequestService();
            this.userService = new UserService();
        } catch (DatabaseException e) {
            logger.severe("Failed to initialize services: " + e.getMessage());
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
        
        // Enforce REQUESTER role (backend security check)
        if (!currentUser.getRole().toString().equals("REQUESTER")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only requesters can perform this action");
            logger.warning("Unauthorized requester action attempt by user: " + currentUser.getUserId());
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
            
            // Validate requester owns this request
            if (!targetRequest.getRequesterId().equals(currentUser.getUserId())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, 
                    "You can only modify your own requests");
                logger.warning("Unauthorized request modification attempt by requester: " + 
                    currentUser.getUserId() + " on request: " + requestId);
                return;
            }
            
            // Route to appropriate action
            if ("edit-request".equals(action)) {
                handleEditRequest(targetRequest, currentUser, request, response, redirectUrl);
            } else if ("cancel-request".equals(action)) {
                handleCancelRequest(targetRequest, currentUser, request, response, redirectUrl);
            } else if ("add-note".equals(action)) {
                handleAddNote(targetRequest, currentUser, request, response, redirectUrl);
            } else if ("submit-feedback".equals(action)) {
                handleSubmitFeedback(targetRequest, currentUser, request, response, redirectUrl);
            } else {
                session.setAttribute("error", "Unknown action: " + action);
                response.sendRedirect(redirectUrl);
            }
            
        } catch (DatabaseException e) {
            logger.severe("Database error in requester action: " + e.getMessage());
            session.setAttribute("error", "Database error: " + e.getMessage());
            response.sendRedirect(redirectUrl);
        }
    }
    
    /**
     * Handle requester editing their request
     * Only allowed when status = PENDING
     */
    private void handleEditRequest(Request targetRequest, User requester, 
            HttpServletRequest request, HttpServletResponse response, String redirectUrl) 
            throws ServletException, IOException, DatabaseException {
        
        // Validate current status is PENDING
        if (targetRequest.getStatus() != RequestStatus.PENDING) {
            HttpSession session = request.getSession();
            session.setAttribute("error", "Request can only be edited when status is PENDING");
            response.sendRedirect(redirectUrl);
            return;
        }
        
        String description = request.getParameter("description");
        String urgencyLevel = request.getParameter("urgencyLevel");
        
        // Validate input
        if (description == null || description.trim().isEmpty()) {
            HttpSession session = request.getSession();
            session.setAttribute("error", "Description cannot be empty");
            response.sendRedirect(redirectUrl);
            return;
        }
        
        if (description.length() > 500) {
            HttpSession session = request.getSession();
            session.setAttribute("error", "Description must be 500 characters or less");
            response.sendRedirect(redirectUrl);
            return;
        }
        
        if (urgencyLevel == null || urgencyLevel.trim().isEmpty()) {
            HttpSession session = request.getSession();
            session.setAttribute("error", "Urgency level is required");
            response.sendRedirect(redirectUrl);
            return;
        }
        
        // Sanitize input
        description = ValidationUtils.sanitizeInput(description);
        
        // Update request
        targetRequest.setDescription(description);
        targetRequest.setUrgencyLevel(com.communityhub.model.UrgencyLevel.valueOf(urgencyLevel));
        requestService.updateRequest(targetRequest);
        
        logger.info("Requester " + requester.getUserId() + " edited request: " + targetRequest.getRequestId());
        HttpSession session = request.getSession();
        session.setAttribute("success", "Request updated successfully!");
        response.sendRedirect(redirectUrl);
    }
    
    /**
     * Handle requester cancelling their request
     * Only allowed when status = PENDING or ASSIGNED
     */
    private void handleCancelRequest(Request targetRequest, User requester, 
            HttpServletRequest request, HttpServletResponse response, String redirectUrl) 
            throws ServletException, IOException, DatabaseException {
        
        // Validate current status
        if (targetRequest.getStatus() != RequestStatus.PENDING && 
            targetRequest.getStatus() != RequestStatus.ASSIGNED) {
            HttpSession session = request.getSession();
            session.setAttribute("error", "Request can only be cancelled when status is PENDING or ASSIGNED");
            response.sendRedirect(redirectUrl);
            return;
        }
        
        String reason = request.getParameter("reason");
        if (reason == null) {
            reason = "Cancelled by requester";
        }
        
        // Sanitize reason
        reason = ValidationUtils.sanitizeInput(reason);
        
        // Update status to CANCELLED
        targetRequest.setStatus(RequestStatus.CANCELLED);
        targetRequest.setDescription(targetRequest.getDescription() + " [Cancelled by requester: " + reason + "]");
        requestService.updateRequest(targetRequest);
        
        logger.info("Requester " + requester.getUserId() + " cancelled request: " + targetRequest.getRequestId() + 
            " with reason: " + reason);
        HttpSession session = request.getSession();
        session.setAttribute("success", "Request cancelled successfully!");
        response.sendRedirect(redirectUrl);
    }
    
    /**
     * Handle requester adding notes to their request
     */
    private void handleAddNote(Request targetRequest, User requester, 
            HttpServletRequest request, HttpServletResponse response, String redirectUrl) 
            throws ServletException, IOException, DatabaseException {
        
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
            "\n[Note from requester " + requester.getUsername() + "]: " + note;
        targetRequest.setDescription(updatedDescription);
        requestService.updateRequest(targetRequest);
        
        logger.info("Requester " + requester.getUserId() + " added note to request: " + targetRequest.getRequestId());
        HttpSession session = request.getSession();
        session.setAttribute("success", "Note added successfully!");
        response.sendRedirect(redirectUrl);
    }
    
    /**
     * Handle requester submitting feedback on completed request
     */
    private void handleSubmitFeedback(Request targetRequest, User requester, 
            HttpServletRequest request, HttpServletResponse response, String redirectUrl) 
            throws ServletException, IOException, DatabaseException {
        
        // Validate request is completed
        if (targetRequest.getStatus() != RequestStatus.COMPLETED) {
            HttpSession session = request.getSession();
            session.setAttribute("error", "Feedback can only be submitted for completed requests");
            response.sendRedirect(redirectUrl);
            return;
        }
        
        String ratingStr = request.getParameter("rating");
        String comment = request.getParameter("comment");
        
        // Validate rating
        if (ratingStr == null || ratingStr.trim().isEmpty()) {
            HttpSession session = request.getSession();
            session.setAttribute("error", "Rating is required");
            response.sendRedirect(redirectUrl);
            return;
        }
        
        int rating;
        try {
            rating = Integer.parseInt(ratingStr);
            if (rating < 1 || rating > 5) {
                HttpSession session = request.getSession();
                session.setAttribute("error", "Rating must be between 1 and 5");
                response.sendRedirect(redirectUrl);
                return;
            }
        } catch (NumberFormatException e) {
            HttpSession session = request.getSession();
            session.setAttribute("error", "Invalid rating value");
            response.sendRedirect(redirectUrl);
            return;
        }
        
        // Sanitize comment
        if (comment != null && !comment.trim().isEmpty()) {
            comment = ValidationUtils.sanitizeInput(comment);
        } else {
            comment = "";
        }
        
        // Append feedback to description
        String feedbackText = "\n[Feedback from requester " + requester.getUsername() + " - Rating: " + rating + "/5";
        if (!comment.isEmpty()) {
            feedbackText += " - Comment: " + comment;
        }
        feedbackText += "]";
        
        targetRequest.setDescription(targetRequest.getDescription() + feedbackText);
        requestService.updateRequest(targetRequest);
        
        logger.info("Requester " + requester.getUserId() + " submitted feedback on request: " + 
            targetRequest.getRequestId() + " with rating: " + rating);
        HttpSession session = request.getSession();
        session.setAttribute("success", "Feedback submitted successfully! Thank you for your feedback.");
        response.sendRedirect(redirectUrl);
    }
}
