package com.communityhub.servlet;

import com.communityhub.exception.DatabaseException;
import com.communityhub.exception.InvalidInputException;
import com.communityhub.model.User;
import com.communityhub.model.UserRole;
import com.communityhub.service.AuthenticationService;
import com.communityhub.util.ValidationUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;

@WebServlet(name = "RegistrationServlet", urlPatterns = {"/register"})
public class RegistrationServlet extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(RegistrationServlet.class.getName());
    private AuthenticationService authService;
    
    @Override
    public void init() throws ServletException {
        try {
            authService = new AuthenticationService();
            logger.info("RegistrationServlet initialized successfully");
        } catch (DatabaseException e) {
            logger.log(Level.SEVERE, "Failed to initialize AuthenticationService", e);
            throw new ServletException("Service initialization failed", e);
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Forward to registration page
        request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Get parameters
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String roleStr = request.getParameter("role");
        
        try {
            
            // Server-side validation
            if (!validateRegistrationInput(username, email, password, confirmPassword, roleStr, request)) {
                request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
                return;
            }
            
            // Sanitize input
            username = ValidationUtils.sanitizeInput(username);
            email = ValidationUtils.sanitizeInput(email);
            
            // Parse role
            UserRole role = UserRole.valueOf(roleStr);
            
            // Register user
            User newUser = authService.register(username, email, password, confirmPassword, role);
            
            logger.info("User registered successfully: " + username);
            
            // Redirect to login with success message
            request.getSession().setAttribute("success", 
                "Account created successfully! You can now log in.");
            response.sendRedirect(request.getContextPath() + "/login");
            
        } catch (InvalidInputException e) {
            logger.warning("Invalid input during registration: " + e.getMessage());
            request.setAttribute("error", e.getUserMessage());
            request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
            
        } catch (DatabaseException e) {
            logger.log(Level.SEVERE, "Database error during registration", e);
            request.setAttribute("error", "System error occurred. Please try again later.");
            request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
            
        } catch (IllegalArgumentException e) {
            logger.warning("Invalid role specified: " + roleStr);
            request.setAttribute("error", "Invalid role selected");
            request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error during registration", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "An unexpected error occurred");
        }
    }
    
    private boolean validateRegistrationInput(String username, String email, String password, 
            String confirmPassword, String role, HttpServletRequest request) {
        
        boolean isValid = true;
        StringBuilder errors = new StringBuilder();
        
        // Validate username
        try {
            ValidationUtils.validateUsername(username);
        } catch (InvalidInputException e) {
            errors.append(e.getUserMessage()).append(" ");
            isValid = false;
        }
        
        // Validate email
        try {
            ValidationUtils.validateEmail(email);
        } catch (InvalidInputException e) {
            errors.append(e.getUserMessage()).append(" ");
            isValid = false;
        }
        
        // Validate password
        try {
            ValidationUtils.validatePassword(password);
        } catch (InvalidInputException e) {
            errors.append(e.getUserMessage()).append(" ");
            isValid = false;
        }
        
        // Validate password match
        try {
            ValidationUtils.validatePasswordMatch(password, confirmPassword);
        } catch (InvalidInputException e) {
            errors.append(e.getUserMessage()).append(" ");
            isValid = false;
        }
        
        // Validate role
        try {
            ValidationUtils.validateRequired(role, "role");
            if (role != null && !role.equals("VOLUNTEER") && !role.equals("REQUESTER")) {
                errors.append("Invalid role selected. ");
                isValid = false;
            }
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