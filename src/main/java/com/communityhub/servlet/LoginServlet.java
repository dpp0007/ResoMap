package com.communityhub.servlet;

import com.communityhub.exception.AuthenticationException;
import com.communityhub.exception.DatabaseException;
import com.communityhub.exception.InvalidInputException;
import com.communityhub.model.User;
import com.communityhub.service.AuthenticationService;
import com.communityhub.util.ValidationUtils;
import com.communityhub.util.RequestContext;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(LoginServlet.class.getName());
    private AuthenticationService authService;
    
    @Override
    public void init() throws ServletException {
        try {
            authService = new AuthenticationService();
            logger.info("LoginServlet initialized successfully");
        } catch (DatabaseException e) {
            logger.log(Level.SEVERE, "Failed to initialize AuthenticationService", e);
            throw new ServletException("Service initialization failed", e);
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Check if user is already logged in
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        // Forward to login page
        request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Initialize request context for correlation tracking
        RequestContext.initialize();
        
        try {
            // Defensive null checks: request parameters could be null
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            
            // Server-side validation
            if (!validateLoginInput(username, password, request)) {
                request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
                return;
            }
            
            // Normalize input: trim whitespace
            username = username != null ? username.trim() : "";
            
            // Authenticate user
            User user = authService.login(username, password);
            
            // Defensive check: ensure user object is valid
            if (user == null) {
                throw new AuthenticationException("Authentication failed: user not found");
            }
            
            // Create session
            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getUserId());
            session.setAttribute("username", user.getUsername());
            session.setAttribute("userRole", user.getRole().toString());
            session.setMaxInactiveInterval(30 * 60); // 30 minutes
            
            // Update request context with authenticated user
            RequestContext.setUserId(user.getUserId());
            RequestContext.logInfo("User logged in successfully: " + username);
            
            // Redirect to dashboard
            response.sendRedirect(request.getContextPath() + "/dashboard");
            
        } catch (AuthenticationException e) {
            RequestContext.logWarning("Authentication failed: " + e.getMessage());
            request.setAttribute("error", e.getUserMessage());
            request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
            
        } catch (InvalidInputException e) {
            RequestContext.logWarning("Invalid input: " + e.getMessage());
            request.setAttribute("error", e.getUserMessage());
            request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
            
        } catch (DatabaseException e) {
            RequestContext.logError("Database error during login", e);
            request.setAttribute("error", "System error occurred. Please try again later.");
            request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
            
        } catch (Exception e) {
            RequestContext.logError("Unexpected error during login", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "An unexpected error occurred");
        } finally {
            // Clear request context to prevent ThreadLocal leaks
            RequestContext.clear();
        }
    }
    
    private boolean validateLoginInput(String username, String password, HttpServletRequest request) {
        boolean isValid = true;
        
        try {
            ValidationUtils.validateRequired(username, "username");
        } catch (InvalidInputException e) {
            request.setAttribute("error", "Username is required");
            isValid = false;
        }
        
        try {
            ValidationUtils.validateRequired(password, "password");
        } catch (InvalidInputException e) {
            request.setAttribute("error", "Password is required");
            isValid = false;
        }
        
        return isValid;
    }
}