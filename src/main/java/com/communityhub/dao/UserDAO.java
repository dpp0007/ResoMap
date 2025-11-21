package com.communityhub.dao;

import com.communityhub.exception.DatabaseException;
import com.communityhub.model.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for User entities
 * Demonstrates concrete DAO implementation with polymorphic user creation
 */
public class UserDAO extends BaseDAO<User> {
    
    public UserDAO() throws DatabaseException {
        super();
    }
    
    @Override
    protected String getTableName() {
        return "users";
    }
    
    @Override
    protected String getPrimaryKeyColumn() {
        return "user_id";
    }
    
    @Override
    protected String getInsertSQL() {
        return "INSERT INTO users (user_id, username, email, password_hash, role, created_at, updated_at) " +
               "VALUES (?, ?, ?, ?, ?, ?, ?)";
    }
    
    @Override
    protected String getUpdateSQL() {
        return "UPDATE users SET username = ?, email = ?, password_hash = ?, role = ?, updated_at = ? " +
               "WHERE user_id = ?";
    }
    
    @Override
    protected void setInsertParameters(PreparedStatement stmt, User user) throws SQLException {
        stmt.setString(1, user.getUserId());
        stmt.setString(2, user.getUsername());
        stmt.setString(3, user.getEmail());
        stmt.setString(4, user.getPasswordHash());
        stmt.setString(5, user.getRole().toString());
        stmt.setTimestamp(6, Timestamp.valueOf(user.getCreatedAt()));
        stmt.setTimestamp(7, Timestamp.valueOf(user.getUpdatedAt()));
    }
    
    @Override
    protected void setUpdateParameters(PreparedStatement stmt, User user) throws SQLException {
        stmt.setString(1, user.getUsername());
        stmt.setString(2, user.getEmail());
        stmt.setString(3, user.getPasswordHash());
        stmt.setString(4, user.getRole().toString());
        stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
        stmt.setString(6, user.getUserId());
    }
    
    @Override
    protected User mapResultSetToEntity(ResultSet rs) throws SQLException {
        String userId = rs.getString("user_id");
        String username = rs.getString("username");
        String email = rs.getString("email");
        String passwordHash = rs.getString("password_hash");
        UserRole role = UserRole.fromString(rs.getString("role"));
        LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
        LocalDateTime updatedAt = rs.getTimestamp("updated_at").toLocalDateTime();
        
        // Polymorphic user creation based on role
        switch (role) {
            case ADMIN:
                return new Admin(userId, username, email, passwordHash, createdAt, updatedAt);
            case VOLUNTEER:
                return new Volunteer(userId, username, email, passwordHash, createdAt, updatedAt);
            case REQUESTER:
                return new Requester(userId, username, email, passwordHash, createdAt, updatedAt);
            default:
                throw new SQLException("Unknown user role: " + role);
        }
    }
    
    @Override
    public void create(User user) throws DatabaseException {
        validateEntity(user, "create");
        
        if (!user.isValid()) {
            throw new DatabaseException("Cannot create user with invalid data");
        }
        
        executeInTransaction(() -> {
            PreparedStatement stmt = null;
            try {
                stmt = connection.prepareStatement(getInsertSQL());
                setInsertParameters(stmt, user);
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("Creating user failed, no rows affected");
                }
                
                logger.info("User created successfully: " + user.getUsername());
                
            } catch (SQLException e) {
                if (e.getMessage().contains("UNIQUE constraint failed") || 
                    e.getMessage().contains("Duplicate entry")) {
                    throw DatabaseException.duplicateKey("username or email", user.getUsername());
                }
                throw new DatabaseException("Failed to create user", "create user", e);
            } finally {
                closeStatement(stmt);
            }
        });
    }
    
    @Override
    public User read(String userId) throws DatabaseException {
        validateId(userId, "read");
        
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            stmt = connection.prepareStatement(getSelectByIdSQL());
            stmt.setString(1, userId);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToEntity(rs);
            }
            return null;
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to read user", "read user", e);
        } finally {
            closeResources(rs, stmt);
        }
    }
    
    @Override
    public void update(User user) throws DatabaseException {
        validateEntity(user, "update");
        validateId(user.getUserId(), "update");
        
        if (!user.isValid()) {
            throw new DatabaseException("Cannot update user with invalid data");
        }
        
        executeInTransaction(() -> {
            PreparedStatement stmt = null;
            try {
                stmt = connection.prepareStatement(getUpdateSQL());
                setUpdateParameters(stmt, user);
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("User not found for update: " + user.getUserId());
                }
                
                logger.info("User updated successfully: " + user.getUsername());
                
            } catch (SQLException e) {
                if (e.getMessage().contains("UNIQUE constraint failed") || 
                    e.getMessage().contains("Duplicate entry")) {
                    throw DatabaseException.duplicateKey("username or email", user.getUsername());
                }
                throw new DatabaseException("Failed to update user", "update user", e);
            } finally {
                closeStatement(stmt);
            }
        });
    }
    
    @Override
    public void delete(String userId) throws DatabaseException {
        validateId(userId, "delete");
        
        executeInTransaction(() -> {
            PreparedStatement stmt = null;
            try {
                stmt = connection.prepareStatement(getDeleteSQL());
                stmt.setString(1, userId);
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("User not found for deletion: " + userId);
                }
                
                logger.info("User deleted successfully: " + userId);
                
            } catch (SQLException e) {
                throw new DatabaseException("Failed to delete user", "delete user", e);
            } finally {
                closeStatement(stmt);
            }
        });
    }
    
    @Override
    public List<User> findAll() throws DatabaseException {
        List<User> users = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            stmt = connection.prepareStatement(getSelectAllSQL());
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                users.add(mapResultSetToEntity(rs));
            }
            
            return users;
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve all users", "find all users", e);
        } finally {
            closeResources(rs, stmt);
        }
    }
    
    @Override
    public boolean exists(String userId) throws DatabaseException {
        validateId(userId, "check existence");
        
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            stmt = connection.prepareStatement(getExistsSQL());
            stmt.setString(1, userId);
            rs = stmt.executeQuery();
            
            return rs.next();
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to check user existence", "check user existence", e);
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
            throw new DatabaseException("Failed to count users", "count users", e);
        } finally {
            closeResources(rs, stmt);
        }
    }
    
    @Override
    public List<User> findByField(String fieldName, Object value) throws DatabaseException {
        List<User> users = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            String sql = "SELECT * FROM " + getTableName() + " WHERE " + fieldName + " = ?";
            stmt = connection.prepareStatement(sql);
            stmt.setObject(1, value);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                users.add(mapResultSetToEntity(rs));
            }
            
            return users;
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find users by " + fieldName, "find users", e);
        } finally {
            closeResources(rs, stmt);
        }
    }
    
    /**
     * Finds a user by username
     * @param username Username to search for
     * @return User if found, null otherwise
     * @throws DatabaseException if search fails
     */
    public User findByUsername(String username) throws DatabaseException {
        if (username == null || username.trim().isEmpty()) {
            throw new DatabaseException("Username cannot be null or empty");
        }
        
        List<User> users = findByField("username", username);
        return users.isEmpty() ? null : users.get(0);
    }
    
    /**
     * Finds a user by email
     * @param email Email to search for
     * @return User if found, null otherwise
     * @throws DatabaseException if search fails
     */
    public User findByEmail(String email) throws DatabaseException {
        if (email == null || email.trim().isEmpty()) {
            throw new DatabaseException("Email cannot be null or empty");
        }
        
        List<User> users = findByField("email", email);
        return users.isEmpty() ? null : users.get(0);
    }
    
    /**
     * Finds users by role
     * @param role Role to search for
     * @return List of users with the specified role
     * @throws DatabaseException if search fails
     */
    public List<User> findByRole(UserRole role) throws DatabaseException {
        if (role == null) {
            throw new DatabaseException("Role cannot be null");
        }
        
        return findByField("role", role.toString());
    }
    
    /**
     * Authenticates a user by username and password hash
     * @param username Username
     * @param passwordHash Hashed password
     * @return User if authentication successful, null otherwise
     * @throws DatabaseException if authentication check fails
     */
    public User authenticate(String username, String passwordHash) throws DatabaseException {
        if (username == null || username.trim().isEmpty() || 
            passwordHash == null || passwordHash.trim().isEmpty()) {
            return null;
        }
        
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            String sql = "SELECT * FROM " + getTableName() + " WHERE username = ? AND password_hash = ?";
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, passwordHash);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToEntity(rs);
            }
            return null;
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to authenticate user", "authenticate user", e);
        } finally {
            closeResources(rs, stmt);
        }
    }
}