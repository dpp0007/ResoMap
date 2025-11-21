package com.communityhub.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.communityhub.dao.RequestDAO;
import com.communityhub.dao.ResourceDAO;
import com.communityhub.dao.UserDAO;
import com.communityhub.exception.DatabaseException;
import com.communityhub.exception.InvalidInputException;
import com.communityhub.model.Request;
import com.communityhub.model.RequestStatus;
import com.communityhub.model.Resource;
import com.communityhub.model.UrgencyLevel;
import com.communityhub.model.User;
import com.communityhub.model.UserRole;
import com.communityhub.util.ValidationUtils;

/**
 * Service class for request management and volunteer coordination
 * Demonstrates concurrent collections, streams, and business logic
 */
public class RequestService {
    
    private static final Logger logger = Logger.getLogger(RequestService.class.getName());
    
    private final RequestDAO requestDAO;
    private final ResourceDAO resourceDAO;
    private final UserDAO userDAO;
    
    // Thread-safe collections for request management
    private final Map<String, Request> activeRequests;
    private final Map<String, List<String>> volunteerAssignments;
    private final Set<String> criticalRequests;
    
    /**
     * Constructor initializes the request service
     * @throws DatabaseException if DAO initialization fails
     */
    public RequestService() throws DatabaseException {
        this.requestDAO = new RequestDAO();
        this.resourceDAO = new ResourceDAO();
        this.userDAO = new UserDAO();
        this.activeRequests = new ConcurrentHashMap<>();
        this.volunteerAssignments = new ConcurrentHashMap<>();
        this.criticalRequests = ConcurrentHashMap.newKeySet();
        
        // Initialize cache
        refreshActiveRequests(); // @SuppressWarnings("OverridableMethodCallInConstructor") - Safe pattern for service initialization
    }
    
    /**
     * Submits a new help request
     * @param requesterId ID of the requester
     * @param resourceId ID of the requested resource
     * @param description Request description
     * @param urgencyLevel Urgency level
     * @param quantityRequested Quantity requested
     * @return Created request
     * @throws InvalidInputException if input validation fails
     * @throws DatabaseException if database operation fails
     */
    public synchronized Request submitRequest(String requesterId, String resourceId, String description, 
                                           UrgencyLevel urgencyLevel, int quantityRequested) 
            throws InvalidInputException, DatabaseException {
        
        // Validate input
        ValidationUtils.validateRequired(requesterId, "requester ID");
        ValidationUtils.validateRequired(resourceId, "resource ID");
        ValidationUtils.validateRequired(description, "description");
        ValidationUtils.validateNotNull(urgencyLevel, "urgency level");
        ValidationUtils.validateNumericRange(quantityRequested, "quantity requested", 1, 1000);
        
        // Verify requester exists and is a requester
        User requester = userDAO.read(requesterId);
        if (requester == null || requester.getRole() != UserRole.REQUESTER) {
            throw new InvalidInputException("requester ID", requesterId, "Invalid requester");
        }
        
        // Verify resource exists and has sufficient quantity
        Resource resource = resourceDAO.read(resourceId);
        if (resource == null) {
            throw new InvalidInputException("resource ID", resourceId, "Resource not found");
        }
        
        if (resource.getQuantity() < quantityRequested) {
            throw new InvalidInputException("quantity requested", String.valueOf(quantityRequested), 
                "Insufficient resource quantity available");
        }
        
        // Create request
        Request request = new Request(requesterId, resourceId, description, urgencyLevel, quantityRequested);
        
        // Save to database
        requestDAO.create(request);
        
        // Update cache
        activeRequests.put(request.getRequestId(), request);
        if (urgencyLevel == UrgencyLevel.CRITICAL) {
            criticalRequests.add(request.getRequestId());
        }
        
        logger.log(Level.INFO, "Request submitted: {0} by {1}", new Object[]{request.getRequestId(), requester.getUsername()});
        return request;
    }
    
    /**
     * Assigns a volunteer to a request
     * @param requestId Request ID
     * @param volunteerId Volunteer ID
     * @throws InvalidInputException if input validation fails
     * @throws DatabaseException if database operation fails
     */
    public synchronized void assignVolunteer(String requestId, String volunteerId) 
            throws InvalidInputException, DatabaseException {
        
        ValidationUtils.validateRequired(requestId, "request ID");
        ValidationUtils.validateRequired(volunteerId, "volunteer ID");
        
        // Verify request exists and is pending
        Request request = getRequest(requestId);
        if (request == null) {
            throw new InvalidInputException("request ID", requestId, "Request not found");
        }
        
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new InvalidInputException("request status", request.getStatus().toString(), 
                "Request is not in pending status");
        }
        
        // Verify volunteer exists and is available
        User volunteer = userDAO.read(volunteerId);
        if (volunteer == null || volunteer.getRole() != UserRole.VOLUNTEER) {
            throw new InvalidInputException("volunteer ID", volunteerId, "Invalid volunteer");
        }
        
        // Check volunteer availability (not overloaded)
        List<String> currentAssignments = volunteerAssignments.getOrDefault(volunteerId, new ArrayList<>());
        if (currentAssignments.size() >= 5) { // Max 5 concurrent assignments
            throw new InvalidInputException("volunteer assignment", volunteerId, 
                "Volunteer has reached maximum concurrent assignments");
        }
        
        // Assign volunteer
        requestDAO.assignVolunteer(requestId, volunteerId);
        
        // Update cache
        request.setVolunteerId(volunteerId);
        request.setStatus(RequestStatus.ASSIGNED);
        currentAssignments.add(requestId);
        volunteerAssignments.put(volunteerId, currentAssignments);
        
        logger.log(Level.INFO, "Volunteer assigned: {0} to request {1}", new Object[]{volunteerId, requestId});
    }
    
    /**
     * Updates request status
     * @param requestId Request ID
     * @param newStatus New status
     * @param updatedBy User ID making the update
     * @throws InvalidInputException if input validation fails
     * @throws DatabaseException if database operation fails
     */
    public synchronized void updateRequestStatus(String requestId, RequestStatus newStatus, String updatedBy) 
            throws InvalidInputException, DatabaseException {
        
        ValidationUtils.validateRequired(requestId, "request ID");
        ValidationUtils.validateNotNull(newStatus, "new status");
        ValidationUtils.validateRequired(updatedBy, "updated by");
        
        Request request = getRequest(requestId);
        if (request == null) {
            throw new InvalidInputException("request ID", requestId, "Request not found");
        }
        
        // Validate status transition
        if (!request.getStatus().canTransitionTo(newStatus)) {
            throw new InvalidInputException("status transition", 
                request.getStatus() + " -> " + newStatus, "Invalid status transition");
        }
        
        // Update in database
        requestDAO.updateStatus(requestId, newStatus);
        
        // Update cache
        request.setStatus(newStatus);
        
        // Handle completion
        if (newStatus == RequestStatus.COMPLETED) {
            activeRequests.remove(requestId);
            criticalRequests.remove(requestId);
            
            // Remove from volunteer assignments
            if (request.getVolunteerId() != null) {
                List<String> assignments = volunteerAssignments.get(request.getVolunteerId());
                if (assignments != null) {
                    assignments.remove(requestId);
                }
            }
        }
        
        logger.log(Level.INFO, "Request status updated: {0} -> {1} by {2}", new Object[]{requestId, newStatus, updatedBy});
    }
    
    /**
     * Gets a request by ID
     * @param requestId Request ID
     * @return Request if found, null otherwise
     * @throws DatabaseException if database operation fails
     */
    public Request getRequest(String requestId) throws DatabaseException {
        try {
            ValidationUtils.validateRequired(requestId, "request ID");
        } catch (InvalidInputException e) {
            throw new DatabaseException("Invalid request ID: " + e.getMessage());
        }
        
        // Check cache first
        Request request = activeRequests.get(requestId);
        if (request != null) {
            return request;
        }
        
        // Load from database
        return requestDAO.read(requestId);
    }
    
    /**
     * Gets requests by requester using streams
     * @param requesterId Requester ID
     * @return List of requests by the requester
     * @throws DatabaseException if database operation fails
     */
    public List<Request> getRequestsByRequester(String requesterId) throws DatabaseException {
        try {
            ValidationUtils.validateRequired(requesterId, "requester ID");
        } catch (InvalidInputException e) {
            throw new DatabaseException("Invalid requester ID: " + e.getMessage());
        }
        
        return activeRequests.values().stream()
            .filter(request -> request.getRequesterId().equals(requesterId))
            .sorted(Comparator.comparing(Request::getCreatedAt).reversed())
            .collect(Collectors.toList());
    }
    
    /**
     * Gets requests assigned to a volunteer using streams
     * @param volunteerId Volunteer ID
     * @return List of requests assigned to the volunteer
     * @throws DatabaseException if database operation fails
     */
    public List<Request> getRequestsByVolunteer(String volunteerId) throws DatabaseException {
        try {
            ValidationUtils.validateRequired(volunteerId, "volunteer ID");
        } catch (InvalidInputException e) {
            throw new DatabaseException("Invalid volunteer ID: " + e.getMessage());
        }
        
        return activeRequests.values().stream()
            .filter(request -> volunteerId.equals(request.getVolunteerId()))
            .sorted((r1, r2) -> {
                // Sort by urgency (critical first) then by creation date
                int urgencyCompare = Integer.compare(r2.getUrgencyLevel().getPriority(), 
                                                   r1.getUrgencyLevel().getPriority());
                return urgencyCompare != 0 ? urgencyCompare : 
                       r1.getCreatedAt().compareTo(r2.getCreatedAt());
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Gets pending requests (not assigned) using streams
     * @return List of pending requests sorted by urgency and age
     */
    public List<Request> getPendingRequests() {
        return activeRequests.values().stream()
            .filter(request -> request.getStatus() == RequestStatus.PENDING)
            .sorted((r1, r2) -> {
                // Sort by urgency (critical first) then by creation date (oldest first)
                int urgencyCompare = Integer.compare(r2.getUrgencyLevel().getPriority(), 
                                                   r1.getUrgencyLevel().getPriority());
                return urgencyCompare != 0 ? urgencyCompare : 
                       r1.getCreatedAt().compareTo(r2.getCreatedAt());
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Gets critical requests that need immediate attention
     * @return List of critical requests
     */
    public List<Request> getCriticalRequests() {
        return activeRequests.values().stream()
            .filter(request -> request.getUrgencyLevel() == UrgencyLevel.CRITICAL && request.isActive())
            .sorted(Comparator.comparing(Request::getCreatedAt))
            .collect(Collectors.toList());
    }
    
    /**
     * Gets overdue requests using streams
     * @param hours Number of hours to consider overdue
     * @return List of overdue requests
     */
    public List<Request> getOverdueRequests(int hours) {
        return activeRequests.values().stream()
            .filter(request -> request.isOverdue(hours))
            .sorted(Comparator.comparing(Request::getCreatedAt))
            .collect(Collectors.toList());
    }
    
    /**
     * Gets request statistics using streams and collectors
     * @return Map with request statistics
     */
    public Map<String, Object> getRequestStatistics() {
        List<Request> allRequests = new ArrayList<>(activeRequests.values());
        
        Map<RequestStatus, Long> statusCounts = allRequests.stream()
            .collect(Collectors.groupingBy(Request::getStatus, Collectors.counting()));
        
        Map<UrgencyLevel, Long> urgencyCounts = allRequests.stream()
            .collect(Collectors.groupingBy(Request::getUrgencyLevel, Collectors.counting()));
        
        long totalRequests = allRequests.size();
        long criticalCount = urgencyCounts.getOrDefault(UrgencyLevel.CRITICAL, 0L);
        long overdueCount = allRequests.stream()
            .filter(request -> request.isOverdue(24))
            .count();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalActiveRequests", totalRequests);
        stats.put("pendingRequests", statusCounts.getOrDefault(RequestStatus.PENDING, 0L));
        stats.put("assignedRequests", statusCounts.getOrDefault(RequestStatus.ASSIGNED, 0L));
        stats.put("inProgressRequests", statusCounts.getOrDefault(RequestStatus.IN_PROGRESS, 0L));
        stats.put("criticalRequests", criticalCount);
        stats.put("overdueRequests", overdueCount);
        stats.put("statusBreakdown", statusCounts);
        stats.put("urgencyBreakdown", urgencyCounts);
        
        return stats;
    }
    
    /**
     * Gets volunteer workload statistics
     * @return Map of volunteer ID to number of assigned requests
     */
    public Map<String, Integer> getVolunteerWorkload() {
        return activeRequests.values().stream()
            .filter(request -> request.getVolunteerId() != null && request.isActive())
            .collect(Collectors.groupingBy(
                Request::getVolunteerId,
                Collectors.collectingAndThen(Collectors.counting(), Math::toIntExact)
            ));
    }
    
    /**
     * Finds available volunteers for a request based on workload and specialization
     * @param requestId Request ID
     * @return List of suitable volunteer IDs
     * @throws DatabaseException if database operation fails
     */
    public List<String> findAvailableVolunteers(String requestId) throws DatabaseException {
        Request request = getRequest(requestId);
        if (request == null) {
            return new ArrayList<>();
        }
        
        // Get all volunteers
        List<User> volunteers = userDAO.findByRole(UserRole.VOLUNTEER);
        Map<String, Integer> workload = getVolunteerWorkload();
        
        return volunteers.stream()
            .filter(volunteer -> {
                int currentWorkload = workload.getOrDefault(volunteer.getUserId(), 0);
                return currentWorkload < 5; // Max 5 concurrent assignments
            })
            .sorted((v1, v2) -> {
                // Sort by current workload (ascending)
                int workload1 = workload.getOrDefault(v1.getUserId(), 0);
                int workload2 = workload.getOrDefault(v2.getUserId(), 0);
                return Integer.compare(workload1, workload2);
            })
            .map(User::getUserId)
            .collect(Collectors.toList());
    }
    
    /**
     * Gets available requests (pending requests) for volunteers
     * @return List of available requests
     */
    public List<Request> getAvailableRequests() {
        return getPendingRequests();
    }
    
    /**
     * Gets requests by user ID (works for both requesters and volunteers)
     * @param userId User ID
     * @return List of requests
     * @throws DatabaseException if database operation fails
     */
    public List<Request> getRequestsByUser(String userId) throws DatabaseException {
        try {
            ValidationUtils.validateRequired(userId, "user ID");
        } catch (InvalidInputException e) {
            throw new DatabaseException("Invalid user ID: " + e.getMessage());
        }
        
        // First try as requester
        List<Request> requests = getRequestsByRequester(userId);
        if (!requests.isEmpty()) {
            return requests;
        }
        
        // Then try as volunteer
        return getRequestsByVolunteer(userId);
    }
    
    /**
     * Creates a new request
     * @param request Request to create
     * @throws DatabaseException if database operation fails
     */
    public void createRequest(Request request) throws DatabaseException {
        try {
            ValidationUtils.validateNotNull(request, "request");
            ValidationUtils.validateRequired(request.getRequesterId(), "user ID");
            ValidationUtils.validateRequired(request.getResourceId(), "resource ID");
            ValidationUtils.validateRequired(request.getDescription(), "description");
        } catch (InvalidInputException e) {
            throw new DatabaseException("Invalid request data: " + e.getMessage());
        }
        
        // Set default values if not set
        if (request.getStatus() == null) {
            request.setStatus(RequestStatus.PENDING);
        }
        
        // Save to database
        requestDAO.create(request);
        
        // Update cache
        activeRequests.put(request.getRequestId(), request);
        
        logger.log(Level.INFO, "Request created: {0} by user {1}", new Object[]{request.getRequestId(), request.getRequesterId()});
    }
    
    /**
     * Updates an existing request
     * @param request Request to update
     * @throws DatabaseException if database operation fails
     */
    public void updateRequest(Request request) throws DatabaseException {
        try {
            ValidationUtils.validateNotNull(request, "request");
            ValidationUtils.validateRequired(request.getRequestId(), "request ID");
        } catch (InvalidInputException e) {
            throw new DatabaseException("Invalid request data: " + e.getMessage());
        }
        
        // Update in database
        requestDAO.update(request);
        
        // Update cache
        activeRequests.put(request.getRequestId(), request);
        
        logger.log(Level.INFO, "Request updated: {0}", request.getRequestId());
    }
    
    /**
     * Refreshes active requests cache from database
     * @throws DatabaseException if database operation fails
     */
    public void refreshActiveRequests() throws DatabaseException {
        List<Request> requests = requestDAO.findAll().stream()
            .filter(Request::isActive)
            .collect(Collectors.toList());
        
        synchronized (this) {
            activeRequests.clear();
            criticalRequests.clear();
            volunteerAssignments.clear();
            
            requests.forEach(request -> {
                activeRequests.put(request.getRequestId(), request);
                
                if (request.getUrgencyLevel() == UrgencyLevel.CRITICAL) {
                    criticalRequests.add(request.getRequestId());
                }
                
                if (request.getVolunteerId() != null) {
                    volunteerAssignments.computeIfAbsent(request.getVolunteerId(), k -> new ArrayList<>())
                                      .add(request.getRequestId());
                }
            });
        }
        
        logger.log(Level.INFO, "Active requests cache refreshed with {0} requests", requests.size());
    }
}