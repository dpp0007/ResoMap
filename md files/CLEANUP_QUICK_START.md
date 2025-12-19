# Data Cleanup - Quick Start Guide

## What Gets Deleted?

### Users (6 total)
- `admin-001` (admin)
- `vol-001`, `vol-002` (volunteers)
- `req-001`, `req-002`, `req-003` (requesters)

### Resources (25 total)
- All resources with IDs: `res-food-*`, `res-clothing-*`, `res-shelter-*`, `res-medical-*`, `res-education-*`, `res-other-*`

### Requests (8 total)
- All requests with IDs: `req-001` through `req-008`

---

## How to Run Cleanup

### Step 1: Backup Database
```bash
cp community_hub.db community_hub.db.backup
```

### Step 2: Run Cleanup
```bash
java -cp target/community-hub.jar com.communityhub.util.DataCleanupRunner
```

### Step 3: Verify Results
```bash
sqlite3 community_hub.db
sqlite> SELECT COUNT(*) FROM users;      -- Should be 0
sqlite> SELECT COUNT(*) FROM resources;  -- Should be 0
sqlite> SELECT COUNT(*) FROM requests;   -- Should be 0
sqlite> .quit
```

---

## Expected Output

```
[INFO] ========== STARTING DATA CLEANUP ==========
[INFO] Deleted 8 dummy requests
[INFO] Deleted 25 dummy resources
[INFO] Deleted 6 dummy users
[INFO] ========== DATA CLEANUP COMPLETED SUCCESSFULLY ==========
[INFO] Summary: 8 requests, 25 resources, 6 users removed

[INFO] ========== VERIFYING DATA CLEANUP ==========
[INFO] ✓ No dummy users found
[INFO] ✓ No dummy resources found
[INFO] ✓ No dummy requests found
[INFO] ========== FINAL DATABASE STATE ==========
[INFO] Total Users: 0
[INFO] Total Resources: 0
[INFO] Total Requests: 0
[INFO] ========== VERIFICATION COMPLETE ==========
```

---

## Safety Features

✓ **Transaction-based** - All changes committed together or rolled back
✓ **Automatic rollback** - If any step fails, all changes are undone
✓ **Referential integrity** - No orphaned records
✓ **Logging** - Detailed logs of what was deleted
✓ **Verification** - Confirms cleanup was successful

---

## If Something Goes Wrong

### Cleanup Failed?
- Database automatically rolled back
- No changes were made
- Check logs for error details
- Try again after fixing the issue

### Need to Restore?
```bash
# Stop application
# Restore backup
cp community_hub.db.backup community_hub.db
# Restart application
```

---

## After Cleanup

1. Restart application
2. Hard refresh browser (Ctrl+Shift+R)
3. Verify all pages load correctly
4. Test creating new resources
5. Test creating new requests
6. Check admin dashboard

---

## Dummy Data Patterns

**User IDs:** `admin-001`, `vol-001`, `vol-002`, `req-001`, `req-002`, `req-003`

**Resource IDs:** `res-food-001`, `res-clothing-001`, etc.

**Request IDs:** `req-001` through `req-008`

**Email Domain:** `@example.com`

---

## Questions?

See `DATA_CLEANUP_AUDIT.md` for detailed information about:
- Identification criteria
- Cleanup strategy
- Transaction handling
- Verification procedures
- Rollback procedures
