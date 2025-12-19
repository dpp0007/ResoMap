package com.communityhub.service;

import com.communityhub.dao.ResourceDAO;
import com.communityhub.exception.DatabaseException;
import com.communityhub.model.Resource;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Service class for resource management operations
 */
public class ResourceService {
    
    private static final Logger logger = Logger.getLogger(ResourceService.class.getName());
    private final ResourceDAO resourceDAO;
    
    public ResourceService() throws DatabaseException {
        this.resourceDAO = new ResourceDAO();
    }
    
    /**
     * Gets all resources
     * @return List of all resources
     * @throws DatabaseException if database operation fails
     */
    public List<Resource> getAllResources() throws DatabaseException {
        return resourceDAO.findAll();
    }
    
    /**
     * Gets a resource by ID
     * @param resourceId Resource ID
     * @return Resource if found, null otherwise
     * @throws DatabaseException if database operation fails
     */
    public Resource getResource(String resourceId) throws DatabaseException {
        return resourceDAO.read(resourceId);
    }
    
    /**
     * Creates a new resource
     * @param resource Resource to create
     * @throws DatabaseException if database operation fails
     */
    public void createResource(Resource resource) throws DatabaseException {
        resourceDAO.create(resource);
        logger.info("Resource created: " + resource.getName());
    }
    
    /**
     * Updates an existing resource
     * @param resource Resource to update
     * @throws DatabaseException if database operation fails
     */
    public void updateResource(Resource resource) throws DatabaseException {
        resourceDAO.update(resource);
        logger.info("Resource updated: " + resource.getName());
    }
    
    /**
     * Deletes a resource
     * @param resourceId Resource ID to delete
     * @throws DatabaseException if database operation fails
     */
    public void deleteResource(String resourceId) throws DatabaseException {
        resourceDAO.delete(resourceId);
        logger.info("Resource deleted: " + resourceId);
    }
    
    /**
     * Gets the total count of resources
     * @return Total resource count
     * @throws DatabaseException if database operation fails
     */
    public long getResourceCount() throws DatabaseException {
        return resourceDAO.count();
    }
    
    /**
     * Searches resources by name, description, or category
     * @param query Search query
     * @return List of matching resources
     * @throws DatabaseException if database operation fails
     */
    public List<Resource> searchResources(String query) throws DatabaseException {
        List<Resource> allResources = resourceDAO.findAll();
        String lowerQuery = query.toLowerCase();
        
        return allResources.stream()
            .filter(resource -> 
                resource.getName().toLowerCase().contains(lowerQuery) ||
                resource.getDescription().toLowerCase().contains(lowerQuery) ||
                resource.getCategory().toLowerCase().contains(lowerQuery)
            )
            .collect(Collectors.toList());
    }
    
    /**
     * Gets resources by category
     * @param category Resource category
     * @return List of resources in the category
     * @throws DatabaseException if database operation fails
     */
    public List<Resource> getResourcesByCategory(String category) throws DatabaseException {
        return resourceDAO.findByField("category", category);
    }
    
    /**
     * Admin action: Update resource quantity
     * @param resourceId Resource ID
     * @param newQuantity New quantity
     * @throws DatabaseException if database operation fails
     */
    public void updateResourceQuantity(String resourceId, int newQuantity) throws DatabaseException {
        Resource resource = resourceDAO.read(resourceId);
        if (resource != null) {
            resource.setQuantity(newQuantity);
            resourceDAO.update(resource);
            logger.info("Resource quantity updated by admin: " + resourceId + " -> " + newQuantity);
        }
    }
    
    /**
     * Admin action: Update resource category
     * @param resourceId Resource ID
     * @param newCategory New category
     * @throws DatabaseException if database operation fails
     */
    public void updateResourceCategory(String resourceId, String newCategory) throws DatabaseException {
        Resource resource = resourceDAO.read(resourceId);
        if (resource != null) {
            resource.setCategory(newCategory);
            resourceDAO.update(resource);
            logger.info("Resource category updated by admin: " + resourceId + " -> " + newCategory);
        }
    }
}