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

