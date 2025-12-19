package com.communityhub.servlet;

import com.communityhub.exception.DatabaseException;
import com.communityhub.exception.InvalidInputException;
import com.communityhub.model.Request;
import com.communityhub.model.RequestStatus;
import com.communityhub.model.Resource;
import com.communityhub.model.User;
import com.communityhub.service.RequestService;
import com.communityhub.service.ResourceService;
import com.communityhub.service.UserService;
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

@WebServlet(name = "RequestServlet", urlPatterns = {"/requests"})
public class RequestServlet extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(RequestServlet.class.getName());
    private RequestService requestService;
    private ResourceService resourceService;
    
    @Override
    public void init() throws ServletException {
        try {
            requestService = new RequestService();
            resourceService = new ResourceService();
            logger.info("RequestServlet initialized successfully");
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
        String action = request.getParameter("action");
        
        try {
            if ("new".equals(action)) {
                // Show new request form
                List<Resource> resources = resourceService.getAllResources();
                request.setAttribute("resources", resources);
                request.getRequestDispatcher("/jsp/new-request.jsp").forward(request, response);
                return;
            }
            
            // Load requests based on user role
            List<Request> requests;
            if (currentUser.isAdmin()) {
                // Admins see all requests
                requests = requestService.getAllRequests();
            } else if (currentUser.getRole().toString().equals("VOLUNTEER")) {
                // Volunteers see assigned requests
                requests = requestService.getRequestsByVolunteer(currentUser.getUserId());
            } else {
                // Requesters see their own requests
                requests = requestService.getRequestsByUser(currentUser.getUserId());
            }
            
            request.setAttribute("requests", requests);
            request.setAttribute("userRole", currentUser.getRole().toString());
            
            // Load all resources and create a map for resource name lookup
            try {
                List<Resource> allResources = resourceService.getAllResources();
                java.util.Map<String, String> resourceNameMap = new java.util.HashMap<>();
                for (Resource res : allResources) {
                    resourceNameMap.put(res.getResourceId(), res.getName());
                }
                request.setAttribute("resourceNameMap", resourceNameMap);
                request.setAttribute("allResources", allResources);
            } catch (DatabaseException e) {
                logger.log(Level.WARNING, "Failed to load resources for lookup", e);
            }
            
            // For admin: load list of volunteers for assignment
            if (currentUser.isAdmin()) {
                try {
                    UserService userService = new UserService();
                    List<User> volunteers = userService.getUsersByRole(com.communityhub.model.UserRole.VOLUNTEER);
                    StringBuilder volunteerJson = new StringBuilder();
                    for (int i = 0; i < volunteers.size(); i++) {
                        User vol = volunteers.get(i);
                        if (i > 0) volunteerJson.append(",");
                        volunteerJson.append("{id: '").append(vol.getUserId()).append("', name: '").append(vol.getUsername()).append("'}");
                    }
                    request.setAttribute("volunteers", volunteerJson.toString());
                } catch (DatabaseException e) {
                    logger.log(Level.WARNING, "Failed to load volunteers for admin", e);
                }
            }
            
            request.getRequestDispatcher("/jsp/requests.jsp").forward(request, response);
            
        } catch (DatabaseException e) {
            logger.log(Level.SEVERE, "Database error loading requests", e);
            request.setAttribute("error", "Unable to load requests");
            request.getRequestDispatcher("/jsp/requests.jsp").forward(request, response);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error loading requests", e);
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
                handleCreateRequest(request, response, currentUser);
            } else if ("update".equals(action)) {
                handleUpdateRequest(request, response, currentUser);
            } else if ("assign".equals(action)) {
                handleAssignVolunteer(request, response, currentUser);
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error processing request", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "An unexpected error occurred");
        }
    }
    
    private void handleCreateRequest(HttpServletRequest request, HttpServletResponse response, 
            User currentUser) throws ServletException, IOException {
        
        String resourceId = request.getParameter("resourceId");
        String description = request.getParameter("description");
        String urgencyLevel = request.getParameter("urgencyLevel");
        
        try {
            // Validate input
            if (!validateRequestInput(resourceId, description, urgencyLevel, request)) {
                List<Resource> resources = resourceService.getAllResources();
                request.setAttribute("resources", resources);
                request.getRequestDispatcher("/jsp/new-request.jsp").forward(request, response);
                return;
            }
            
            // Sanitize input
            description = ValidationUtils.sanitizeInput(description);
            
            // Create request
            Request newRequest = new Request(
                currentUser.getUserId(),
                resourceId,
                description,
                com.communityhub.model.UrgencyLevel.valueOf(urgencyLevel)
            );
            
            requestService.createRequest(newRequest);
            
            logger.info("Request created successfully by " + currentUser.getUsername());
            HttpSession sess = request.getSession();
            sess.setAttribute("success", "Request created successfully!");
            response.sendRedirect(request.getContextPath() + "/requests");
            
        } catch (DatabaseException e) {
            logger.log(Level.SEVERE, "Database error creating request", e);
            request.setAttribute("error", "System error occurred. Please try again later.");
            try {
                List<Resource> resources = resourceService.getAllResources();
                request.setAttribute("resources", resources);
            } catch (DatabaseException ex) {
                logger.log(Level.SEVERE, "Failed to load resources", ex);
            }
            request.getRequestDispatcher("/jsp/new-request.jsp").forward(request, response);
        }
    }
    
    private void handleUpdateRequest(HttpServletRequest request, HttpServletResponse response, 
            User currentUser) throws ServletException, IOException {
        
        String requestId = request.getParameter("requestId");
        String status = request.getParameter("status");
        
        try {
            // Only admins and assigned volunteers can update
            Request existingRequest = requestService.getRequest(requestId);
            if (existingRequest == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Request not found");
                return;
            }
            
            boolean canUpdate = currentUser.isAdmin() || 
                (currentUser.getRole().toString().equals("VOLUNTEER") && requestId.equals(existingRequest.getVolunteerId()));
            
            if (!canUpdate) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, 
                    "You don't have permission to update this request");
                return;
            }
            
            // Update status
            existingRequest.setStatus(RequestStatus.valueOf(status));
            requestService.updateRequest(existingRequest);
            
            logger.info("Request updated by " + currentUser.getUsername());
            HttpSession sess = request.getSession();
            sess.setAttribute("success", "Request updated successfully!");
            response.sendRedirect(request.getContextPath() + "/requests");
            
        } catch (DatabaseException e) {
            logger.log(Level.SEVERE, "Database error updating request", e);
            request.setAttribute("error", "Unable to update request");
            request.getRequestDispatcher("/jsp/requests.jsp").forward(request, response);
        }
    }
    
    private void handleAssignVolunteer(HttpServletRequest request, HttpServletResponse response, 
            User currentUser) throws ServletException, IOException {
        
        // Only admins can assign volunteers
        if (!currentUser.isAdmin()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, 
                "Only administrators can assign volunteers");
            return;
        }
        
        String requestId = request.getParameter("requestId");
        String volunteerId = request.getParameter("volunteerId");
        
        try {
            requestService.assignVolunteer(requestId, volunteerId);
            
            logger.info("Volunteer assigned to request by " + currentUser.getUsername());
            HttpSession sess = request.getSession();
            sess.setAttribute("success", "Volunteer assigned successfully!");
            response.sendRedirect(request.getContextPath() + "/requests");
            
        } catch (DatabaseException e) {
            logger.log(Level.SEVERE, "Database error assigning volunteer", e);
            request.setAttribute("error", "Unable to assign volunteer");
            request.getRequestDispatcher("/jsp/requests.jsp").forward(request, response);
        }
    }
    
    private boolean validateRequestInput(String resourceId, String description, 
            String urgencyLevel, HttpServletRequest request) {
        
        boolean isValid = true;
        StringBuilder errors = new StringBuilder();
        
        try {
            ValidationUtils.validateRequired(resourceId, "resource");
        } catch (InvalidInputException e) {
            errors.append(e.getUserMessage()).append(" ");
            isValid = false;
        }
        
        try {
            ValidationUtils.validateRequired(description, "description");
            ValidationUtils.validateStringLength(description, "description", 1, 500);
        } catch (InvalidInputException e) {
            errors.append(e.getUserMessage()).append(" ");
            isValid = false;
        }
        
        try {
            ValidationUtils.validateRequired(urgencyLevel, "urgency level");
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
