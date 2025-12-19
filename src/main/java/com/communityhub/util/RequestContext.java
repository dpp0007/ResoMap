package com.communityhub.util;

import java.util.UUID;
import java.util.logging.Logger;

/**
 * Request context utility for tracking requests across the application
 * 
 * DESIGN DECISION: Request correlation IDs enable tracing of a single user request
 * through multiple layers (servlet → service → DAO → database). This improves
 * debugging and monitoring capabilities without adding complexity.
 * 
 * WHY: When multiple requests are processed concurrently, correlation IDs help
 * identify which log entries belong to which request, making troubleshooting easier.
 */
public class RequestContext {
    
    private static final Logger logger = Logger.getLogger(RequestContext.class.getName());
    private static final ThreadLocal<String> correlationId = new ThreadLocal<>();
    private static final ThreadLocal<String> userId = new ThreadLocal<>();
    private static final ThreadLocal<Long> startTime = new ThreadLocal<>();
    
    /**
     * Initializes request context with a unique correlation ID
     * WHY: Each request gets a unique ID for tracing through logs
     */
    public static void initialize() {
        correlationId.set(UUID.randomUUID().toString());
        startTime.set(System.currentTimeMillis());
    }
    
    /**
     * Sets the user ID for this request context
     * @param id User ID
     */
    public static void setUserId(String id) {
        if (id != null && !id.trim().isEmpty()) {
            userId.set(id);
        }
    }
    
    /**
     * Gets the correlation ID for this request
     * @return Correlation ID
     */
    public static String getCorrelationId() {
        String id = correlationId.get();
        return id != null ? id : "UNKNOWN";
    }
    
    /**
     * Gets the user ID for this request
     * @return User ID or "ANONYMOUS"
     */
    public static String getUserId() {
        String id = userId.get();
        return id != null ? id : "ANONYMOUS";
    }
    
    /**
     * Gets the elapsed time for this request in milliseconds
     * @return Elapsed time
     */
    public static long getElapsedTime() {
        Long start = startTime.get();
        return start != null ? System.currentTimeMillis() - start : 0;
    }
    
    /**
     * Logs a message with correlation ID context
     * WHY: Consistent logging format makes it easier to trace requests
     * @param message Message to log
     */
    public static void logInfo(String message) {
        logger.info(String.format("[%s] [%s] %s", getCorrelationId(), getUserId(), message));
    }
    
    /**
     * Logs a warning with correlation ID context
     * @param message Message to log
     */
    public static void logWarning(String message) {
        logger.warning(String.format("[%s] [%s] %s", getCorrelationId(), getUserId(), message));
    }
    
    /**
     * Logs an error with correlation ID context
     * @param message Message to log
     * @param exception Exception to log
     */
    public static void logError(String message, Exception exception) {
        logger.severe(String.format("[%s] [%s] %s - %s", getCorrelationId(), getUserId(), message, exception.getMessage()));
    }
    
    /**
     * Clears the request context (call at end of request)
     * WHY: Prevents ThreadLocal memory leaks in thread pools
     */
    public static void clear() {
        correlationId.remove();
        userId.remove();
        startTime.remove();
    }
}
