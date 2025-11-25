package com.communityhub.core;

import java.io.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Configuration Manager
 * Manages application configuration from properties file
 * 
 * Features:
 * - Load configuration from file
 * - Provide default values
 * - Type-safe configuration access
 * - Environment-specific configuration
 * 
 * @author ResoMap Team
 * @version 2.0
 */
public final class ConfigurationManager {
    
    private static final Logger logger = Logger.getLogger(ConfigurationManager.class.getName());
    private static volatile ConfigurationManager instance;
    
    private final Properties properties;
    private static final String CONFIG_FILE = "application.properties";
    private static final String DEFAULT_CONFIG_FILE = "application-default.properties";
    
    /**
     * Private constructor loads configuration
     */
    private ConfigurationManager() {
        this.properties = new Properties();
        loadConfiguration();
    }
    
    /**
     * Gets singleton instance
     * 
     * @return ConfigurationManager instance
     */
    public static ConfigurationManager getInstance() {
        if (instance == null) {
            synchronized (ConfigurationManager.class) {
                if (instance == null) {
                    instance = new ConfigurationManager();
                }
            }
        }
        return instance;
    }
    
    /**
     * Loads configuration from properties file
     */
    private void loadConfiguration() {
        // Try to load custom configuration
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input != null) {
                properties.load(input);
                logger.info("Loaded configuration from " + CONFIG_FILE);
                return;
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to load " + CONFIG_FILE, e);
        }
        
        // Fall back to default configuration
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(DEFAULT_CONFIG_FILE)) {
            if (input != null) {
                properties.load(input);
                logger.info("Loaded default configuration from " + DEFAULT_CONFIG_FILE);
            } else {
                logger.warning("No configuration file found, using hardcoded defaults");
                loadDefaultProperties();
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load default configuration", e);
            loadDefaultProperties();
        }
    }
    
    /**
     * Loads hardcoded default properties
     */
    private void loadDefaultProperties() {
        // Database
        properties.setProperty("db.url", Constants.Database.URL);
        properties.setProperty("db.driver", Constants.Database.DRIVER);
        properties.setProperty("db.connection.timeout", String.valueOf(Constants.Database.CONNECTION_TIMEOUT));
        properties.setProperty("db.pool.max", String.valueOf(Constants.Database.MAX_POOL_SIZE));
        properties.setProperty("db.pool.min", String.valueOf(Constants.Database.MIN_POOL_SIZE));
        
        // Session
        properties.setProperty("session.timeout.hours", String.valueOf(Constants.Session.TIMEOUT_HOURS));
        properties.setProperty("session.refresh.minutes", String.valueOf(Constants.Session.REFRESH_INTERVAL_MINUTES));
        
        // Security
        properties.setProperty("security.max.login.attempts", String.valueOf(Constants.Security.MAX_LOGIN_ATTEMPTS));
        properties.setProperty("security.lockout.duration.ms", String.valueOf(Constants.Security.LOCKOUT_DURATION_MS));
        properties.setProperty("security.password.min.length", String.valueOf(Constants.Security.PASSWORD_MIN_LENGTH));
        
        // Logging
        properties.setProperty("logging.directory", Constants.Logging.LOG_DIRECTORY);
        properties.setProperty("logging.file.pattern", Constants.Logging.LOG_FILE_PATTERN);
        properties.setProperty("logging.file.count", String.valueOf(Constants.Logging.LOG_FILE_COUNT));
        properties.setProperty("logging.file.size.mb", String.valueOf(Constants.Logging.LOG_FILE_SIZE_MB));
        
        logger.info("Loaded hardcoded default properties");
    }
    
    /**
     * Gets a string property
     * 
     * @param key Property key
     * @param defaultValue Default value if property not found
     * @return Property value or default
     */
    public String getString(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    /**
     * Gets a string property
     * 
     * @param key Property key
     * @return Property value or null
     */
    public String getString(String key) {
        return properties.getProperty(key);
    }
    
    /**
     * Gets an integer property
     * 
     * @param key Property key
     * @param defaultValue Default value if property not found or invalid
     * @return Property value or default
     */
    public int getInt(String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logger.warning("Invalid integer value for " + key + ": " + value);
            return defaultValue;
        }
    }
    
    /**
     * Gets a long property
     * 
     * @param key Property key
     * @param defaultValue Default value if property not found or invalid
     * @return Property value or default
     */
    public long getLong(String key, long defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            logger.warning("Invalid long value for " + key + ": " + value);
            return defaultValue;
        }
    }
    
    /**
     * Gets a boolean property
     * 
     * @param key Property key
     * @param defaultValue Default value if property not found
     * @return Property value or default
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        
        return Boolean.parseBoolean(value);
    }
    
    /**
     * Gets a double property
     * 
     * @param key Property key
     * @param defaultValue Default value if property not found or invalid
     * @return Property value or default
     */
    public double getDouble(String key, double defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            logger.warning("Invalid double value for " + key + ": " + value);
            return defaultValue;
        }
    }
    
    /**
     * Sets a property value
     * 
     * @param key Property key
     * @param value Property value
     */
    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }
    
    /**
     * Checks if a property exists
     * 
     * @param key Property key
     * @return true if property exists
     */
    public boolean hasProperty(String key) {
        return properties.containsKey(key);
    }
    
    /**
     * Gets all properties
     * 
     * @return Properties object
     */
    public Properties getProperties() {
        return new Properties(properties);
    }
    
    /**
     * Saves current properties to file
     * 
     * @param file File to save to
     * @throws IOException if save fails
     */
    public void saveToFile(File file) throws IOException {
        try (OutputStream output = new FileOutputStream(file)) {
            properties.store(output, "ResoMap Configuration");
            logger.info("Configuration saved to " + file.getAbsolutePath());
        }
    }
    
    /**
     * Reloads configuration from file
     */
    public synchronized void reload() {
        properties.clear();
        loadConfiguration();
        logger.info("Configuration reloaded");
    }
}
