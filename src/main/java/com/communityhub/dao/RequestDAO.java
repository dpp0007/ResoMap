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
        String volunteerId = rs.getString("volunteer_id"); // Can be null for unassigned requests
        RequestStatus status = RequestStatus.valueOf(rs.getString("status"));
        String description = rs.getString("description");
        UrgencyLevel urgencyLevel = UrgencyLevel.valueOf(rs.getString("urgency_level"));
        
        // Handle null timestamps safely
        Timestamp createdTimestamp = rs.getTimestamp("created_at");
        LocalDateTime createdAt = (createdTimestamp != null) ? createdTimestamp.toLocalDateTime() : LocalDateTime.now();
        
        Timestamp updatedTimestamp = rs.getTimestamp("updated_at");
        LocalDateTime updatedAt = (updatedTimestamp != null) ? updatedTimestamp.toLocalDateTime() : LocalDateTime.now();
        
        return new Request(requestId, requesterId, resourceId, volunteerId, 
                         status, description, urgencyLevel, createdAt, updatedAt);
    }
    
    @Override
    public void create(Request request) throws DatabaseException {
        validateEntity(request, "create");
        
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
}