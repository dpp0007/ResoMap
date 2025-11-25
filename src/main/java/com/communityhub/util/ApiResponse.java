package com.communityhub.util;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Unified API response wrapper
 * Provides consistent response format across all endpoints
 * @param <T> Type of data being returned
 */
public class ApiResponse<T> {
    
    private boolean success;
    private String message;
    private T data;
    private Map<String, String> errors;
    private LocalDateTime timestamp;
    private String requestId;
    
    /**
     * Private constructor to enforce factory methods
     */
    private ApiResponse() {
        this.timestamp = LocalDateTime.now();
        this.requestId = UUID.randomUUID().toString();
    }
    
    /**
     * Creates a successful response with data
     * @param data Response data
     * @param <T> Type of data
     * @return ApiResponse with success status
     */
    public static <T> ApiResponse<T> success(T data) {
        return success(data, "Operation successful");
    }
    
    /**
     * Creates a successful response with data and custom message
     * @param data Response data
     * @param message Success message
     * @param <T> Type of data
     * @return ApiResponse with success status
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = true;
        response.message = message;
        response.data = data;
        return response;
    }
    
    /**
     * Creates a successful response without data
     * @param message Success message
     * @param <T> Type of data
     * @return ApiResponse with success status
     */
    public static <T> ApiResponse<T> success(String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = true;
        response.message = message;
        return response;
    }
    
    /**
     * Creates an error response with message
     * @param message Error message
     * @param <T> Type of data
     * @return ApiResponse with error status
     */
    public static <T> ApiResponse<T> error(String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = false;
        response.message = message;
        return response;
    }
    
    /**
     * Creates an error response with message and field errors
     * @param message Error message
     * @param errors Map of field-specific errors
     * @param <T> Type of data
     * @return ApiResponse with error status
     */
    public static <T> ApiResponse<T> error(String message, Map<String, String> errors) {
        ApiResponse<T> response = error(message);
        response.errors = errors;
        return response;
    }
    
    /**
     * Creates an error response with message and single field error
     * @param message Error message
     * @param field Field name
     * @param fieldError Field error message
     * @param <T> Type of data
     * @return ApiResponse with error status
     */
    public static <T> ApiResponse<T> error(String message, String field, String fieldError) {
        Map<String, String> errors = new HashMap<>();
        errors.put(field, fieldError);
        return error(message, errors);
    }
    
    /**
     * Creates an error response from exception
     * @param exception Exception to convert
     * @param <T> Type of data
     * @return ApiResponse with error status
     */
    public static <T> ApiResponse<T> fromException(Exception exception) {
        return error(exception.getMessage());
    }
    
    // Getters and setters
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
    
    public Map<String, String> getErrors() {
        return errors;
    }
    
    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getRequestId() {
        return requestId;
    }
    
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    
    @Override
    public String toString() {
        return "ApiResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", errors=" + errors +
                ", timestamp=" + timestamp +
                ", requestId='" + requestId + '\'' +
                '}';
    }
}
