package com.communityhub.exception;

/**
 * Exception thrown when database operations fail
 * Provides database-specific error handling and recovery information
 */
public class DatabaseException extends CommunityHubException {
    
    private String operation;
    private String tableName;
    
    /**
     * Creates a new DatabaseException
     * @param message Technical error message for logging
     * @param cause The underlying database exception
     */
    public DatabaseException(String message, Throwable cause) {
        super(message, "DATABASE_ERROR", 
              "A system error occurred. Please try again later.", cause);
    }
    
    /**
     * Creates a new DatabaseException with operation context
     * @param message Technical error message for logging
     * @param operation The database operation that failed
     * @param cause The underlying database exception
     */
    public DatabaseException(String message, String operation, Throwable cause) {
        super(message, "DATABASE_ERROR", 
              "Failed to " + operation + ". Please try again later.", cause);
        this.operation = operation;
    }
    
    /**
     * Creates a new DatabaseException with table context
     * @param message Technical error message for logging
     * @param operation The database operation that failed
     * @param tableName The table involved in the operation
     * @param cause The underlying database exception
     */
    public DatabaseException(String message, String operation, String tableName, Throwable cause) {
        super(message, "DATABASE_ERROR", 
              "Failed to " + operation + " " + tableName + ". Please try again later.", cause);
        this.operation = operation;
        this.tableName = tableName;
    }
    
    /**
     * Creates a new DatabaseException without cause
     * @param message Technical error message
     */
    public DatabaseException(String message) {
        super(message, "DATABASE_ERROR", 
              "A database error occurred. Please try again later.");
    }
    
    /**
     * Gets the database operation that failed
     * @return Operation name
     */
    public String getOperation() {
        return operation;
    }
    
    /**
     * Gets the table name involved in the operation
     * @return Table name
     */
    public String getTableName() {
        return tableName;
    }
    
    /**
     * Creates an exception for connection failures
     * @param cause The underlying connection exception
     * @return DatabaseException for connection failure
     */
    public static DatabaseException connectionFailed(Throwable cause) {
        return new DatabaseException("Database connection failed", "connect to database", cause);
    }
    
    /**
     * Creates an exception for transaction failures
     * @param operation The operation being performed in the transaction
     * @param cause The underlying exception
     * @return DatabaseException for transaction failure
     */
    public static DatabaseException transactionFailed(String operation, Throwable cause) {
        DatabaseException ex = new DatabaseException("Transaction failed during " + operation, 
                                                    operation, cause);
        ex.setUserMessage("The operation could not be completed. All changes have been rolled back.");
        return ex;
    }
    
    /**
     * Creates an exception for constraint violations
     * @param constraintName The name of the violated constraint
     * @param tableName The table where violation occurred
     * @return DatabaseException for constraint violation
     */
    public static DatabaseException constraintViolation(String constraintName, String tableName) {
        DatabaseException ex = new DatabaseException("Constraint violation: " + constraintName + " on table " + tableName, 
                                                    "validate data", tableName, null);
        ex.setUserMessage("The data you entered conflicts with existing records. Please check your input.");
        return ex;
    }
    
    /**
     * Creates an exception for duplicate key violations
     * @param fieldName The field that has a duplicate value
     * @param value The duplicate value
     * @return DatabaseException for duplicate key
     */
    public static DatabaseException duplicateKey(String fieldName, String value) {
        DatabaseException ex = new DatabaseException("Duplicate key violation for " + fieldName + ": " + value, 
                                                    "insert data", null);
        ex.setUserMessage("A record with this " + fieldName + " already exists. Please use a different value.");
        return ex;
    }
}