/**
 * Core Infrastructure Package
 * 
 * This package contains the foundational classes that support the entire ResoMap application.
 * These classes implement clean code principles and design patterns to reduce code duplication
 * and improve maintainability.
 * 
 * <h2>Key Components:</h2>
 * 
 * <h3>ServiceFactory</h3>
 * Implements the Dependency Injection pattern for service management.
 * Provides centralized, thread-safe service instance creation and caching.
 * 
 * <h3>ErrorHandler</h3>
 * Centralized error handling with user-friendly error messages.
 * Provides consistent error dialogs and logging integration.
 * 
 * <h3>BaseController</h3>
 * Abstract base class for all JavaFX controllers.
 * Provides common functionality like service injection, error handling, and navigation.
 * 
 * <h3>Constants</h3>
 * Application-wide constants organized by category.
 * Eliminates magic numbers and strings throughout the codebase.
 * 
 * <h3>ConfigurationManager</h3>
 * Manages application configuration from properties files.
 * Supports environment-specific configuration and runtime reloading.
 * 
 * <h2>Design Patterns Used:</h2>
 * <ul>
 *   <li>Singleton Pattern - ServiceFactory, ConfigurationManager</li>
 *   <li>Factory Pattern - Service creation</li>
 *   <li>Template Method Pattern - BaseController</li>
 *   <li>Dependency Injection - Service injection</li>
 * </ul>
 * 
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * public class MyController extends BaseController {
 *     
 *     @Override
 *     public void initialize(URL location, ResourceBundle resources) {
 *         try {
 *             initializeServices();
 *             loadData();
 *         } catch (DatabaseException e) {
 *             ErrorHandler.handleDatabaseError(e);
 *         }
 *     }
 *     
 *     private void loadData() {
 *         executeAsync(
 *             () -> resourceService.getAllResources(),
 *             resources -> updateUI(resources)
 *         );
 *     }
 * }
 * }</pre>
 * 
 * @author ResoMap Team
 * @version 2.0
 * @since 2.0
 */
package com.communityhub.core;
