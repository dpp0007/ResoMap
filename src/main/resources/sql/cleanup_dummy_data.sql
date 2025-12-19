-- Community Resource Hub - Dummy Data Cleanup Script
-- FOR INITIAL DATA CLEANUP ONLY
-- This script removes all test/dummy data from the database
-- 
-- IMPORTANT: 
-- 1. Backup database before running: cp community_hub.db community_hub.db.backup
-- 2. Run in correct order (requests → resources → users)
-- 3. Use transaction management for safety
-- 4. Verify cleanup with verification queries below

-- ============================================
-- STEP 1: DELETE DUMMY REQUESTS
-- ============================================
-- Deletes all requests with dummy IDs and requests created by dummy users

DELETE FROM requests WHERE request_id IN (
    'req-001', 'req-002', 'req-003', 'req-004', 
    'req-005', 'req-006', 'req-007', 'req-008'
);

DELETE FROM requests WHERE requester_id IN (
    'req-001', 'req-002', 'req-003', 'admin-001', 'vol-001', 'vol-002'
);

DELETE FROM requests WHERE volunteer_id IN (
    'vol-001', 'vol-002', 'admin-001'
);

-- ============================================
-- STEP 2: DELETE DUMMY RESOURCES
-- ============================================
-- Deletes all resources with dummy ID patterns

DELETE FROM resources WHERE resource_id LIKE 'res-food-%';
DELETE FROM resources WHERE resource_id LIKE 'res-clothing-%';
DELETE FROM resources WHERE resource_id LIKE 'res-shelter-%';
DELETE FROM resources WHERE resource_id LIKE 'res-medical-%';
DELETE FROM resources WHERE resource_id LIKE 'res-education-%';
DELETE FROM resources WHERE resource_id LIKE 'res-other-%';

-- Also delete resources created by dummy admin
DELETE FROM resources WHERE created_by = 'admin-001';

-- ============================================
-- STEP 3: DELETE DUMMY USERS
-- ============================================
-- Deletes all users with dummy IDs and usernames

DELETE FROM users WHERE user_id IN (
    'admin-001', 'vol-001', 'vol-002', 'req-001', 'req-002', 'req-003'
);

DELETE FROM users WHERE username IN (
    'admin', 'volunteer1', 'volunteer2', 'requester1', 'requester2', 'requester3'
);

-- Delete test users with @example.com email domain
DELETE FROM users WHERE email LIKE '%@example.com';

-- ============================================
-- VERIFICATION QUERIES
-- ============================================
-- Run these queries to verify cleanup was successful

-- Check for remaining dummy users
-- Expected result: 0 rows
SELECT 'Remaining dummy users:' as check_name;
SELECT * FROM users WHERE user_id LIKE 'admin-%' OR user_id LIKE 'vol-%' OR user_id LIKE 'req-%';

-- Check for remaining dummy resources
-- Expected result: 0 rows
SELECT 'Remaining dummy resources:' as check_name;
SELECT * FROM resources WHERE resource_id LIKE 'res-%';

-- Check for remaining dummy requests
-- Expected result: 0 rows
SELECT 'Remaining dummy requests:' as check_name;
SELECT * FROM requests WHERE request_id LIKE 'req-%';

-- Check for orphaned requests (requests with non-existent resources)
-- Expected result: 0 rows
SELECT 'Orphaned requests (resource not found):' as check_name;
SELECT * FROM requests WHERE resource_id NOT IN (SELECT resource_id FROM resources);

-- Check for orphaned requests (requester not found)
-- Expected result: 0 rows
SELECT 'Orphaned requests (requester not found):' as check_name;
SELECT * FROM requests WHERE requester_id NOT IN (SELECT user_id FROM users);

-- Show final database state
SELECT 'Final database state:' as info;
SELECT 'Total Users:' as metric, COUNT(*) as count FROM users;
SELECT 'Total Resources:' as metric, COUNT(*) as count FROM resources;
SELECT 'Total Requests:' as metric, COUNT(*) as count FROM requests;
SELECT 'Total Feedback:' as metric, COUNT(*) as count FROM feedback;

-- ============================================
-- CLEANUP SUMMARY
-- ============================================
-- Expected results after cleanup:
-- - 0 dummy users
-- - 0 dummy resources
-- - 0 dummy requests
-- - 0 orphaned records
-- - Database ready for production use

-- ============================================
-- ROLLBACK PROCEDURE
-- ============================================
-- If cleanup fails or you need to restore:
-- 1. Stop the application
-- 2. Restore from backup: cp community_hub.db.backup community_hub.db
-- 3. Restart the application

-- ============================================
-- NOTES
-- ============================================
-- - This script uses DELETE statements (not DROP)
-- - Database schema is preserved
-- - Foreign key constraints are maintained
-- - Only dummy/test data is removed
-- - Real production data is preserved
-- - Use transaction management for safety
-- - Always backup before running cleanup
