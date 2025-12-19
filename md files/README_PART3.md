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

