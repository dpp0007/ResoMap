package com.communityhub.core;

import com.communityhub.exception.DatabaseException;
import com.communityhub.service.*;
import com.communityhub.util.SessionManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service Factory - Implements Dependency Injection pattern
 * Provides centralized service instance management with lazy initialization
 * Thread-safe singleton implementation
 * 
 * Benefits:
 * - Single point of service creation
 * - Reduces tight coupling
 * - Enables easy testing with mock services
 * - Manages service lifecycle
 * 
 * @author ResoMap Team
 * @version 2.0
 */
public final class ServiceFactory {
    
    private static final Logger logger = Logger.getLogger(ServiceFactory.class.getName());
    private static volatile ServiceFactory instance;
    
    // Service cache for singleton services
    private final Map<Class<?>, Object> serviceCache;
    
    // Private constructor prevents direct instantiation
    private ServiceFactory() {
        this.serviceCache = new ConcurrentHashMap<>();
        logger.info("ServiceFactory initialized");
    }
    
    /**
     * Gets the singleton instance of ServiceFactory
     * Thread-safe double-checked locking
     * 
     * @return ServiceFactory instance
     */
    public static ServiceFactory getInstance() {
        if (instance == null) {
            synchronized (ServiceFactory.class) {
                if (instance == null) {
                    instance = new ServiceFactory();
                }
            }
        }
        return instance;
    }
    
    /**
     * Gets or creates an AuthenticationService instance
     * 
     * @return AuthenticationService instance
     * @throws DatabaseException if service initialization fails
     */
    public AuthenticationService getAuthenticationService() throws DatabaseException {
        return getOrCreateService(AuthenticationService.class, AuthenticationService::new);
    }
    
    /**
     * Gets or creates a ResourceService instance
     * 
     * @return ResourceService instance
     * @throws DatabaseException if service initialization fails
     */
    public ResourceService getResourceService() throws DatabaseException {
        return getOrCreateService(ResourceService.class, ResourceService::new);
    }
    
    /**
     * Gets or creates a RequestService instance
     * 
     * @return RequestService instance
     * @throws DatabaseException if service initialization fails
     */
    public RequestService getRequestService() throws DatabaseException {
        return getOrCreateService(RequestService.class, RequestService::new);
    }
    
    /**
     * Gets or creates a UserService instance
     * 
     * @return UserService instance
     * @throws DatabaseException if service initialization fails
     */
    public UserService getUserService() throws DatabaseException {
        return getOrCreateService(UserService.class, UserService::new);
    }
    
    /**
     * Gets or creates a NotificationService instance
     * 
     * @return NotificationService instance
     * @throws DatabaseException if service initialization fails
     */
    public NotificationService getNotificationService() throws DatabaseException {
        return getOrCreateService(NotificationService.class, NotificationService::new);
    }
    
    /**
     * Gets the SessionManager instance
     * 
     * @return SessionManager instance
     */
    public SessionManager getSessionManager() {
        return SessionManager.getInstance();
    }
    
    /**
     * Generic method to get or create a service
     * Uses double-checked locking for thread safety
     * 
     * @param <T> Service type
     * @param serviceClass Service class
     * @param creator Service creator function
     * @return Service instance
     * @throws DatabaseException if service creation fails
     */
    @SuppressWarnings("unchecked")
    private <T> T getOrCreateService(Class<T> serviceClass, ServiceCreator<T> creator) throws DatabaseException {
        Object service = serviceCache.get(serviceClass);
        
        if (service == null) {
            synchronized (serviceCache) {
                service = serviceCache.get(serviceClass);
                if (service == null) {
                    try {
                        service = creator.create();
                        serviceCache.put(serviceClass, service);
                        logger.info("Created service: " + serviceClass.getSimpleName());
                    } catch (DatabaseException e) {
                        logger.log(Level.SEVERE, "Failed to create service: " + serviceClass.getSimpleName(), e);
                        throw e;
                    }
                }
            }
        }
        
        return (T) service;
    }
    
    /**
     * Clears all cached services
     * Useful for testing or application reset
     */
    public synchronized void clearCache() {
        serviceCache.clear();
        logger.info("Service cache cleared");
    }
    
    /**
     * Checks if a service is cached
     * 
     * @param serviceClass Service class to check
     * @return true if service is cached
     */
    public boolean isCached(Class<?> serviceClass) {
        return serviceCache.containsKey(serviceClass);
    }
    
    /**
     * Functional interface for service creation
     * 
     * @param <T> Service type
     */
    @FunctionalInterface
    private interface ServiceCreator<T> {
        T create() throws DatabaseException;
    }
    
    /**
     * Resets the factory instance
     * WARNING: Only use for testing purposes
     */
    public static synchronized void resetInstance() {
        if (instance != null) {
            instance.clearCache();
            instance = null;
            logger.warning("ServiceFactory instance reset");
        }
    }
}
