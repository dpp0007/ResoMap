package com.communityhub.servlet;

import com.communityhub.exception.DatabaseException;
import com.communityhub.exception.InvalidInputException;
import com.communityhub.model.Resource;
import com.communityhub.model.User;
import com.communityhub.service.ResourceService;
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

@WebServlet(name = "ResourceServlet", urlPatterns = {"/resources"})
public class ResourceServlet extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(ResourceServlet.class.getName());
    private ResourceService resourceService;
    
    @Override
    public void init() throws ServletException {
        try {
            resourceService = new ResourceService();
            logger.info("ResourceServlet initialized successfully");
        } catch (DatabaseException e) {
            logger.log(Level.SEVERE, "Failed to initialize ResourceService", e);
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
        
        try {
            String action = request.getParameter("action");
            String resourceId = request.getParameter("id");
            
            if ("delete".equals(action) && resourceId != null) {
                handleDeleteResource(request, response, resourceId);
                return;
            }
            
            // Load all resources
            List<Resource> resources = resourceService.getAllResources();
            request.setAttribute("resources", resources);
            
            // Forward to resources page
            request.getRequestDispatcher("/jsp/resources.jsp").forward(request, response);
            
        } catch (DatabaseException e) {
            logger.log(Level.SEVERE, "Database error loading resources", e);
            request.setAttribute("error", "Unable to load resources");
            request.getRequestDispatcher("/jsp/resources.jsp").forward(request, response);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error loading resources", e);
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
            if ("create".equals(action)) {
                handleCreateResource(request, response, currentUser);
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
            }
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error processing resource request", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "An unexpected error occurred");
        }
    }
    
    private void handleCreateResource(HttpServletRequest request, HttpServletResponse response, 
            User currentUser) throws ServletException, IOException {
        
        // Check if user is admin
        if (!currentUser.isAdmin()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, 
                "Only administrators can create resources");
            return;
        }
        
        // Get and validate parameters
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        String category = request.getParameter("category");
        String quantityStr = request.getParameter("quantity");
        String location = request.getParameter("location");
        String contactInfo = request.getParameter("contactInfo");
        
        try {
            
            // Server-side validation
            if (!validateResourceInput(name, category, quantityStr, request)) {
                // Reload resources and forward back to form
                List<Resource> resources = resourceService.getAllResources();
                request.setAttribute("resources", resources);
                request.getRequestDispatcher("/jsp/resources.jsp").forward(request, response);
                return;
            }
            
            // Sanitize input
            name = ValidationUtils.sanitizeInput(name);
            description = ValidationUtils.sanitizeInput(description);
            location = ValidationUtils.sanitizeInput(location);
            contactInfo = ValidationUtils.sanitizeInput(contactInfo);
            
            // Parse quantity
            int quantity = Integer.parseInt(quantityStr);
            
            // Create resource
            Resource resource = new Resource(name, description, category, quantity, 
                location, contactInfo, currentUser.getUserId());
            
            resourceService.createResource(resource);
            
            logger.info("Resource created successfully: " + name + " by " + currentUser.getUsername());
            
            // Redirect with success message
            request.getSession().setAttribute("success", "Resource created successfully!");
            response.sendRedirect(request.getContextPath() + "/resources");
            
        } catch (DatabaseException e) {
            logger.log(Level.SEVERE, "Database error creating resource", e);
            request.setAttribute("error", "System error occurred. Please try again later.");
            try {
                List<Resource> resources = resourceService.getAllResources();
                request.setAttribute("resources", resources);
            } catch (DatabaseException ex) {
                logger.log(Level.SEVERE, "Failed to load resources for error page", ex);
            }
            request.getRequestDispatcher("/jsp/resources.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            logger.warning("Invalid quantity format: " + quantityStr);
            request.setAttribute("error", "Invalid quantity format");
            try {
                List<Resource> resources = resourceService.getAllResources();
                request.setAttribute("resources", resources);
            } catch (DatabaseException ex) {
                logger.log(Level.SEVERE, "Failed to load resources for error page", ex);
            }
            request.getRequestDispatcher("/jsp/resources.jsp").forward(request, response);
        }
    }
    
    private void handleDeleteResource(HttpServletRequest request, HttpServletResponse response, 
            String resourceId) throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        User currentUser = (User) session.getAttribute("user");
        
        // Check if user is admin
        if (!currentUser.isAdmin()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, 
                "Only administrators can delete resources");
            return;
        }
        
        try {
            resourceService.deleteResource(resourceId);
            logger.info("Resource deleted successfully: " + resourceId + " by " + currentUser.getUsername());
            
            request.getSession().setAttribute("success", "Resource deleted successfully!");
            response.sendRedirect(request.getContextPath() + "/resources");
            
        } catch (DatabaseException e) {
            logger.log(Level.SEVERE, "Database error deleting resource", e);
            request.setAttribute("error", "Unable to delete resource");
            try {
                List<Resource> resources = resourceService.getAllResources();
                request.setAttribute("resources", resources);
            } catch (DatabaseException ex) {
                logger.log(Level.SEVERE, "Failed to load resources for error page", ex);
            }
            request.getRequestDispatcher("/jsp/resources.jsp").forward(request, response);
        }
    }
    
    private boolean validateResourceInput(String name, String category, String quantityStr, 
            HttpServletRequest request) {
        
        boolean isValid = true;
        StringBuilder errors = new StringBuilder();
        
        // Validate name
        try {
            ValidationUtils.validateRequired(name, "name");
            ValidationUtils.validateStringLength(name, "name", 1, 100);
        } catch (InvalidInputException e) {
            errors.append(e.getUserMessage()).append(" ");
            isValid = false;
        }
        
        // Validate category
        try {
            ValidationUtils.validateRequired(category, "category");
        } catch (InvalidInputException e) {
            errors.append(e.getUserMessage()).append(" ");
            isValid = false;
        }
        
        // Validate quantity
        try {
            ValidationUtils.validateRequired(quantityStr, "quantity");
            int quantity = Integer.parseInt(quantityStr);
            if (quantity < 0) {
                errors.append("Quantity cannot be negative. ");
                isValid = false;
            }
        } catch (InvalidInputException e) {
            errors.append(e.getUserMessage()).append(" ");
            isValid = false;
        } catch (NumberFormatException e) {
            errors.append("Quantity must be a valid number. ");
            isValid = false;
        }
        
        if (!isValid) {
            request.setAttribute("error", errors.toString().trim());
        }
        
        return isValid;
    }
}