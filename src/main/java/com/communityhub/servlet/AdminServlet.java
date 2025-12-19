package com.communityhub.servlet;

import com.communityhub.exception.DatabaseException;
import com.communityhub.model.User;
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
 * Admin Panel Servlet
 * Provides administrative dashboard and management functions
 */
@WebServlet(name = "AdminServlet", urlPatterns = {"/admin"})
public class AdminServlet extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(AdminServlet.class.getName());
    private UserService userService;
    private ResourceService resourceService;
    private RequestService requestService;
    
    @Override
    public void init() throws ServletException {
        try {
            userService = new UserService();
            resourceService = new ResourceService();
            requestService = new RequestService();
            logger.info("AdminServlet initialized successfully");
        } catch (DatabaseException e) {
            logger.log(Level.SEVERE, "Failed to initialize services", e);
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
        
        // Only admins can access admin panel
        if (!currentUser.isAdmin()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, 
                "You don't have permission to access the admin panel");
            return;
        }
        
        try {
            // Load admin statistics
            long totalUsers = userService.getAllUsers().size();
            long totalResources = resourceService.getAllResources().size();
            long totalRequests = requestService.getAllRequests().size();
            long activeRequests = requestService.getActiveRequestCount();
            long completedRequests = requestService.getCompletedRequestCount();
            
            request.setAttribute("totalUsers", totalUsers);
            request.setAttribute("totalResources", totalResources);
            request.setAttribute("totalRequests", totalRequests);
            request.setAttribute("activeRequests", activeRequests);
            request.setAttribute("completedRequests", completedRequests);
            
            // Load all users for management
            request.setAttribute("users", userService.getAllUsers());
            
            // Load all resources for management
            request.setAttribute("resources", resourceService.getAllResources());
            
            // Load all requests for management
            request.setAttribute("requests", requestService.getAllRequests());
            
            request.getRequestDispatcher("/jsp/admin.jsp").forward(request, response);
            
        } catch (DatabaseException e) {
            logger.log(Level.SEVERE, "Database error loading admin data", e);
            request.setAttribute("error", "Unable to load admin data");
            request.getRequestDispatcher("/jsp/admin.jsp").forward(request, response);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error in admin panel", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "An unexpected error occurred");
        }
    }
}
