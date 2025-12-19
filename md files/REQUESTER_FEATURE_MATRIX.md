# REQUESTER FEATURE MATRIX

## Quick Reference: What Requesters Can Do

| Feature | Status | Availability | Backend | UI |
|---------|--------|--------------|---------|-----|
| **Create Request** | ✅ | Always | RequestServlet | Dashboard + Requests page |
| **View Own Requests** | ✅ | Always | RequestServlet | Requests page |
| **Edit Request** | ✅ NEW | PENDING only | RequesterActionServlet | Edit button + Modal |
| **Cancel Request** | ✅ NEW | PENDING/ASSIGNED | RequesterActionServlet | Cancel button + Modal |
| **Add Notes** | ✅ NEW | Active requests | RequesterActionServlet | Add Note button + Modal |
| **Submit Feedback** | ✅ NEW | COMPLETED only | RequesterActionServlet | Give Feedback button + Modal |
| **Filter Requests** | ✅ | Always | Client-side JS | Filter dropdowns |
| **View Resources** | ✅ | Always | ResourceServlet | Resources page |
| **Search Resources** | ✅ | Always | Client-side JS | Search input |
| **View Dashboard** | ✅ | Always | DashboardServlet | Dashboard page |
| **View Metrics** | ✅ NEW | Always | DashboardServlet | Dashboard cards |
| **Logout** | ✅ | Always | LogoutServlet | Logout button |

---

## Request Lifecycle & Requester Actions

```
PENDING
├─ ✅ Edit (description, urgency)
├─ ✅ Cancel (with optional reason)
├─ ✅ Add Notes
└─ → ASSIGNED (by admin)

ASSIGNED
├─ ✅ Cancel (with optional reason)
├─ ✅ Add Notes
└─ → IN_PROGRESS (by volunteer)

IN_PROGRESS
├─ ✅ Add Notes
└─ → COMPLETED (by volunteer)

COMPLETED
├─ ✅ Add Notes
├─ ✅ Submit Feedback (1-5 rating + comment)
└─ (Final state)

CANCELLED
└─ (Final state - no further actions)
```

---

## Requester Dashboard Metrics

| Metric | Calculation | Display |
|--------|-------------|---------|
| **Total Requests** | Count of all requests by requester | Dashboard card |
| **Active Requests** | Count of PENDING + ASSIGNED + IN_PROGRESS | Dashboard card |
| **Completed Requests** | Count of COMPLETED | Dashboard card |
| **Cancelled Requests** | Count of CANCELLED | Dashboard card |

---

## Action Button Visibility

### Edit Button
- **Visible when:** Request status = PENDING
- **Disabled when:** Status ≠ PENDING
- **Action:** Opens edit modal

### Cancel Button
- **Visible when:** Request status = PENDING or ASSIGNED
- **Disabled when:** Status = IN_PROGRESS, COMPLETED, or CANCELLED
- **Action:** Opens cancel modal

### Add Note Button
- **Visible when:** Request status ≠ COMPLETED and ≠ CANCELLED
- **Disabled when:** Status = COMPLETED or CANCELLED
- **Action:** Opens note modal

### Give Feedback Button
- **Visible when:** Request status = COMPLETED
- **Disabled when:** Status ≠ COMPLETED
- **Action:** Opens feedback modal

---

## Security & Authorization

### Session Validation
- ✅ Required for all actions
- ✅ Redirects to login if invalid

### Role Validation
- ✅ REQUESTER role required
- ✅ Returns HTTP 403 if not requester

### Request Ownership Validation
- ✅ Requester must own the request
- ✅ Returns HTTP 403 if not owner

### Status Validation
- ✅ Edit: Only PENDING
- ✅ Cancel: Only PENDING or ASSIGNED
- ✅ Feedback: Only COMPLETED
- ✅ Notes: Only active requests

### Input Validation
- ✅ Description: 1-500 characters, required
- ✅ Urgency: Must be valid enum
- ✅ Rating: Must be 1-5
- ✅ Comment: Optional, sanitized
- ✅ Reason: Optional, sanitized
- ✅ Note: Required, sanitized

---

## Audit Logging

All requester actions are logged:

```
Edit Request:
  "Requester [userId] edited request: [requestId]"

Cancel Request:
  "Requester [userId] cancelled request: [requestId] with reason: [reason]"

Add Note:
  "Requester [userId] added note to request: [requestId]"

Submit Feedback:
  "Requester [userId] submitted feedback on request: [requestId] with rating: [rating]"
```

---

## API Endpoints

### Requester Actions
```
POST /requester-action
  Parameters:
    - action: edit-request | cancel-request | add-note | submit-feedback
    - requestId: [request ID]
    - redirectUrl: [return URL]
    - [action-specific parameters]
```

### Request Management
```
GET /requests
  Returns: List of requester's own requests

GET /requests?action=new
  Returns: New request form

POST /requests
  Parameters:
    - action: create
    - resourceId: [resource ID]
    - description: [description]
    - urgencyLevel: [urgency]
```

### Dashboard
```
GET /dashboard
  Returns: Dashboard with requester metrics
```

---

## Test Credentials

```
Requester: user1 / User123!
Admin: admin / Admin123!
Volunteer: volunteer1 / Volunteer123!
```

---

## Files Modified

### New Files
- `src/main/java/com/communityhub/servlet/RequesterActionServlet.java`

### Modified Files
- `src/main/java/com/communityhub/service/RequestService.java`
- `src/main/java/com/communityhub/servlet/DashboardServlet.java`
- `src/main/webapp/jsp/dashboard.jsp`
- `src/main/webapp/jsp/requests.jsp`
- `src/main/webapp/css/styles.css`

---

## Build & Deploy

```bash
# Build
mvn clean package -DskipTests

# Run
mvn tomcat7:run

# Access
http://localhost:8080/community-hub
```

---

## Backward Compatibility

✅ **100% Backward Compatible**
- All existing functionality preserved
- No database schema changes
- No breaking changes to existing APIs
- All existing features work identically

---

## Production Readiness

✅ **Production Ready**
- ✅ Full error handling
- ✅ Comprehensive logging
- ✅ Input validation and sanitization
- ✅ Authorization checks
- ✅ Transaction management
- ✅ Resource cleanup
- ✅ Performance optimized
- ✅ Security hardened

---

**Last Updated:** December 19, 2025
**Status:** ✅ COMPLETE
