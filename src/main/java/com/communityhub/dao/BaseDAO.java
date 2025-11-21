package com.communityhub.dao;

import com.communityhub.exception.DatabaseException;
import com.communityhub.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Abstract base class for all DAO implementations
 * Provides common database operations and transaction management
 * Demonstrates inheritance and template method pattern
 * @param <T> The entity type this DAO operates on
 */
public abstract class BaseDAO<T> implements DatabaseOperations<T> {
    
    protected static final Logger logger = Logger.getLogger(BaseDAO.class.getName());
    protected Connection connection;
    
    /**
     * Constructor initializes database connection
     * @throws DatabaseException if connection cannot be established
     */
    protected BaseDAO() throws DatabaseException {
        this.connection = DBConnection.getInstance().getConnection();
    }
    
    /**
     * Executes a database operation within a transaction
     * Provides automatic commit/rollback functionality
     * @param operation The operation to execute
     * @throws DatabaseException if operation fails
     */
    protected void executeInTransaction(DatabaseOperation operation) throws DatabaseException {
        boolean originalAutoCommit = true;
        
        try {
            originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            
            operation.execute();
            
            connection.commit();
            logger.info("Transaction committed successfully");
            
        } catch (SQLException e) {
            try {
                connection.rollback();
                logger.warning("Transaction rolled back due to error: " + e.getMessage());
            } catch (SQLException rollbackEx) {
                logger.log(Level.SEVERE, "Failed to rollback transaction", rollbackEx);
                throw new DatabaseException("Transaction rollback failed", rollbackEx);
            }
            throw new DatabaseException("Transaction failed", e);
        } catch (Exception e) {
            try {
                connection.rollback();
                logger.warning("Transaction rolled back due to error: " + e.getMessage());
            } catch (SQLException rollbackEx) {
                logger.log(Level.SEVERE, "Failed to rollback transaction", rollbackEx);
                throw new DatabaseException("Transaction rollback failed", rollbackEx);
            }
            throw new DatabaseException("Transaction failed", e);
        } finally {
            try {
                connection.setAutoCommit(originalAutoCommit);
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Failed to restore auto-commit setting", e);
            }
        }
    }
    
    /**
     * Safely closes database resources
     * @param rs ResultSet to close
     * @param stmt PreparedStatement to close
     */
    protected void closeResources(ResultSet rs, PreparedStatement stmt) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Failed to close ResultSet", e);
            }
        }
        
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Failed to close PreparedStatement", e);
            }
        }
    }
    
    /**
     * Safely closes a PreparedStatement
     * @param stmt PreparedStatement to close
     */
    protected void closeStatement(PreparedStatement stmt) {
        closeResources(null, stmt);
    }
    
    /**
     * Validates that an entity is not null
     * @param entity Entity to validate
     * @param operation Operation being performed (for error message)
     * @throws DatabaseException if entity is null
     */
    protected void validateEntity(T entity, String operation) throws DatabaseException {
        if (entity == null) {
            throw new DatabaseException("Cannot " + operation + " null entity");
        }
    }
    
    /**
     * Validates that an ID is not null or empty
     * @param id ID to validate
     * @param operation Operation being performed (for error message)
     * @throws DatabaseException if ID is invalid
     */
    protected void validateId(String id, String operation) throws DatabaseException {
        if (id == null || id.trim().isEmpty()) {
            throw new DatabaseException("Cannot " + operation + " entity with null or empty ID");
        }
    }
    
    /**
     * Gets the table name for this DAO
     * Must be implemented by concrete DAO classes
     * @return Table name
     */
    protected abstract String getTableName();
    
    /**
     * Gets the primary key column name for this DAO
     * Must be implemented by concrete DAO classes
     * @return Primary key column name
     */
    protected abstract String getPrimaryKeyColumn();
    
    /**
     * Maps a ResultSet row to an entity object
     * Must be implemented by concrete DAO classes
     * @param rs ResultSet positioned at a valid row
     * @return Entity object created from the row data
     * @throws SQLException if mapping fails
     */
    protected abstract T mapResultSetToEntity(ResultSet rs) throws SQLException;
    
    /**
     * Sets parameters for an insert PreparedStatement
     * Must be implemented by concrete DAO classes
     * @param stmt PreparedStatement for insert operation
     * @param entity Entity to insert
     * @throws SQLException if parameter setting fails
     */
    protected abstract void setInsertParameters(PreparedStatement stmt, T entity) throws SQLException;
    
    /**
     * Sets parameters for an update PreparedStatement
     * Must be implemented by concrete DAO classes
     * @param stmt PreparedStatement for update operation
     * @param entity Entity to update
     * @throws SQLException if parameter setting fails
     */
    protected abstract void setUpdateParameters(PreparedStatement stmt, T entity) throws SQLException;
    
    /**
     * Gets the insert SQL statement
     * Must be implemented by concrete DAO classes
     * @return SQL insert statement
     */
    protected abstract String getInsertSQL();
    
    /**
     * Gets the update SQL statement
     * Must be implemented by concrete DAO classes
     * @return SQL update statement
     */
    protected abstract String getUpdateSQL();
    
    /**
     * Gets the select by ID SQL statement
     * @return SQL select statement
     */
    protected String getSelectByIdSQL() {
        return "SELECT * FROM " + getTableName() + " WHERE " + getPrimaryKeyColumn() + " = ?";
    }
    
    /**
     * Gets the select all SQL statement
     * @return SQL select all statement
     */
    protected String getSelectAllSQL() {
        return "SELECT * FROM " + getTableName();
    }
    
    /**
     * Gets the delete SQL statement
     * @return SQL delete statement
     */
    protected String getDeleteSQL() {
        return "DELETE FROM " + getTableName() + " WHERE " + getPrimaryKeyColumn() + " = ?";
    }
    
    /**
     * Gets the count SQL statement
     * @return SQL count statement
     */
    protected String getCountSQL() {
        return "SELECT COUNT(*) FROM " + getTableName();
    }
    
    /**
     * Gets the exists SQL statement
     * @return SQL exists statement
     */
    protected String getExistsSQL() {
        return "SELECT 1 FROM " + getTableName() + " WHERE " + getPrimaryKeyColumn() + " = ? LIMIT 1";
    }
    
    /**
     * Functional interface for database operations that can be executed in transactions
     */
    @FunctionalInterface
    protected interface DatabaseOperation {
        void execute() throws Exception;
    }
}