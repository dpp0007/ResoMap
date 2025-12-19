package com.communityhub.servlet;

import com.communityhub.exception.DatabaseException;
import com.communityhub.model.Resource;
import com.communityhub.service.ResourceService;
import com.communityhub.util.ValidationUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;

@WebServlet(name = "SearchServlet", urlPatterns = {"/search"})
public class SearchServlet extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(SearchServlet.class.getName());
    private ResourceService resourceService;
    private ObjectMapper objectMapper;
    
    @Override
    public void init() throws ServletException {
        try {
            resourceService = new ResourceService();
            objectMapper = new ObjectMapper();
            logger.info("SearchServlet initialized successfully");
        } catch (DatabaseException e) {
            logger.log(Level.SEVERE, "Failed to initialize ResourceService", e);
            throw new ServletException("Service initialization failed", e);
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Check authentication
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Authentication required\"}");
            return;
        }
        
        try {
            // Get search query parameter
            String query = request.getParameter("q");
            
            // Validate search query
            if (query == null || query.trim().isEmpty()) {
                sendJsonResponse(response, new ArrayList<>());
                return;
            }
            
            // Sanitize input
            query = ValidationUtils.sanitizeInput(query.trim());
            
            // Minimum search length
            if (query.length() < 2) {
                sendJsonResponse(response, new ArrayList<>());
                return;
            }
            
            // Perform search
            List<Resource> searchResults = resourceService.searchResources(query);
            
            // Convert to JSON-friendly format
            List<Map<String, Object>> jsonResults = convertToJsonFormat(searchResults);
            
            // Send JSON response
            sendJsonResponse(response, jsonResults);
            
            logger.info("Search performed for query: " + query + ", results: " + searchResults.size());
            
        } catch (DatabaseException e) {
            logger.log(Level.SEVERE, "Database error during search", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Search failed\"}");
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error during search", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"An unexpected error occurred\"}");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Search only supports GET requests
        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"Method not allowed\"}");
    }
    
    private List<Map<String, Object>> convertToJsonFormat(List<Resource> resources) {
        List<Map<String, Object>> jsonResults = new ArrayList<>();
        
        for (Resource resource : resources) {
            Map<String, Object> resourceMap = new HashMap<>();
            resourceMap.put("resourceId", resource.getResourceId());
            resourceMap.put("name", resource.getName());
            resourceMap.put("description", resource.getDescription());
            resourceMap.put("category", resource.getCategory());
            resourceMap.put("quantity", resource.getQuantity());
            resourceMap.put("location", resource.getLocation());
            resourceMap.put("contactInfo", resource.getContactInfo());
            
            jsonResults.add(resourceMap);
        }
        
        return jsonResults;
    }
    
    private void sendJsonResponse(HttpServletResponse response, List<Map<String, Object>> data) 
            throws IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // Enable CORS for AJAX requests
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        
        PrintWriter out = response.getWriter();
        
        try {
            String json = objectMapper.writeValueAsString(data);
            out.write(json);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error serializing search results to JSON", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"error\":\"Serialization failed\"}");
        } finally {
            out.flush();
        }
    }
}