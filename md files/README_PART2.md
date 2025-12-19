## 4. System Architecture

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                        │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  JSP Pages (login.jsp, dashboard.jsp, etc.)         │   │
│  │  + HTML5 Forms + CSS3 Styling + JavaScript          │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                           ↕ HTTP
┌─────────────────────────────────────────────────────────────┐
│                    Servlet Layer                             │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  LoginServlet, DashboardServlet, ResourceServlet,   │   │
│  │  RequestServlet, FeedbackServlet, SearchServlet     │   │
│  │  + AuthFilter + SessionListener                     │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                           ↕
┌─────────────────────────────────────────────────────────────┐
│                  Business Logic Layer                        │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  AuthenticationService, ResourceService,            │   │
│  │  RequestService, ActivityService, FeedbackService   │   │
│  │  + Business rule enforcement + Transaction mgmt     │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                           ↕
┌─────────────────────────────────────────────────────────────┐
│                  Data Access Layer                           │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  BaseDAO, UserDAO, ResourceDAO, RequestDAO,         │   │
│  │  FeedbackDAO, ActivityDAO                           │   │
│  │  + PreparedStatements + Connection pooling          │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                           ↕ JDBC
┌─────────────────────────────────────────────────────────────┐
│                    Database Layer                            │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  SQLite (Development) / MySQL (Production)          │   │
│  │  Tables: users, resources, requests, feedback       │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

### Layered Architecture Design

#### Presentation Layer (JSP + Servlets)
**Responsibility**: HTTP request/response handling, form rendering, user interaction

- Receives HTTP requests from client browsers
- Validates form submissions
- Delegates business logic to services
- Renders JSP templates with data
- Manages redirects and forwards
- Handles session attributes

**Key Components**:
- `login.jsp`, `dashboard.jsp`, `resources.jsp`, `requests.jsp`
- `LoginServlet`, `DashboardServlet`, `ResourceServlet`, `RequestServlet`
- `AuthFilter` (authentication enforcement)
- `SessionListener` (session lifecycle management)

#### Business Logic Layer (Services)
**Responsibility**: Core business rules, transaction management, data validation

- Enforces business rules (e.g., account lockout after 5 failed attempts)
- Manages transactions for multi-step operations
- Validates input data before database operations
- Implements role-based access control
- Coordinates between multiple DAOs
- Handles error recovery and logging

**Key Components**:
- `AuthenticationService`: User authentication, password hashing, account lockout
- `ResourceService`: Resource CRUD, search, category filtering
- `RequestService`: Request lifecycle, volunteer assignment, status updates
- `ActivityService`: Activity feed generation, role-based filtering
- `FeedbackService`: Feedback submission and retrieval

#### Data Access Layer (DAOs)
**Responsibility**: Database abstraction, SQL execution, connection management

- Encapsulates all database operations
- Uses PreparedStatements exclusively (SQL injection prevention)
- Manages database connections and resources
- Implements transaction boundaries
- Handles database-specific operations
- Provides consistent error handling

**Key Components**:
- `BaseDAO`: Abstract base class with common CRUD operations
- `UserDAO`: User account management
- `ResourceDAO`: Resource inventory management
- `RequestDAO`: Request lifecycle management
- `FeedbackDAO`: Feedback storage and retrieval
- `ActivityDAO`: Activity feed queries

#### Database Layer
**Responsibility**: Persistent data storage, referential integrity, performance optimization

- Stores all application data
- Enforces foreign key constraints
- Maintains data consistency through transactions
- Provides indexes for query optimization
- Supports both SQLite (development) and MySQL (production)

### Architecture Rationale

**Why Layered Design?**

1. **Separation of Concerns**: Each layer has a single, well-defined responsibility
2. **Testability**: Layers can be tested independently with mocking
3. **Maintainability**: Changes in one layer don't cascade to others
4. **Reusability**: Services can be used by multiple servlets
5. **Scalability**: Easy to add new features without affecting existing code
6. **Security**: Centralized validation and authorization enforcement

**Why This Specific Structure?**

- **Servlet Layer**: Standard Java web framework, widely understood, no external dependencies
- **Service Layer**: Centralizes business logic, enables transaction management, facilitates testing
- **DAO Layer**: Abstracts database operations, enables database switching, prevents SQL injection
- **Database Layer**: Relational model ensures data consistency, ACID properties guarantee reliability

---

## 5. Role-Based Access Control (RBAC)

### Admin Role

**Capabilities**:
- Create, read, update, delete resources
- View all users in the system
- View all requests (regardless of owner)
- Manually assign volunteers to requests
- Update request status on behalf of volunteers
- View system-wide statistics and analytics
- Access admin panel for system configuration

**Access Enforcement**:
```java
if (!currentUser.isAdmin()) {
    response.sendError(HttpServletResponse.SC_FORBIDDEN);
    return;
}
```

**Protected Endpoints**:
- `/admin` - Admin panel
- `/resources?action=create` - Resource creation
- `/resources?action=edit` - Resource editing
- `/resources?action=delete` - Resource deletion
- `/requests?action=assign` - Volunteer assignment

### Volunteer Role

**Capabilities**:
- View requests assigned to them
- Update request status (Assigned → In Progress → Completed)
- Submit completion notes
- View personal performance metrics
- View recent activity related to their assignments
- Cannot create or modify resources
- Cannot access admin functions

**Access Enforcement**:
```java
if (!currentUser.getRole().equals(UserRole.VOLUNTEER)) {
    response.sendError(HttpServletResponse.SC_FORBIDDEN);
    return;
}
// Additional check: verify volunteer_id matches current user
if (!request.getVolunteerId().equals(currentUser.getUserId())) {
    response.sendError(HttpServletResponse.SC_FORBIDDEN);
    return;
}
```

**Protected Endpoints**:
- `/requests` - View assigned requests
- `/requests?action=update-status` - Update request status
- `/requests?action=add-note` - Add progress notes

### Requester Role

**Capabilities**:
- Create new resource requests
- View their own requests
- View request status and updates
- Submit feedback on completed requests
- View recent activity related to their requests
- Cannot modify other users' requests
- Cannot assign volunteers
- Cannot access admin functions

**Access Enforcement**:
```java
if (!currentUser.getRole().equals(UserRole.REQUESTER)) {
    response.sendError(HttpServletResponse.SC_FORBIDDEN);
    return;
}
// Additional check: verify requester_id matches current user
if (!request.getRequesterId().equals(currentUser.getUserId())) {
    response.sendError(HttpServletResponse.SC_FORBIDDEN);
    return;
}
```

**Protected Endpoints**:
- `/requests?action=create` - Create new request
- `/requests` - View own requests
- `/feedback?action=submit` - Submit feedback

### Access Enforcement Strategy

**Dual-Layer Enforcement**:

1. **UI Layer**: Role-based conditional rendering in JSP
   ```jsp
   <c:if test="${sessionScope.user.role == 'ADMIN'}">
       <!-- Admin-only buttons and forms -->
   </c:if>
   ```

2. **Backend Layer**: Role verification in every servlet
   ```java
   if (!currentUser.isAdmin()) {
       response.sendError(HttpServletResponse.SC_FORBIDDEN);
       return;
   }
   ```

**Why Both Layers?**
- UI hiding improves user experience
- Backend enforcement prevents unauthorized access via direct URL manipulation
- Defense in depth principle: multiple layers of security

**AuthFilter Implementation**:
```java
@Override
public void doFilter(ServletRequest request, ServletResponse response, 
                     FilterChain chain) throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpSession session = httpRequest.getSession(false);
    
    if (session == null || session.getAttribute("user") == null) {
        ((HttpServletResponse) response).sendRedirect("/login");
        return;
    }
    
    chain.doFilter(request, response);
}
```

---

## 6. Application Flow

### Login Flow

```
1. User visits /login
   ↓
2. LoginServlet.doGet() displays login form
   ↓
3. User enters credentials and submits
   ↓
4. LoginServlet.doPost() receives request
   ↓
5. Server-side validation:
   - Check username not empty
   - Check password not empty
   - Verify format
   ↓
6. AuthenticationService.login(username, password)
   - Query UserDAO for user by username
   - Compare password hash with stored hash
   - Check account lockout status
   - Update last login timestamp
   ↓
7. If authentication succeeds:
   - Create HttpSession
   - Store user object in session
   - Set session timeout (30 minutes)
   - Redirect to /dashboard
   ↓
8. If authentication fails:
   - Increment failed attempt counter
   - Check if lockout threshold reached (5 attempts)
   - If locked: Set 15-minute lockout
   - Return to login page with error message
```

### Request Creation Flow

```
1. Requester visits /requests?action=create
   ↓
2. RequestServlet.doGet() displays request form
   - Loads available resources from ResourceDAO
   - Displays resource selector
   ↓
3. Requester selects resource, enters description, chooses urgency
   ↓
4. RequestServlet.doPost() receives form submission
   ↓
5. Server-side validation:
   - Verify resource exists
   - Validate description (10-500 characters)
   - Verify urgency level is valid
   ↓
6. RequestService.createRequest(request)
   - Create Request object with requester_id = current user
   - Set status = PENDING
   - Set created_at = current timestamp
   - Call RequestDAO.create()
   ↓
7. RequestDAO.create() executes:
   INSERT INTO requests (request_id, requester_id, resource_id, 
                        status, description, urgency_level, created_at)
   VALUES (?, ?, ?, ?, ?, ?, ?)
   ↓
8. Redirect to /requests with success message
```

### Volunteer Assignment Flow

```
1. Admin views /requests (sees all requests)
   ↓
2. Admin clicks "Assign Volunteer" on a PENDING request
   ↓
3. Modal dialog displays list of available volunteers
   ↓
4. Admin selects volunteer and confirms
   ↓
5. RequestServlet.doPost() receives assignment request
   ↓
6. Authorization check:
   - Verify current user is ADMIN
   - Verify request exists
   - Verify volunteer exists
   ↓
7. RequestService.assignVolunteer(requestId, volunteerId)
   - Load Request from RequestDAO
   - Set volunteer_id = selected volunteer
   - Set status = ASSIGNED
   - Call RequestDAO.update()
   ↓
8. RequestDAO.update() executes:
   UPDATE requests SET volunteer_id = ?, status = 'ASSIGNED', 
                      updated_at = ? WHERE request_id = ?
   ↓
9. Redirect to /requests with success message
```

### Status Update Flow

```
1. Volunteer views /requests (sees assigned requests)
   ↓
2. Volunteer clicks "Update Status" on assigned request
   ↓
3. Status dropdown shows available transitions:
   - ASSIGNED → IN_PROGRESS
   - IN_PROGRESS → COMPLETED
   ↓
4. Volunteer selects new status and submits
   ↓
5. RequestServlet.doPost() receives status update
   ↓
6. Authorization check:
   - Verify current user is VOLUNTEER
   - Verify volunteer_id matches current user
   ↓
7. RequestService.updateRequestStatus(requestId, newStatus)
   - Load Request from RequestDAO
   - Validate status transition
   - Set status = newStatus
   - Set updated_at = current timestamp
   - Call RequestDAO.update()
   ↓
8. RequestDAO.update() executes:
   UPDATE requests SET status = ?, updated_at = ? WHERE request_id = ?
   ↓
9. If status = COMPLETED:
   - Requester can now submit feedback
   - Activity logged as "Request completed"
   ↓
10. Redirect to /requests with success message
```

### Feedback Submission Flow

```
1. Requester views completed request
   ↓
2. Requester clicks "Submit Feedback"
   ↓
3. Feedback form displays:
   - 1-5 star rating selector
   - Optional comment text area
   ↓
4. Requester selects rating and enters comment
   ↓
5. FeedbackServlet.doPost() receives submission
   ↓
6. Server-side validation:
   - Verify rating is 1-5
   - Verify comment length (if provided)
   - Verify request exists and is COMPLETED
   ↓
7. FeedbackService.submitFeedback(feedback)
   - Create Feedback object
   - Set user_id = current user
   - Set request_id = request
   - Set created_at = current timestamp
   - Call FeedbackDAO.create()
   ↓
8. FeedbackDAO.create() executes:
   INSERT INTO feedback (feedback_id, user_id, request_id, 
                        rating, comments, created_at)
   VALUES (?, ?, ?, ?, ?, ?)
   ↓
9. Activity logged as "Feedback submitted"
   ↓
10. Redirect with success message
```

### Logout & Session Lifecycle

```
1. User clicks "Logout" button
   ↓
2. LoginServlet.doGet() with logout parameter
   ↓
3. HttpSession.invalidate() called
   - Session attributes cleared
   - Session ID invalidated
   - Cookies cleared
   ↓
4. Redirect to /login
   ↓
5. Session timeout (30 minutes):
   - If user inactive for 30 minutes
   - Session automatically invalidated
   - Next request redirected to /login
   - AuthFilter detects missing session
```

