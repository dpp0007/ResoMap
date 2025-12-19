==============================
REQUESTER DASHBOARD — CURRENT STATE AUDIT
==============================

**Audit Date:** December 19, 2025
**Audit Scope:** Requester/User role functionality and dashboard capabilities
**Audit Type:** Factual reporting only (no suggestions or improvements)

---

## SECTION 1 — PURPOSE & ACCESS

### Purpose
The Requester Dashboard is the primary interface for users with the REQUESTER role to:
- Create new resource requests
- View their own submitted requests
- Monitor request status and progress
- View available resources
- Navigate between core application sections

### Access Control
- **Who can access:** Users with role = "REQUESTER" only
- **Authentication:** Session-based (HttpSession)
- **Session attribute:** `sessionScope.user` with role verification
- **Unauthorized access:** Redirects to login page (/login)

### Entry Point
- **URL:** `/dashboard` (main dashboard) or `/requests` (requests page)
- **Servlet:** `DashboardServlet` (src/main/java/com/communityhub/servlet/DashboardServlet.java)
- **JSP:** `dashboard.jsp` (src/main/webapp/jsp/dashboard.jsp)
- **HTTP Method:** GET only (POST redirects to GET)

### HTTP Methods Supported
- **GET:** Retrieve dashboard data and display page
- **POST:** Redirects to GET (no POST handling)

---

## SECTION 2 — DATA DISPLAYED

### Data Item 1: Total Resources Count
- **Display:** "Total Resources" stat card on dashboard
- **Value:** System-wide count of all resources
- **Read-only / Editable:** READ-ONLY
- **Backend source:** 
  - Servlet: `DashboardServlet.gatherDashboardStats()`
  - Service: `ResourceService.getResourceCount()`
  - DAO: `ResourceDAO.count()`
- **Database query:** `SELECT COUNT(*) FROM resources`
- **Requester can modify:** NO

### Data Item 2: Active Requests Count
- **Display:** "Active Requests" stat card on dashboard
- **Value:** Count of requests with status NOT COMPLETED and NOT CANCELLED
- **Read-only / Editable:** READ-ONLY
- **Backend source:**
  - Servlet: `DashboardServlet.gatherDashboardStats()`
  - Service: `RequestService.getActiveRequestCount()`
  - DAO: `RequestDAO.findAll()` (filtered in service)
- **Calculation:** Streams all requests, filters by status
- **Requester can modify:** NO

### Data Item 3: Total Volunteers Count
- **Display:** "Volunteers" stat card on dashboard
- **Value:** System-wide count of users with role = VOLUNTEER
- **Read-only / Editable:** READ-ONLY
- **Backend source:**
  - Servlet: `DashboardServlet.gatherDashboardStats()`
  - Service: `UserService.getVolunteerCount()`
  - DAO: `UserDAO.findByRole(UserRole.VOLUNTEER)`
- **Requester can modify:** NO

### Data Item 4: Completed Requests Count
- **Display:** "Completed Requests" stat card on dashboard
- **Value:** Count of requests with status = COMPLETED
- **Read-only / Editable:** READ-ONLY
- **Backend source:**
  - Servlet: `DashboardServlet.gatherDashboardStats()`
  - Service: `RequestService.getCompletedRequestCount()`
  - DAO: `RequestDAO.findByField("status", "COMPLETED")`
- **Requester can modify:** NO

### Data Item 5: User Identity
- **Display:** "Welcome, [username] ([role])" in header
- **Value:** Current logged-in user's username and role
- **Read-only / Editable:** READ-ONLY
- **Backend source:** `sessionScope.user` (from session)
- **Requester can modify:** NO

### Data Item 6: Recent Activity
- **Display:** "Recent Activity" section on dashboard
- **Value:** EMPTY (no data populated)
- **Read-only / Editable:** READ-ONLY (if data existed)
- **Backend source:** `request.setAttribute("recentActivity", Collections.emptyList())`
- **Status:** NOT IMPLEMENTED - always displays empty list
- **Requester can modify:** NO

### Data Item 7: Requester's Own Requests
- **Display:** Table on requests.jsp page
- **Value:** List of requests where requester_id = current user's ID
- **Read-only / Editable:** READ-ONLY (display only)
- **Backend source:**
  - Servlet: `RequestServlet.doGet()`
  - Service: `RequestService.getRequestsByUser(userId)`
  - DAO: `RequestDAO.findByField("requester_id", userId)`
- **Visible Fields:** Request ID, Resource ID, Description, Status, Urgency Level, Created Date
- **Editable Fields:** NONE (display only for requesters)
- **Requester can modify:** NO

### Data Item 8: Request Status
- **Display:** Status badge on each request row (color-coded)
- **Value:** Current status of request (PENDING, ASSIGNED, IN_PROGRESS, COMPLETED, CANCELLED)
- **Read-only / Editable:** READ-ONLY
- **Backend source:** `Request.getStatus()` from database
- **Requester can modify:** NO

### Data Item 9: Request Urgency Level
- **Display:** Urgency badge on each request row (color-coded)
- **Value:** Urgency level (LOW, MEDIUM, HIGH, CRITICAL)
- **Read-only / Editable:** READ-ONLY
- **Backend source:** `Request.getUrgencyLevel()` from database
- **Requester can modify:** NO

### Data Item 10: Assigned Volunteer
- **Display:** Embedded in request description or notes (if any)
- **Value:** Volunteer ID (if assigned) or null (if unassigned)
- **Read-only / Editable:** READ-ONLY
- **Backend source:** `Request.getVolunteerId()` from database
- **Requester can modify:** NO

---

## SECTION 3 — REQUESTER ACTIONS

### Action 1: Create New Request
**Action Name:** Create New Request
**Trigger:** Click "Create Request" button on dashboard OR "New Request" button on requests page
**UI Location:** Dashboard quick actions section OR requests page header
**Backend Handling:**
- Servlet: `RequestServlet.doGet(action=new)` → displays form
- Servlet: `RequestServlet.doPost(action=create)` → processes submission
- Service: `RequestService.createRequest(request)`
- DAO: `RequestDAO.create(request)`
**Database Update:** YES - inserts new row into requests table
**Persistence:** Request persisted to database with:
- requestId (UUID)
- requester_id (current user's ID)
- resourceId (selected resource)
- description (user-provided text)
- urgencyLevel (user-selected level)
- status (defaults to PENDING)
- createdAt (current timestamp)
- updatedAt (current timestamp)
**Form Fields:**
- Resource selection (dropdown with search)
- Description (textarea, 1-500 characters)
- Urgency level (dropdown: LOW, MEDIUM, HIGH, CRITICAL)
**Validation:**
- Client-side: JavaScript validateForm() checks all fields
- Server-side: ValidationUtils validates required fields and length
**Error Handling:** Displays error message if validation fails, returns to form
**Success Handling:** Redirects to requests page with success message

### Action 2: View Own Requests
**Action Name:** View Own Requests
**Trigger:** Click "Requests" in navigation menu OR click "View Available Requests" on dashboard
**Backend Handling:**
- Servlet: `RequestServlet.doGet()` (no action parameter)
- Service: `RequestService.getRequestsByUser(userId)`
- DAO: `RequestDAO.findByField("requester_id", userId)`
**Database Update:** NO (read-only operation)
**Persistence:** N/A
**Data Returned:** List of Request objects where requester_id = current user's ID
**Visible Fields:** Request ID, Resource ID, Description, Status, Urgency Level, Created Date
**Editable Fields:** NONE (display only for requesters)

### Action 3: Filter Own Requests
**Action Name:** Filter Requests by Status or Urgency
**Trigger:** Change dropdown selection in filter section
**Backend Handling:** JavaScript function `filterRequests()` (client-side only)
**Database Update:** NO
**Persistence:** N/A
**Implementation:** Client-side filtering of displayed requests
**Scope:** Filters by status (PENDING, ASSIGNED, IN_PROGRESS, COMPLETED, CANCELLED) or urgency (LOW, MEDIUM, HIGH, CRITICAL)
**Behavior:** Hides/shows table rows based on filter criteria

### Action 4: View Resources
**Action Name:** View All Resources
**Trigger:** Click "Resources" in navigation menu
**Backend Handling:**
- Servlet: `ResourceServlet.doGet()`
- Service: `ResourceService.getAllResources()`
- DAO: `ResourceDAO.findAll()`
**Database Update:** NO (read-only operation)
**Persistence:** N/A
**Data Returned:** List of all Resource objects in system
**Visible Fields:** Resource name, description, category, quantity, location, contact info
**Editable Fields:** NONE (display only for requesters)
**Search Capability:** YES - JavaScript-based client-side search on resource name/description

### Action 5: Navigate Dashboard
**Action Name:** Navigate Between Pages
**Trigger:** Click navigation links (Dashboard, Resources, Requests)
**Backend Handling:** HTTP GET requests to respective servlets
**Database Update:** NO
**Persistence:** N/A
**Navigation Options for Requesters:**
- Dashboard (always visible)
- Resources (always visible)
- Requests (always visible)
- Admin Panel (NOT visible - hidden by role check)

### Action 6: Logout
**Action Name:** Logout
**Trigger:** Click "Logout" button in header
**Backend Handling:**
- Servlet: `LogoutServlet.doGet()` or `LogoutServlet.doPost()`
- Session: Invalidates current session
**Database Update:** NO
**Persistence:** Session invalidated, user redirected to login
**Result:** User logged out, session destroyed

---

## SECTION 4 — EVENT HANDLING

### Events Implemented

#### Event 1: Page Load (Dashboard)
- **Trigger:** GET request to `/dashboard`
- **Handler:** `DashboardServlet.doGet()`
- **Action:** Gathers statistics, forwards to JSP
- **Error Handling:** Catches DatabaseException, displays error message

#### Event 2: Page Load (Requests)
- **Trigger:** GET request to `/requests`
- **Handler:** `RequestServlet.doGet()`
- **Action:** Loads requests based on user role (requesters see only their own)
- **Error Handling:** Catches DatabaseException, displays error message

#### Event 3: Page Load (New Request Form)
- **Trigger:** GET request to `/requests?action=new`
- **Handler:** `RequestServlet.doGet(action=new)`
- **Action:** Loads all resources and displays form
- **Error Handling:** Catches DatabaseException, displays error message

#### Event 4: Form Submission (Create Request)
- **Trigger:** POST request to `/requests` with action=create
- **Handler:** `RequestServlet.doPost(action=create)`
- **Action:** Validates input, creates request, redirects to requests page
- **Error Handling:** Displays validation errors, returns to form

#### Event 5: Filter Requests (Client-Side)
- **Trigger:** Change dropdown selection in filter section
- **Handler:** JavaScript `filterRequests()` function
- **Action:** Filters displayed requests based on status/urgency
- **Error Handling:** None (client-side only)

#### Event 6: Navigation Click
- **Trigger:** Click on navigation menu items
- **Handler:** Browser navigation to new URL
- **Action:** Loads new page
- **Error Handling:** Standard HTTP error handling

#### Event 7: Logout Click
- **Trigger:** Click "Logout" button
- **Handler:** `LogoutServlet`
- **Action:** Invalidates session, redirects to login
- **Error Handling:** Standard HTTP error handling

### Events NOT Implemented

- **AJAX requests:** NOT IMPLEMENTED - all navigation is full page reload
- **Real-time updates:** NOT IMPLEMENTED - no WebSocket or polling
- **Request editing:** NOT IMPLEMENTED - requesters cannot edit submitted requests
- **Request cancellation:** NOT IMPLEMENTED - requesters cannot cancel requests
- **Drag-and-drop:** NOT IMPLEMENTED
- **Modal dialogs:** NOT IMPLEMENTED for requester actions
- **Inline editing:** NOT IMPLEMENTED
- **Bulk operations:** NOT IMPLEMENTED
- **Request search:** NOT IMPLEMENTED - only filter by status/urgency

---

## SECTION 5 — VALIDATION & ERROR HANDLING

### Validation Present

#### Validation 1: Session Validation
- **Where:** All servlets (DashboardServlet, RequestServlet, ResourceServlet)
- **Check:** `session == null || session.getAttribute("user") == null`
- **Action:** Redirects to login if session invalid
- **Code Location:** All servlet doGet() methods

#### Validation 2: Role-Based Access
- **Where:** Dashboard JSP, Resources JSP, Requests JSP
- **Check:** `${sessionScope.user.role == 'REQUESTER'}`
- **Action:** Hides admin-only features (Admin Panel link, Add Resource button)
- **Code Location:** JSP conditional tags `<c:if>`

#### Validation 3: User Authentication
- **Where:** LoginServlet
- **Check:** Username/password verification
- **Action:** Allows login only with valid credentials
- **Code Location:** `LoginServlet.doPost()`

#### Validation 4: Request Input Validation
- **Where:** RequestServlet.doPost(action=create)
- **Checks:**
  - Resource ID is required
  - Description is required and 1-500 characters
  - Urgency level is required
- **Action:** Displays error message if validation fails
- **Code Location:** `RequestServlet.validateRequestInput()`

#### Validation 5: Client-Side Form Validation
- **Where:** new-request.jsp
- **Check:** JavaScript `validateForm()` function
- **Validates:**
  - Resource selected
  - Description not empty
  - Description ≤ 500 characters
  - Urgency level selected
- **Action:** Shows alert and prevents form submission if invalid

### Error Handling Present

#### Error 1: Database Connection Failure
- **Catch:** `DatabaseException`
- **Display:** "Unable to load [resource type]" message
- **Logging:** Logs to logger at SEVERE level
- **Recovery:** Displays error page or error message in JSP
- **Code Location:** All servlet try-catch blocks

#### Error 2: Session Timeout
- **Catch:** `session == null`
- **Display:** Redirects to login page
- **Logging:** No explicit logging
- **Recovery:** User must log in again
- **Code Location:** All servlet doGet() methods

#### Error 3: Unexpected Errors
- **Catch:** Generic `Exception`
- **Display:** HTTP 500 error page
- **Logging:** Logs to logger at SEVERE level
- **Recovery:** User sees error page
- **Code Location:** All servlet try-catch blocks

#### Error 4: Validation Errors
- **Catch:** `InvalidInputException`
- **Display:** Error message displayed on form
- **Logging:** Logs to logger at WARNING level
- **Recovery:** User returns to form to correct input
- **Code Location:** `RequestServlet.validateRequestInput()`

### Validation Gaps

- **Input validation:** NOT IMPLEMENTED for search queries (client-side only)
- **XSS prevention:** NOT EXPLICITLY IMPLEMENTED in requester-facing pages
- **CSRF protection:** NOT IMPLEMENTED
- **Rate limiting:** NOT IMPLEMENTED
- **Data sanitization:** NOT IMPLEMENTED for display values (except in create request)
- **Request ID validation:** NOT IMPLEMENTED - no check if requester owns the request they're viewing

---

## SECTION 6 — SECURITY & SESSION

### Session Usage

#### Session Attribute 1: User Object
- **Attribute Name:** `sessionScope.user`
- **Type:** `User` object (polymorphic - Admin, Volunteer, or Requester)
- **Set By:** `LoginServlet.doPost()` after successful authentication
- **Used By:** All servlets and JSPs for role-based access control
- **Lifetime:** Session duration (30 minutes default)
- **Cleared By:** `LogoutServlet` via `session.invalidate()`

#### Session Attribute 2: Success Messages
- **Attribute Name:** `success`
- **Type:** String
- **Set By:** Various servlets after successful operations
- **Used By:** JSPs to display success notifications
- **Lifetime:** Single page load (cleared after display)

#### Session Attribute 3: Error Messages
- **Attribute Name:** `error`
- **Type:** String
- **Set By:** Various servlets on error conditions
- **Used By:** JSPs to display error notifications
- **Lifetime:** Single page load (cleared after display)

### Authorization Checks

#### Check 1: Session Existence
- **Location:** All servlet doGet() methods
- **Code:** `if (session == null || session.getAttribute("user") == null)`
- **Action:** Redirects to login
- **Enforced:** YES

#### Check 2: Role-Based UI Hiding
- **Location:** JSP pages (dashboard.jsp, resources.jsp, requests.jsp)
- **Code:** `<c:if test="${sessionScope.user.role == 'REQUESTER'}">`
- **Action:** Hides admin-only UI elements
- **Enforced:** YES (UI level only)
- **Backend Enforcement:** PARTIAL (some servlets check role, some don't)

#### Check 3: Requester Request Filtering
- **Location:** RequestServlet.doGet()
- **Code:** `if (currentUser.getRole().toString().equals("REQUESTER"))`
- **Action:** Returns only requests created by requester
- **Enforced:** YES

### Security Gaps

- **No CSRF tokens:** NOT IMPLEMENTED
- **No input sanitization:** NOT IMPLEMENTED for display
- **No rate limiting:** NOT IMPLEMENTED
- **No audit logging:** NOT IMPLEMENTED for requester actions
- **No encryption:** Session data not encrypted (standard HTTP)
- **No two-factor authentication:** NOT IMPLEMENTED
- **No request ownership validation:** Requesters could potentially view/modify other users' requests if they knew the request ID

---

## SECTION 7 — CURRENT LIMITATIONS

### Limitation 1: Read-Only Request Viewing
**Description:** Requesters can only view their requests, cannot modify them after creation
**Impact:** Requesters cannot update request details, urgency, or description
**Scope:** Requests page
**Status:** BY DESIGN
**Evidence:** No edit buttons or forms for requesters on requests.jsp

### Limitation 2: No Request Cancellation
**Description:** Requesters cannot cancel or withdraw submitted requests
**Impact:** Requesters cannot remove requests they no longer need
**Scope:** Requests page
**Status:** NOT IMPLEMENTED
**Evidence:** No cancel/delete buttons for requesters

### Limitation 3: No Request Status Update
**Description:** Requesters cannot update request status
**Impact:** Requesters cannot mark requests as completed or in-progress
**Scope:** Requests page
**Status:** NOT IMPLEMENTED
**Evidence:** Status dropdowns only visible to ADMIN role

### Limitation 4: No Volunteer Assignment Visibility
**Description:** Requesters cannot see which volunteer is assigned to their request
**Impact:** Requesters cannot contact or track assigned volunteer
**Scope:** Requests page
**Status:** NOT IMPLEMENTED
**Evidence:** Volunteer ID not displayed in request table for requesters

### Limitation 5: No Request Notes or Comments
**Description:** Requesters cannot add notes or comments to their requests
**Impact:** No communication channel for requesters to provide updates
**Scope:** Requests page
**Status:** NOT IMPLEMENTED
**Evidence:** No comment/note fields in requests.jsp for requesters

### Limitation 6: No Activity History
**Description:** Recent Activity section is always empty
**Impact:** Requesters cannot see their own activity or system activity
**Scope:** Dashboard
**Status:** NOT IMPLEMENTED
**Evidence:** `Collections.emptyList()` hardcoded in DashboardServlet

### Limitation 7: No Feedback Submission
**Description:** Requesters cannot submit feedback or ratings on completed requests
**Impact:** No mechanism for requesters to provide feedback on volunteer work
**Scope:** Entire application
**Status:** NOT IMPLEMENTED
**Evidence:** No feedback form or FeedbackServlet integration for requesters

### Limitation 8: No Request Search
**Description:** Requesters cannot search their requests by keyword
**Impact:** Difficult to find specific requests if many are submitted
**Scope:** Requests page
**Status:** NOT IMPLEMENTED
**Evidence:** Only filter by status/urgency, not by description or resource

### Limitation 9: No Request Tracking Timeline
**Description:** Requesters cannot see timeline of request status changes
**Impact:** No visibility into when request was assigned, started, etc.
**Scope:** Requests page
**Status:** NOT IMPLEMENTED
**Evidence:** No timeline or history view

### Limitation 10: No Notification System
**Description:** Requesters are not notified when request is assigned or completed
**Impact:** Requesters must manually check dashboard for updates
**Scope:** Entire application
**Status:** NOT IMPLEMENTED
**Evidence:** No email/notification service integrated

### Limitation 11: No Request Urgency Modification
**Description:** Requesters cannot change urgency level after request creation
**Impact:** Requesters cannot escalate urgent requests
**Scope:** Requests page
**Status:** NOT IMPLEMENTED
**Evidence:** Urgency dropdowns only visible to ADMIN role

### Limitation 12: No Bulk Operations
**Description:** Requesters cannot perform bulk actions on multiple requests
**Impact:** Cannot cancel multiple requests at once
**Scope:** Requests page
**Status:** NOT IMPLEMENTED
**Evidence:** No checkboxes or bulk action buttons

---

## SECTION 8 — DATA FLOW SUMMARY

### Requester Dashboard Data Flow
```
Requester Login
    ↓
Session Created (user object stored)
    ↓
GET /dashboard
    ↓
DashboardServlet.doGet()
    ↓
gatherDashboardStats(currentUser)
    ├→ ResourceService.getResourceCount()
    ├→ RequestService.getActiveRequestCount()
    ├→ UserService.getVolunteerCount()
    └→ RequestService.getCompletedRequestCount()
    ↓
Forward to dashboard.jsp
    ↓
Display stats (read-only)
```

### Requester Create Request Data Flow
```
GET /requests?action=new
    ↓
RequestServlet.doGet(action=new)
    ↓
ResourceService.getAllResources()
    ↓
ResourceDAO.findAll()
    ↓
Return List<Resource>
    ↓
Forward to new-request.jsp
    ↓
Display form with resource dropdown
    ↓
User fills form and submits
    ↓
POST /requests (action=create)
    ↓
RequestServlet.doPost(action=create)
    ↓
Validate input
    ↓
Sanitize description
    ↓
Create Request object
    ↓
RequestService.createRequest(request)
    ↓
RequestDAO.create(request)
    ↓
Insert into database
    ↓
Redirect to /requests
    ↓
Display success message
```

### Requester View Own Requests Data Flow
```
GET /requests
    ↓
RequestServlet.doGet()
    ↓
Check role: if REQUESTER
    ↓
RequestService.getRequestsByUser(userId)
    ↓
RequestDAO.findByField("requester_id", userId)
    ↓
Return List<Request>
    ↓
Forward to requests.jsp
    ↓
Display requests (read-only for requesters)
```

---

## SECTION 9 — ROLE-BASED FEATURE COMPARISON

| Feature | Admin | Volunteer | Requester |
|---------|-------|-----------|-----------|
| View Dashboard | YES | YES | YES |
| View Resources | YES | YES | YES |
| Add Resources | YES | NO | NO |
| Edit Resources | YES | NO | NO |
| Delete Resources | YES | NO | NO |
| View All Requests | YES | NO | NO |
| View Own Requests | YES | NO | YES |
| View Assigned Requests | YES | YES | NO |
| Create Requests | NO | NO | YES |
| Edit Own Requests | NO | NO | NO |
| Cancel Own Requests | NO | NO | NO |
| Assign Volunteers | YES | NO | NO |
| Update Request Status | YES | YES (own) | NO |
| Change Request Urgency | YES | NO | NO |
| Access Admin Panel | YES | NO | NO |
| Manage Users | YES | NO | NO |
| View Feedback | YES | NO | NO |
| Submit Feedback | NO | NO | NO |
| Accept/Reject Assignments | NO | YES | NO |
| Add Progress Notes | NO | YES | NO |

---

## SECTION 10 — CONCLUSION

### Current State Summary
The Requester Dashboard is a **LIMITED INTERFACE** that allows requesters to:
- Create new resource requests
- View their own submitted requests
- View available resources
- Filter requests by status or urgency
- Navigate between pages
- Logout

### What Requesters CAN Do
- Create requests with resource, description, and urgency level
- View all their submitted requests
- View request status and urgency
- Filter requests by status or urgency
- View available resources
- Search resources by name/description
- Navigate between dashboard, resources, and requests pages
- Logout

### What Requesters CANNOT Do
- Modify requests after creation
- Cancel or withdraw requests
- Update request status
- See assigned volunteer details
- Add notes or comments to requests
- Submit feedback on completed requests
- Search requests by keyword
- View request timeline or history
- Receive notifications
- Change request urgency
- Perform bulk operations

### Baseline Assessment
The Requester Dashboard is **FUNCTIONAL but LIMITED**. It serves as a request submission and viewing portal but does not provide interactive capabilities for requesters to actively manage their requests after creation or track progress in detail.

---

**Audit Completed:** December 19, 2025
**Auditor:** Kiro IDE (Strict Factual Reporting)
**Status:** READY FOR ENHANCEMENT
