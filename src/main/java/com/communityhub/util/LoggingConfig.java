package com.communityhub.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.*;

/**
 * Centralized logging configuration for the Community Hub application
 * Provides file logging with rotation and performance monitoring
 */
public class LoggingConfig {
    
    private static final String LOG_DIR = "logs";
    private static final String LOG_FILE_PATTERN = LOG_DIR + "/community-hub-%g.log";
    private static final int MAX_LOG_FILES = 5;
    private static final int MAX_LOG_SIZE = 10 * 1024 * 1024; // 10MB
    
    private static boolean initialized = false;
    
    /**
     * Initializes the logging system
     */
    public static synchronized void initialize() {
        if (initialized) {
            return;
        }
        
        try {
            // Create logs directory if it doesn't exist
            Path logDir = Paths.get(LOG_DIR);
            if (!Files.exists(logDir)) {
                Files.createDirectories(logDir);
            }
            
            // Get root logger
            Logger rootLogger = Logger.getLogger("");
            
            // Remove default console handler
            Handler[] handlers = rootLogger.getHandlers();
            for (Handler handler : handlers) {
                rootLogger.removeHandler(handler);
            }
            
            // Add custom console handler
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.INFO);
            consoleHandler.setFormatter(new CustomFormatter());
            rootLogger.addHandler(consoleHandler);
            
            // Add file handler with rotation
            FileHandler fileHandler = new FileHandler(LOG_FILE_PATTERN, MAX_LOG_SIZE, MAX_LOG_FILES, true);
            fileHandler.setLevel(Level.ALL);
            fileHandler.setFormatter(new DetailedFormatter());
            rootLogger.addHandler(fileHandler);
            
            // Set root logger level
            rootLogger.setLevel(Level.ALL);
            
            // Configure specific loggers
            configureSpecificLoggers();
            
            initialized = true;
            
            Logger logger = Logger.getLogger(LoggingConfig.class.getName());
            logger.info("Logging system initialized successfully");
            logger.info("Log files location: " + logDir.toAbsolutePath());
            
        } catch (IOException e) {
            System.err.println("Failed to initialize logging system: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Configures specific loggers with appropriate levels
     */
    private static void configureSpecificLoggers() {
        // Application loggers
        Logger.getLogger("com.communityhub").setLevel(Level.ALL);
        
        // Database loggers
        Logger.getLogger("com.communityhub.dao").setLevel(Level.INFO);
        Logger.getLogger("com.communityhub.util.DBConnection").setLevel(Level.INFO);
        
        // Service loggers
        Logger.getLogger("com.communityhub.service").setLevel(Level.INFO);
        
        // UI loggers
        Logger.getLogger("com.communityhub.ui").setLevel(Level.WARNING);
        
        // External library loggers (reduce noise)
        Logger.getLogger("javafx").setLevel(Level.WARNING);
        Logger.getLogger("org.sqlite").setLevel(Level.WARNING);
    }
    
    /**
     * Custom formatter for console output
     */
    private static class CustomFormatter extends Formatter {
        private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        
        @Override
        public String format(LogRecord record) {
            LocalDateTime time = LocalDateTime.now();
            String level = record.getLevel().getName();
            String className = getSimpleClassName(record.getSourceClassName());
            String message = record.getMessage();
            
            StringBuilder sb = new StringBuilder();
            sb.append(time.format(timeFormatter))
              .append(" [").append(level).append("] ")
              .append(className).append(" - ")
              .append(message)
              .append(System.lineSeparator());
            
            if (record.getThrown() != null) {
                sb.append("Exception: ").append(record.getThrown().toString())
                  .append(System.lineSeparator());
            }
            
            return sb.toString();
        }
    }
    
    /**
     * Detailed formatter for file output
     */
    private static class DetailedFormatter extends Formatter {
        private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        
        @Override
        public String format(LogRecord record) {
            LocalDateTime time = LocalDateTime.now();
            String level = record.getLevel().getName();
            String thread = Thread.currentThread().getName();
            String className = record.getSourceClassName();
            String methodName = record.getSourceMethodName();
            String message = record.getMessage();
            
            StringBuilder sb = new StringBuilder();
            sb.append(time.format(timeFormatter))
              .append(" [").append(thread).append("] ")
              .append(level).append(" ")
              .append(className);
            
            if (methodName != null) {
                sb.append(".").append(methodName);
            }
            
            sb.append(" - ").append(message)
              .append(System.lineSeparator());
            
            if (record.getThrown() != null) {
                sb.append("Exception: ").append(record.getThrown().toString())
                  .append(System.lineSeparator());
                
                StackTraceElement[] stackTrace = record.getThrown().getStackTrace();
                for (StackTraceElement element : stackTrace) {
                    sb.append("    at ").append(element.toString())
                      .append(System.lineSeparator());
                }
            }
            
            return sb.toString();
        }
    }
    
    /**
     * Gets simple class name from fully qualified name
     * @param fullClassName Fully qualified class name
     * @return Simple class name
     */
    private static String getSimpleClassName(String fullClassName) {
        if (fullClassName == null) {
            return "Unknown";
        }
        
        int lastDot = fullClassName.lastIndexOf('.');
        return lastDot >= 0 ? fullClassName.substring(lastDot + 1) : fullClassName;
    }
    
    /**
     * Creates a performance logger for timing operations
     * @param className Class name for the logger
     * @return Performance logger instance
     */
    public static PerformanceLogger createPerformanceLogger(String className) {
        return new PerformanceLogger(Logger.getLogger(className + ".Performance"));
    }
    
    /**
     * Performance logger for timing operations
     */
    public static class PerformanceLogger {
        private final Logger logger;
        
        private PerformanceLogger(Logger logger) {
            this.logger = logger;
        }
        
        /**
         * Times an operation and logs the result
         * @param operationName Name of the operation
         * @param operation Operation to time
         * @param <T> Return type of the operation
         * @return Result of the operation
         */
        public <T> T timeOperation(String operationName, java.util.function.Supplier<T> operation) {
            long startTime = System.nanoTime();
            try {
                T result = operation.get();
                long duration = System.nanoTime() - startTime;
                logger.info(String.format("%s completed in %.2f ms", operationName, duration / 1_000_000.0));
                return result;
            } catch (Exception e) {
                long duration = System.nanoTime() - startTime;
                logger.warning(String.format("%s failed after %.2f ms: %s", operationName, duration / 1_000_000.0, e.getMessage()));
                throw e;
            }
        }
        
        /**
         * Times a void operation and logs the result
         * @param operationName Name of the operation
         * @param operation Operation to time
         */
        public void timeVoidOperation(String operationName, Runnable operation) {
            timeOperation(operationName, () -> {
                operation.run();
                return null;
            });
        }
        
        /**
         * Logs memory usage information
         * @param context Context for the memory measurement
         */
        public void logMemoryUsage(String context) {
            Runtime runtime = Runtime.getRuntime();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;
            long maxMemory = runtime.maxMemory();
            
            logger.info(String.format("Memory usage [%s]: Used=%.2f MB, Free=%.2f MB, Total=%.2f MB, Max=%.2f MB",
                context,
                usedMemory / 1024.0 / 1024.0,
                freeMemory / 1024.0 / 1024.0,
                totalMemory / 1024.0 / 1024.0,
                maxMemory / 1024.0 / 1024.0));
        }
    }
    
    /**
     * Shuts down the logging system gracefully
     */
    public static void shutdown() {
        Logger rootLogger = Logger.getLogger("");
        Handler[] handlers = rootLogger.getHandlers();
        
        for (Handler handler : handlers) {
            handler.close();
            rootLogger.removeHandler(handler);
        }
        
        System.out.println("Logging system shut down");
    }
}