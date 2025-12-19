-- Sample Data for Community Resource Hub
-- This script populates the database with sample resources and requests

-- Clear existing data (optional - comment out if you want to keep existing data)
DELETE FROM feedback;
DELETE FROM requests;
DELETE FROM resources;
DELETE FROM users;

-- Insert sample users
INSERT INTO users (user_id, username, email, password_hash, role) VALUES
('admin-001', 'admin', 'admin@communityhub.org', 'hashed_password_here', 'ADMIN'),
('vol-001', 'volunteer1', 'volunteer1@example.com', 'hashed_password_here', 'VOLUNTEER'),
('vol-002', 'volunteer2', 'volunteer2@example.com', 'hashed_password_here', 'VOLUNTEER'),
('req-001', 'requester1', 'requester1@example.com', 'hashed_password_here', 'REQUESTER'),
('req-002', 'requester2', 'requester2@example.com', 'hashed_password_here', 'REQUESTER'),
('req-003', 'requester3', 'requester3@example.com', 'hashed_password_here', 'REQUESTER');

-- Insert sample resources with proper categories
INSERT INTO resources (resource_id, name, description, category, quantity, location, contact_info, created_by) VALUES

-- FOOD category
('res-food-001', 'Canned Vegetables', 'Assorted canned vegetables including corn, peas, and carrots. Non-perishable and ready to use.', 'FOOD', 45, 'Community Center - Storage A', 'food@community.org', 'admin-001'),
('res-food-002', 'Canned Fruits', 'Mixed canned fruits in light syrup. Great source of vitamins and minerals for families in need.', 'FOOD', 38, 'Community Center - Storage A', 'food@community.org', 'admin-001'),
('res-food-003', 'Pasta & Rice', 'Bulk pasta and rice supplies for meal preparation. Staple foods for emergency assistance.', 'FOOD', 60, 'Community Center - Storage B', 'food@community.org', 'admin-001'),
('res-food-004', 'Peanut Butter', 'High-protein peanut butter jars. Excellent nutrition source for children and families.', 'FOOD', 25, 'Community Center - Storage A', 'food@community.org', 'admin-001'),
('res-food-005', 'Baby Formula', 'Infant formula and baby food supplies for families with young children. Various brands available.', 'FOOD', 30, 'Family Services - Building C', 'family@community.org', 'admin-001'),

-- CLOTHING category
('res-clothing-001', 'Winter Coats', 'Warm winter coats for adults and children. Various sizes from XS to XXL available.', 'CLOTHING', 28, 'Donation Center - Building B', 'donations@community.org', 'admin-001'),
('res-clothing-002', 'Warm Sweaters', 'Wool and fleece sweaters for cold weather. Suitable for all ages and sizes.', 'CLOTHING', 35, 'Donation Center - Building B', 'donations@community.org', 'admin-001'),
('res-clothing-003', 'Thermal Socks', 'Thermal and wool socks for winter warmth. Bulk supply available for distribution.', 'CLOTHING', 100, 'Donation Center - Building B', 'donations@community.org', 'admin-001'),
('res-clothing-004', 'Gloves & Scarves', 'Winter gloves and scarves in various colors and materials. Perfect for cold season.', 'CLOTHING', 50, 'Donation Center - Building B', 'donations@community.org', 'admin-001'),
('res-clothing-005', 'Children Clothing', 'Gently used children clothing in various sizes. Suitable for ages 2-16 years.', 'CLOTHING', 75, 'Donation Center - Building B', 'donations@community.org', 'admin-001'),

-- SHELTER category
('res-shelter-001', 'Emergency Blankets', 'Thermal emergency blankets for temporary shelter and warmth. High-quality materials.', 'SHELTER', 120, 'Emergency Services - Warehouse', 'emergency@community.org', 'admin-001'),
('res-shelter-002', 'Sleeping Bags', 'Warm sleeping bags rated for cold weather. Suitable for emergency shelter situations.', 'SHELTER', 20, 'Emergency Services - Warehouse', 'emergency@community.org', 'admin-001'),
('res-shelter-003', 'Tents', 'Emergency tents for temporary shelter. Various sizes available for families and individuals.', 'SHELTER', 10, 'Emergency Services - Warehouse', 'emergency@community.org', 'admin-001'),
('res-shelter-004', 'Pillows & Bedding', 'Pillows and bedding supplies for emergency shelter situations. Clean and sanitized.', 'SHELTER', 40, 'Emergency Services - Warehouse', 'emergency@community.org', 'admin-001'),

-- MEDICAL category
('res-medical-001', 'First Aid Kits', 'Complete first aid kits with bandages, antiseptic, and basic medical supplies.', 'MEDICAL', 18, 'Health Center - Supply Room', 'health@community.org', 'admin-001'),
('res-medical-002', 'Prescription Assistance', 'Help accessing affordable prescription medications through community programs.', 'MEDICAL', 999, 'Health Center - Main Office', 'health@community.org', 'admin-001'),
('res-medical-003', 'Medical Masks', 'N95 and surgical masks for health protection. Bulk supply available.', 'MEDICAL', 500, 'Health Center - Supply Room', 'health@community.org', 'admin-001'),
('res-medical-004', 'Vitamins & Supplements', 'Essential vitamins and nutritional supplements for health maintenance.', 'MEDICAL', 50, 'Health Center - Supply Room', 'health@community.org', 'admin-001'),
('res-medical-005', 'Medical Equipment', 'Basic medical equipment including thermometers, blood pressure monitors, and more.', 'MEDICAL', 15, 'Health Center - Supply Room', 'health@community.org', 'admin-001'),

-- EDUCATION category
('res-education-001', 'School Supplies', 'Notebooks, pens, pencils, erasers, and basic school supplies for students.', 'EDUCATION', 80, 'Education Center - Room 101', 'education@community.org', 'admin-001'),
('res-education-002', 'Textbooks', 'Used textbooks for various subjects and grade levels. Excellent condition.', 'EDUCATION', 45, 'Education Center - Room 102', 'education@community.org', 'admin-001'),
('res-education-003', 'Computers & Tablets', 'Refurbished computers and tablets for educational access and digital literacy.', 'EDUCATION', 12, 'Education Center - Lab', 'education@community.org', 'admin-001'),
('res-education-004', 'Educational Software', 'Licensed educational software and learning programs for students.', 'EDUCATION', 25, 'Education Center - Room 101', 'education@community.org', 'admin-001'),
('res-education-005', 'Tutoring Services', 'Free tutoring services in math, science, English, and other subjects.', 'EDUCATION', 999, 'Education Center - Main Office', 'education@community.org', 'admin-001'),

-- OTHER category
('res-other-001', 'Hygiene Products', 'Soap, shampoo, toothpaste, and other personal hygiene items.', 'OTHER', 60, 'Community Center - Storage C', 'supplies@community.org', 'admin-001'),
('res-other-002', 'Cleaning Supplies', 'Household cleaning supplies including disinfectants and cleaning tools.', 'OTHER', 40, 'Community Center - Storage C', 'supplies@community.org', 'admin-001'),
('res-other-003', 'Pet Supplies', 'Pet food and supplies for families with pets in need of assistance.', 'OTHER', 35, 'Community Center - Storage D', 'supplies@community.org', 'admin-001'),
('res-other-004', 'Tools & Equipment', 'Basic tools and equipment for home repairs and maintenance.', 'OTHER', 20, 'Community Center - Storage D', 'supplies@community.org', 'admin-001'),
('res-other-005', 'Furniture', 'Gently used furniture including beds, tables, and chairs for families in need.', 'OTHER', 15, 'Donation Center - Building D', 'donations@community.org', 'admin-001');

-- Insert sample requests
INSERT INTO requests (request_id, requester_id, resource_id, status, description, urgency_level) VALUES
('req-001', 'req-001', 'res-food-001', 'PENDING', 'Family of four needs emergency food assistance after job loss. Any canned goods would be greatly appreciated.', 'HIGH'),
('req-002', 'req-002', 'res-clothing-001', 'PENDING', 'Single mother with two children needs winter coats for upcoming cold season. Children are ages 8 and 12.', 'MEDIUM'),
('req-003', 'req-001', 'res-medical-001', 'PENDING', 'Need first aid kit for elderly parent living alone. Want to ensure they have basic medical supplies at home.', 'LOW'),
('req-004', 'req-003', 'res-education-001', 'ASSIGNED', 'Student needs school supplies for new school year. Budget is very limited.', 'MEDIUM'),
('req-005', 'req-002', 'res-shelter-001', 'PENDING', 'Temporary shelter needed for family. Emergency blankets would help during transition period.', 'CRITICAL'),
('req-006', 'req-001', 'res-food-005', 'PENDING', 'New mother needs baby formula. Infant has specific dietary needs.', 'HIGH'),
('req-007', 'req-003', 'res-clothing-003', 'PENDING', 'Need warm socks for homeless outreach program participants.', 'MEDIUM'),
('req-008', 'req-002', 'res-other-001', 'PENDING', 'Hygiene products needed for family in temporary housing.', 'MEDIUM');

-- Verify data insertion
SELECT 'Sample Data Insertion Complete!' as Status;
SELECT COUNT(*) as 'Total Users' FROM users;
SELECT COUNT(*) as 'Total Resources' FROM resources;
SELECT COUNT(*) as 'Total Requests' FROM requests;
SELECT 'Resources by Category:' as Info;
SELECT category, COUNT(*) as count, SUM(quantity) as total_quantity FROM resources GROUP BY category;
