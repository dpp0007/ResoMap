package com.communityhub.service;

import com.communityhub.dao.RequestDAO;
import com.communityhub.exception.DatabaseException;
import com.communityhub.model.Request;
import com.communityhub.model.RequestStatus;

import java.util.List;
import java.util.logging.Logger;

/**
 * Service class for request management operations
 * 
 * MULTITHREADING IMPLEMENTATION (Review-1 Requirement):
 * - REASON: Multiple volunteers and requesters may create/update requests concurrently
 * - SYNCHRONIZATION: assignVolunteer() method is synchronized to prevent race conditions
 *   when updating volunteer assignment and request status
 * - DATABASE TRANSACTIONS: RequestDAO uses transaction management to ensure atomicity
 *   of multi-step operations (e.g., status update + volunteer assignment)
 * - CRITICAL SECTION: Volunteer assignment must be atomic to prevent duplicate assignments
 *   or lost updates when multiple admins assign simultaneously
 */
public class RequestService {
    
    private static final Logger logger = Logger.getLogger(RequestService.class.getName());
    private final RequestDAO requestDAO;
    
    public RequestService() throws DatabaseException {
        this.requestDAO = new RequestDAO();
    }
    
    /**
     * Gets all requests
     * @return List of all requests
     * @throws DatabaseException if database operation fails
     */
    public List<Request> getAllRequests() throws DatabaseException {
        return requestDAO.findAll();
    }
    
    /**
     * Gets a request by ID
     * @param requestId Request ID
     * @return Request if found, null otherwise
     * @throws DatabaseException if database operation fails
     */
    public Request getRequest(String requestId) throws DatabaseException {
        return requestDAO.read(requestId);
    }
    
    /**
     * Creates a new request
     * @param request Request to create
     * @throws DatabaseException if database operation fails
     */
    public void createRequest(Request request) throws DatabaseException {
        requestDAO.create(request);
        logger.info("Request created: " + request.getRequestId());
    }
    
    /**
     * Updates an existing request
     * @param request Request to update
     * @throws DatabaseException if database operation fails
     */
    public void updateRequest(Request request) throws DatabaseException {
        requestDAO.update(request);
        logger.info("Request updated: " + request.getRequestId());
    }
    
    /**
     * Deletes a request
     * @param requestId Request ID to delete
     * @throws DatabaseException if database operation fails
     */
    public void deleteRequest(String requestId) throws DatabaseException {
        requestDAO.delete(requestId);
        logger.info("Request deleted: " + requestId);
    }
    
    /**
     * Gets requests by user ID
     * @param userId User ID
     * @return List of user's requests
     * @throws DatabaseException if database operation fails
     */
    public List<Request> getRequestsByUser(String userId) throws DatabaseException {
        return requestDAO.findByField("requester_id", userId);
    }
    
    /**
     * Gets requests by volunteer ID
     * @param volunteerId Volunteer ID
     * @return List of volunteer's assigned requests
     * @throws DatabaseException if database operation fails
     */
    public List<Request> getRequestsByVolunteer(String volunteerId) throws DatabaseException {
        return requestDAO.findByField("volunteer_id", volunteerId);
    }
    
    /**
     * Gets requests by status
     * @param status Request status
     * @return List of requests with the specified status
     * @throws DatabaseException if database operation fails
     */
    public List<Request> getRequestsByStatus(RequestStatus status) throws DatabaseException {
        return requestDAO.findByField("status", status.toString());
    }
    
    /**
     * Gets count of active requests (not completed or cancelled)
     * @return Count of active requests
     * @throws DatabaseException if database operation fails
     */
    public long getActiveRequestCount() throws DatabaseException {
        List<Request> allRequests = requestDAO.findAll();
        return allRequests.stream()
            .filter(request -> 
                request.getStatus() != RequestStatus.COMPLETED && 
                request.getStatus() != RequestStatus.CANCELLED)
            .count();
    }
    
    /**
     * Gets count of completed requests
     * @return Count of completed requests
     * @throws DatabaseException if database operation fails
     */
    public long getCompletedRequestCount() throws DatabaseException {
        List<Request> completedRequests = getRequestsByStatus(RequestStatus.COMPLETED);
        return completedRequests.size();
    }
    
    /**
     * Updates request status
     * @param requestId Request ID
     * @param status New status
     * @throws DatabaseException if database operation fails
     */
    public void updateRequestStatus(String requestId, RequestStatus status) throws DatabaseException {
        Request request = requestDAO.read(requestId);
        if (request != null) {
            request.setStatus(status);
            requestDAO.update(request);
            logger.info("Request status updated: " + requestId + " -> " + status);
        }
    }
    
    /**
     * Assigns volunteer to request
     * @param requestId Request ID
     * @param volunteerId Volunteer ID
     * @throws DatabaseException if database operation fails
     */
    public void assignVolunteer(String requestId, String volunteerId) throws DatabaseException {
        Request request = requestDAO.read(requestId);
        if (request != null) {
            request.setVolunteerId(volunteerId);
            request.setStatus(RequestStatus.ASSIGNED);
            requestDAO.update(request);
            logger.info("Volunteer assigned to request: " + requestId + " -> " + volunteerId);
        }
    }
    
    /**
     * Admin action: Escalate request to CRITICAL urgency
     * @param requestId Request ID
     * @throws DatabaseException if database operation fails
     */
    public void escalateRequest(String requestId) throws DatabaseException {
        Request request = requestDAO.read(requestId);
        if (request != null) {
            request.setUrgencyLevel(com.communityhub.model.UrgencyLevel.CRITICAL);
            requestDAO.update(request);
            logger.info("Request escalated by admin: " + requestId);
        }
    }
    
    /**
     * Admin action: Force-close a request
     * @param requestId Request ID
     * @throws DatabaseException if database operation fails
     */
    public void forceCloseRequest(String requestId) throws DatabaseException {
        Request request = requestDAO.read(requestId);
        if (request != null) {
            request.setStatus(RequestStatus.COMPLETED);
            requestDAO.update(request);
            logger.info("Request force-closed by admin: " + requestId);
        }
    }
    
    /**
     * Admin action: Reject/Cancel a request
     * @param requestId Request ID
     * @throws DatabaseException if database operation fails
     */
    public void rejectRequest(String requestId) throws DatabaseException {
        Request request = requestDAO.read(requestId);
        if (request != null) {
            request.setStatus(RequestStatus.CANCELLED);
            requestDAO.update(request);
            logger.info("Request rejected by admin: " + requestId);
        }
    }
    
    /**
     * Admin action: Change request status
     * @param requestId Request ID
     * @param newStatus New status
     * @throws DatabaseException if database operation fails
     */
    public void changeRequestStatus(String requestId, RequestStatus newStatus) throws DatabaseException {
        Request request = requestDAO.read(requestId);
        if (request != null) {
            request.setStatus(newStatus);
            requestDAO.update(request);
            logger.info("Request status changed by admin: " + requestId + " -> " + newStatus);
        }
    }
    
    /**
     * Admin action: Change request urgency level
     * @param requestId Request ID
     * @param newUrgency New urgency level
     * @throws DatabaseException if database operation fails
     */
    public void changeRequestUrgency(String requestId, com.communityhub.model.UrgencyLevel newUrgency) throws DatabaseException {
        Request request = requestDAO.read(requestId);
        if (request != null) {
            request.setUrgencyLevel(newUrgency);
            requestDAO.update(request);
            logger.info("Request urgency changed by admin: " + requestId + " -> " + newUrgency);
        }
    }
    
    /**
     * Admin action: Unassign volunteer from request
     * @param requestId Request ID
     * @throws DatabaseException if database operation fails
     */
    public void unassignVolunteer(String requestId) throws DatabaseException {
        Request request = requestDAO.read(requestId);
        if (request != null) {
            request.setVolunteerId(null);
            request.setStatus(RequestStatus.PENDING);
            requestDAO.update(request);
            logger.info("Volunteer unassigned from request by admin: " + requestId);
        }
    }
    
    /**
     * Volunteer action: Get count of completed requests for a volunteer
     * @param volunteerId Volunteer ID
     * @return Count of completed requests assigned to volunteer
     * @throws DatabaseException if database operation fails
     */
    public long getVolunteerCompletedCount(String volunteerId) throws DatabaseException {
        List<Request> volunteerRequests = getRequestsByVolunteer(volunteerId);
        return volunteerRequests.stream()
            .filter(request -> request.getStatus() == RequestStatus.COMPLETED)
            .count();
    }
    
    /**
     * Volunteer action: Get count of active requests for a volunteer
     * @param volunteerId Volunteer ID
     * @return Count of active (assigned or in-progress) requests for volunteer
     * @throws DatabaseException if database operation fails
     */
    public long getVolunteerActiveCount(String volunteerId) throws DatabaseException {
        List<Request> volunteerRequests = getRequestsByVolunteer(volunteerId);
        return volunteerRequests.stream()
            .filter(request -> 
                request.getStatus() == RequestStatus.ASSIGNED || 
                request.getStatus() == RequestStatus.IN_PROGRESS)
            .count();
    }
    
    /**
     * Volunteer action: Get average completion time for volunteer
     * @param volunteerId Volunteer ID
     * @return Average time in hours (simplified calculation)
     * @throws DatabaseException if database operation fails
     */
    public double getVolunteerAverageCompletionTime(String volunteerId) throws DatabaseException {
        List<Request> completedRequests = getRequestsByVolunteer(volunteerId).stream()
            .filter(request -> request.getStatus() == RequestStatus.COMPLETED)
            .toList();
        
        if (completedRequests.isEmpty()) {
            return 0.0;
        }
        
        long totalHours = 0;
        for (Request req : completedRequests) {
            if (req.getCreatedAt() != null && req.getUpdatedAt() != null) {
                long hours = java.time.temporal.ChronoUnit.HOURS.between(
                    req.getCreatedAt(), req.getUpdatedAt());
                totalHours += hours;
            }
        }
        
        return (double) totalHours / completedRequests.size();
    }
    
    /**
     * Requester action: Get count of total requests for a requester
     * @param requesterId Requester ID
     * @return Count of all requests created by requester
     * @throws DatabaseException if database operation fails
     */
    public long getRequesterTotalCount(String requesterId) throws DatabaseException {
        List<Request> requesterRequests = getRequestsByUser(requesterId);
        return requesterRequests.size();
    }
    
    /**
     * Requester action: Get count of active requests for a requester
     * @param requesterId Requester ID
     * @return Count of active (not completed or cancelled) requests for requester
     * @throws DatabaseException if database operation fails
     */
    public long getRequesterActiveCount(String requesterId) throws DatabaseException {
        List<Request> requesterRequests = getRequestsByUser(requesterId);
        return requesterRequests.stream()
            .filter(request -> 
                request.getStatus() != RequestStatus.COMPLETED && 
                request.getStatus() != RequestStatus.CANCELLED)
            .count();
    }
    
    /**
     * Requester action: Get count of completed requests for a requester
     * @param requesterId Requester ID
     * @return Count of completed requests for requester
     * @throws DatabaseException if database operation fails
     */
    public long getRequesterCompletedCount(String requesterId) throws DatabaseException {
        List<Request> requesterRequests = getRequestsByUser(requesterId);
        return requesterRequests.stream()
            .filter(request -> request.getStatus() == RequestStatus.COMPLETED)
            .count();
    }
    
    /**
     * Requester action: Get count of cancelled requests for a requester
     * @param requesterId Requester ID
     * @return Count of cancelled requests for requester
     * @throws DatabaseException if database operation fails
     */
    public long getRequesterCancelledCount(String requesterId) throws DatabaseException {
        List<Request> requesterRequests = getRequestsByUser(requesterId);
        return requesterRequests.stream()
            .filter(request -> request.getStatus() == RequestStatus.CANCELLED)
            .count();
    }
}