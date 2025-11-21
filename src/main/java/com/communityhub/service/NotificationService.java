package com.communityhub.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.communityhub.dao.UserDAO;
import com.communityhub.exception.DatabaseException;
import com.communityhub.model.User;
import com.communityhub.model.UserRole;

/**
 * Service for handling notifications with multithreading support
 * Demonstrates ExecutorService, background processing, and concurrent operations
 */
public class NotificationService {
    
    private static final Logger logger = Logger.getLogger(NotificationService.class.getName());
    
    private final ExecutorService notificationExecutor;
    private final ScheduledExecutorService scheduledExecutor;
    private final UserDAO userDAO;
    
    // Thread-safe collections for notification management
    private final BlockingQueue<NotificationTask> notificationQueue;
    private final Map<String, LocalDateTime> lastNotificationTime;
    
    private volatile boolean isRunning = false;
    
    /**
     * Constructor initializes the notification service
     * @throws DatabaseException if DAO initialization fails
     */
    public NotificationService() throws DatabaseException {
        this.notificationExecutor = Executors.newFixedThreadPool(3);
        this.scheduledExecutor = Executors.newScheduledThreadPool(2);
        this.userDAO = new UserDAO();
        this.notificationQueue = new LinkedBlockingQueue<>();
        this.lastNotificationTime = new ConcurrentHashMap<>();
        
        startNotificationProcessor();
    }
    
    /**
     * Starts the background notification processor
     */
    private void startNotificationProcessor() {
        isRunning = true;
        
        // Background thread to process notification queue
        notificationExecutor.submit(() -> {
            while (isRunning) {
                try {
                    NotificationTask task = notificationQueue.take(); // Blocks until available
                    processNotification(task);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Error processing notification", e);
                }
            }
        });
        
        logger.info("Notification processor started");
    }
    
    /**
     * Sends a notification to a specific user
     * @param userId Target user ID
     * @param title Notification title
     * @param message Notification message
     * @param priority Priority level (1-5, 5 being highest)
     */
    public void sendNotification(String userId, String title, String message, int priority) {
        NotificationTask task = new NotificationTask(userId, title, message, priority, LocalDateTime.now());
        
        try {
            notificationQueue.offer(task, 5, TimeUnit.SECONDS);
            logger.log(Level.INFO, "Notification queued for user: {0} - {1}", new Object[]{userId, title});
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.log(Level.WARNING, "Failed to queue notification", e);
        }
    }
    
    /**
     * Sends notifications to multiple users asynchronously
     * @param userIds List of target user IDs
     * @param title Notification title
     * @param message Notification message
     * @param priority Priority level
     */
    public void sendBulkNotification(List<String> userIds, String title, String message, int priority) {
        CompletableFuture.runAsync(() -> {
            userIds.parallelStream().forEach(userId -> {
                sendNotification(userId, title, message, priority);
            });
        }, notificationExecutor);
        
        logger.log(Level.INFO, "Bulk notification initiated for {0} users", userIds.size());
    }
    
    /**
     * Sends notification to all users with a specific role
     * @param role Target user role
     * @param title Notification title
     * @param message Notification message
     * @param priority Priority level
     */
    public void sendRoleBasedNotification(UserRole role, String title, String message, int priority) {
        CompletableFuture.supplyAsync(() -> {
            try {
                return userDAO.findByRole(role);
            } catch (DatabaseException e) {
                logger.log(Level.SEVERE, "Failed to fetch users by role", e);
                return List.<User>of();
            }
        }, notificationExecutor).thenAccept(users -> {
            List<String> userIds = users.stream()
                .map(User::getUserId)
                .collect(Collectors.toList());
            sendBulkNotification(userIds, title, message, priority);
        });
    }
    
    /**
     * Schedules a delayed notification
     * @param userId Target user ID
     * @param title Notification title
     * @param message Notification message
     * @param priority Priority level
     * @param delay Delay in minutes
     */
    public void scheduleNotification(String userId, String title, String message, int priority, long delay) {
        scheduledExecutor.schedule(() -> {
            sendNotification(userId, title, message, priority);
        }, delay, TimeUnit.MINUTES);
        
        logger.log(Level.INFO, "Notification scheduled for user {0} in {1} minutes", new Object[]{userId, delay});
    }
    
    /**
     * Schedules recurring notifications
     * @param userId Target user ID
     * @param title Notification title
     * @param message Notification message
     * @param priority Priority level
     * @param initialDelay Initial delay in minutes
     * @param period Period between notifications in minutes
     * @return ScheduledFuture for cancellation
     */
    public ScheduledFuture<?> scheduleRecurringNotification(String userId, String title, String message, 
                                                          int priority, long initialDelay, long period) {
        return scheduledExecutor.scheduleAtFixedRate(() -> {
            sendNotification(userId, title, message, priority);
        }, initialDelay, period, TimeUnit.MINUTES);
    }
    
    /**
     * Processes a notification task
     * @param task Notification task to process
     */
    private void processNotification(NotificationTask task) {
        try {
            // Check rate limiting
            String userId = task.getUserId();
            LocalDateTime lastNotification = lastNotificationTime.get(userId);
            LocalDateTime now = LocalDateTime.now();
            
            if (lastNotification != null && lastNotification.plusMinutes(1).isAfter(now)) {
                logger.log(Level.INFO, "Rate limiting notification for user: {0}", userId);
                return;
            }
            
            // Simulate notification processing (in real app, this would send email, push notification, etc.)
            Thread.sleep(100); // Simulate processing time
            
            // Update last notification time
            lastNotificationTime.put(userId, now);
            
            logger.info(String.format("Notification sent to user %s: %s - %s (Priority: %d)", 
                       userId, task.getTitle(), task.getMessage(), task.getPriority()));
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to process notification", e);
        }
    }
    
    /**
     * Gets notification queue statistics
     * @return Map with queue statistics
     */
    public Map<String, Object> getQueueStatistics() {
        Map<String, Object> stats = new ConcurrentHashMap<>();
        stats.put("queueSize", notificationQueue.size());
        stats.put("isRunning", isRunning);
        stats.put("activeThreads", ((ThreadPoolExecutor) notificationExecutor).getActiveCount());
        stats.put("completedTasks", ((ThreadPoolExecutor) notificationExecutor).getCompletedTaskCount());
        return stats;
    }
    
    /**
     * Shuts down the notification service gracefully
     */
    public void shutdown() {
        isRunning = false;
        
        notificationExecutor.shutdown();
        scheduledExecutor.shutdown();
        
        try {
            if (!notificationExecutor.awaitTermination(30, TimeUnit.SECONDS)) {
                notificationExecutor.shutdownNow();
            }
            if (!scheduledExecutor.awaitTermination(30, TimeUnit.SECONDS)) {
                scheduledExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            notificationExecutor.shutdownNow();
            scheduledExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        logger.info("Notification service shut down");
    }
    
    /**
     * Inner class representing a notification task
     */
    private static class NotificationTask {
        private final String userId;
        private final String title;
        private final String message;
        private final int priority;
        private final LocalDateTime createdAt;
        
        public NotificationTask(String userId, String title, String message, int priority, LocalDateTime createdAt) {
            this.userId = userId;
            this.title = title;
            this.message = message;
            this.priority = priority;
            this.createdAt = createdAt;
        }
        
        public String getUserId() { return userId; }
        public String getTitle() { return title; }
        public String getMessage() { return message; }
        public int getPriority() { return priority; }
        public LocalDateTime getCreatedAt() { return createdAt; }
    }
}