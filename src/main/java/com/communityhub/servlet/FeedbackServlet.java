package com.communityhub.servlet;

import com.communityhub.exception.DatabaseException;
import com.communityhub.exception.InvalidInputException;
import com.communityhub.model.Feedback;
import com.communityhub.model.User;
import com.communityhub.service.FeedbackService;
import com.communityhub.util.ValidationUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

@WebServlet(name = "FeedbackServlet", urlPatterns = {"/feedback"})
public class FeedbackServlet extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(FeedbackServlet.class.getName());
    private FeedbackService feedbackService;
    
    @Override
    public void init() throws ServletException {
        try {
            feedbackService = new FeedbackService();
            logger.info("FeedbackServlet initialized successfully");
        } catch (DatabaseException e) {
            logger.log(Level.SEVERE, "Failed to initialize FeedbackService", e);
            throw new ServletException("Service initialization failed", e);
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User currentUser = (User) session.getAttribute("user");
        String action = request.getParameter("action");
        
        try {
            if ("new".equals(action)) {
                // Show feedback form
                request.getRequestDispatcher("/jsp/feedback.jsp").forward(request, response);
                return;
            }
            
            // Load all feedback (admin only)
            if (!currentUser.isAdmin()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, 
                    "Only administrators can view all feedback");
                return;
            }
            
            List<Feedback> feedbackList = feedbackService.getAllFeedback();
            request.setAttribute("feedbackList", feedbackList);
            request.getRequestDispatcher("/jsp/feedback.jsp").forward(request, response);
            
        } catch (DatabaseException e) {
            logger.log(Level.SEVERE, "Database error loading feedback", e);
            request.setAttribute("error", "Unable to load feedback");
            request.getRequestDispatcher("/jsp/feedback.jsp").forward(request, response);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error loading feedback", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "An unexpected error occurred");
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
        String action = request.getParameter("action");
        
        try {
            if ("submit".equals(action)) {
                handleSubmitFeedback(request, response, currentUser, session);
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error processing feedback", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "An unexpected error occurred");
        }
    }
    
    private void handleSubmitFeedback(HttpServletRequest request, HttpServletResponse response, 
            User currentUser, HttpSession session) throws ServletException, IOException {
        
        String ratingStr = request.getParameter("rating");
        String comments = request.getParameter("comments");
        String feedbackType = request.getParameter("feedbackType");
        String requestId = request.getParameter("requestId");
        
        try {
            // Validate input
            if (!validateFeedbackInput(ratingStr, comments, feedbackType, request)) {
                request.getRequestDispatcher("/jsp/feedback.jsp").forward(request, response);
                return;
            }
            
            // Sanitize input
            comments = ValidationUtils.sanitizeInput(comments);
            
            // Parse rating
            int rating = Integer.parseInt(ratingStr);
            
            // Create feedback
            Feedback feedback = new Feedback(
                java.util.UUID.randomUUID().toString(),
                currentUser.getUserId(),
                requestId != null && !requestId.isEmpty() ? requestId : null,
                rating,
                comments,
                com.communityhub.model.FeedbackType.valueOf(feedbackType),
                java.time.LocalDateTime.now()
            );
            
            feedbackService.submitFeedback(feedback);
            
            logger.info("Feedback submitted by " + currentUser.getUsername());
            session.setAttribute("success", "Thank you for your feedback!");
            response.sendRedirect(request.getContextPath() + "/dashboard");
            
        } catch (DatabaseException e) {
            logger.log(Level.SEVERE, "Database error submitting feedback", e);
            request.setAttribute("error", "System error occurred. Please try again later.");
            request.getRequestDispatcher("/jsp/feedback.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            logger.warning("Invalid rating format");
            request.setAttribute("error", "Invalid rating format");
            request.getRequestDispatcher("/jsp/feedback.jsp").forward(request, response);
        }
    }
    
    private boolean validateFeedbackInput(String ratingStr, String comments, 
            String feedbackType, HttpServletRequest request) {
        
        boolean isValid = true;
        StringBuilder errors = new StringBuilder();
        
        try {
            ValidationUtils.validateRequired(ratingStr, "rating");
            int rating = Integer.parseInt(ratingStr);
            if (rating < 1 || rating > 5) {
                errors.append("Rating must be between 1 and 5. ");
                isValid = false;
            }
        } catch (InvalidInputException e) {
            errors.append(e.getUserMessage()).append(" ");
            isValid = false;
        } catch (NumberFormatException e) {
            errors.append("Rating must be a valid number. ");
            isValid = false;
        }
        
        try {
            ValidationUtils.validateRequired(comments, "comments");
            ValidationUtils.validateStringLength(comments, "comments", 1, 1000);
        } catch (InvalidInputException e) {
            errors.append(e.getUserMessage()).append(" ");
            isValid = false;
        }
        
        try {
            ValidationUtils.validateRequired(feedbackType, "feedback type");
        } catch (InvalidInputException e) {
            errors.append(e.getUserMessage()).append(" ");
            isValid = false;
        }
        
        if (!isValid) {
            request.setAttribute("error", errors.toString().trim());
        }
        
        return isValid;
    }
}
