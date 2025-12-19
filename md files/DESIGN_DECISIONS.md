# Design Decisions & Architecture Rationale

**Project**: Community Resource Hub (ResoMap)  
**Purpose**: Document key architectural decisions and their justifications

---

## 1. LAYERED ARCHITECTURE

### Decision
Implement a 4-layer architecture: Presentation → Servlet → Service → DAO → Database

### Rationale
- **Separation of Concerns**: Each layer has a single responsibility
- **Testability**: Layers can be tested independently
- **Maintainability**: Changes in one layer don't affect others
- **Scalability**: Easy to add new features without rewriting existing code
- **Reusability**: Services can be used by multiple servlets

### Implementation
```
LoginServlet (Presentation)
    ↓
AuthenticationService (Business Logic)
    ↓
UserDAO (Data Access)
    ↓
Database (Persistence)
```

### Trade-offs
- **Pro**: Clean separation, easy to maintain
- **Con**: More classes to manage, slight performance overhead
- **Decision**: Pro outweighs con for long-term maintainability

---

## 2. SYNCHRONIZED LOGIN METHOD

### Decision
Make `AuthenticationService.login()` synchronized

### Rationale
- **Concurrent Access**: Multiple users may login simultaneously
- **Race Condition Prevention**: Account lockout mechanism must be atomic
- **Data Consistency**: Login attempts counter must be accurate
- **Security**: Prevents bypass of lockout protection

### Implementation
```java
public synchronized User login(String username, String password) {
    // Check if account is locked
    if (isAccountLocked(username)) {
        throw AccountLockedException();
    }
    // ... rest of login logic
}
```

### Trade-offs
- **Pro**: Prevents race conditions, ensures security
- **Con**: Slight performance impact (serialized logins)
- **Decision**: Security is more important than performance for login

---

## 3. CONCURRENT COLLECTIONS

### Decision
Use `ConcurrentHashMap` for thread-safe collections instead of synchronized blocks

### Rationale
- **Performance**: Allows concurrent reads without locking
- **Scalability**: Multiple threads can access different buckets simultaneously
- **Simplicity**: No need for explicit synchronization
- **Correctness**: Prevents data corruption under concurrent access

### Implementation
```java
private final Map<String, User> activeUsers = new ConcurrentHashMap<>();
private final Map<String, Integer> loginAttempts = new ConcurrentHashMap<>();
```

### Trade-offs
- **Pro**: Better performance, cleaner code
- **Con**: Slightly more memory overhead
- **Decision**: Pro outweighs con for concurrent scenarios

---

## 4. DUAL-LAYER VALIDATION

### Decision
Implement both client-side and server-side validation

### Rationale
- **Client-Side**: Immediate feedback, better UX, reduces server load
- **Server-Side**: Security requirement, prevents malicious input
- **Defense in Depth**: Multiple layers of protection

### Implementation
```
User Input → JavaScript Validation → Server Validation → Sanitization → Database
```

### Trade-offs
- **Pro**: Better UX, improved security
- **Con**: Validation logic duplicated
- **Decision**: Pro outweighs con; duplication is acceptable for security

---

## 5. PREPARED STATEMENTS FOR SQL INJECTION PREVENTION

### Decision
Use `PreparedStatement` for all database queries

### Rationale
- **Security**: Prevents SQL injection attacks
- **Performance**: Query plans cached by database
- **Correctness**: Parameter binding prevents type mismatches
- **Maintainability**: Cleaner code than string concatenation

### Implementation
```java
String sql = "SELECT * FROM users WHERE username = ?";
PreparedStatement stmt = connection.prepareStatement(sql);
stmt.setString(1, username);  // Parameter binding
```

### Trade-offs
- **Pro**: Secure, performant, clean
- **Con**: Slightly more verbose than string concatenation
- **Decision**: Pro outweighs con; security is non-negotiable

---

## 6. TRANSACTION MANAGEMENT

### Decision
Wrap multi-step operations in database transactions

### Rationale
- **Atomicity**: All-or-nothing semantics
- **Consistency**: Database remains in valid state
- **Isolation**: Concurrent operations don't interfere
- **Durability**: Committed data survives failures

### Implementation
```java
protected void executeInTransaction(DatabaseOperation operation) {
    connection.setAutoCommit(false);
    try {
        operation.execute();
        connection.commit();
    } catch (Exception e) {
        connection.rollback();
        throw new DatabaseException(...);
    }
}
```

### Trade-offs
- **Pro**: Data consistency, error recovery
- **Con**: Slight performance overhead
- **Decision**: Pro outweighs con; data integrity is critical

---

## 7. REQUEST CORRELATION IDs

### Decision
Add correlation IDs to track requests through multiple layers

### Rationale
- **Debugging**: Trace a single request through logs
- **Monitoring**: Identify performance bottlenecks
- **Troubleshooting**: Correlate errors across layers
- **Concurrency**: Distinguish between concurrent requests

### Implementation
```java
RequestContext.initialize();  // Generate unique ID
RequestContext.logInfo("User logged in");  // Logs include ID
RequestContext.clear();  // Cleanup
```

### Trade-offs
- **Pro**: Better debugging, improved monitoring
- **Con**: Additional code, ThreadLocal management
- **Decision**: Pro outweighs con; debugging value is high

---

## 8. INPUT SANITIZATION

### Decision
Sanitize all user input before display

### Rationale
- **XSS Prevention**: Escape HTML characters
- **Consistency**: Centralized sanitization
- **Maintainability**: Single point of change
- **Security**: Defense in depth

### Implementation
```java
public static String sanitizeInput(String input) {
    return input.replaceAll("&", "&amp;")
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("\"", "&quot;")
                .replaceAll("'", "&#x27;");
}
```

### Trade-offs
- **Pro**: Prevents XSS, centralized
- **Con**: Slight performance overhead
- **Decision**: Pro outweighs con; security is critical

---

## 9. CENTRALIZED VALIDATION MESSAGES

### Decision
Store validation error messages as constants

### Rationale
- **Consistency**: Same message across all validations
- **Maintainability**: Single point to update messages
- **Internationalization**: Easy to translate messages
- **Testability**: Messages can be tested independently

### Implementation
```java
private static final String MSG_REQUIRED = "%s is required";
private static final String MSG_INVALID_EMAIL = "Please enter a valid email address";
```

### Trade-offs
- **Pro**: Consistent, maintainable, testable
- **Con**: More constants to manage
- **Decision**: Pro outweighs con; consistency is important

---

## 10. AJAX SEARCH WITH DEBOUNCING

### Decision
Implement AJAX search with 300ms debouncing

### Rationale
- **Performance**: Reduces server load
- **UX**: Prevents excessive requests while typing
- **Responsiveness**: Still feels real-time to users
- **Bandwidth**: Reduces network traffic

### Implementation
```javascript
let searchTimeout;
searchInput.addEventListener('input', function() {
    clearTimeout(searchTimeout);
    searchTimeout = setTimeout(function() {
        performAjaxSearch(query);
    }, 300);
});
```

### Trade-offs
- **Pro**: Better performance, reduced server load
- **Con**: Slight delay in search results
- **Decision**: Pro outweighs con; 300ms is imperceptible to users

---

## 11. ROLE-BASED ACCESS CONTROL

### Decision
Implement role-based access control (RBAC) with three roles: Admin, Volunteer, Requester

### Rationale
- **Security**: Different users have different permissions
- **Simplicity**: Three roles cover all use cases
- **Extensibility**: Easy to add new roles
- **Maintainability**: Clear permission model

### Implementation
```java
public abstract boolean canPerformAction(String action);

// Admin can perform all actions
// Volunteer can accept requests, update status
// Requester can create requests, view status
```

### Trade-offs
- **Pro**: Secure, simple, extensible
- **Con**: Requires careful permission management
- **Decision**: Pro outweighs con; security is critical

---

## 12. RESPONSIVE DESIGN

### Decision
Implement responsive CSS with mobile-first approach

### Rationale
- **Accessibility**: Works on all devices
- **User Experience**: Optimized for mobile and desktop
- **Maintainability**: Single codebase for all devices
- **Performance**: No separate mobile app needed

### Implementation
```css
@media (max-width: 768px) {
    .dashboard-nav ul {
        flex-direction: column;
    }
    .stats-grid {
        grid-template-columns: 1fr;
    }
}
```

### Trade-offs
- **Pro**: Works everywhere, single codebase
- **Con**: More CSS to manage
- **Decision**: Pro outweighs con; mobile support is essential

---

## SUMMARY OF KEY PRINCIPLES

1. **Security First**: All decisions prioritize security
2. **Maintainability**: Code should be easy to understand and modify
3. **Performance**: Optimize where it matters (database, network)
4. **Simplicity**: Avoid over-engineering
5. **Consistency**: Uniform patterns across codebase
6. **Testability**: Code should be easy to test
7. **Scalability**: Architecture supports growth

---

## FUTURE CONSIDERATIONS

1. **Distributed Sessions**: For multi-server deployment
2. **Caching Layer**: Redis for performance
3. **Message Queue**: For asynchronous operations
4. **Microservices**: If system grows significantly
5. **API Gateway**: For external integrations

