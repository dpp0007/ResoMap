package com.communityhub.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.communityhub.dao.ResourceDAO;
import com.communityhub.exception.DatabaseException;
import com.communityhub.exception.InvalidInputException;
import com.communityhub.model.Resource;
import com.communityhub.util.ValidationUtils;

/**
 * Service class for resource management
 * Demonstrates collections usage, lambda expressions, streams, and thread-safe operations
 */
public class ResourceService {
    
    private static final Logger logger = Logger.getLogger(ResourceService.class.getName());
    
    private final ResourceDAO resourceDAO;
    
    // Thread-safe collections for caching and categorization
    private final Map<String, Resource> resourceCache;
    private final Set<String> resourceCategories;
    private final Map<String, List<Resource>> categoryCache;
    
    // Synchronization objects for thread safety
    private final Object cacheLock = new Object();
    private final Object categoryLock = new Object();
    
    /**
     * Constructor initializes the resource service
     * @throws DatabaseException if DAO initialization fails
     */
    public ResourceService() throws DatabaseException {
        this.resourceDAO = new ResourceDAO();
        this.resourceCache = new ConcurrentHashMap<>();
        this.resourceCategories = ConcurrentHashMap.newKeySet();
        this.categoryCache = new ConcurrentHashMap<>();
        
        // Initialize cache
        refreshCache(); // @SuppressWarnings("OverridableMethodCallInConstructor") - Safe pattern for service initialization
    }
    
    /**
     * Adds a new resource (thread-safe)
     * @param resource Resource to add
     * @throws InvalidInputException if resource data is invalid
     * @throws DatabaseException if database operation fails
     */
    public synchronized void addResource(Resource resource) throws InvalidInputException, DatabaseException {
        validateResource(resource);
        
        // Check for duplicate names in the same category
        boolean duplicateExists = resourceCache.values().stream()
            .anyMatch(r -> r.getName().equalsIgnoreCase(resource.getName()) && 
                          r.getCategory().equalsIgnoreCase(resource.getCategory()));
        
        if (duplicateExists) {
            throw new InvalidInputException("resource name", resource.getName(), 
                "A resource with this name already exists in the " + resource.getCategory() + " category");
        }
        
        // Save to database
        resourceDAO.create(resource);
        
        // Update cache
        synchronized (cacheLock) {
            resourceCache.put(resource.getResourceId(), resource);
            resourceCategories.add(resource.getCategory());
            updateCategoryCache();
        }
        
        logger.log(Level.INFO, "Resource added successfully: {0}", resource.getName());
    }
    
    /**
     * Updates an existing resource
     * @param resource Resource to update
     * @throws InvalidInputException if resource data is invalid
     * @throws DatabaseException if database operation fails
     */
    public synchronized void updateResource(Resource resource) throws InvalidInputException, DatabaseException {
        validateResource(resource);
        try {
            ValidationUtils.validateRequired(resource.getResourceId(), "resource ID");
        } catch (InvalidInputException e) {
            throw new DatabaseException("Invalid resource ID: " + e.getMessage());
        }
        
        if (!resourceDAO.exists(resource.getResourceId())) {
            throw new DatabaseException("Resource not found: " + resource.getResourceId());
        }
        
        // Update in database
        resourceDAO.update(resource);
        
        // Update cache
        synchronized (cacheLock) {
            resourceCache.put(resource.getResourceId(), resource);
            resourceCategories.add(resource.getCategory());
            updateCategoryCache();
        }
        
        logger.log(Level.INFO, "Resource updated successfully: {0}", resource.getName());
    }
    
    /**
     * Deletes a resource
     * @param resourceId ID of the resource to delete
     * @throws DatabaseException if database operation fails
     */
    public synchronized void deleteResource(String resourceId) throws DatabaseException {
        try {
            ValidationUtils.validateRequired(resourceId, "resource ID");
        } catch (InvalidInputException e) {
            throw new DatabaseException("Invalid resource ID: " + e.getMessage());
        }
        
        Resource resource = resourceCache.get(resourceId);
        if (resource == null) {
            resource = resourceDAO.read(resourceId);
            if (resource == null) {
                throw new DatabaseException("Resource not found: " + resourceId);
            }
        }
        
        // Delete from database
        resourceDAO.delete(resourceId);
        
        // Update cache
        synchronized (cacheLock) {
            resourceCache.remove(resourceId);
            updateCategoryCache();
        }
        
        logger.log(Level.INFO, "Resource deleted successfully: {0}", resourceId);
    }
    
    /**
     * Gets a resource by ID
     * @param resourceId Resource ID
     * @return Resource if found, null otherwise
     * @throws DatabaseException if database operation fails
     */
    public Resource getResource(String resourceId) throws DatabaseException {
        try {
            ValidationUtils.validateRequired(resourceId, "resource ID");
        } catch (InvalidInputException e) {
            throw new DatabaseException("Invalid resource ID: " + e.getMessage());
        }
        
        // Check cache first
        Resource resource = resourceCache.get(resourceId);
        if (resource != null) {
            return resource;
        }
        
        // Load from database
        resource = resourceDAO.read(resourceId);
        if (resource != null) {
            synchronized (cacheLock) {
                resourceCache.put(resourceId, resource);
            }
        }
        
        return resource;
    }
    
    /**
     * Gets all resources
     * @return List of all resources
     */
    public List<Resource> getAllResources() {
        synchronized (cacheLock) {
            return new ArrayList<>(resourceCache.values());
        }
    }
    
    /**
     * Searches resources using lambda expressions and streams
     * @param query Search query
     * @return List of matching resources
     */
    public List<Resource> searchResources(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllResources();
        }
        
        String lowerQuery = query.toLowerCase();
        
        synchronized (cacheLock) {
            return resourceCache.values().stream()
                .filter(resource -> resource.getName().toLowerCase().contains(lowerQuery) ||
                                  resource.getDescription().toLowerCase().contains(lowerQuery) ||
                                  resource.getCategory().toLowerCase().contains(lowerQuery) ||
                                  (resource.getLocation() != null && 
                                   resource.getLocation().toLowerCase().contains(lowerQuery)))
                .sorted((r1, r2) -> {
                    // Sort by relevance: exact matches first, then partial matches
                    boolean r1ExactMatch = r1.getName().toLowerCase().equals(lowerQuery);
                    boolean r2ExactMatch = r2.getName().toLowerCase().equals(lowerQuery);
                    
                    if (r1ExactMatch && !r2ExactMatch) return -1;
                    if (r2ExactMatch && !r1ExactMatch) return 1;
                    
                    // Then sort by availability and name
                    if (r1.isAvailable() && !r2.isAvailable()) return -1;
                    if (r2.isAvailable() && !r1.isAvailable()) return 1;
                    
                    return r1.getName().compareToIgnoreCase(r2.getName());
                })
                .collect(Collectors.toList());
        }
    }
    
    /**
     * Filters resources by category using streams
     * @param category Category to filter by
     * @return List of resources in the category
     */
    public List<Resource> getResourcesByCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return getAllResources();
        }
        
        // Check category cache first
        List<Resource> cached = categoryCache.get(category);
        if (cached != null) {
            return new ArrayList<>(cached);
        }
        
        synchronized (cacheLock) {
            List<Resource> resources = resourceCache.values().stream()
                .filter(resource -> resource.getCategory().equalsIgnoreCase(category))
                .sorted(Comparator.comparing(Resource::getName))
                .collect(Collectors.toList());
            
            // Update category cache
            categoryCache.put(category, resources);
            return new ArrayList<>(resources);
        }
    }
    
    /**
     * Gets available resources (quantity > 0) using streams
     * @return List of available resources
     */
    public List<Resource> getAvailableResources() {
        synchronized (cacheLock) {
            return resourceCache.values().stream()
                .filter(Resource::isAvailable)
                .sorted((r1, r2) -> {
                    // Sort by quantity (descending) then by name
                    int quantityCompare = Integer.compare(r2.getQuantity(), r1.getQuantity());
                    return quantityCompare != 0 ? quantityCompare : 
                           r1.getName().compareToIgnoreCase(r2.getName());
                })
                .collect(Collectors.toList());
        }
    }
    
    /**
     * Gets low stock resources (quantity < threshold) using streams
     * @param threshold Quantity threshold
     * @return List of low stock resources
     */
    public List<Resource> getLowStockResources(int threshold) {
        synchronized (cacheLock) {
            return resourceCache.values().stream()
                .filter(resource -> resource.getQuantity() > 0 && resource.getQuantity() < threshold)
                .sorted(Comparator.comparingInt(Resource::getQuantity))
                .collect(Collectors.toList());
        }
    }
    
    /**
     * Gets all unique categories using streams
     * @return Set of categories
     */
    public Set<String> getAllCategories() {
        synchronized (categoryLock) {
            return new HashSet<>(resourceCategories);
        }
    }
    
    /**
     * Gets resource statistics by category using streams and collectors
     * @return Map of category to resource count
     */
    public Map<String, Long> getResourceStatsByCategory() {
        synchronized (cacheLock) {
            return resourceCache.values().stream()
                .collect(Collectors.groupingBy(
                    Resource::getCategory,
                    Collectors.counting()
                ));
        }
    }
    
    /**
     * Gets availability statistics using streams
     * @return Map with availability statistics
     */
    public Map<String, Object> getAvailabilityStatistics() {
        synchronized (cacheLock) {
            List<Resource> resources = new ArrayList<>(resourceCache.values());
            
            long totalResources = resources.size();
            long availableResources = resources.stream()
                .filter(Resource::isAvailable)
                .count();
            
            int totalQuantity = resources.stream()
                .mapToInt(Resource::getQuantity)
                .sum();
            
            OptionalDouble avgQuantity = resources.stream()
                .mapToInt(Resource::getQuantity)
                .average();
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalResources", totalResources);
            stats.put("availableResources", availableResources);
            stats.put("unavailableResources", totalResources - availableResources);
            stats.put("totalQuantity", totalQuantity);
            stats.put("averageQuantity", avgQuantity.orElse(0.0));
            stats.put("availabilityRate", totalResources > 0 ? 
                     (double) availableResources / totalResources * 100 : 0.0);
            
            return stats;
        }
    }
    
    /**
     * Updates resource quantity
     * @param resourceId Resource ID
     * @param newQuantity New quantity
     * @throws DatabaseException if update fails
     */
    public synchronized void updateResourceQuantity(String resourceId, int newQuantity) throws DatabaseException {
        try {
            ValidationUtils.validateRequired(resourceId, "resource ID");
        } catch (InvalidInputException e) {
            throw new DatabaseException("Invalid resource ID: " + e.getMessage());
        }
        
        if (newQuantity < 0) {
            throw new DatabaseException("Quantity cannot be negative: " + newQuantity);
        }
        
        // Update in database
        resourceDAO.updateQuantity(resourceId, newQuantity);
        
        // Update cache
        synchronized (cacheLock) {
            Resource resource = resourceCache.get(resourceId);
            if (resource != null) {
                resource.setQuantity(newQuantity);
            }
        }
        
        logger.log(Level.INFO, "Resource quantity updated: {0} -> {1}", new Object[]{resourceId, newQuantity});
    }
    
    /**
     * Refreshes the resource cache from database
     * @throws DatabaseException if database operation fails
     */
    public void refreshCache() throws DatabaseException {
        try {
            List<Resource> resources = resourceDAO.findAll();
            
            synchronized (cacheLock) {
                resourceCache.clear();
                resourceCategories.clear();
                categoryCache.clear();
                
                resources.forEach(resource -> {
                    resourceCache.put(resource.getResourceId(), resource);
                    resourceCategories.add(resource.getCategory());
                });
                
                updateCategoryCache();
            }
            
            logger.log(Level.INFO, "Resource cache refreshed with {0} resources", resources.size());
            
        } catch (DatabaseException e) {
            logger.log(Level.SEVERE, "Failed to refresh resource cache", e);
            throw e;
        }
    }
    
    /**
     * Updates the category cache
     */
    private void updateCategoryCache() {
        categoryCache.clear();
        
        Map<String, List<Resource>> newCategoryCache = resourceCache.values().stream()
            .collect(Collectors.groupingBy(Resource::getCategory));
        
        categoryCache.putAll(newCategoryCache);
    }
    
    /**
     * Validates resource data
     * @param resource Resource to validate
     * @throws InvalidInputException if validation fails
     */
    private void validateResource(Resource resource) throws InvalidInputException {
        ValidationUtils.validateNotNull(resource, "resource");
        ValidationUtils.validateRequired(resource.getName(), "resource name");
        ValidationUtils.validateRequired(resource.getCategory(), "resource category");
        ValidationUtils.validateStringLength(resource.getName(), "resource name", 1, 100);
        ValidationUtils.validateStringLength(resource.getCategory(), "resource category", 1, 50);
        
        if (resource.getQuantity() < 0) {
            throw new InvalidInputException("quantity", String.valueOf(resource.getQuantity()), 
                "Quantity cannot be negative");
        }
        
        if (resource.getDescription() != null && resource.getDescription().length() > 1000) {
            throw new InvalidInputException("description", resource.getDescription(), 
                "Description cannot exceed 1000 characters");
        }
    }
}