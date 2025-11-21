package com.communityhub.service;

import javafx.application.Platform;
import javafx.concurrent.Task;

import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Map;

/**
 * Service for managing background data loading operations
 * Demonstrates multithreading with JavaFX Task integration and progress tracking
 */
public class DataLoadingService {
    
    private static final Logger logger = Logger.getLogger(DataLoadingService.class.getName());
    
    private final ExecutorService backgroundExecutor;
    private final ScheduledExecutorService scheduledExecutor;
    
    // Thread-safe collections for task management
    private final Map<String, Future<?>> activeTasks;
    private final Map<String, ScheduledFuture<?>> scheduledTasks;
    
    /**
     * Constructor initializes the data loading service
     */
    public DataLoadingService() {
        this.backgroundExecutor = Executors.newCachedThreadPool(r -> {
            Thread t = new Thread(r, "DataLoader-" + System.currentTimeMillis());
            t.setDaemon(true);
            return t;
        });
        
        this.scheduledExecutor = Executors.newScheduledThreadPool(2, r -> {
            Thread t = new Thread(r, "DataScheduler-" + System.currentTimeMillis());
            t.setDaemon(true);
            return t;
        });
        
        this.activeTasks = new ConcurrentHashMap<>();
        this.scheduledTasks = new ConcurrentHashMap<>();
        
        logger.info("DataLoadingService initialized");
    }
    
    /**
     * Loads data asynchronously with progress tracking
     * @param taskName Unique name for the task
     * @param dataLoader Function that loads the data
     * @param onSuccess Callback for successful completion
     * @param onFailure Callback for failure
     * @param <T> Type of data being loaded
     * @return ProgressAwareTask for monitoring
     */
    public <T> ProgressAwareTask<T> loadDataAsync(String taskName, 
                                                 Supplier<T> dataLoader,
                                                 Consumer<T> onSuccess,
                                                 Consumer<Throwable> onFailure) {
        
        ProgressAwareTask<T> task = new ProgressAwareTask<>(taskName, dataLoader);
        
        task.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                try {
                    onSuccess.accept(task.getValue());
                } catch (Exception ex) {
                    logger.log(Level.WARNING, "Error in success callback", ex);
                }
            });
            activeTasks.remove(taskName);
        });
        
        task.setOnFailed(e -> {
            Platform.runLater(() -> {
                try {
                    onFailure.accept(task.getException());
                } catch (Exception ex) {
                    logger.log(Level.WARNING, "Error in failure callback", ex);
                }
            });
            activeTasks.remove(taskName);
        });
        
        Future<?> future = backgroundExecutor.submit(task);
        activeTasks.put(taskName, future);
        
        logger.info("Started async data loading task: " + taskName);
        return task;
    }
    
    /**
     * Loads data with automatic retry mechanism
     * @param taskName Unique name for the task
     * @param dataLoader Function that loads the data
     * @param onSuccess Callback for successful completion
     * @param onFailure Callback for final failure
     * @param maxRetries Maximum number of retry attempts
     * @param retryDelay Delay between retries in seconds
     * @param <T> Type of data being loaded
     */
    public <T> void loadDataWithRetry(String taskName,
                                     Supplier<T> dataLoader,
                                     Consumer<T> onSuccess,
                                     Consumer<Throwable> onFailure,
                                     int maxRetries,
                                     int retryDelay) {
        
        RetryTask<T> retryTask = new RetryTask<>(taskName, dataLoader, maxRetries, retryDelay);
        
        retryTask.setOnSucceeded(e -> {
            Platform.runLater(() -> onSuccess.accept(retryTask.getValue()));
            activeTasks.remove(taskName);
        });
        
        retryTask.setOnFailed(e -> {
            Platform.runLater(() -> onFailure.accept(retryTask.getException()));
            activeTasks.remove(taskName);
        });
        
        Future<?> future = backgroundExecutor.submit(retryTask);
        activeTasks.put(taskName, future);
        
        logger.info("Started retry task: " + taskName + " (max retries: " + maxRetries + ")");
    }
    
    /**
     * Schedules periodic data refresh
     * @param taskName Unique name for the scheduled task
     * @param dataLoader Function that loads the data
     * @param onSuccess Callback for successful completion
     * @param onFailure Callback for failure
     * @param initialDelay Initial delay in seconds
     * @param period Period between refreshes in seconds
     * @param <T> Type of data being loaded
     * @return ScheduledFuture for cancellation
     */
    public <T> ScheduledFuture<?> schedulePeriodicRefresh(String taskName,
                                                         Supplier<T> dataLoader,
                                                         Consumer<T> onSuccess,
                                                         Consumer<Throwable> onFailure,
                                                         long initialDelay,
                                                         long period) {
        
        ScheduledFuture<?> scheduledFuture = scheduledExecutor.scheduleAtFixedRate(() -> {
            try {
                T data = dataLoader.get();
                Platform.runLater(() -> onSuccess.accept(data));
                logger.fine("Periodic refresh completed: " + taskName);
            } catch (Exception e) {
                logger.log(Level.WARNING, "Periodic refresh failed: " + taskName, e);
                Platform.runLater(() -> onFailure.accept(e));
            }
        }, initialDelay, period, TimeUnit.SECONDS);
        
        scheduledTasks.put(taskName, scheduledFuture);
        logger.info("Scheduled periodic refresh: " + taskName + " (period: " + period + "s)");
        
        return scheduledFuture;
    }
    
    /**
     * Cancels a running task
     * @param taskName Name of the task to cancel
     * @return true if task was cancelled
     */
    public boolean cancelTask(String taskName) {
        Future<?> task = activeTasks.remove(taskName);
        if (task != null) {
            boolean cancelled = task.cancel(true);
            logger.info("Task cancelled: " + taskName + " (success: " + cancelled + ")");
            return cancelled;
        }
        return false;
    }
    
    /**
     * Cancels a scheduled task
     * @param taskName Name of the scheduled task to cancel
     * @return true if task was cancelled
     */
    public boolean cancelScheduledTask(String taskName) {
        ScheduledFuture<?> task = scheduledTasks.remove(taskName);
        if (task != null) {
            boolean cancelled = task.cancel(true);
            logger.info("Scheduled task cancelled: " + taskName + " (success: " + cancelled + ")");
            return cancelled;
        }
        return false;
    }
    
    /**
     * Gets statistics about active tasks
     * @return Map with task statistics
     */
    public Map<String, Object> getTaskStatistics() {
        Map<String, Object> stats = new ConcurrentHashMap<>();
        stats.put("activeTasks", activeTasks.size());
        stats.put("scheduledTasks", scheduledTasks.size());
        stats.put("activeThreads", ((ThreadPoolExecutor) backgroundExecutor).getActiveCount());
        stats.put("completedTasks", ((ThreadPoolExecutor) backgroundExecutor).getCompletedTaskCount());
        stats.put("queuedTasks", ((ThreadPoolExecutor) backgroundExecutor).getQueue().size());
        return stats;
    }
    
    /**
     * Shuts down the data loading service
     */
    public void shutdown() {
        // Cancel all active tasks
        activeTasks.values().forEach(task -> task.cancel(true));
        scheduledTasks.values().forEach(task -> task.cancel(true));
        
        backgroundExecutor.shutdown();
        scheduledExecutor.shutdown();
        
        try {
            if (!backgroundExecutor.awaitTermination(30, TimeUnit.SECONDS)) {
                backgroundExecutor.shutdownNow();
            }
            if (!scheduledExecutor.awaitTermination(30, TimeUnit.SECONDS)) {
                scheduledExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            backgroundExecutor.shutdownNow();
            scheduledExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        logger.info("DataLoadingService shut down");
    }
    
    /**
     * Progress-aware task that provides detailed progress information
     */
    public static class ProgressAwareTask<T> extends Task<T> {
        private final String taskName;
        private final Supplier<T> operation;
        
        public ProgressAwareTask(String taskName, Supplier<T> operation) {
            this.taskName = taskName;
            this.operation = operation;
        }
        
        @Override
        protected T call() throws Exception {
            updateTitle(taskName);
            updateMessage("Initializing...");
            updateProgress(0, 100);
            
            try {
                // Simulate progress updates
                for (int i = 0; i <= 100; i += 20) {
                    if (isCancelled()) {
                        updateMessage("Cancelled");
                        break;
                    }
                    
                    updateProgress(i, 100);
                    updateMessage("Processing... " + i + "%");
                    Thread.sleep(50); // Simulate work
                }
                
                updateMessage("Loading data...");
                T result = operation.get();
                
                updateProgress(100, 100);
                updateMessage("Completed");
                
                return result;
                
            } catch (Exception e) {
                updateMessage("Failed: " + e.getMessage());
                throw e;
            }
        }
    }
    
    /**
     * Task with automatic retry capability
     */
    private static class RetryTask<T> extends Task<T> {
        private final String taskName;
        private final Supplier<T> operation;
        private final int maxRetries;
        private final int retryDelay;
        
        public RetryTask(String taskName, Supplier<T> operation, int maxRetries, int retryDelay) {
            this.taskName = taskName;
            this.operation = operation;
            this.maxRetries = maxRetries;
            this.retryDelay = retryDelay;
        }
        
        @Override
        protected T call() throws Exception {
            updateTitle(taskName + " (with retry)");
            
            Exception lastException = null;
            
            for (int attempt = 1; attempt <= maxRetries + 1; attempt++) {
                if (isCancelled()) {
                    updateMessage("Cancelled");
                    break;
                }
                
                try {
                    updateMessage("Attempt " + attempt + " of " + (maxRetries + 1));
                    updateProgress(attempt - 1, maxRetries + 1);
                    
                    T result = operation.get();
                    
                    updateProgress(maxRetries + 1, maxRetries + 1);
                    updateMessage("Completed on attempt " + attempt);
                    
                    return result;
                    
                } catch (Exception e) {
                    lastException = e;
                    logger.log(Level.WARNING, "Attempt " + attempt + " failed for task: " + taskName, e);
                    
                    if (attempt <= maxRetries) {
                        updateMessage("Retrying in " + retryDelay + " seconds...");
                        Thread.sleep(retryDelay * 1000L);
                    }
                }
            }
            
            updateMessage("Failed after " + (maxRetries + 1) + " attempts");
            throw new RuntimeException("Task failed after " + (maxRetries + 1) + " attempts", lastException);
        }
    }
}