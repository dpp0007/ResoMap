package com.communityhub.dao;

import com.communityhub.exception.DatabaseException;
import com.communityhub.model.Resource;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Resource entities
 * Demonstrates concrete DAO implementation with resource-specific operations
 */
public class ResourceDAO extends BaseDAO<Resource> {
    
    public ResourceDAO() throws DatabaseException {
        super();
    }
    
    @Override
    protected String getTableName() {
        return "resources";
    }
    
    @Override
    protected String getPrimaryKeyColumn() {
        return "resource_id";
    }
    
    @Override
    protected String getInsertSQL() {
        return "INSERT INTO resources (resource_id, name, description, category, quantity, location, contact_info, created_by, created_at, updated_at) " +
               "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }
    
    @Override
    protected String getUpdateSQL() {
        return "UPDATE resources SET name = ?, description = ?, category = ?, quantity = ?, location = ?, contact_info = ?, updated_at = ? " +
               "WHERE resource_id = ?";
    }
    
    @Override
    protected void setInsertParameters(PreparedStatement stmt, Resource resource) throws SQLException {
        stmt.setString(1, resource.getResourceId());
        stmt.setString(2, resource.getName());
        stmt.setString(3, resource.getDescription());
        stmt.setString(4, resource.getCategory());
        stmt.setInt(5, resource.getQuantity());
        stmt.setString(6, resource.getLocation());
        stmt.setString(7, resource.getContactInfo());
        stmt.setString(8, resource.getCreatedBy());
        stmt.setTimestamp(9, Timestamp.valueOf(resource.getCreatedAt()));
        stmt.setTimestamp(10, Timestamp.valueOf(resource.getUpdatedAt()));
    }
    
    @Override
    protected void setUpdateParameters(PreparedStatement stmt, Resource resource) throws SQLException {
        stmt.setString(1, resource.getName());
        stmt.setString(2, resource.getDescription());
        stmt.setString(3, resource.getCategory());
        stmt.setInt(4, resource.getQuantity());
        stmt.setString(5, resource.getLocation());
        stmt.setString(6, resource.getContactInfo());
        stmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
        stmt.setString(8, resource.getResourceId());
    }
    
    @Override
    protected Resource mapResultSetToEntity(ResultSet rs) throws SQLException {
        String resourceId = rs.getString("resource_id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        String category = rs.getString("category");
        int quantity = rs.getInt("quantity");
        String location = rs.getString("location");
        String contactInfo = rs.getString("contact_info");
        String createdBy = rs.getString("created_by");
        LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
        LocalDateTime updatedAt = rs.getTimestamp("updated_at").toLocalDateTime();
        
        return new Resource(resourceId, name, description, category, quantity, 
                          location, contactInfo, createdBy, createdAt, updatedAt);
    }
    
    @Override
    public void create(Resource resource) throws DatabaseException {
        validateEntity(resource, "create");
        
        if (!resource.isValid()) {
            throw new DatabaseException("Cannot create resource with invalid data");
        }
        
        executeInTransaction(() -> {
            PreparedStatement stmt = null;
            try {
                stmt = connection.prepareStatement(getInsertSQL());
                setInsertParameters(stmt, resource);
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("Creating resource failed, no rows affected");
                }
                
                logger.info("Resource created successfully: " + resource.getName());
                
            } catch (SQLException e) {
                throw new DatabaseException("Failed to create resource", "create resource", e);
            } finally {
                closeStatement(stmt);
            }
        });
    }
    
    @Override
    public Resource read(String resourceId) throws DatabaseException {
        validateId(resourceId, "read");
        
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            stmt = connection.prepareStatement(getSelectByIdSQL());
            stmt.setString(1, resourceId);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToEntity(rs);
            }
            return null;
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to read resource", "read resource", e);
        } finally {
            closeResources(rs, stmt);
        }
    }
    
    @Override
    public void update(Resource resource) throws DatabaseException {
        validateEntity(resource, "update");
        validateId(resource.getResourceId(), "update");
        
        if (!resource.isValid()) {
            throw new DatabaseException("Cannot update resource with invalid data");
        }
        
        executeInTransaction(() -> {
            PreparedStatement stmt = null;
            try {
                stmt = connection.prepareStatement(getUpdateSQL());
                setUpdateParameters(stmt, resource);
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("Resource not found for update: " + resource.getResourceId());
                }
                
                logger.info("Resource updated successfully: " + resource.getName());
                
            } catch (SQLException e) {
                throw new DatabaseException("Failed to update resource", "update resource", e);
            } finally {
                closeStatement(stmt);
            }
        });
    }
    
    @Override
    public void delete(String resourceId) throws DatabaseException {
        validateId(resourceId, "delete");
        
        executeInTransaction(() -> {
            PreparedStatement stmt = null;
            try {
                stmt = connection.prepareStatement(getDeleteSQL());
                stmt.setString(1, resourceId);
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("Resource not found for deletion: " + resourceId);
                }
                
                logger.info("Resource deleted successfully: " + resourceId);
                
            } catch (SQLException e) {
                throw new DatabaseException("Failed to delete resource", "delete resource", e);
            } finally {
                closeStatement(stmt);
            }
        });
    }
    
    @Override
    public List<Resource> findAll() throws DatabaseException {
        List<Resource> resources = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            stmt = connection.prepareStatement(getSelectAllSQL());
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                resources.add(mapResultSetToEntity(rs));
            }
            
            return resources;
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve all resources", "find all resources", e);
        } finally {
            closeResources(rs, stmt);
        }
    }
    
    @Override
    public boolean exists(String resourceId) throws DatabaseException {
        validateId(resourceId, "check existence");
        
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            stmt = connection.prepareStatement(getExistsSQL());
            stmt.setString(1, resourceId);
            rs = stmt.executeQuery();
            
            return rs.next();
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to check resource existence", "check resource existence", e);
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
            throw new DatabaseException("Failed to count resources", "count resources", e);
        } finally {
            closeResources(rs, stmt);
        }
    }
    
    @Override
    public List<Resource> findByField(String fieldName, Object value) throws DatabaseException {
        List<Resource> resources = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            String sql = "SELECT * FROM " + getTableName() + " WHERE " + fieldName + " = ?";
            stmt = connection.prepareStatement(sql);
            stmt.setObject(1, value);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                resources.add(mapResultSetToEntity(rs));
            }
            
            return resources;
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find resources by " + fieldName, "find resources", e);
        } finally {
            closeResources(rs, stmt);
        }
    }
    
    /**
     * Finds resources by category
     * @param category Category to search for
     * @return List of resources in the specified category
     * @throws DatabaseException if search fails
     */
    public List<Resource> findByCategory(String category) throws DatabaseException {
        if (category == null || category.trim().isEmpty()) {
            throw new DatabaseException("Category cannot be null or empty");
        }
        
        return findByField("category", category);
    }
    
    /**
     * Finds available resources (quantity > 0)
     * @return List of available resources
     * @throws DatabaseException if search fails
     */
    public List<Resource> findAvailable() throws DatabaseException {
        List<Resource> resources = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            String sql = "SELECT * FROM " + getTableName() + " WHERE quantity > 0";
            stmt = connection.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                resources.add(mapResultSetToEntity(rs));
            }
            
            return resources;
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find available resources", "find available resources", e);
        } finally {
            closeResources(rs, stmt);
        }
    }
    
    /**
     * Searches resources by name or description
     * @param searchTerm Search term to look for
     * @return List of matching resources
     * @throws DatabaseException if search fails
     */
    public List<Resource> searchByNameOrDescription(String searchTerm) throws DatabaseException {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return findAll();
        }
        
        List<Resource> resources = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            String sql = "SELECT * FROM " + getTableName() + 
                        " WHERE LOWER(name) LIKE ? OR LOWER(description) LIKE ?";
            stmt = connection.prepareStatement(sql);
            String searchPattern = "%" + searchTerm.toLowerCase() + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                resources.add(mapResultSetToEntity(rs));
            }
            
            return resources;
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to search resources", "search resources", e);
        } finally {
            closeResources(rs, stmt);
        }
    }
    
    /**
     * Gets all unique categories
     * @return List of unique categories
     * @throws DatabaseException if retrieval fails
     */
    public List<String> getAllCategories() throws DatabaseException {
        List<String> categories = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            String sql = "SELECT DISTINCT category FROM " + getTableName() + " ORDER BY category";
            stmt = connection.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                categories.add(rs.getString("category"));
            }
            
            return categories;
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to get categories", "get categories", e);
        } finally {
            closeResources(rs, stmt);
        }
    }
    
    /**
     * Updates resource quantity
     * @param resourceId ID of the resource
     * @param newQuantity New quantity value
     * @throws DatabaseException if update fails
     */
    public void updateQuantity(String resourceId, int newQuantity) throws DatabaseException {
        validateId(resourceId, "update quantity");
        
        executeInTransaction(() -> {
            PreparedStatement stmt = null;
            try {
                String sql = "UPDATE " + getTableName() + " SET quantity = ?, updated_at = ? WHERE resource_id = ?";
                stmt = connection.prepareStatement(sql);
                stmt.setInt(1, Math.max(0, newQuantity));
                stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                stmt.setString(3, resourceId);
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("Resource not found for quantity update: " + resourceId);
                }
                
                logger.info("Resource quantity updated: " + resourceId + " -> " + newQuantity);
                
            } catch (SQLException e) {
                throw new DatabaseException("Failed to update resource quantity", "update quantity", e);
            } finally {
                closeStatement(stmt);
            }
        });
    }
    
}
