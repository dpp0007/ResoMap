package com.communityhub.servlet;

import com.communityhub.dto.ActivityDTO;
import com.communityhub.exception.DatabaseException;
import com.communityhub.model.User;
import com.communityhub.service.ActivityService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;

@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard"})
public class DashboardServlet extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(DashboardServlet.class.getName());
    private ResourceService resourceService;
    private RequestService requestService;
    private UserService userService;
    private ActivityService activityService;
    
    @Override
    public void init() throws ServletException {
        try {
            resourceService = new ResourceService();
            requestService = new RequestService();
            userService = new UserService();
            activityService = new ActivityService();
            logger.info("DashboardServlet initialized successfully");
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
        
        try {
            User currentUser = (User) session.getAttribute("user");
            
            // Gather dashboard statistics
            Map<String, Object> stats = gatherDashboardStats(currentUser);
            request.setAttribute("stats", stats);
            
            // Get recent activity using ActivityService
            List<ActivityDTO> recentActivity = activityService.getRecentActivity(currentUser, 10);
            request.setAttribute("recentActivity", recentActivity);
            
            // Forward to dashboard page
            request.getRequestDispatcher("/jsp/dashboard.jsp").forward(request, response);
            
        } catch (DatabaseException e) {
            logger.log(Level.SEVERE, "Database error loading dashboard", e);
            request.setAttribute("error", "Unable to load dashboard data");
            request.getRequestDispatcher("/jsp/error.jsp").forward(request, response);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error loading dashboard", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "An unexpected error occurred");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Dashboard doesn't handle POST requests, redirect to GET
        doGet(request, response);
    }
    

    private Map<String, Object> gatherDashboardStats(User currentUser) throws DatabaseException {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // Get total resources count
            long totalResources = resourceService.getResourceCount();
            stats.put("totalResources", totalResources);
            
            // Get active requests count
            long activeRequests = requestService.getActiveRequestCount();
            stats.put("activeRequests", activeRequests);
            
            // Get total volunteers count
            long totalVolunteers = userService.getVolunteerCount();
            stats.put("totalVolunteers", totalVolunteers);
            
            // Get completed requests count
            long completedRequests = requestService.getCompletedRequestCount();
            stats.put("completedRequests", completedRequests);
            
            // VOLUNTEER-SPECIFIC METRICS
            if (currentUser.getRole().toString().equals("VOLUNTEER")) {
                // Get volunteer's completed requests
                long volunteerCompleted = requestService.getVolunteerCompletedCount(currentUser.getUserId());
                stats.put("volunteerCompleted", volunteerCompleted);
                
                // Get volunteer's active assignments
                long volunteerActive = requestService.getVolunteerActiveCount(currentUser.getUserId());
                stats.put("volunteerActive", volunteerActive);
                
                // Get volunteer's average completion time
                double avgCompletionTime = requestService.getVolunteerAverageCompletionTime(currentUser.getUserId());
                stats.put("volunteerAvgTime", String.format("%.1f", avgCompletionTime));
                
                logger.info("Volunteer dashboard stats gathered for: " + currentUser.getUsername());
            }
            
            // REQUESTER-SPECIFIC METRICS
            if (currentUser.getRole().toString().equals("REQUESTER")) {
                // Get requester's total requests
                long requesterTotal = requestService.getRequesterTotalCount(currentUser.getUserId());
                stats.put("requesterTotal", requesterTotal);
                
                // Get requester's active requests
                long requesterActive = requestService.getRequesterActiveCount(currentUser.getUserId());
                stats.put("requesterActive", requesterActive);
                
                // Get requester's completed requests
                long requesterCompleted = requestService.getRequesterCompletedCount(currentUser.getUserId());
                stats.put("requesterCompleted", requesterCompleted);
                
                // Get requester's cancelled requests
                long requesterCancelled = requestService.getRequesterCancelledCount(currentUser.getUserId());
                stats.put("requesterCancelled", requesterCancelled);
                
                logger.info("Requester dashboard stats gathered for: " + currentUser.getUsername());
            }
            
            logger.info("Dashboard stats gathered for user: " + currentUser.getUsername());
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error gathering dashboard stats", e);
            // Set default values if there's an error
            stats.put("totalResources", 0L);
            stats.put("activeRequests", 0L);
            stats.put("totalVolunteers", 0L);
            stats.put("completedRequests", 0L);
            stats.put("volunteerCompleted", 0L);
            stats.put("volunteerActive", 0L);
            stats.put("volunteerAvgTime", "0.0");
        }
        
        return stats;
    }
}