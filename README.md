# ResoMap: Community Resource Hub

A production-grade Java Servlet-based web application for managing community resources and coordinating volunteer assistance with those in need.

## 1. Project Overview

### Problem Statement

Communities face significant challenges in efficiently managing and distributing resources to those in need. Traditional systems lack centralized coordination, making it difficult to:
- Track available resources across multiple locations
- Match resource requests with available inventory
- Coordinate volunteer efforts effectively
- Maintain audit trails of resource allocation

### Solution

ResoMap provides a unified platform that:
- Centralizes resource inventory management
- Automates volunteer assignment to requests
- Tracks request lifecycle from creation to completion
- Maintains comprehensive activity logs
- Enforces role-based access control

### Target Users

- **Community Organizations**: Manage donations and resource distribution
- **Non-profit Organizations**: Track aid requests and volunteer assignments
- **Local Government Agencies**: Coordinate emergency resource allocation
- **Volunteer Networks**: Organize community support initiatives

### Core Objectives

1. Provide a centralized resource management system
2. Enable efficient matching of requests with available resources
3. Facilitate volunteer coordination and task assignment
4. Maintain data integrity and audit trails
5. Ensure secure, role-based access to system functions

### Key Capabilities

- Multi-role authentication and authorization
- Real-time resource inventory tracking
- Automated volunteer assignment workflow
- Comprehensive request lifecycle management
- Role-specific dashboards with relevant metrics
- Full-text search and advanced filtering
- Activity tracking and audit logging
- Responsive web interface

---

## 2. Feature Summary

### Authentication & Authorization

| Feature | Description |
|---------|-------------|
| User Registration | Self-service account creation with email validation |
| Secure Login | SHA-256 password hashing with random salt generation |
| Account Lockout | Automatic lockout after 5 failed attempts (15-minute duration) |
| Session Management | 30-minute session timeout with automatic cleanup |
| Role-Based Access | Three distinct roles: Admin, Volunteer, Requester |
| Permission Enforcement | Backend validation on every request |

### Resource Management

| Feature | Description |
|---------|-------------|
| Resource Catalog | Centralized inventory of available resources |
| Category Classification | Six categories: Food, Clothing, Shelter, Medical, Education, Other |
| Quantity Tracking | Real-time inventory level management |
| Location Management | Geographic tracking of resource locations |
| Contact Information | Maintainer contact details for each resource |
| Search Functionality | Full-text search across resource names and descriptions |
| Category Filtering | Filter resources by category with real-time updates |

### Request Lifecycle Management

| Feature | Description |
|---------|-------------|
| Request Creation | Requesters create requests for specific resources |
| Status Tracking | Five-state workflow: Pending → Assigned → In Progress → Completed/Cancelled |
| Urgency Levels | Four priority levels: Low, Medium, High, Critical |
| Volunteer Assignment | Admin assigns volunteers to requests |
| Status Updates | Volunteers update request progress |
| Request History | Complete audit trail of all request changes |

### Role-Based Dashboards

| Role | Dashboard Features |
|------|-------------------|
| Admin | System-wide statistics, user management, resource oversight, request assignment |
| Volunteer | Active assignments, completion history, performance metrics, recent activity |
| Requester | Request status tracking, resource availability, feedback submission, activity log |

### Search & Filtering

| Feature | Description |
|---------|-------------|
| Full-Text Search | Search resources by name, description, or category |
| Status Filtering | Filter requests by status (Pending, Assigned, In Progress, Completed, Cancelled) |
| Urgency Filtering | Filter requests by urgency level |
| Category Filtering | Filter resources by category |
| Combined Filters | Apply multiple filters simultaneously |
| Real-Time Results | AJAX-based search with instant feedback |

### Feedback System

| Feature | Description |
|---------|-------------|
| Rating Submission | 1-5 star rating system for completed requests |
| Comment Submission | Optional detailed feedback comments |
| Feedback Tracking | Historical record of all feedback |
| Feedback Visibility | Requesters can view feedback on their requests |

### Activity Tracking

| Feature | Description |
|---------|-------------|
| Recent Activity Feed | Dashboard displays 10 most recent activities |
| Role-Aware Filtering | Each role sees only relevant activities |
| Activity Types | Request creation, assignment, status updates, resource creation, feedback |
| Timestamp Tracking | Precise activity timestamps for audit purposes |
| Activity Derivation | Activities derived from actual database records (not static) |

### Admin Controls

| Feature | Description |
|---------|-------------|
| User Management | Create, view, and manage user accounts |
| Resource Management | Full CRUD operations on resource inventory |
| Request Oversight | View and manage all requests system-wide |
| Volunteer Assignment | Manually assign or reassign volunteers to requests |
| Status Management | Update request status on behalf of volunteers |
| System Configuration | Configure system parameters and settings |

---

## 3. Technology Stack

### Backend

| Component | Technology | Version |
|-----------|-----------|---------|
| Language | Java | 11 |
| Web Framework | Java Servlets | 4.0 |
| View Technology | JSP (JavaServer Pages) | 2.2 |
| Template Library | JSTL (JSP Standard Tag Library) | 1.2 |
| JSON Processing | Jackson | 2.15.2 |

### Frontend

| Component | Technology |
|-----------|-----------|
| Markup | HTML5 |
| Styling | CSS3 (Pure CSS, no frameworks) |
| Interactivity | Vanilla JavaScript (ES6+) |
| HTTP Requests | Fetch API / XMLHttpRequest |
| Responsive Design | Mobile-first CSS Grid |

### Database

| Aspect | Technology |
|--------|-----------|
| Development | SQLite 3.42 |
| Production | MySQL 8.0 (supported) |
| JDBC Driver | SQLite JDBC 3.42.0.0 / MySQL Connector 8.0.33 |
| Connection Management | JDBC Connection Pooling |

### Build & Deployment

| Tool | Purpose | Version |
|------|---------|---------|
| Maven | Build automation | 3.8+ |
| Tomcat | Application server | 9.0+ |
| JUnit | Unit testing | 5.9.2 |
| Mockito | Test mocking | 5.3.1 |

### Libraries & Frameworks

| Library | Purpose |
|---------|---------|
| Jackson | JSON serialization/deserialization |
| JSTL | Server-side template processing |
| Servlet API | HTTP request/response handling |
| JDBC | Database connectivity |
| Java Logger | Application logging |


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


## 7. Database Design

### Database Type Support

| Environment | Database | Driver | Version |
|-------------|----------|--------|---------|
| Development | SQLite | sqlite-jdbc | 3.42.0.0 |
| Production | MySQL | mysql-connector-java | 8.0.33 |

**Configuration**: Set `USE_MYSQL` flag in `DBConnection.java` to switch between databases.

### Schema Overview

The application uses a relational schema with four main entities:

| Table | Purpose | Records |
|-------|---------|---------|
| users | User accounts and authentication | ~6 (sample) |
| resources | Available community resources | ~15 (sample) |
| requests | Resource requests from users | ~8 (sample) |
| feedback | User feedback on completed requests | Variable |

### Entity Relationship Diagram

```
┌─────────────────────┐
│      users          │
├─────────────────────┤
│ user_id (PK)        │
│ username (UNIQUE)   │
│ email (UNIQUE)      │
│ password_hash       │
│ role (ENUM)         │
│ created_at          │
│ updated_at          │
└─────────────────────┘
         ▲
         │ (1:N)
         │
    ┌────┴────┐
    │          │
    │          │
┌───┴──────────┴──┐      ┌──────────────────┐
│   resources     │      │    requests      │
├─────────────────┤      ├──────────────────┤
│ resource_id(PK) │      │ request_id (PK)  │
│ name            │◄─────│ resource_id (FK) │
│ description     │      │ requester_id(FK) │
│ category        │      │ volunteer_id(FK) │
│ quantity        │      │ status (ENUM)    │
│ location        │      │ urgency_level    │
│ contact_info    │      │ description      │
│ created_by (FK) │      │ created_at       │
│ created_at      │      │ updated_at       │
│ updated_at      │      └──────────────────┘
└─────────────────┘              │
                                 │ (1:N)
                                 │
                          ┌──────┴──────┐
                          │  feedback   │
                          ├─────────────┤
                          │feedback_id  │
                          │user_id (FK) │
                          │request_id   │
                          │rating       │
                          │comments     │
                          │created_at   │
                          └─────────────┘
```

### Table Descriptions

#### users Table
Stores user account information and authentication credentials.

| Column | Type | Constraints | Purpose |
|--------|------|-------------|---------|
| user_id | TEXT | PRIMARY KEY | Unique user identifier (UUID) |
| username | TEXT | UNIQUE, NOT NULL | Login username |
| email | TEXT | UNIQUE, NOT NULL | User email address |
| password_hash | TEXT | NOT NULL | SHA-256 hashed password with salt |
| role | TEXT | NOT NULL, CHECK | User role: ADMIN, VOLUNTEER, REQUESTER |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | Account creation timestamp |
| updated_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | Last update timestamp |

#### resources Table
Stores available community resources.

| Column | Type | Constraints | Purpose |
|--------|------|-------------|---------|
| resource_id | TEXT | PRIMARY KEY | Unique resource identifier (UUID) |
| name | TEXT | NOT NULL | Resource name (e.g., "Canned Vegetables") |
| description | TEXT | | Detailed resource description |
| category | TEXT | NOT NULL | Resource category (FOOD, CLOTHING, etc.) |
| quantity | INTEGER | DEFAULT 0 | Current available quantity |
| location | TEXT | | Physical location of resource |
| contact_info | TEXT | | Contact information for resource manager |
| created_by | TEXT | FOREIGN KEY → users | Admin who created resource |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | Creation timestamp |
| updated_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | Last update timestamp |

#### requests Table
Stores resource requests from users.

| Column | Type | Constraints | Purpose |
|--------|------|-------------|---------|
| request_id | TEXT | PRIMARY KEY | Unique request identifier (UUID) |
| requester_id | TEXT | FOREIGN KEY → users | User who created request |
| resource_id | TEXT | FOREIGN KEY → resources | Requested resource |
| volunteer_id | TEXT | FOREIGN KEY → users, NULL | Assigned volunteer (if any) |
| status | TEXT | NOT NULL, CHECK | Request status (PENDING, ASSIGNED, IN_PROGRESS, COMPLETED, CANCELLED) |
| description | TEXT | NOT NULL | Request description/reason |
| urgency_level | TEXT | NOT NULL, CHECK | Urgency: LOW, MEDIUM, HIGH, CRITICAL |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | Request creation timestamp |
| updated_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | Last status update timestamp |

#### feedback Table
Stores user feedback on completed requests.

| Column | Type | Constraints | Purpose |
|--------|------|-------------|---------|
| feedback_id | TEXT | PRIMARY KEY | Unique feedback identifier (UUID) |
| user_id | TEXT | FOREIGN KEY → users | User submitting feedback |
| request_id | TEXT | FOREIGN KEY → requests, NULL | Associated request |
| rating | INTEGER | CHECK (1-5) | Rating from 1 (poor) to 5 (excellent) |
| comments | TEXT | | Optional feedback comments |
| feedback_type | TEXT | DEFAULT 'GENERAL' | Feedback type (GENERAL, REQUEST_SPECIFIC, SYSTEM_IMPROVEMENT) |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | Feedback submission timestamp |

### Constraints & Indexes

**Foreign Key Constraints**:
- `resources.created_by` → `users.user_id` (ON DELETE SET NULL)
- `requests.requester_id` → `users.user_id` (ON DELETE CASCADE)
- `requests.resource_id` → `resources.resource_id` (ON DELETE CASCADE)
- `requests.volunteer_id` → `users.user_id` (ON DELETE SET NULL)
- `feedback.user_id` → `users.user_id` (ON DELETE CASCADE)
- `feedback.request_id` → `requests.request_id` (ON DELETE SET NULL)

**Indexes for Performance**:
- `users(username)` - Fast username lookup during login
- `users(email)` - Fast email lookup during registration
- `resources(category)` - Fast category filtering
- `resources(created_by)` - Fast resource lookup by creator
- `requests(requester_id)` - Fast request lookup by requester
- `requests(volunteer_id)` - Fast request lookup by volunteer
- `requests(resource_id)` - Fast request lookup by resource
- `requests(status)` - Fast status filtering
- `requests(urgency_level)` - Fast urgency filtering
- `requests(created_at)` - Fast sorting by creation date
- `feedback(user_id)` - Fast feedback lookup by user
- `feedback(request_id)` - Fast feedback lookup by request

---

## 8. Database Schema (SQL)

### Complete Schema Definition

```sql
-- Users table: Stores user accounts and authentication
CREATE TABLE IF NOT EXISTS users (
    user_id TEXT PRIMARY KEY,
    username TEXT UNIQUE NOT NULL,
    email TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    role TEXT NOT NULL CHECK (role IN ('ADMIN', 'VOLUNTEER', 'REQUESTER')),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Resources table: Stores available community resources
CREATE TABLE IF NOT EXISTS resources (
    resource_id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT,
    category TEXT NOT NULL,
    quantity INTEGER DEFAULT 0,
    location TEXT,
    contact_info TEXT,
    created_by TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(user_id)
);

-- Requests table: Stores resource requests from users
CREATE TABLE IF NOT EXISTS requests (
    request_id TEXT PRIMARY KEY,
    requester_id TEXT NOT NULL,
    resource_id TEXT NOT NULL,
    volunteer_id TEXT,
    status TEXT DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'ASSIGNED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED')),
    description TEXT,
    urgency_level TEXT DEFAULT 'MEDIUM' CHECK (urgency_level IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL')),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (requester_id) REFERENCES users(user_id),
    FOREIGN KEY (resource_id) REFERENCES resources(resource_id),
    FOREIGN KEY (volunteer_id) REFERENCES users(user_id)
);

-- Feedback table: Stores user feedback on completed requests
CREATE TABLE IF NOT EXISTS feedback (
    feedback_id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL,
    request_id TEXT,
    rating INTEGER CHECK (rating >= 1 AND rating <= 5),
    comments TEXT,
    feedback_type TEXT DEFAULT 'GENERAL' CHECK (feedback_type IN ('GENERAL', 'REQUEST_SPECIFIC', 'SYSTEM_IMPROVEMENT')),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (request_id) REFERENCES requests(request_id)
);

-- Indexes for performance optimization
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);

CREATE INDEX IF NOT EXISTS idx_resources_category ON resources(category);
CREATE INDEX IF NOT EXISTS idx_resources_name ON resources(name);
CREATE INDEX IF NOT EXISTS idx_resources_created_by ON resources(created_by);

CREATE INDEX IF NOT EXISTS idx_requests_requester ON requests(requester_id);
CREATE INDEX IF NOT EXISTS idx_requests_volunteer ON requests(volunteer_id);
CREATE INDEX IF NOT EXISTS idx_requests_resource ON requests(resource_id);
CREATE INDEX IF NOT EXISTS idx_requests_status ON requests(status);
CREATE INDEX IF NOT EXISTS idx_requests_urgency ON requests(urgency_level);
CREATE INDEX IF NOT EXISTS idx_requests_created_at ON requests(created_at);

CREATE INDEX IF NOT EXISTS idx_feedback_user ON feedback(user_id);
CREATE INDEX IF NOT EXISTS idx_feedback_request ON feedback(request_id);
```

### Sample Data Initialization

```sql
-- Sample users
INSERT OR IGNORE INTO users (user_id, username, email, password_hash, role) VALUES
('admin-001', 'admin', 'admin@communityhub.org', 'salt:hash', 'ADMIN'),
('vol-001', 'volunteer1', 'volunteer1@example.com', 'salt:hash', 'VOLUNTEER'),
('vol-002', 'volunteer2', 'volunteer2@example.com', 'salt:hash', 'VOLUNTEER'),
('req-001', 'requester1', 'requester1@example.com', 'salt:hash', 'REQUESTER'),
('req-002', 'requester2', 'requester2@example.com', 'salt:hash', 'REQUESTER'),
('req-003', 'requester3', 'requester3@example.com', 'salt:hash', 'REQUESTER');

-- Sample resources (15 total across 6 categories)
INSERT OR IGNORE INTO resources (resource_id, name, description, category, quantity, location, contact_info, created_by) VALUES
('res-food-001', 'Canned Vegetables', 'Assorted canned vegetables', 'FOOD', 45, 'Community Center - Storage A', 'food@community.org', 'admin-001'),
('res-food-002', 'Canned Fruits', 'Mixed canned fruits in light syrup', 'FOOD', 38, 'Community Center - Storage A', 'food@community.org', 'admin-001'),
('res-food-003', 'Pasta & Rice', 'Bulk pasta and rice supplies', 'FOOD', 60, 'Community Center - Storage B', 'food@community.org', 'admin-001'),
('res-food-004', 'Peanut Butter', 'High-protein peanut butter jars', 'FOOD', 25, 'Community Center - Storage A', 'food@community.org', 'admin-001'),
('res-food-005', 'Baby Formula', 'Infant formula and baby food', 'FOOD', 30, 'Family Services - Building C', 'family@community.org', 'admin-001'),
('res-clothing-001', 'Winter Coats', 'Warm winter coats for adults and children', 'CLOTHING', 28, 'Donation Center - Building B', 'donations@community.org', 'admin-001'),
('res-clothing-002', 'Warm Sweaters', 'Wool and fleece sweaters', 'CLOTHING', 35, 'Donation Center - Building B', 'donations@community.org', 'admin-001'),
('res-clothing-003', 'Thermal Socks', 'Thermal and wool socks', 'CLOTHING', 100, 'Donation Center - Building B', 'donations@community.org', 'admin-001'),
('res-shelter-001', 'Emergency Blankets', 'Thermal emergency blankets', 'SHELTER', 120, 'Emergency Services - Warehouse', 'emergency@community.org', 'admin-001'),
('res-shelter-002', 'Sleeping Bags', 'Warm sleeping bags', 'SHELTER', 20, 'Emergency Services - Warehouse', 'emergency@community.org', 'admin-001'),
('res-medical-001', 'First Aid Kits', 'Complete first aid kits', 'MEDICAL', 18, 'Health Center - Supply Room', 'health@community.org', 'admin-001'),
('res-medical-002', 'Medical Masks', 'N95 and surgical masks', 'MEDICAL', 500, 'Health Center - Supply Room', 'health@community.org', 'admin-001'),
('res-education-001', 'School Supplies', 'Notebooks, pens, pencils', 'EDUCATION', 80, 'Education Center - Room 101', 'education@community.org', 'admin-001'),
('res-education-002', 'Textbooks', 'Used textbooks for various subjects', 'EDUCATION', 45, 'Education Center - Room 102', 'education@community.org', 'admin-001'),
('res-other-001', 'Hygiene Products', 'Soap, shampoo, toothpaste', 'OTHER', 60, 'Community Center - Storage C', 'supplies@community.org', 'admin-001');

-- Sample requests (8 total)
INSERT OR IGNORE INTO requests (request_id, requester_id, resource_id, description, urgency_level, status) VALUES
('req-001', 'req-001', 'res-food-001', 'Family of four needs emergency food assistance', 'HIGH', 'PENDING'),
('req-002', 'req-002', 'res-clothing-001', 'Single mother needs winter coats for children', 'MEDIUM', 'PENDING'),
('req-003', 'req-001', 'res-medical-001', 'Need first aid kit for elderly parent', 'LOW', 'PENDING'),
('req-004', 'req-003', 'res-education-001', 'Student needs school supplies', 'MEDIUM', 'ASSIGNED'),
('req-005', 'req-002', 'res-shelter-001', 'Temporary shelter needed for family', 'CRITICAL', 'PENDING'),
('req-006', 'req-001', 'res-food-005', 'New mother needs baby formula', 'HIGH', 'PENDING'),
('req-007', 'req-003', 'res-clothing-003', 'Need warm socks for homeless outreach', 'MEDIUM', 'PENDING'),
('req-008', 'req-002', 'res-other-001', 'Hygiene products for family in temporary housing', 'MEDIUM', 'PENDING');
```


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


## 12. UI / UX Design Philosophy

### Design Goals

1. **Clarity**: Information presented clearly without clutter
2. **Efficiency**: Users accomplish tasks with minimal clicks
3. **Consistency**: Uniform design patterns across all pages
4. **Accessibility**: Usable by people with different abilities
5. **Responsiveness**: Works on desktop, tablet, and mobile devices

### Navigation Structure

**Primary Navigation**:
- Sidebar menu (persistent across all pages)
- Role-specific menu items
- Active page highlighting
- Logout button in footer

**Page Hierarchy**:
```
Login Page
    ↓
Dashboard (role-specific)
    ├── Resources Page
    ├── Requests Page
    ├── Feedback Page (if applicable)
    └── Admin Panel (admin only)
```

### Dashboard Design Rationale

**Admin Dashboard**:
- System-wide statistics (total resources, active requests, volunteers)
- Recent activity feed (all activities)
- Quick access to resource management
- Request oversight and assignment tools

**Volunteer Dashboard**:
- Active assignments count
- Completed requests count
- Performance metrics
- Recent activity (assigned requests only)

**Requester Dashboard**:
- Active requests count
- Completed requests count
- Cancelled requests count
- Recent activity (own requests only)

### Accessibility Considerations

**Color Contrast**:
- Text: #333 on #FFFFFF (21:1 ratio, AAA compliant)
- Links: #0066CC on #FFFFFF (8.6:1 ratio, AAA compliant)
- Buttons: #FFFFFF on #4CAF50 (4.5:1 ratio, AA compliant)

**Keyboard Navigation**:
- All interactive elements accessible via Tab key
- Focus indicators visible
- Form submission via Enter key
- Escape key closes modals

**Semantic HTML**:
```html
<header><!-- Page header --></header>
<nav><!-- Navigation menu --></nav>
<main><!-- Main content --></main>
<footer><!-- Footer --></footer>
<form><!-- Form elements --></form>
<button><!-- Clickable elements --></button>
<label><!-- Form labels --></label>
```

### Responsive Behavior

**Breakpoints**:
- Mobile: < 768px (single column layout)
- Tablet: 768px - 1024px (two column layout)
- Desktop: > 1024px (full layout)

**Responsive Elements**:
- Sidebar collapses to hamburger menu on mobile
- Tables stack vertically on small screens
- Forms adapt to screen width
- Navigation becomes vertical on mobile

**CSS Grid Implementation**:
```css
.grid {
    display: grid;
    gap: 20px;
}

@media (min-width: 768px) {
    .grid {
        grid-template-columns: repeat(2, 1fr);
    }
}

@media (min-width: 1024px) {
    .grid {
        grid-template-columns: repeat(3, 1fr);
    }
}
```

---

## 13. Build & Deployment

### Prerequisites

| Component | Requirement | Version |
|-----------|-------------|---------|
| Java Development Kit | Required | 11 or higher |
| Apache Maven | Required | 3.8 or higher |
| Apache Tomcat | Required | 9.0 or higher |
| Git | Optional | Latest |

**Verification**:
```bash
java -version          # Should show Java 11+
mvn -version          # Should show Maven 3.8+
```

### Build Steps

**Step 1: Clone Repository**
```bash
git clone https://github.com/yourusername/resomap.git
cd resomap
```

**Step 2: Clean Build**
```bash
mvn clean compile
```

**Step 3: Run Tests**
```bash
mvn test
```

**Step 4: Package WAR**
```bash
mvn package
```

**Output**: `target/community-resource-hub.war`

### WAR Generation

**Maven WAR Plugin Configuration**:
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-war-plugin</artifactId>
    <version>3.2.3</version>
    <configuration>
        <webXml>src/main/webapp/WEB-INF/web.xml</webXml>
    </configuration>
</plugin>
```

**WAR Contents**:
```
community-resource-hub.war
├── WEB-INF/
│   ├── web.xml (servlet configuration)
│   ├── classes/ (compiled Java classes)
│   └── lib/ (JAR dependencies)
├── jsp/ (JSP pages)
├── css/ (stylesheets)
├── js/ (JavaScript files)
└── META-INF/ (manifest)
```

### Tomcat Deployment

**Option 1: Manual Deployment**
```bash
# Copy WAR to Tomcat webapps directory
cp target/community-resource-hub.war $TOMCAT_HOME/webapps/

# Start Tomcat
$TOMCAT_HOME/bin/startup.sh

# Access application
# http://localhost:8080/community-resource-hub/
```

**Option 2: Tomcat Manager Deployment**
1. Access Tomcat Manager: `http://localhost:8080/manager`
2. Upload WAR file using web interface
3. Deploy and start application

**Option 3: Maven Plugin Deployment**
```bash
mvn tomcat7:deploy
```

### Configuration Notes

**Database Configuration**:
```java
// File: src/main/java/com/communityhub/util/DBConnection.java

// Development (SQLite)
private static final boolean USE_MYSQL = false;
private static final String DB_URL = "jdbc:sqlite:community_hub.db";

// Production (MySQL)
private static final boolean USE_MYSQL = true;
private static final String MYSQL_URL = "jdbc:mysql://localhost:3306/community_hub";
private static final String MYSQL_USER = "your_username";
private static final String MYSQL_PASSWORD = "your_password";
```

**Session Configuration**:
```xml
<!-- web.xml -->
<session-config>
    <session-timeout>30</session-timeout>
</session-config>
```

**Logging Configuration**:
```java
// File: src/main/java/com/communityhub/util/LoggingConfig.java
Logger logger = Logger.getLogger("com.communityhub");
FileHandler fileHandler = new FileHandler("logs/application.log");
logger.addHandler(fileHandler);
```

---

## 14. Project Structure

### Directory Tree

```
resomap/
├── src/
│   ├── main/
│   │   ├── java/com/communityhub/
│   │   │   ├── servlet/
│   │   │   │   ├── LoginServlet.java
│   │   │   │   ├── DashboardServlet.java
│   │   │   │   ├── ResourceServlet.java
│   │   │   │   ├── RequestServlet.java
│   │   │   │   ├── FeedbackServlet.java
│   │   │   │   ├── SearchServlet.java
│   │   │   │   ├── filter/
│   │   │   │   │   └── AuthFilter.java
│   │   │   │   └── listener/
│   │   │   │       └── SessionListener.java
│   │   │   ├── service/
│   │   │   │   ├── AuthenticationService.java
│   │   │   │   ├── ResourceService.java
│   │   │   │   ├── RequestService.java
│   │   │   │   ├── ActivityService.java
│   │   │   │   └── FeedbackService.java
│   │   │   ├── dao/
│   │   │   │   ├── BaseDAO.java
│   │   │   │   ├── UserDAO.java
│   │   │   │   ├── ResourceDAO.java
│   │   │   │   ├── RequestDAO.java
│   │   │   │   ├── FeedbackDAO.java
│   │   │   │   └── ActivityDAO.java
│   │   │   ├── model/
│   │   │   │   ├── User.java
│   │   │   │   ├── Resource.java
│   │   │   │   ├── Request.java
│   │   │   │   ├── Feedback.java
│   │   │   │   ├── UserRole.java
│   │   │   │   ├── RequestStatus.java
│   │   │   │   └── UrgencyLevel.java
│   │   │   ├── dto/
│   │   │   │   └── ActivityDTO.java
│   │   │   ├── exception/
│   │   │   │   ├── CommunityHubException.java
│   │   │   │   ├── DatabaseException.java
│   │   │   │   ├── AuthenticationException.java
│   │   │   │   └── InvalidInputException.java
│   │   │   └── util/
│   │   │       ├── DBConnection.java
│   │   │       ├── ValidationUtils.java
│   │   │       ├── PasswordUtils.java
│   │   │       ├── RequestContext.java
│   │   │       └── LoggingConfig.java
│   │   ├── webapp/
│   │   │   ├── WEB-INF/
│   │   │   │   └── web.xml
│   │   │   ├── jsp/
│   │   │   │   ├── login.jsp
│   │   │   │   ├── dashboard.jsp
│   │   │   │   ├── resources.jsp
│   │   │   │   ├── requests.jsp
│   │   │   │   ├── new-request.jsp
│   │   │   │   ├── admin.jsp
│   │   │   │   ├── feedback.jsp
│   │   │   │   └── error.jsp
│   │   │   ├── css/
│   │   │   │   └── styles.css
│   │   │   └── js/
│   │   │       ├── search.js
│   │   │       ├── filters.js
│   │   │       └── validation.js
│   │   └── resources/
│   │       └── sql/
│   │           ├── schema_sqlite.sql
│   │           └── sample_data.sql
│   └── test/
│       └── java/com/communityhub/
│           ├── dao/
│           ├── service/
│           └── util/
├── pom.xml
├── README.md
├── LICENSE
└── .gitignore
```

### Package Purposes

| Package | Purpose |
|---------|---------|
| `servlet` | HTTP request handlers and filters |
| `service` | Business logic and rule enforcement |
| `dao` | Database abstraction layer |
| `model` | Domain objects and enums |
| `dto` | Data transfer objects |
| `exception` | Custom exception classes |
| `util` | Utility classes (DB, validation, logging) |

### Naming Conventions

**Classes**:
- Servlets: `*Servlet` (e.g., `LoginServlet`)
- Services: `*Service` (e.g., `AuthenticationService`)
- DAOs: `*DAO` (e.g., `UserDAO`)
- Exceptions: `*Exception` (e.g., `DatabaseException`)
- Utilities: `*Utils` (e.g., `ValidationUtils`)

**Methods**:
- Getters: `get*` (e.g., `getUsername()`)
- Setters: `set*` (e.g., `setUsername()`)
- Checkers: `is*` or `has*` (e.g., `isAdmin()`)
- Finders: `find*` or `get*` (e.g., `findByUsername()`)

**Variables**:
- Constants: `UPPER_CASE` (e.g., `MAX_LOGIN_ATTEMPTS`)
- Local variables: `camelCase` (e.g., `userName`)
- Instance variables: `camelCase` (e.g., `userId`)

---

## 15. Logging & Error Handling

### Logging Strategy

**Java Logger Configuration**:
```java
private static final Logger logger = Logger.getLogger(LoginServlet.class.getName());

// Log levels
logger.info("User logged in successfully: " + username);
logger.warning("Failed login attempt for user: " + username);
logger.severe("Database connection failed");
```

**Log Levels**:
| Level | Usage | Example |
|-------|-------|---------|
| INFO | Normal operations | "User logged in successfully" |
| WARNING | Recoverable issues | "Failed login attempt" |
| SEVERE | Critical errors | "Database connection failed" |

**Log Output**:
```
[2025-12-19 14:30:45] INFO: User logged in successfully: admin
[2025-12-19 14:31:12] WARNING: Failed login attempt for user: requester1
[2025-12-19 14:32:00] SEVERE: Database connection failed
```

### Error Pages

**404 Error Page** (`error.jsp`):
```jsp
<h1>Page Not Found</h1>
<p>The page you requested could not be found.</p>
<a href="${pageContext.request.contextPath}/dashboard">Return to Dashboard</a>
```

**500 Error Page** (`error.jsp`):
```jsp
<h1>Server Error</h1>
<p>An unexpected error occurred. Please try again later.</p>
<a href="${pageContext.request.contextPath}/dashboard">Return to Dashboard</a>
```

**web.xml Configuration**:
```xml
<error-page>
    <error-code>404</error-code>
    <location>/jsp/error.jsp</location>
</error-page>
<error-page>
    <error-code>500</error-code>
    <location>/jsp/error.jsp</location>
</error-page>
```

### Exception Hierarchy

```
Throwable
    ├── Exception
    │   ├── CommunityHubException (base)
    │   │   ├── DatabaseException
    │   │   ├── AuthenticationException
    │   │   └── InvalidInputException
    │   └── IOException, SQLException, etc.
    └── Error
```

**Exception Usage**:
```java
// Custom exception with user-friendly message
public class AuthenticationException extends CommunityHubException {
    private String userMessage;
    
    public AuthenticationException(String userMessage) {
        super(userMessage);
        this.userMessage = userMessage;
    }
    
    public String getUserMessage() {
        return userMessage;
    }
}

// Usage in servlet
try {
    User user = authService.login(username, password);
} catch (AuthenticationException e) {
    request.setAttribute("error", e.getUserMessage());
    request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
}
```

### Recovery Behavior

**Graceful Degradation**:
- Application continues running on non-critical errors
- User receives helpful error message
- System logs error for debugging
- No sensitive information exposed to user

**Example: Database Connection Failure**
```java
try {
    List<Resource> resources = resourceService.getAllResources();
    request.setAttribute("resources", resources);
} catch (DatabaseException e) {
    logger.log(Level.WARNING, "Failed to load resources", e);
    request.setAttribute("error", "Unable to load resources. Please try again later.");
    request.setAttribute("resources", new ArrayList<>());
}
request.getRequestDispatcher("/jsp/resources.jsp").forward(request, response);
```

---

## 16. Testing & Verification

### Manual Testing Approach

**Test Environment Setup**:
1. Build application: `mvn clean package`
2. Deploy to Tomcat: Copy WAR to webapps/
3. Start Tomcat: `$TOMCAT_HOME/bin/startup.sh`
4. Access application: `http://localhost:8080/community-resource-hub/`

### End-to-End Flow Verification

**Flow 1: Login → Dashboard → Logout**
```
1. Navigate to /login
   Expected: Login form displayed
   
2. Enter invalid credentials
   Expected: Error message shown, account not locked
   
3. Enter valid credentials (admin/Admin123!)
   Expected: Redirected to dashboard, session created
   
4. Verify dashboard displays admin-specific content
   Expected: Admin panel link visible, all requests visible
   
5. Click logout
   Expected: Session invalidated, redirected to login
```

**Flow 2: Create Resource (Admin)**
```
1. Login as admin
2. Navigate to Resources page
3. Click "Create Resource"
   Expected: Resource creation form displayed
   
4. Enter resource details:
   - Name: "Test Resource"
   - Category: "FOOD"
   - Quantity: 50
   - Location: "Test Location"
   
5. Submit form
   Expected: Resource created, success message shown
   
6. Verify resource appears in list
   Expected: Resource visible with correct details
```

**Flow 3: Create Request (Requester)**
```
1. Login as requester
2. Navigate to Requests page
3. Click "Create Request"
   Expected: Request creation form displayed
   
4. Select resource from dropdown
5. Enter description: "Need this resource urgently"
6. Select urgency: "HIGH"
7. Submit form
   Expected: Request created, success message shown
   
8. Verify request appears in list
   Expected: Request visible with PENDING status
```

**Flow 4: Assign Volunteer (Admin)**
```
1. Login as admin
2. Navigate to Requests page
3. Find PENDING request
4. Click "Assign Volunteer"
   Expected: Volunteer selection modal displayed
   
5. Select volunteer from dropdown
6. Confirm assignment
   Expected: Request status changed to ASSIGNED
   
7. Verify volunteer can see assigned request
   Expected: Volunteer sees request in their dashboard
```

**Flow 5: Update Request Status (Volunteer)**
```
1. Login as volunteer
2. Navigate to Requests page
3. Find ASSIGNED request
4. Click "Update Status"
   Expected: Status dropdown displayed
   
5. Select "IN_PROGRESS"
6. Submit
   Expected: Status updated, success message shown
   
7. Select "COMPLETED"
8. Submit
   Expected: Status updated to COMPLETED
```

**Flow 6: Submit Feedback (Requester)**
```
1. Login as requester
2. Navigate to Requests page
3. Find COMPLETED request
4. Click "Submit Feedback"
   Expected: Feedback form displayed
   
5. Select 5-star rating
6. Enter comment: "Great service!"
7. Submit
   Expected: Feedback submitted, success message shown
```

### Role-Based Testing

**Admin Testing**:
- [ ] Can create resources
- [ ] Can edit resources
- [ ] Can delete resources
- [ ] Can view all requests
- [ ] Can assign volunteers
- [ ] Can update request status
- [ ] Can access admin panel
- [ ] Cannot create requests (not requester)

**Volunteer Testing**:
- [ ] Can view assigned requests
- [ ] Can update request status
- [ ] Can add progress notes
- [ ] Cannot create resources
- [ ] Cannot assign volunteers
- [ ] Cannot access admin panel
- [ ] Cannot view other volunteers' requests

**Requester Testing**:
- [ ] Can create requests
- [ ] Can view own requests
- [ ] Can submit feedback
- [ ] Cannot create resources
- [ ] Cannot assign volunteers
- [ ] Cannot update request status
- [ ] Cannot access admin panel

### Edge Cases Tested

| Scenario | Expected Behavior |
|----------|-------------------|
| Login with empty username | Error message shown |
| Login with empty password | Error message shown |
| Login with invalid credentials | Error message shown |
| 5 failed login attempts | Account locked for 15 minutes |
| Create request with empty description | Validation error shown |
| Create request with description < 10 chars | Validation error shown |
| Create request with description > 500 chars | Validation error shown |
| Assign volunteer to non-existent request | Error message shown |
| Update request status with invalid transition | Error message shown |
| Submit feedback with rating < 1 or > 5 | Validation error shown |
| Access protected URL without login | Redirected to login |
| Access admin page as non-admin | 403 Forbidden error |
| Session timeout after 30 minutes | Redirected to login |

---

## 17. Known Limitations

### Explicit Non-Implemented Features

1. **Email Notifications**
   - Rationale: Requires SMTP configuration, external dependency
   - Future: Can be added via JavaMail API

2. **Mobile App**
   - Rationale: Web application is responsive, covers mobile use case
   - Future: Native mobile app possible with REST API

3. **Real-Time Updates**
   - Rationale: Requires WebSocket, adds complexity
   - Current: Page refresh shows latest data
   - Future: Can be added via WebSocket or Server-Sent Events

4. **Advanced Analytics**
   - Rationale: Scope limited to core functionality
   - Future: Can add reporting module with charts

5. **Multi-Language Support**
   - Rationale: English-only for initial release
   - Future: Can add i18n framework

### Design Tradeoffs

| Tradeoff | Choice | Justification |
|----------|--------|---------------|
| Database | SQLite (dev) / MySQL (prod) | Lightweight for dev, scalable for prod |
| Framework | Pure Servlet | No external dependencies, full control |
| Frontend | Vanilla JS | No build tools required, simple deployment |
| Caching | None | Small dataset, not needed for performance |
| API | REST-like | Sufficient for web application |

### Justifications

**Why No ORM Framework (Hibernate)?**
- Requirement: Use JDBC directly
- Benefit: Full control over queries
- Trade-off: More boilerplate code

**Why No Frontend Framework (React)?**
- Requirement: Pure HTML/CSS/JS
- Benefit: No build process, simple deployment
- Trade-off: More manual DOM manipulation

**Why Single Database Connection?**
- Benefit: Simple connection management
- Trade-off: Not suitable for very high concurrency
- Mitigation: Connection pooling can be added

---

## 18. Future Enhancements

### Scalability Improvements

1. **Connection Pooling**
   - Implement HikariCP for connection pool management
   - Benefit: Better performance under high load

2. **Caching Layer**
   - Add Redis for frequently accessed data
   - Benefit: Reduced database queries

3. **Load Balancing**
   - Deploy multiple Tomcat instances
   - Benefit: Horizontal scaling

4. **Database Replication**
   - MySQL master-slave replication
   - Benefit: High availability

### Feature Expansion

1. **Email Notifications**
   - Notify requesters when request assigned
   - Notify volunteers of new assignments
   - Notify admins of critical requests

2. **Advanced Search**
   - Elasticsearch integration
   - Full-text search across all fields
   - Faceted search

3. **Reporting Module**
   - Resource utilization reports
   - Volunteer performance reports
   - Community impact dashboards

4. **Mobile App**
   - Native iOS/Android apps
   - Push notifications
   - Offline support

5. **API Layer**
   - RESTful API for third-party integration
   - OAuth 2.0 authentication
   - API documentation (Swagger)

### Security Upgrades

1. **HTTPS/TLS**
   - Encrypt all data in transit
   - SSL certificate configuration

2. **Two-Factor Authentication**
   - SMS or authenticator app
   - Enhanced account security

3. **Audit Logging**
   - Log all user actions
   - Compliance with regulations

4. **Data Encryption**
   - Encrypt sensitive data at rest
   - Key management system

---

## 19. Academic Compliance

### Mapping to Marking Rubric

#### Object-Oriented Programming (OOP)

| Concept | Implementation | Evidence |
|---------|----------------|----------|
| Inheritance | User hierarchy (Admin, Volunteer, Requester) | `model/User.java` |
| Polymorphism | Service interface implementations | `service/*Service.java` |
| Encapsulation | Private fields with getters/setters | All model classes |
| Abstraction | BaseDAO abstract class | `dao/BaseDAO.java` |

#### JDBC Usage

| Requirement | Implementation |
|-------------|----------------|
| PreparedStatements | All queries use parameterized statements |
| Connection Management | DBConnection singleton with pooling |
| Transaction Management | Explicit commit/rollback in services |
| Exception Handling | SQLException wrapped in DatabaseException |
| Resource Cleanup | Try-with-resources for statements |

#### Servlet Compliance

| Requirement | Implementation |
|-------------|----------------|
| HTTP Methods | Proper doGet/doPost separation |
| Request/Response | Correct parameter handling and forwarding |
| Session Management | HttpSession for user authentication |
| Filters | AuthFilter for protected URLs |
| Error Handling | Custom error pages and exception handling |

#### Innovation Elements

| Feature | Innovation |
|---------|-----------|
| Recent Activity Feed | Derived from database records, role-aware |
| AJAX Search | Real-time search without page reload |
| Responsive Design | Mobile-first CSS Grid layout |
| Account Lockout | Security feature with time-based unlock |
| Activity Tracking | Comprehensive audit trail |

---

## 20. How to Run (Quick Start)

### Prerequisites Check

```bash
# Verify Java installation
java -version
# Expected: openjdk version "11" or higher

# Verify Maven installation
mvn -version
# Expected: Apache Maven 3.8.0 or higher
```

### Step-by-Step Setup

**Step 1: Clone Repository**
```bash
git clone https://github.com/yourusername/resomap.git
cd resomap
```

**Step 2: Build Application**
```bash
mvn clean package
# Output: target/community-resource-hub.war
```

**Step 3: Deploy to Tomcat**
```bash
# Copy WAR file
cp target/community-resource-hub.war $TOMCAT_HOME/webapps/

# Start Tomcat
$TOMCAT_HOME/bin/startup.sh

# Wait for startup (30-60 seconds)
```

**Step 4: Access Application**
```
URL: http://localhost:8080/community-resource-hub/
Expected: Redirects to login page
```

### First Login Instructions

**Initial Setup**:
1. Application automatically creates sample data on first run
2. Database file created: `community_hub.db`
3. Sample users and resources loaded

**Login Steps**:
1. Navigate to login page
2. Enter credentials (see below)
3. Click "Login"
4. Redirected to role-specific dashboard

### Sample Credentials

**Admin Account**:
```
Username: admin
Password: Admin123!
```

**Volunteer Account**:
```
Username: volunteer1
Password: Volunteer123!
```

**Requester Account**:
```
Username: requester1
Password: Requester123!
```

### Verification Checklist

After startup, verify:
- [ ] Login page loads without errors
- [ ] Can login with sample credentials
- [ ] Dashboard displays role-specific content
- [ ] Resources page shows sample resources
- [ ] Requests page shows sample requests
- [ ] Search functionality works
- [ ] Filters work correctly
- [ ] Can create new request (as requester)
- [ ] Can assign volunteer (as admin)
- [ ] Recent activity displays correctly

---

## 21. Author & Project Info

### Developer Information

**Name**: [Your Name]
**Institution**: [University Name]
**Course**: [Course Code - Course Name]
**Semester**: [Semester/Year]

### Project Context

**Assignment Type**: Servlet-Based Web Application
**Requirements**:
- Java 11+ with Servlets 4.0
- JDBC with PreparedStatements
- JSP for view layer
- Role-based access control
- Database design and implementation
- Multithreading and concurrency
- Security best practices

### Submission Details

**Repository**: [GitHub URL]
**Build Command**: `mvn clean package`
**Deployment**: Tomcat 9.0+
**Database**: SQLite (development) / MySQL (production)

### Acknowledgments

- Java Servlet API documentation
- JDBC best practices guide
- OWASP security guidelines
- W3C accessibility standards

---

## License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## Support & Documentation

For additional documentation, see:
- `DATABASE.md` - Detailed database schema documentation
- `ARCHITECTURE.md` - System architecture details
- `SECURITY.md` - Security implementation details
- `TESTING.md` - Testing procedures and results

---

**End of README**
