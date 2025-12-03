-- Community Resource Hub Database Schema - SQLite Compatible
-- This schema is fully compatible with SQLite and matches the Java application

-- Drop existing tables (in correct order to handle foreign keys)
DROP TABLE IF EXISTS feedback;
DROP TABLE IF EXISTS requests;
DROP TABLE IF EXISTS resources;
DROP TABLE IF EXISTS users;

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    user_id TEXT PRIMARY KEY,
    username TEXT UNIQUE NOT NULL,
    email TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    role TEXT NOT NULL CHECK (role IN ('ADMIN', 'VOLUNTEER', 'REQUESTER')),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Create resources table
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

-- Create requests table
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

-- Create feedback table
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

-- Create indexes for performance optimization
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
CREATE INDEX IF NOT EXISTS idx_feedback_type ON feedback(feedback_type);

-- Insert sample data for testing
INSERT OR IGNORE INTO users (user_id, username, email, password_hash, role) VALUES
('admin-001', 'admin', 'admin@communityhub.org', 'salt:hash', 'ADMIN'),
('vol-001', 'volunteer1', 'volunteer1@example.com', 'salt:hash', 'VOLUNTEER'),
('req-001', 'requester1', 'requester1@example.com', 'salt:hash', 'REQUESTER');

INSERT OR IGNORE INTO resources (resource_id, name, description, category, quantity, location, contact_info) VALUES
('res-001', 'Canned Food', 'Non-perishable canned goods', 'Food & Groceries', 50, 'Community Center', 'contact@community.org'),
('res-002', 'Winter Coats', 'Warm winter coats', 'Clothing & Textiles', 25, 'Donation Center', 'donations@community.org'),
('res-003', 'First Aid Kits', 'Basic first aid supplies', 'Medical Supplies', 15, 'Health Center', 'health@community.org');

INSERT OR IGNORE INTO requests (request_id, requester_id, resource_id, description, urgency_level, status) VALUES
('req-001', 'req-001', 'res-001', 'Family of four needs emergency food assistance', 'HIGH', 'PENDING'),
('req-002', 'req-001', 'res-002', 'Need winter coats for children', 'MEDIUM', 'PENDING');
