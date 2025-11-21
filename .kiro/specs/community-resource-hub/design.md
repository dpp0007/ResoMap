# Design Document

## Overview

The Community Resource Hub is a JavaFX-based desktop application following MVC architecture with DAO pattern implementation. The system uses MySQL/SQLite for data persistence, implements comprehensive OOP principles, and provides a modern GUI experience. The application supports three user roles (Admin, Volunteer, User) with role-based access control and real-time data synchronization through multithreading.

## Architecture

### High-Level Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Presentation  │    │    Business     │    │   Data Access   │
│     Layer       │◄──►│     Layer       │◄──►│     Layer       │
│   (JavaFX UI)   │    │   (Services)    │    │   (DAO/JDBC)    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Controllers   │    │   Model Classes │    │    Database     │
│   Event Handlers│    │   Business Logic│    │  (MySQL/SQLite) │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### Package Structure

```
com.communityhub/
├── model/
│   ├── User.java (abstract base)
│   ├── Admin.java
│   ├── Volunteer.java
│   ├── Requester.java
│   ├── Resource.java
│   ├── Request.java
│   └── Feedback.java
├── dao/
│   ├── DatabaseOperations.java (interface)
│   ├── UserDAO.java
│   ├── ResourceDAO.java
│   ├── RequestDAO.java
│   └── FeedbackDAO.java
├── service/
│   ├── AuthenticationService.java
│   ├── ResourceService.java
│   ├── RequestService.java
│   └── NotificationService.java
├── ui/
│   ├── controllers/
│   ├── views/ (FXML files)
│   └── styles/ (CSS files)
├── util/
│   ├── DBConnection.java
│   ├── SessionManager.java
│   └── ValidationUtils.java
└── exception/
    ├── InvalidInputException.java
    ├── DatabaseException.java
    └── AuthenticationException.java
```

## Components and Interfaces

### Core Model Hierarchy

```java
// Abstract base class demonstrating inheritance and polymorphism
public abstract class User {
    protected String userId;
    protected String username;
    protected String email;
    protected String password;
    protected UserRole role;
    
    // Abstract method for polymorphism
    public abstract void performRoleSpecificAction();
    public abstract String getDashboardView();
}

// Concrete implementations
public class Admin extends User {
    private Set<String> permissions;
    
    @Override
    public void performRoleSpecificAction() {
        // Admin-specific resource management
    }
}

public class Volunteer extends User {
    private List<Request> assignedRequests;
    
    @Override
    public void performRoleSpecificAction() {
        // Volunteer-specific request handling
    }
}

public class Requester extends User {
    private List<Request> submittedRequests;
    
    @Override
    public void performRoleSpecificAction() {
        // User-specific resource requests
    }
}
```

### DAO Interface Pattern

```java
public interface DatabaseOperations<T> {
    void create(T entity) throws DatabaseException;
    T read(String id) throws DatabaseException;
    void update(T entity) throws DatabaseException;
    void delete(String id) throws DatabaseException;
    List<T> findAll() throws DatabaseException;
}

// Generic DAO implementation
public abstract class BaseDAO<T> implements DatabaseOperations<T> {
    protected Connection connection;
    
    protected BaseDAO() {
        this.connection = DBConnection.getInstance().getConnection();
    }
    
    // Common database operations with transaction support
    protected void executeInTransaction(Runnable operation) throws DatabaseException {
        try {
            connection.setAutoCommit(false);
            operation.run();
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                throw new DatabaseException("Transaction rollback failed", rollbackEx);
            }
            throw new DatabaseException("Transaction failed", e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                // Log warning
            }
        }
    }
}
```

### Service Layer Architecture

```java
public class ResourceService {
    private ResourceDAO resourceDAO;
    private Map<String, Resource> resourceCache;
    private Set<String> resourceCategories;
    
    // Thread-safe operations with synchronization
    public synchronized void addResource(Resource resource) throws DatabaseException {
        validateResource(resource);
        resourceDAO.create(resource);
        resourceCache.put(resource.getId(), resource);
        resourceCategories.add(resource.getCategory());
    }
    
    // Lambda expressions and streams for filtering
    public List<Resource> searchResources(String query) {
        return resourceCache.values().stream()
            .filter(resource -> resource.getName().toLowerCase().contains(query.toLowerCase()))
            .sorted((r1, r2) -> r1.getName().compareTo(r2.getName()))
            .collect(Collectors.toList());
    }
}
```

## Data Models

### Database Schema

```sql
-- Users table with role-based inheritance
CREATE TABLE users (
    user_id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'VOLUNTEER', 'REQUESTER') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Resources table
CREATE TABLE resources (
    resource_id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    category VARCHAR(50) NOT NULL,
    quantity INT DEFAULT 0,
    location VARCHAR(200),
    contact_info VARCHAR(100),
    created_by VARCHAR(36),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(user_id)
);

-- Requests table
CREATE TABLE requests (
    request_id VARCHAR(36) PRIMARY KEY,
    requester_id VARCHAR(36) NOT NULL,
    resource_id VARCHAR(36) NOT NULL,
    volunteer_id VARCHAR(36),
    status ENUM('PENDING', 'ASSIGNED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED') DEFAULT 'PENDING',
    description TEXT,
    urgency_level ENUM('LOW', 'MEDIUM', 'HIGH', 'CRITICAL') DEFAULT 'MEDIUM',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (requester_id) REFERENCES users(user_id),
    FOREIGN KEY (resource_id) REFERENCES resources(resource_id),
    FOREIGN KEY (volunteer_id) REFERENCES users(user_id)
);

-- Feedback table
CREATE TABLE feedback (
    feedback_id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    request_id VARCHAR(36),
    rating INT CHECK (rating >= 1 AND rating <= 5),
    comments TEXT,
    feedback_type ENUM('GENERAL', 'REQUEST_SPECIFIC', 'SYSTEM_IMPROVEMENT') DEFAULT 'GENERAL',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (request_id) REFERENCES requests(request_id)
);
```

### Collections Usage Strategy

```java
public class DataManager {
    // List for ordered resource collections
    private List<Resource> availableResources = new ArrayList<>();
    
    // Map for fast user lookup and session management
    private Map<String, User> activeUsers = new ConcurrentHashMap<>();
    
    // Set for unique categories (prevents duplicates)
    private Set<String> resourceCategories = new HashSet<>();
    
    // Thread-safe collections for concurrent access
    private final Object resourceLock = new Object();
    
    public void updateResourceList(List<Resource> newResources) {
        synchronized(resourceLock) {
            availableResources.clear();
            availableResources.addAll(newResources);
            
            // Update categories using streams
            resourceCategories = newResources.stream()
                .map(Resource::getCategory)
                .collect(Collectors.toSet());
        }
    }
}
```

## Error Handling

### Custom Exception Hierarchy

```java
public class CommunityHubException extends Exception {
    protected String errorCode;
    protected String userMessage;
    
    public CommunityHubException(String message, String errorCode, String userMessage) {
        super(message);
        this.errorCode = errorCode;
        this.userMessage = userMessage;
    }
}

public class InvalidInputException extends CommunityHubException {
    public InvalidInputException(String field, String value) {
        super("Invalid input for field: " + field, "INVALID_INPUT", 
              "Please check your " + field + " and try again.");
    }
}

public class DatabaseException extends CommunityHubException {
    public DatabaseException(String message, Throwable cause) {
        super(message, "DATABASE_ERROR", "A system error occurred. Please try again later.");
        initCause(cause);
    }
}

public class AuthenticationException extends CommunityHubException {
    public AuthenticationException(String message) {
        super(message, "AUTH_FAILED", "Invalid credentials. Please check your username and password.");
    }
}
```

### Exception Handling Strategy

```java
public class ExceptionHandler {
    private static final Logger logger = Logger.getLogger(ExceptionHandler.class.getName());
    
    public static void handleException(Exception e, Stage parentStage) {
        logger.log(Level.SEVERE, "Exception occurred", e);
        
        String userMessage = "An unexpected error occurred.";
        if (e instanceof CommunityHubException) {
            userMessage = ((CommunityHubException) e).getUserMessage();
        }
        
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(userMessage);
            alert.initOwner(parentStage);
            alert.showAndWait();
        });
    }
}
```

## Testing Strategy

### Unit Testing Approach

```java
// Example test structure for DAO layer
@Test
public void testResourceDAO_CreateResource_Success() {
    // Given
    Resource resource = new Resource("Test Resource", "Food", "Test Description");
    
    // When
    assertDoesNotThrow(() -> resourceDAO.create(resource));
    
    // Then
    Resource retrieved = resourceDAO.read(resource.getId());
    assertEquals(resource.getName(), retrieved.getName());
}

// Integration testing for service layer
@Test
public void testResourceService_SearchWithFilters_ReturnsFilteredResults() {
    // Given
    List<Resource> testResources = createTestResources();
    resourceService.addResources(testResources);
    
    // When
    List<Resource> results = resourceService.searchResources("food");
    
    // Then
    assertTrue(results.stream().allMatch(r -> 
        r.getName().toLowerCase().contains("food") || 
        r.getCategory().toLowerCase().contains("food")));
}
```

### Multithreading Testing

```java
@Test
public void testConcurrentResourceUpdates_MaintainsDataIntegrity() {
    // Test concurrent access to shared resources
    ExecutorService executor = Executors.newFixedThreadPool(10);
    CountDownLatch latch = new CountDownLatch(10);
    
    for (int i = 0; i < 10; i++) {
        executor.submit(() -> {
            try {
                resourceService.addResource(createTestResource());
            } finally {
                latch.countDown();
            }
        });
    }
    
    assertDoesNotThrow(() -> latch.await(5, TimeUnit.SECONDS));
    assertEquals(10, resourceService.getAllResources().size());
}
```

## Multithreading Design

### Background Data Loading

```java
public class DataLoadingService {
    private final ExecutorService backgroundExecutor = Executors.newCachedThreadPool();
    private final ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(2);
    
    public void loadResourcesAsync(Consumer<List<Resource>> callback) {
        backgroundExecutor.submit(() -> {
            try {
                List<Resource> resources = resourceDAO.findAll();
                Platform.runLater(() -> callback.accept(resources));
            } catch (DatabaseException e) {
                Platform.runLater(() -> ExceptionHandler.handleException(e, null));
            }
        });
    }
    
    // Auto-refresh mechanism
    public void startAutoRefresh() {
        scheduledExecutor.scheduleAtFixedRate(() -> {
            loadResourcesAsync(this::updateResourceCache);
        }, 0, 30, TimeUnit.SECONDS);
    }
    
    private synchronized void updateResourceCache(List<Resource> resources) {
        // Thread-safe cache update
        resourceCache.clear();
        resources.forEach(r -> resourceCache.put(r.getId(), r));
    }
}
```

### Progress Indication

```java
public class ProgressAwareTask<T> extends Task<T> {
    private final Supplier<T> operation;
    private final String taskName;
    
    public ProgressAwareTask(String taskName, Supplier<T> operation) {
        this.taskName = taskName;
        this.operation = operation;
    }
    
    @Override
    protected T call() throws Exception {
        updateTitle(taskName);
        updateProgress(0, 100);
        
        // Simulate progress updates
        for (int i = 0; i <= 100; i += 10) {
            if (isCancelled()) break;
            updateProgress(i, 100);
            Thread.sleep(100);
        }
        
        return operation.get();
    }
}
```

## GUI Design Architecture

### JavaFX Controller Pattern

```java
public abstract class BaseController {
    protected Stage stage;
    protected Scene scene;
    
    public void initialize() {
        setupEventHandlers();
        loadInitialData();
    }
    
    protected abstract void setupEventHandlers();
    protected abstract void loadInitialData();
    
    protected void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(stage);
        alert.showAndWait();
    }
}

public class AdminDashboardController extends BaseController {
    @FXML private TableView<Resource> resourceTable;
    @FXML private TextField searchField;
    @FXML private ProgressIndicator loadingIndicator;
    
    private ResourceService resourceService;
    private ObservableList<Resource> resourceList;
    
    @Override
    protected void setupEventHandlers() {
        // Search functionality with streams
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filterResources(newVal);
        });
    }
    
    private void filterResources(String query) {
        if (query == null || query.isEmpty()) {
            resourceTable.setItems(resourceList);
        } else {
            List<Resource> filtered = resourceList.stream()
                .filter(r -> r.getName().toLowerCase().contains(query.toLowerCase()) ||
                           r.getCategory().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
            resourceTable.setItems(FXCollections.observableArrayList(filtered));
        }
    }
}
```

### CSS Styling Strategy

```css
/* Modern flat design with soft shadows */
.root {
    -fx-font-family: "Segoe UI", Arial, sans-serif;
    -fx-background-color: #f5f5f5;
}

.main-container {
    -fx-background-color: white;
    -fx-background-radius: 8px;
    -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);
    -fx-padding: 20px;
}

.primary-button {
    -fx-background-color: #2196F3;
    -fx-text-fill: white;
    -fx-background-radius: 4px;
    -fx-padding: 10px 20px;
    -fx-font-weight: bold;
}

.primary-button:hover {
    -fx-background-color: #1976D2;
}

.table-view {
    -fx-background-color: white;
    -fx-border-color: #e0e0e0;
    -fx-border-radius: 4px;
}

.sidebar {
    -fx-background-color: #263238;
    -fx-padding: 20px;
}

.sidebar-button {
    -fx-background-color: transparent;
    -fx-text-fill: white;
    -fx-padding: 15px;
    -fx-alignment: center-left;
}

.sidebar-button:hover {
    -fx-background-color: rgba(255,255,255,0.1);
}
```

This design provides a comprehensive foundation for implementing all the rubric requirements while maintaining clean architecture, proper separation of concerns, and modern development practices. The multithreading approach ensures responsive UI, the OOP design demonstrates all required principles, and the database layer provides robust data management with transaction support.