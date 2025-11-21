-- Community Resource Hub Database Initialization Script
-- This script creates the database schema and inserts sample data

-- Create database
CREATE DATABASE IF NOT EXISTS community_hub;
USE community_hub;

-- Drop existing tables (in correct order to handle foreign keys)
DROP TABLE IF EXISTS feedback;
DROP TABLE IF EXISTS requests;
DROP TABLE IF EXISTS resources;
DROP TABLE IF EXISTS users;

-- Create users table
CREATE TABLE users (
    user_id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'VOLUNTEER', 'REQUESTER') NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_role (role)
);

-- Create resources table
CREATE TABLE resources (
    resource_id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    category VARCHAR(50) NOT NULL,
    quantity INT DEFAULT 0,
    location VARCHAR(200),
    contact_info VARCHAR(200),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    
    INDEX idx_category (category),
    INDEX idx_name (name),
    INDEX idx_quantity (quantity)
);

-- Create requests table
CREATE TABLE requests (
    request_id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    resource_id VARCHAR(36) NOT NULL,
    volunteer_id VARCHAR(36) NULL,
    description TEXT NOT NULL,
    urgency VARCHAR(50) NOT NULL,
    status VARCHAR(50) DEFAULT 'Pending Review',
    quantity_requested INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    assigned_at TIMESTAMP NULL,
    completed_at TIMESTAMP NULL,
    completion_notes TEXT,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (resource_id) REFERENCES resources(resource_id) ON DELETE CASCADE,
    FOREIGN KEY (volunteer_id) REFERENCES users(user_id) ON DELETE SET NULL,
    
    INDEX idx_user_id (user_id),
    INDEX idx_resource_id (resource_id),
    INDEX idx_volunteer_id (volunteer_id),
    INDEX idx_status (status),
    INDEX idx_urgency (urgency),
    INDEX idx_created_at (created_at)
);

-- Create feedback table
CREATE TABLE feedback (
    feedback_id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    request_id VARCHAR(36) NULL,
    feedback_type ENUM('GENERAL', 'REQUEST_SPECIFIC', 'SYSTEM_IMPROVEMENT') NOT NULL,
    rating INT CHECK (rating >= 1 AND rating <= 5),
    comments TEXT NOT NULL,
    is_anonymous BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (request_id) REFERENCES requests(request_id) ON DELETE SET NULL,
    
    INDEX idx_user_id (user_id),
    INDEX idx_request_id (request_id),
    INDEX idx_feedback_type (feedback_type),
    INDEX idx_rating (rating),
    INDEX idx_created_at (created_at)
);

-- Insert sample users
INSERT INTO users (user_id, username, email, password_hash, role, display_name) VALUES
('admin-001', 'admin', 'admin@communityhub.org', '$2a$10$N9qo8uLOickgx2ZMRZoMye', 'ADMIN', 'System Administrator'),
('vol-001', 'volunteer1', 'volunteer1@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye', 'VOLUNTEER', 'John Volunteer'),
('vol-002', 'volunteer2', 'volunteer2@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye', 'VOLUNTEER', 'Sarah Helper'),
('req-001', 'requester1', 'requester1@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye', 'REQUESTER', 'Maria Requester'),
('req-002', 'requester2', 'requester2@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye', 'REQUESTER', 'David Needhelp');

-- Insert sample resources
INSERT INTO resources (resource_id, name, description, category, quantity, location, contact_info) VALUES
('res-001', 'Canned Food', 'Non-perishable canned goods for emergency food assistance. Includes vegetables, fruits, and proteins for families in need.', 'Food & Groceries', 50, 'Community Center - Storage Room A', 'contact@communitycenter.org'),
('res-002', 'Winter Coats', 'Warm winter coats for adults and children. Various sizes available for cold weather protection during winter months.', 'Clothing & Textiles', 25, 'Donation Center - Building B', 'donations@community.org'),
('res-003', 'First Aid Kits', 'Basic first aid supplies and emergency medical kits for home and community use. Essential medical supplies included.', 'Medical Supplies', 15, 'Health Center - Supply Room', 'health@community.org'),
('res-004', 'Emergency Blankets', 'Thermal emergency blankets for temporary shelter and warmth during crisis situations. High-quality materials for protection.', 'Shelter & Housing', 100, 'Emergency Services - Warehouse', 'emergency@community.org'),
('res-005', 'School Supplies', 'Educational materials including notebooks, pens, pencils, and basic school supplies for students in need.', 'Educational Resources', 75, 'Education Center - Room 101', 'education@community.org'),
('res-006', 'Baby Formula', 'Infant formula and baby food supplies for families with young children. Various brands and types available.', 'Food & Groceries', 30, 'Family Services - Building C', 'family@community.org');

-- Insert sample requests
INSERT INTO requests (request_id, user_id, resource_id, description, urgency, status, quantity_requested) VALUES
('req-001', 'req-001', 'res-001', 'Family of four needs emergency food assistance after job loss. Any canned goods would be greatly appreciated.', '🟠 High - Urgent, within 24 hours', '⏳ Pending Review', 10),
('req-002', 'req-002', 'res-002', 'Single mother with two children needs winter coats for upcoming cold season. Children are ages 8 and 12.', '🟡 Medium - Within a few days', '⏳ Pending Review', 3),
('req-003', 'req-001', 'res-003', 'Need first aid kit for elderly parent living alone. Want to ensure they have basic medical supplies at home.', '🟢 Low - When convenient', '⏳ Pending Review', 1);

-- Insert sample feedback
INSERT INTO feedback (feedback_id, user_id, request_id, feedback_type, rating, comments) VALUES
('fb-001', 'req-001', NULL, 'GENERAL', 5, 'The Community Resource Hub has been incredibly helpful for our family. The volunteers are compassionate and the system is easy to use.'),
('fb-002', 'vol-001', NULL, 'SYSTEM_IMPROVEMENT', 4, 'Great platform for connecting with people who need help. Would love to see a mobile app version in the future.'),
('fb-003', 'req-002', NULL, 'GENERAL', 5, 'Amazing service! The response time was quick and the volunteers were very professional and kind.');

-- Create views for common queries
CREATE VIEW active_requests AS
SELECT 
    r.request_id,
    r.description,
    r.urgency,
    r.status,
    r.quantity_requested,
    r.created_at,
    u.display_name as requester_name,
    u.email as requester_email,
    res.name as resource_name,
    res.category as resource_category,
    vol.display_name as volunteer_name
FROM requests r
JOIN users u ON r.user_id = u.user_id
JOIN resources res ON r.resource_id = res.resource_id
LEFT JOIN users vol ON r.volunteer_id = vol.user_id
WHERE r.status != '✅ Completed' AND r.status != '❌ Cancelled';

CREATE VIEW resource_summary AS
SELECT 
    category,
    COUNT(*) as total_resources,
    SUM(quantity) as total_quantity,
    AVG(quantity) as avg_quantity,
    MIN(quantity) as min_quantity,
    MAX(quantity) as max_quantity
FROM resources 
WHERE is_active = TRUE
GROUP BY category;

CREATE VIEW user_statistics AS
SELECT 
    role,
    COUNT(*) as user_count,
    COUNT(CASE WHEN is_active = TRUE THEN 1 END) as active_users,
    COUNT(CASE WHEN created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY) THEN 1 END) as new_users_30_days
FROM users
GROUP BY role;

-- Create stored procedures for common operations
DELIMITER //

CREATE PROCEDURE GetUserRequests(IN userId VARCHAR(36))
BEGIN
    SELECT 
        r.request_id,
        r.description,
        r.urgency,
        r.status,
        r.quantity_requested,
        r.created_at,
        res.name as resource_name,
        vol.display_name as volunteer_name
    FROM requests r
    JOIN resources res ON r.resource_id = res.resource_id
    LEFT JOIN users vol ON r.volunteer_id = vol.user_id
    WHERE r.user_id = userId
    ORDER BY r.created_at DESC;
END //

CREATE PROCEDURE GetVolunteerAssignments(IN volunteerId VARCHAR(36))
BEGIN
    SELECT 
        r.request_id,
        r.description,
        r.urgency,
        r.status,
        r.quantity_requested,
        r.assigned_at,
        u.display_name as requester_name,
        u.email as requester_email,
        res.name as resource_name
    FROM requests r
    JOIN users u ON r.user_id = u.user_id
    JOIN resources res ON r.resource_id = res.resource_id
    WHERE r.volunteer_id = volunteerId
    ORDER BY r.assigned_at DESC;
END //

CREATE PROCEDURE GetAvailableRequests()
BEGIN
    SELECT 
        r.request_id,
        r.description,
        r.urgency,
        r.quantity_requested,
        r.created_at,
        u.display_name as requester_name,
        res.name as resource_name,
        res.category as resource_category
    FROM requests r
    JOIN users u ON r.user_id = u.user_id
    JOIN resources res ON r.resource_id = res.resource_id
    WHERE r.status = '⏳ Pending Review'
    ORDER BY 
        CASE r.urgency
            WHEN '🔴 Critical - Immediate attention needed' THEN 1
            WHEN '🟠 High - Urgent, within 24 hours' THEN 2
            WHEN '🟡 Medium - Within a few days' THEN 3
            WHEN '🟢 Low - When convenient' THEN 4
            ELSE 5
        END,
        r.created_at ASC;
END //

DELIMITER ;

-- Create triggers for audit logging
CREATE TABLE audit_log (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    table_name VARCHAR(50) NOT NULL,
    operation VARCHAR(10) NOT NULL,
    record_id VARCHAR(36) NOT NULL,
    old_values JSON,
    new_values JSON,
    user_id VARCHAR(36),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

DELIMITER //

CREATE TRIGGER requests_audit_insert
AFTER INSERT ON requests
FOR EACH ROW
BEGIN
    INSERT INTO audit_log (table_name, operation, record_id, new_values, user_id)
    VALUES ('requests', 'INSERT', NEW.request_id, 
            JSON_OBJECT('user_id', NEW.user_id, 'resource_id', NEW.resource_id, 
                       'description', NEW.description, 'urgency', NEW.urgency, 'status', NEW.status),
            NEW.user_id);
END //

CREATE TRIGGER requests_audit_update
AFTER UPDATE ON requests
FOR EACH ROW
BEGIN
    INSERT INTO audit_log (table_name, operation, record_id, old_values, new_values, user_id)
    VALUES ('requests', 'UPDATE', NEW.request_id,
            JSON_OBJECT('status', OLD.status, 'volunteer_id', OLD.volunteer_id),
            JSON_OBJECT('status', NEW.status, 'volunteer_id', NEW.volunteer_id),
            NEW.volunteer_id);
END //

DELIMITER ;

-- Grant permissions (adjust as needed for your environment)
-- CREATE USER 'community_hub_user'@'localhost' IDENTIFIED BY 'secure_password';
-- GRANT SELECT, INSERT, UPDATE, DELETE ON community_hub.* TO 'community_hub_user'@'localhost';
-- FLUSH PRIVILEGES;

-- Display summary information
SELECT 'Database initialization completed successfully!' as Status;
SELECT COUNT(*) as 'Total Users' FROM users;
SELECT COUNT(*) as 'Total Resources' FROM resources;
SELECT COUNT(*) as 'Total Requests' FROM requests;
SELECT COUNT(*) as 'Total Feedback' FROM feedback;

-- Show sample data
SELECT 'Sample Users:' as Info;
SELECT username, role, display_name FROM users LIMIT 5;

SELECT 'Sample Resources:' as Info;
SELECT name, category, quantity, location FROM resources LIMIT 5;

SELECT 'Sample Requests:' as Info;
SELECT description, urgency, status FROM requests LIMIT 3;