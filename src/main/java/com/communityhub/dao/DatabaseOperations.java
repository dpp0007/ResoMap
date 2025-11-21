package com.communityhub.dao;

import com.communityhub.exception.DatabaseException;

import java.util.List;

/**
 * Generic interface for database operations
 * Demonstrates generics and provides consistent CRUD operations for all entities
 * @param <T> The entity type this DAO operates on
 */
public interface DatabaseOperations<T> {
    
    /**
     * Creates a new entity in the database
     * @param entity The entity to create
     * @throws DatabaseException if creation fails
     */
    void create(T entity) throws DatabaseException;
    
    /**
     * Reads an entity from the database by ID
     * @param id The unique identifier of the entity
     * @return The entity if found, null otherwise
     * @throws DatabaseException if read operation fails
     */
    T read(String id) throws DatabaseException;
    
    /**
     * Updates an existing entity in the database
     * @param entity The entity to update
     * @throws DatabaseException if update fails
     */
    void update(T entity) throws DatabaseException;
    
    /**
     * Deletes an entity from the database by ID
     * @param id The unique identifier of the entity to delete
     * @throws DatabaseException if deletion fails
     */
    void delete(String id) throws DatabaseException;
    
    /**
     * Retrieves all entities from the database
     * @return List of all entities
     * @throws DatabaseException if retrieval fails
     */
    List<T> findAll() throws DatabaseException;
    
    /**
     * Checks if an entity exists in the database
     * @param id The unique identifier to check
     * @return true if entity exists, false otherwise
     * @throws DatabaseException if check operation fails
     */
    boolean exists(String id) throws DatabaseException;
    
    /**
     * Counts the total number of entities in the database
     * @return Total count of entities
     * @throws DatabaseException if count operation fails
     */
    long count() throws DatabaseException;
    
    /**
     * Finds entities by a specific field value
     * @param fieldName The name of the field to search by
     * @param value The value to search for
     * @return List of matching entities
     * @throws DatabaseException if search operation fails
     */
    List<T> findByField(String fieldName, Object value) throws DatabaseException;
}