# 🗄️ Database Documentation

## Table of Contents
- [Overview](#overview)
- [Database Schema](#database-schema)
- [Entity Relationship Diagram](#entity-relationship-diagram)
- [Table Definitions](#table-definitions)
- [Relationships](#relationships)
- [Indexes](#indexes)
- [Sample Queries](#sample-queries)
- [Data Migration](#data-migration)

---

## Overview

ResoMap uses a relational database to store all application data. The system supports both **SQLite** (for development) and **MySQL** (for production).

### Database Configuration

**SQLite (Default)**
- File: `community_hub.db`
- Location: Project root directory
- No configuration required

**MySQL (Production)**
- Host: `localhost:3306`
- Database: `community_hub`
- Configuration: `src/main/java/com/communityhub/util/DBConnection.java`

---

## Database Schema

### Schema Diagram (ASCII)

```
┌─────────────────────────────────────────────────────────────────────┐
│                         USERS TABLE                                  │
├─────────────────────────────────────────────────────────────────────┤
│ PK  user_id          TEXT                                           │
│     username         TEXT    UNIQUE NOT NULL                        │
│     email            TEXT    UNIQUE NOT NULL                        │
│     password_hash    TEXT    NOT NULL                               │
│     role             TEXT    NOT NULL (ADMIN/VOLUNTEER/REQUESTER)   │
│     created_at       DATETIME DEFAULT CURRENT_TIMESTAMP             │
│     updated_at       DATETIME DEFAULT CURRENT_TIMESTAMP             │
└─────────────────────────────────────────────────────────────────────┘
                                    │
                                    │ 1:N
                                    ▼
┌─────────────────────────────────────────────────────────────────────┐
│                       RESOURCES TABLE                                │
├─────────────────────────────────────────────────────────────────────┤
│ PK  resource_id      TEXT                                           │
│     name             TEXT    NOT NULL                               │
│     description      TEXT                                           │
│     category         TEXT    NOT NULL                               │
│     quantity         INTEGER DEFAULT 0                              │
│     location         TEXT                                           │
│     contact_info     TEXT                                           │
│ FK  created_by       TEXT    → users(user_id)                       │
│     created_at       DATETIME DEFAULT CURRENT_TIMESTAMP             │
│     updated_at       DATETIME DEFAULT CURRENT_TIMESTAMP             │
└─────────────────────────────────────────────────────────────────────┘
                                    │
                                    │ 1:N
                                    ▼
┌─────────────────────────────────────────────────────────────────────┐
│                        REQUESTS TABLE                                │
├─────────────────────────────────────────────────────────────────────┤
│ PK  request_id       TEXT                                           │
│ FK  requester_id     TEXT    NOT NULL → users(user_id)              │
│ FK  resource_id      TEXT    NOT NULL → resources(resource_id)      │
│ FK  volunteer_id     TEXT    → users(user_id)                       │
│     status           TEXT    DEFAULT 'PENDING'                      │
│                              (PENDING/ASSIGNED/IN_PROGRESS/          │
│                               COMPLETED/CANCELLED)                   │
│     description      TEXT                                           │
│     urgency_level    TEXT    DEFAULT 'MEDIUM'                       │
│                              (LOW/MEDIUM/HIGH/CRITICAL)              │
│     created_at       DATETIME DEFAULT CURRENT_TIMESTAMP             │
│     updated_at       DATETIME DEFAULT CURRENT_TIMESTAMP             │
└─────────────────────────────────────────────────────────────────────┘
                                    │
                                    │ 1:N
                                    ▼
┌─────────────────────────────────────────────────────────────────────┐
│                        FEEDBACK TABLE                                │
├─────────────────────────────────────────────────────────────────────┤
│ PK  feedback_id      TEXT                                           │
│ FK  user_id          TEXT    NOT NULL → users(user_id)              │
│ FK  request_id       TEXT    → requests(request_id)                 │
│     rating           INTEGER CHECK (rating >= 1 AND rating <= 5)    │
│     comments         TEXT                                           │
│     feedback_type    TEXT    DEFAULT 'GENERAL'                      │
│                              (GENERAL/REQUEST_SPECIFIC/              │
│                               SYSTEM_IMPROVEMENT)                    │
│     created_at       DATETIME DEFAULT CURRENT_TIMESTAMP             │
└─────────────────────────────────────────────────────────────────────┘
```

---

## Entity Relationship Diagram

### Visual ER Diagram

```
                    ┌──────────────┐
                    │    USERS     │
                    ├──────────────┤
                    │ user_id (PK) │
                    │ username     │
                    │ email        │
                    │ password_hash│
                    │ role         │
                    └──────┬───────┘
                           │
                ┌──────────┼──────────┐
                │          │          │
                │ creates  │ requests │ volunteers
                │          │          │
                ▼          ▼          ▼
    ┌──────────────┐  ┌──────────────┐
    │  RESOURCES   │  │   REQUESTS   │
    ├──────────────┤  ├──────────────┤
    │resource_id(PK)│  │request_id(PK)│
    │ name         │  │requester_id  │◄─┐
    │ category     │  │resource_id   │  │
    │ quantity     │  │volunteer_id  │  │
    │ created_by(FK)│  │ status       │  │
    └──────┬───────┘  │urgency_level │  │
           │          └──────┬───────┘  │
           │                 │          │
           │ referenced by   │ has      │
           │                 │          │
           └─────────────────┘          │
                                        │
                                        ▼
                                ┌──────────────┐
                                │   FEEDBACK   │
                                ├──────────────┤
                                │feedback_id(PK)│
                                │ user_id (FK) │
                                │request_id(FK)│
                                │ rating       │
                                │ comments     │
                                └──────────────┘
```

### Relationship Types

| Relationship | Type | Description |
|-------------|------|-------------|
| User → Resource | 1:N | One user can create many resources |
| User → Request (Requester) | 1:N | One user can make many requests |
| User → Request (Volunteer) | 1:N | One volunteer can handle many requests |
| Resource → Request | 1:N | One resource can have many requests |
| Request → Feedback | 1:N | One request can have multiple feedback entries |
| User → Feedback | 1:N | One user can provide multiple feedback entries |

---

## Table Definitions

### 1. USERS Table

Stores user account information and authentication data.

```sql
CREATE TABLE IF NOT EXISTS users (
    user_id TEXT PRIMARY KEY,
    username TEXT UNIQUE NOT NULL,
    email TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    role TEXT NOT NULL CHECK (role IN ('ADMIN', 'VOLUNTEER', 'REQUESTER')),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

**Columns:**

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| user_id | TEXT | PRIMARY KEY | Unique identifier (UUID) |
| username | TEXT | UNIQUE, NOT NULL | User's login name |
| email | TEXT | UNIQUE, NOT NULL | User's email address |
| password_hash | TEXT | NOT NULL | Salted SHA-256 hash (format: salt:hash) |
| role | TEXT | NOT NULL, CHECK | User role (ADMIN/VOLUNTEER/REQUESTER) |
| created_at | DATETIME | DEFAULT NOW | Account creation timestamp |
| updated_at | DATETIME | DEFAULT NOW | Last update timestamp |

**Sample Data:**
```sql
INSERT INTO users VALUES (
    '7e2eda41-45cd-4da2-ab0d-6afbe1537d32',
    'admin',
    'admin@resomap.com',
    'aBcD1234:dGVzdGhhc2g=...',
    'ADMIN',
    '2024-01-15 10:30:00',
    '2024-01-15 10:30:00'
);
```

---

### 2. RESOURCES Table

Stores information about available community resources.

```sql
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
```

**Columns:**

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| resource_id | TEXT | PRIMARY KEY | Unique identifier (UUID) |
| name | TEXT | NOT NULL | Resource name |
| description | TEXT | - | Detailed description |
| category | TEXT | NOT NULL | Resource category |
| quantity | INTEGER | DEFAULT 0 | Available quantity |
| location | TEXT | - | Storage/pickup location |
| contact_info | TEXT | - | Contact information |
| created_by | TEXT | FOREIGN KEY | User who added the resource |
| created_at | DATETIME | DEFAULT NOW | Creation timestamp |
| updated_at | DATETIME | DEFAULT NOW | Last update timestamp |

**Categories:**
- Food & Groceries
- Medical Supplies
- Clothing & Textiles
- Tools & Equipment
- Educational Resources
- Technology
- Shelter & Housing
- Transportation
- Other

**Sample Data:**
```sql
INSERT INTO resources VALUES (
    '13006706-e5c4-4c60-b5ba-994d51fcd910',
    'Canned Beans',
    'Protein-rich canned beans',
    'Food & Groceries',
    50,
    'Community Center - Storage',
    'contact@resomap.com',
    '7e2eda41-45cd-4da2-ab0d-6afbe1537d32',
    '2024-01-15 11:00:00',
    '2024-01-15 11:00:00'
);
```

---

### 3. REQUESTS Table

Stores resource requests from community members.

```sql
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
```

**Columns:**

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| request_id | TEXT | PRIMARY KEY | Unique identifier (UUID) |
| requester_id | TEXT | NOT NULL, FK | User making the request |
| resource_id | TEXT | NOT NULL, FK | Requested resource |
| volunteer_id | TEXT | FK | Assigned volunteer (nullable) |
| status | TEXT | CHECK | Request status |
| description | TEXT | - | Request details |
| urgency_level | TEXT | CHECK | Priority level |
| created_at | DATETIME | DEFAULT NOW | Request creation time |
| updated_at | DATETIME | DEFAULT NOW | Last status update |

**Status Values:**
- **PENDING**: Awaiting volunteer assignment
- **ASSIGNED**: Volunteer assigned, not started
- **IN_PROGRESS**: Volunteer actively fulfilling request
- **COMPLETED**: Request successfully fulfilled
- **CANCELLED**: Request cancelled by requester or admin

**Urgency Levels:**
- **LOW**: Can wait, no immediate need
- **MEDIUM**: Needed within a few days
- **HIGH**: Urgent, needed within 24 hours
- **CRITICAL**: Emergency, immediate attention required

**Sample Data:**
```sql
INSERT INTO requests VALUES (
    '0f7fad6b-633d-46fd-8dff-eb3c0872dc38',
    '7e2eda41-45cd-4da2-ab0d-6afbe1537d32',
    '13006706-e5c4-4c60-b5ba-994d51fcd910',
    NULL,
    'PENDING',
    'Need food supplies for family of 4',
    'HIGH',
    '2024-01-16 09:00:00',
    '2024-01-16 09:00:00'
);
```

---

### 4. FEEDBACK Table

Stores user feedback and ratings.

```sql
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
```

**Columns:**

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| feedback_id | TEXT | PRIMARY KEY | Unique identifier (UUID) |
| user_id | TEXT | NOT NULL, FK | User providing feedback |
| request_id | TEXT | FK | Related request (nullable) |
| rating | INTEGER | CHECK 1-5 | Star rating |
| comments | TEXT | - | Feedback text |
| feedback_type | TEXT | CHECK | Type of feedback |
| created_at | DATETIME | DEFAULT NOW | Submission timestamp |

**Feedback Types:**
- **GENERAL**: General system feedback
- **REQUEST_SPECIFIC**: Feedback about a specific request
- **SYSTEM_IMPROVEMENT**: Suggestions for improvement

---

## Relationships

### Foreign Key Constraints

```sql
-- Resources created by users
ALTER TABLE resources 
ADD CONSTRAINT fk_resources_created_by 
FOREIGN KEY (created_by) REFERENCES users(user_id);

-- Requests made by users
ALTER TABLE requests 
ADD CONSTRAINT fk_requests_requester 
FOREIGN KEY (requester_id) REFERENCES users(user_id);

-- Requests for resources
ALTER TABLE requests 
ADD CONSTRAINT fk_requests_resource 
FOREIGN KEY (resource_id) REFERENCES resources(resource_id);

-- Requests assigned to volunteers
ALTER TABLE requests 
ADD CONSTRAINT fk_requests_volunteer 
FOREIGN KEY (volunteer_id) REFERENCES users(user_id);

-- Feedback from users
ALTER TABLE feedback 
ADD CONSTRAINT fk_feedback_user 
FOREIGN KEY (user_id) REFERENCES users(user_id);

-- Feedback about requests
ALTER TABLE feedback 
ADD CONSTRAINT fk_feedback_request 
FOREIGN KEY (request_id) REFERENCES requests(request_id);
```

---

## Indexes

### Performance Optimization

```sql
-- User lookups
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);

-- Resource searches
CREATE INDEX idx_resources_category ON resources(category);
CREATE INDEX idx_resources_name ON resources(name);
CREATE INDEX idx_resources_created_by ON resources(created_by);

-- Request queries
CREATE INDEX idx_requests_requester ON requests(requester_id);
CREATE INDEX idx_requests_volunteer ON requests(volunteer_id);
CREATE INDEX idx_requests_resource ON requests(resource_id);
CREATE INDEX idx_requests_status ON requests(status);
CREATE INDEX idx_requests_urgency ON requests(urgency_level);
CREATE INDEX idx_requests_created_at ON requests(created_at);

-- Feedback lookups
CREATE INDEX idx_feedback_user ON feedback(user_id);
CREATE INDEX idx_feedback_request ON feedback(request_id);
CREATE INDEX idx_feedback_type ON feedback(feedback_type);
```

---

## Sample Queries

### Common Database Operations

#### 1. User Authentication
```sql
-- Find user by username
SELECT * FROM users 
WHERE username = 'admin';

-- Verify password (application handles hashing)
SELECT user_id, username, role, password_hash 
FROM users 
WHERE username = ? AND password_hash = ?;
```

#### 2. Resource Management
```sql
-- Get all resources by category
SELECT * FROM resources 
WHERE category = 'Food & Groceries' 
ORDER BY name;

-- Get low-stock resources
SELECT * FROM resources 
WHERE quantity < 10 
ORDER BY quantity ASC;

-- Get resources created by specific user
SELECT r.*, u.username as creator_name
FROM resources r
JOIN users u ON r.created_by = u.user_id
WHERE r.created_by = ?;
```

#### 3. Request Tracking
```sql
-- Get all pending requests
SELECT r.*, u.username as requester_name, res.name as resource_name
FROM requests r
JOIN users u ON r.requester_id = u.user_id
JOIN resources res ON r.resource_id = res.resource_id
WHERE r.status = 'PENDING'
ORDER BY r.urgency_level DESC, r.created_at ASC;

-- Get requests by user
SELECT r.*, res.name as resource_name, res.category
FROM requests r
JOIN resources res ON r.resource_id = res.resource_id
WHERE r.requester_id = ?
ORDER BY r.created_at DESC;

-- Get volunteer assignments
SELECT r.*, u.username as requester_name, res.name as resource_name
FROM requests r
JOIN users u ON r.requester_id = u.user_id
JOIN resources res ON r.resource_id = res.resource_id
WHERE r.volunteer_id = ?
AND r.status IN ('ASSIGNED', 'IN_PROGRESS')
ORDER BY r.urgency_level DESC;
```

#### 4. Statistics & Analytics
```sql
-- Count requests by status
SELECT status, COUNT(*) as count
FROM requests
GROUP BY status;

-- Count resources by category
SELECT category, COUNT(*) as count, SUM(quantity) as total_quantity
FROM resources
GROUP BY category
ORDER BY count DESC;

-- User activity summary
SELECT 
    u.username,
    u.role,
    COUNT(DISTINCT r.request_id) as total_requests,
    COUNT(DISTINCT CASE WHEN r.status = 'COMPLETED' THEN r.request_id END) as completed_requests
FROM users u
LEFT JOIN requests r ON u.user_id = r.requester_id
GROUP BY u.user_id, u.username, u.role;

-- Average rating by feedback type
SELECT 
    feedback_type,
    AVG(rating) as avg_rating,
    COUNT(*) as feedback_count
FROM feedback
WHERE rating IS NOT NULL
GROUP BY feedback_type;
```

---

## Data Migration

### Initial Setup

```sql
-- Run schema creation
SOURCE src/main/resources/sql/schema.sql;

-- Verify tables created
SELECT name FROM sqlite_master WHERE type='table';
```

### Sample Data Population

The application automatically populates sample data on first run via `DataInitializer.java`:

- **15 Users**: 3 Admins, 5 Volunteers, 7 Requesters
- **30 Resources**: Across 6 categories
- **20 Requests**: Various statuses and urgency levels

### Backup & Restore

#### SQLite Backup
```bash
# Backup database
sqlite3 community_hub.db ".backup 'backup_$(date +%Y%m%d).db'"

# Restore from backup
sqlite3 community_hub.db ".restore 'backup_20240115.db'"
```

#### MySQL Backup
```bash
# Backup
mysqldump -u username -p community_hub > backup_$(date +%Y%m%d).sql

# Restore
mysql -u username -p community_hub < backup_20240115.sql
```

---

## Database Maintenance

### Regular Maintenance Tasks

```sql
-- Vacuum database (SQLite)
VACUUM;

-- Analyze tables for query optimization
ANALYZE;

-- Check database integrity
PRAGMA integrity_check;

-- View database statistics
SELECT 
    (SELECT COUNT(*) FROM users) as total_users,
    (SELECT COUNT(*) FROM resources) as total_resources,
    (SELECT COUNT(*) FROM requests) as total_requests,
    (SELECT COUNT(*) FROM feedback) as total_feedback;
```

### Data Cleanup

```sql
-- Delete old completed requests (older than 1 year)
DELETE FROM requests 
WHERE status = 'COMPLETED' 
AND created_at < datetime('now', '-1 year');

-- Archive old feedback
CREATE TABLE feedback_archive AS 
SELECT * FROM feedback 
WHERE created_at < datetime('now', '-1 year');

DELETE FROM feedback 
WHERE created_at < datetime('now', '-1 year');
```

---

## Performance Tuning

### Query Optimization Tips

1. **Use Indexes**: Ensure frequently queried columns have indexes
2. **Limit Results**: Use `LIMIT` for large result sets
3. **Avoid SELECT ***: Select only needed columns
4. **Use Prepared Statements**: Prevent SQL injection and improve performance
5. **Batch Operations**: Group multiple inserts/updates

### Example Optimized Query

```sql
-- Instead of:
SELECT * FROM requests WHERE status = 'PENDING';

-- Use:
SELECT request_id, requester_id, resource_id, description, urgency_level
FROM requests 
WHERE status = 'PENDING'
LIMIT 100;
```

---

## Troubleshooting

### Common Issues

#### Database Locked
```sql
-- Check for active connections
PRAGMA busy_timeout = 5000;

-- Close all connections and retry
```

#### Constraint Violations
```sql
-- Check foreign key constraints
PRAGMA foreign_keys = ON;
PRAGMA foreign_key_check;

-- View constraint details
SELECT sql FROM sqlite_master WHERE type='table';
```

#### Performance Issues
```sql
-- Analyze query performance
EXPLAIN QUERY PLAN 
SELECT * FROM requests WHERE status = 'PENDING';

-- Rebuild indexes
REINDEX;
```

---

## Additional Resources

- [SQLite Documentation](https://www.sqlite.org/docs.html)
- [MySQL Documentation](https://dev.mysql.com/doc/)
- [Database Design Best Practices](https://www.sqlshack.com/database-design-best-practices/)
- [SQL Performance Tuning](https://use-the-index-luke.com/)

---

**Last Updated**: January 2024  
**Database Version**: 1.0.0  
**Schema Version**: 1.0
