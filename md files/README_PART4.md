## 9. Data Access Layer (DAO)

### DAO Responsibilities

The Data Access Object (DAO) layer provides abstraction between business logic and database operations:

1. **Database Abstraction**: Encapsulates all SQL operations
2. **Connection Management**: Handles JDBC connections and resource cleanup
3. **Transaction Management**: Manages transaction boundaries
4. **SQL Injection Prevention**: Uses PreparedStatements exclusively
5. **Error Handling**: Converts SQL exceptions to application exceptions
6. **Query Optimization**: Implements efficient queries with proper indexing

### DAO Architecture

```
Service Layer
    ↓
DAO Interface (DatabaseOperations<T>)
    ↓
BaseDAO<T> (Abstract base class)
    ├── UserDAO
    ├── ResourceDAO
    ├── RequestDAO
    ├── FeedbackDAO
    └── ActivityDAO
    ↓
JDBC Connection
    ↓
Database
```

### Transaction Management

**Transaction Boundaries**:
- Transactions begin when a service method starts
- All database operations within the method are part of one transaction
- Transaction commits on successful completion
- Transaction rolls back on exception

**Example: Request Creation Transaction**
```java
public void createRequest(Request request) throws DatabaseException {
    try {
        // Begin transaction (autoCommit = false)
        connection.setAutoCommit(false);
        
        // Operation 1: Insert request
        requestDAO.create(request);
        
        // Operation 2: Update resource quantity (if needed)
        resourceDAO.updateQuantity(request.getResourceId(), -1);
        
        // Commit transaction
        connection.commit();
        
    } catch (SQLException e) {
        // Rollback on error
        connection.rollback();
        throw new DatabaseException("Failed to create request", e);
    }
}
```

### PreparedStatement Usage

**Why PreparedStatements?**
- Prevents SQL injection attacks
- Separates SQL logic from data
- Improves query performance (query plan caching)
- Handles special characters automatically

**Example: Safe Query Execution**
```java
// SAFE: Using PreparedStatement
String sql = "SELECT * FROM users WHERE username = ? AND role = ?";
try (PreparedStatement stmt = connection.prepareStatement(sql)) {
    stmt.setString(1, username);  // Parameter binding
    stmt.setString(2, role);
    ResultSet rs = stmt.executeQuery();
    // Process results
}

// UNSAFE: String concatenation (DO NOT USE)
String sql = "SELECT * FROM users WHERE username = '" + username + "'";
// Vulnerable to SQL injection
```

### Connection Pooling Strategy

**Singleton Pattern**:
```java
public class DBConnection {
    private static DBConnection instance;
    private Connection connection;
    
    public static synchronized DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }
    
    public Connection getConnection() throws DatabaseException {
        if (connection == null || connection.isClosed()) {
            createConnection();
        }
        return connection;
    }
}
```

**Benefits**:
- Single connection reused across application
- Reduces connection overhead
- Ensures consistent transaction state
- Thread-safe implementation

### Error Handling Approach

**Exception Hierarchy**:
```
Exception
    ├── DatabaseException (checked)
    │   └── Wraps SQLException
    ├── AuthenticationException (checked)
    │   └── Login failures
    ├── InvalidInputException (checked)
    │   └── Validation failures
    └── CommunityHubException (base)
```

**Error Handling Pattern**:
```java
public User getUserById(String userId) throws DatabaseException {
    String sql = "SELECT * FROM users WHERE user_id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setString(1, userId);
        ResultSet rs = stmt.executeQuery();
        
        if (rs.next()) {
            return mapResultSetToEntity(rs);
        }
        return null;
        
    } catch (SQLException e) {
        logger.log(Level.SEVERE, "Database error retrieving user", e);
        throw new DatabaseException("Failed to retrieve user: " + userId, e);
    }
}
```

### DAO Implementation Examples

#### UserDAO
```java
public class UserDAO extends BaseDAO<User> {
    
    public User findByUsername(String username) throws DatabaseException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? mapResultSetToEntity(rs) : null;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find user by username", e);
        }
    }
    
    public List<User> findByRole(UserRole role) throws DatabaseException {
        String sql = "SELECT * FROM users WHERE role = ?";
        List<User> users = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, role.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                users.add(mapResultSetToEntity(rs));
            }
            return users;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find users by role", e);
        }
    }
}
```

#### RequestDAO
```java
public class RequestDAO extends BaseDAO<Request> {
    
    public List<Request> findByRequester(String requesterId) throws DatabaseException {
        String sql = "SELECT * FROM requests WHERE requester_id = ? ORDER BY created_at DESC";
        List<Request> requests = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, requesterId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                requests.add(mapResultSetToEntity(rs));
            }
            return requests;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find requests by requester", e);
        }
    }
    
    public List<Request> findByVolunteer(String volunteerId) throws DatabaseException {
        String sql = "SELECT * FROM requests WHERE volunteer_id = ? ORDER BY updated_at DESC";
        List<Request> requests = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, volunteerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                requests.add(mapResultSetToEntity(rs));
            }
            return requests;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find requests by volunteer", e);
        }
    }
}
```

---

## 10. Validation & Security

### Client-Side Validation

**HTML5 Validation Attributes**:
```html
<input type="text" name="username" required minlength="3" maxlength="20">
<input type="email" name="email" required>
<input type="password" name="password" required minlength="8">
<textarea name="description" required minlength="10" maxlength="500"></textarea>
```

**JavaScript Validation**:
```javascript
function validateLoginForm() {
    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value;
    
    if (!username || username.length < 3) {
        showError('Username must be at least 3 characters');
        return false;
    }
    
    if (!password || password.length < 8) {
        showError('Password must be at least 8 characters');
        return false;
    }
    
    return true;
}
```

**Benefits**:
- Immediate user feedback
- Reduces server load
- Improves user experience
- Prevents invalid submission

### Server-Side Validation

**Validation Utilities**:
```java
public class ValidationUtils {
    
    public static void validateRequired(String value, String fieldName) 
            throws InvalidInputException {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidInputException(fieldName + " is required");
        }
    }
    
    public static void validateStringLength(String value, String fieldName, 
                                           int minLength, int maxLength) 
            throws InvalidInputException {
        if (value.length() < minLength || value.length() > maxLength) {
            throw new InvalidInputException(
                fieldName + " must be between " + minLength + " and " + maxLength + " characters"
            );
        }
    }
    
    public static void validateEmail(String email) throws InvalidInputException {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        if (!email.matches(emailRegex)) {
            throw new InvalidInputException("Invalid email format");
        }
    }
    
    public static String sanitizeInput(String input) {
        if (input == null) return null;
        return input.replaceAll("[<>\"'%;()&+]", "");
    }
}
```

**Validation in Servlets**:
```java
@Override
protected void doPost(HttpServletRequest request, HttpServletResponse response) {
    try {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        // Server-side validation
        ValidationUtils.validateRequired(username, "username");
        ValidationUtils.validateRequired(password, "password");
        ValidationUtils.validateStringLength(username, "username", 3, 20);
        ValidationUtils.validateStringLength(password, "password", 8, 50);
        
        // Sanitize input
        username = ValidationUtils.sanitizeInput(username);
        
        // Proceed with authentication
        User user = authService.login(username, password);
        
    } catch (InvalidInputException e) {
        request.setAttribute("error", e.getUserMessage());
        request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
    }
}
```

### SQL Injection Prevention

**Threat**: Attacker injects SQL code through input fields

**Example Attack**:
```
Username: admin' OR '1'='1
Password: anything
Resulting SQL: SELECT * FROM users WHERE username = 'admin' OR '1'='1'
Result: Bypasses authentication
```

**Prevention: PreparedStatements**:
```java
// VULNERABLE CODE (DO NOT USE)
String sql = "SELECT * FROM users WHERE username = '" + username + "'";
Statement stmt = connection.createStatement();
ResultSet rs = stmt.executeQuery(sql);

// SAFE CODE (ALWAYS USE)
String sql = "SELECT * FROM users WHERE username = ?";
PreparedStatement stmt = connection.prepareStatement(sql);
stmt.setString(1, username);  // Parameter binding
ResultSet rs = stmt.executeQuery();
```

**Why PreparedStatements Work**:
- SQL structure defined separately from data
- Data treated as literal values, not code
- Special characters automatically escaped
- Query plan cached for performance

### XSS (Cross-Site Scripting) Prevention

**Threat**: Attacker injects JavaScript through input fields

**Example Attack**:
```
Description: <script>alert('XSS')</script>
Result: JavaScript executed in user's browser
```

**Prevention Strategy**:

1. **Input Sanitization**:
```java
String sanitized = ValidationUtils.sanitizeInput(userInput);
// Removes: < > " ' % ; ( ) & +
```

2. **Output Escaping in JSP**:
```jsp
<!-- VULNERABLE (DO NOT USE) -->
<%= request.getParameter("description") %>

<!-- SAFE (ALWAYS USE) -->
<c:out value="${request.description}" />
<!-- JSTL automatically escapes HTML -->
```

3. **Content Security Policy** (recommended for production):
```
Content-Security-Policy: script-src 'self'; style-src 'self' 'unsafe-inline'
```

### Session Security

**Session Hijacking Prevention**:

1. **Session ID Regeneration on Login**:
```java
HttpSession oldSession = request.getSession(false);
if (oldSession != null) {
    oldSession.invalidate();
}
HttpSession newSession = request.getSession(true);
newSession.setAttribute("user", user);
```

2. **Session Timeout**:
```xml
<!-- web.xml -->
<session-config>
    <session-timeout>30</session-timeout>
</session-config>
```

3. **Secure Cookie Flags** (production):
```
Set-Cookie: JSESSIONID=...; HttpOnly; Secure; SameSite=Strict
```

### Role Enforcement

**Backend Verification on Every Request**:
```java
@Override
protected void doPost(HttpServletRequest request, HttpServletResponse response) {
    HttpSession session = request.getSession(false);
    if (session == null || session.getAttribute("user") == null) {
        response.sendRedirect("/login");
        return;
    }
    
    User currentUser = (User) session.getAttribute("user");
    
    // Verify role
    if (!currentUser.isAdmin()) {
        response.sendError(HttpServletResponse.SC_FORBIDDEN, 
            "Only administrators can perform this action");
        return;
    }
    
    // Proceed with admin operation
}
```

**Why Backend Enforcement?**
- UI hiding can be bypassed by direct URL access
- Client-side checks can be disabled
- Backend is the only trusted authority
- Defense in depth principle

---

## 11. Concurrency & Thread Safety

### Why Concurrency Matters

In a web application, multiple users access the system simultaneously:
- Multiple login attempts at same time
- Concurrent request updates
- Simultaneous resource allocation
- Overlapping session management

Without proper synchronization, race conditions can occur:
- Account lockout counter incremented incorrectly
- Duplicate resource allocation
- Session data corruption
- Lost updates

### Synchronized Sections

**AuthenticationService.login()**:
```java
public synchronized User login(String username, String password) 
        throws AuthenticationException {
    // Synchronized to prevent race conditions during concurrent logins
    
    User user = userDAO.findByUsername(username);
    if (user == null) {
        incrementFailedAttempts(username);
        throw new AuthenticationException("Invalid username or password");
    }
    
    // Check account lockout
    if (isAccountLocked(username)) {
        throw new AuthenticationException("Account locked. Try again in 15 minutes");
    }
    
    // Verify password
    if (!PasswordUtils.verifyPassword(password, user.getPasswordHash())) {
        incrementFailedAttempts(username);
        throw new AuthenticationException("Invalid username or password");
    }
    
    // Reset failed attempts on successful login
    resetFailedAttempts(username);
    return user;
}
```

**Why Synchronized?**
- Prevents multiple threads from executing simultaneously
- Ensures account lockout counter is accurate
- Prevents double-counting of failed attempts
- Guarantees atomic operation

### Thread-Safe Collections

**ConcurrentHashMap Usage**:
```java
public class AuthenticationService {
    // Thread-safe collections for concurrent access
    private static final ConcurrentHashMap<String, Integer> loginAttempts = 
        new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Long> lastLoginAttempt = 
        new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Long> lockoutTime = 
        new ConcurrentHashMap<>();
    
    private void incrementFailedAttempts(String username) {
        loginAttempts.merge(username, 1, Integer::sum);
        lastLoginAttempt.put(username, System.currentTimeMillis());
    }
    
    private boolean isAccountLocked(String username) {
        Long lockTime = lockoutTime.get(username);
        if (lockTime == null) return false;
        
        long elapsedTime = System.currentTimeMillis() - lockTime;
        if (elapsedTime > 15 * 60 * 1000) {  // 15 minutes
            lockoutTime.remove(username);
            loginAttempts.remove(username);
            return false;
        }
        return true;
    }
}
```

**Why ConcurrentHashMap?**
- Allows concurrent reads without locking
- Locks only affected segments during writes
- Better performance than synchronized HashMap
- No deadlock risk

### Transaction Atomicity

**Database Transactions Ensure Consistency**:
```java
public void assignVolunteerToRequest(String requestId, String volunteerId) 
        throws DatabaseException {
    try {
        connection.setAutoCommit(false);
        
        // Step 1: Load request
        Request request = requestDAO.read(requestId);
        
        // Step 2: Verify volunteer exists
        User volunteer = userDAO.read(volunteerId);
        if (volunteer == null) {
            throw new DatabaseException("Volunteer not found");
        }
        
        // Step 3: Update request
        request.setVolunteerId(volunteerId);
        request.setStatus(RequestStatus.ASSIGNED);
        requestDAO.update(request);
        
        // Step 4: Commit (all or nothing)
        connection.commit();
        
    } catch (Exception e) {
        connection.rollback();  // Undo all changes
        throw new DatabaseException("Failed to assign volunteer", e);
    }
}
```

**ACID Properties**:
- **Atomicity**: All steps complete or none do
- **Consistency**: Database remains in valid state
- **Isolation**: Concurrent transactions don't interfere
- **Durability**: Committed data persists

