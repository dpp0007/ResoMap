package com.communityhub.servlet;

import com.communityhub.exception.AuthenticationException;
import com.communityhub.exception.DatabaseException;
import com.communityhub.service.AuthenticationService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;

@WebServlet(name = "LogoutServlet", urlPatterns = {"/logout"})
public class LogoutServlet extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(LogoutServlet.class.getName());
    private AuthenticationService authService;
    
    @Override
    public void init() throws ServletException {
        try {
            authService = new AuthenticationService();
            logger.info("LogoutServlet initialized successfully");
        } catch (DatabaseException e) {
            logger.log(Level.SEVERE, "Failed to initialize AuthenticationService", e);
            throw new ServletException("Service initialization failed", e);
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        handleLogout(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        handleLogout(request, response);
    }
    
    private void handleLogout(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            HttpSession session = request.getSession(false);
            
            if (session != null) {
                String username = (String) session.getAttribute("username");
                
                // Logout from authentication service
                try {
                    authService.logout();
                } catch (AuthenticationException e) {
                    // Log but don't fail the logout process
                    logger.warning("Authentication service logout failed: " + e.getMessage());
                }
                
                // Invalidate session
                session.invalidate();
                
                logger.info("User logged out successfully: " + username);
            }
            
            // Redirect to login page with success message
            HttpSession newSession = request.getSession(true);
            newSession.setAttribute("success", "You have been logged out successfully.");
            response.sendRedirect(request.getContextPath() + "/login");
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error during logout", e);
            // Even if there's an error, redirect to login
            response.sendRedirect(request.getContextPath() + "/login");
        }
    }
}