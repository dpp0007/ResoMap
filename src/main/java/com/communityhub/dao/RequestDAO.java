package com.communityhub.dao;

import com.communityhub.exception.DatabaseException;
import com.communityhub.model.Request;
import com.communityhub.model.RequestStatus;
import com.communityhub.model.UrgencyLevel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Request entities
 * Demonstrates concrete DAO implementation with request-specific operations
 */
public class RequestDAO extends BaseDAO<Request> {
    
    public RequestDAO() throws DatabaseException {
        super();
    }
    
    @Override
    protected String getTableName() {
        return "requests";
    }
    
    @Override
    protected String getPrimaryKeyColumn() {
        return "request_id";
    }
    
    @Override
    protected String getInsertSQL() {
        return "INSERT INTO requests (request_id, requester_id, resource_id, volunteer_id, status, description, urgency_level, created_at, updated_at) " +
               "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }
    
    @Override
    protected String getUpdateSQL() {
        return "UPDATE requests SET requester_id = ?, resource_id = ?, volunteer_id = ?, status = ?, description = ?, urgency_level = ?, updated_at = ? " +
               "WHERE request_id = ?";
    }
    
    @Override
    protected void setInsertParameters(PreparedStatement stmt, Request request) throws SQLException {
        stmt.setString(1, request.getRequestId());
        stmt.setString(2, request.getRequesterId());
        stmt.setString(3, request.getResourceId());
        stmt.setString(4, request.getVolunteerId());
        stmt.setString(5, request.getStatus().toString());
        stmt.setString(6, request.getDescription());
        stmt.setString(7, request.getUrgencyLevel().toString());
        stmt.setTimestamp(8, Timestamp.valueOf(request.getCreatedAt()));
        stmt.setTimestamp(9, Timestamp.valueOf(request.getUpdatedAt()));
    }
    
    @Override
    protected void setUpdateParameters(PreparedStatement stmt, Request request) throws SQLException {
        stmt.setString(1, request.getRequesterId());
        stmt.setString(2, request.getResourceId());
        stmt.setString(3, request.getVolunteerId());
        stmt.setString(4, request.getStatus().toString());
        stmt.setString(5, request.getDescription());
        stmt.setString(6, request.getUrgencyLevel().toString());
        stmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
        stmt.setString(8, request.getRequestId());
    }
    
    @Override
    protected Request mapResultSetToEntity(ResultSet rs) throws SQLException {
        String requestId = rs.getString("request_id");
        String requesterId = rs.getString("requester_id");
        String resourceId = rs.getString("resource_id");
        String volunteerId = rs.getString("volunteer_id");
        RequestStatus status = RequestStatus.fromString(rs.getString("status"));
        String description = rs.getString("description");
        UrgencyLevel urgencyLevel = UrgencyLevel.fromString(rs.getString("urgency_level"));
        LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
        LocalDateTime updatedAt = rs.getTimestamp("updated_at").toLocalDateTime();
        
        return new Request(requestId, requesterId, resourceId, volunteerId, status, 
                         description, urgencyLevel, createdAt, updatedAt);
    }
    
    @Override
    public void create(Request request) throws DatabaseException {
        validateEntity(request, "create");
        
        if (!request.isValid()) {
            throw new DatabaseException("Cannot create request with invalid data");
        }
        
        executeInTransaction(() -> {
            PreparedStatement stmt = null;
            try {
                stmt = connection.prepareStatement(getInsertSQL());
                setInsertParameters(stmt, request);
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("Creating request failed, no rows affected");
                }
                
                logger.info("Request created successfully: " + request.getRequestId());
                
            } catch (SQLException e) {
                throw new DatabaseException("Failed to create request", "create request", e);
            } finally {
                closeStatement(stmt);
            }
        });
    }
    
    @Override
    public Request read(String requestId) throws DatabaseException {
        validateId(requestId, "read");
        
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            stmt = connection.prepareStatement(getSelectByIdSQL());
            stmt.setString(1, requestId);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToEntity(rs);
            }
            return null;
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to read request", "read request", e);
        } finally {
            closeResources(rs, stmt);
        }
    }
    
    @Override
    public void update(Request request) throws DatabaseException {
        validateEntity(request, "update");
        validateId(request.getRequestId(), "update");
        
        if (!request.isValid()) {
            throw new DatabaseException("Cannot update request with invalid data");
        }
        
        executeInTransaction(() -> {
            PreparedStatement stmt = null;
            try {
                stmt = connection.prepareStatement(getUpdateSQL());
                setUpdateParameters(stmt, request);
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("Request not found for update: " + request.getRequestId());
                }
                
                logger.info("Request updated successfully: " + request.getRequestId());
                
            } catch (SQLException e) {
                throw new DatabaseException("Failed to update request", "update request", e);
            } finally {
                closeStatement(stmt);
            }
        });
    }
    
    @Override
    public void delete(String requestId) throws DatabaseException {
        validateId(requestId, "delete");
        
        executeInTransaction(() -> {
            PreparedStatement stmt = null;
            try {
                stmt = connection.prepareStatement(getDeleteSQL());
                stmt.setString(1, requestId);
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("Request not found for deletion: " + requestId);
                }
                
                logger.info("Request deleted successfully: " + requestId);
                
            } catch (SQLException e) {
                throw new DatabaseException("Failed to delete request", "delete request", e);
            } finally {
                closeStatement(stmt);
            }
        });
    }
    
    @Override
    public List<Request> findAll() throws DatabaseException {
        List<Request> requests = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            stmt = connection.prepareStatement(getSelectAllSQL());
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                requests.add(mapResultSetToEntity(rs));
            }
            
            return requests;
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve all requests", "find all requests", e);
        } finally {
            closeResources(rs, stmt);
        }
    }
    
    @Override
    public boolean exists(String requestId) throws DatabaseException {
        validateId(requestId, "check existence");
        
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            stmt = connection.prepareStatement(getExistsSQL());
            stmt.setString(1, requestId);
            rs = stmt.executeQuery();
            
            return rs.next();
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to check request existence", "check request existence", e);
        } finally {
            closeResources(rs, stmt);
        }
    }
    
    @Override
    public long count() throws DatabaseException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            stmt = connection.prepareStatement(getCountSQL());
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count requests", "count requests", e);
        } finally {
            closeResources(rs, stmt);
        }
    }
    
    @Override
    public List<Request> findByField(String fieldName, Object value) throws DatabaseException {
        List<Request> requests = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            String sql = "SELECT * FROM " + getTableName() + " WHERE " + fieldName + " = ?";
            stmt = connection.prepareStatement(sql);
            stmt.setObject(1, value);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                requests.add(mapResultSetToEntity(rs));
            }
            
            return requests;
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find requests by " + fieldName, "find requests", e);
        } finally {
            closeResources(rs, stmt);
        }
    }
    
    /**
     * Finds requests by requester ID
     * @param requesterId ID of the requester
     * @return List of requests by the requester
     * @throws DatabaseException if search fails
     */
    public List<Request> findByRequesterId(String requesterId) throws DatabaseException {
        if (requesterId == null || requesterId.trim().isEmpty()) {
            throw new DatabaseException("Requester ID cannot be null or empty");
        }
        
        return findByField("requester_id", requesterId);
    }
    
    /**
     * Finds requests by volunteer ID
     * @param volunteerId ID of the volunteer
     * @return List of requests assigned to the volunteer
     * @throws DatabaseException if search fails
     */
    public List<Request> findByVolunteerId(String volunteerId) throws DatabaseException {
        if (volunteerId == null || volunteerId.trim().isEmpty()) {
            throw new DatabaseException("Volunteer ID cannot be null or empty");
        }
        
        return findByField("volunteer_id", volunteerId);
    }
    
    /**
     * Finds requests by status
     * @param status Request status
     * @return List of requests with the specified status
     * @throws DatabaseException if search fails
     */
    public List<Request> findByStatus(RequestStatus status) throws DatabaseException {
        if (status == null) {
            throw new DatabaseException("Status cannot be null");
        }
        
        return findByField("status", status.toString());
    }
    
    /**
     * Finds pending requests (not assigned to any volunteer)
     * @return List of pending requests
     * @throws DatabaseException if search fails
     */
    public List<Request> findPendingRequests() throws DatabaseException {
        return findByStatus(RequestStatus.PENDING);
    }
    
    /**
     * Finds requests by urgency level
     * @param urgencyLevel Urgency level
     * @return List of requests with the specified urgency level
     * @throws DatabaseException if search fails
     */
    public List<Request> findByUrgencyLevel(UrgencyLevel urgencyLevel) throws DatabaseException {
        if (urgencyLevel == null) {
            throw new DatabaseException("Urgency level cannot be null");
        }
        
        return findByField("urgency_level", urgencyLevel.toString());
    }
    
    /**
     * Finds critical requests that need immediate attention
     * @return List of critical requests
     * @throws DatabaseException if search fails
     */
    public List<Request> findCriticalRequests() throws DatabaseException {
        List<Request> criticalRequests = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            String sql = "SELECT * FROM " + getTableName() + 
                        " WHERE urgency_level = ? AND status IN (?, ?, ?) ORDER BY created_at ASC";
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, UrgencyLevel.CRITICAL.toString());
            stmt.setString(2, RequestStatus.PENDING.toString());
            stmt.setString(3, RequestStatus.ASSIGNED.toString());
            stmt.setString(4, RequestStatus.IN_PROGRESS.toString());
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                criticalRequests.add(mapResultSetToEntity(rs));
            }
            
            return criticalRequests;
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find critical requests", "find critical requests", e);
        } finally {
            closeResources(rs, stmt);
        }
    }
    
    /**
     * Finds overdue requests (older than specified hours and still active)
     * @param hours Number of hours to consider overdue
     * @return List of overdue requests
     * @throws DatabaseException if search fails
     */
    public List<Request> findOverdueRequests(int hours) throws DatabaseException {
        List<Request> overdueRequests = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            LocalDateTime cutoffTime = LocalDateTime.now().minusHours(hours);
            String sql = "SELECT * FROM " + getTableName() + 
                        " WHERE created_at < ? AND status IN (?, ?, ?) ORDER BY created_at ASC";
            stmt = connection.prepareStatement(sql);
            stmt.setTimestamp(1, Timestamp.valueOf(cutoffTime));
            stmt.setString(2, RequestStatus.PENDING.toString());
            stmt.setString(3, RequestStatus.ASSIGNED.toString());
            stmt.setString(4, RequestStatus.IN_PROGRESS.toString());
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                overdueRequests.add(mapResultSetToEntity(rs));
            }
            
            return overdueRequests;
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find overdue requests", "find overdue requests", e);
        } finally {
            closeResources(rs, stmt);
        }
    }
    
    /**
     * Assigns a volunteer to a request
     * @param requestId ID of the request
     * @param volunteerId ID of the volunteer
     * @throws DatabaseException if assignment fails
     */
    public void assignVolunteer(String requestId, String volunteerId) throws DatabaseException {
        validateId(requestId, "assign volunteer");
        validateId(volunteerId, "assign volunteer");
        
        executeInTransaction(() -> {
            PreparedStatement stmt = null;
            try {
                String sql = "UPDATE " + getTableName() + 
                           " SET volunteer_id = ?, status = ?, updated_at = ? WHERE request_id = ? AND status = ?";
                stmt = connection.prepareStatement(sql);
                stmt.setString(1, volunteerId);
                stmt.setString(2, RequestStatus.ASSIGNED.toString());
                stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                stmt.setString(4, requestId);
                stmt.setString(5, RequestStatus.PENDING.toString());
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("Request not found or not in pending status: " + requestId);
                }
                
                logger.info("Volunteer assigned to request: " + requestId + " -> " + volunteerId);
                
            } catch (SQLException e) {
                throw new DatabaseException("Failed to assign volunteer", "assign volunteer", e);
            } finally {
                closeStatement(stmt);
            }
        });
    }
    
    /**
     * Updates request status
     * @param requestId ID of the request
     * @param newStatus New status
     * @throws DatabaseException if update fails
     */
    public void updateStatus(String requestId, RequestStatus newStatus) throws DatabaseException {
        validateId(requestId, "update status");
        if (newStatus == null) {
            throw new DatabaseException("Status cannot be null");
        }
        
        executeInTransaction(() -> {
            PreparedStatement stmt = null;
            try {
                String sql = "UPDATE " + getTableName() + " SET status = ?, updated_at = ? WHERE request_id = ?";
                stmt = connection.prepareStatement(sql);
                stmt.setString(1, newStatus.toString());
                stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                stmt.setString(3, requestId);
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("Request not found for status update: " + requestId);
                }
                
                logger.info("Request status updated: " + requestId + " -> " + newStatus);
                
            } catch (SQLException e) {
                throw new DatabaseException("Failed to update request status", "update status", e);
            } finally {
                closeStatement(stmt);
            }
        });
    }
}