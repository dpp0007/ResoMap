# Database Reset & Sample Data Guide

## Overview
This guide explains how to reset the Community Resource Hub database and populate it with sample data organized by category.

## Database Files Location
- **SQLite Database:** `community_hub.db` (in project root)
- **Schema File:** `src/main/resources/sql/schema_sqlite.sql`
- **Sample Data:** `src/main/resources/sql/sample_data.sql`

## Step 1: Delete Existing Database

### Option A: Delete the SQLite Database File
```bash
# Windows
del community_hub.db

# Mac/Linux
rm community_hub.db
```

### Option B: Using SQLite Command Line
```bash
sqlite3 community_hub.db
sqlite> .quit
```

## Step 2: Recreate Database Schema

### Using SQLite Command Line
```bash
# Navigate to project root
cd path/to/project

# Create new database with schema
sqlite3 community_hub.db < src/main/resources/sql/schema_sqlite.sql
```

### Using Java Application
The application will automatically create the schema on first run if the database doesn't exist.

## Step 3: Populate Sample Data

### Using SQLite Command Line
```bash
sqlite3 community_hub.db < src/main/resources/sql/sample_data.sql
```

### Verify Data Insertion
```bash
sqlite3 community_hub.db
sqlite> SELECT COUNT(*) as 'Total Resources' FROM resources;
sqlite> SELECT category, COUNT(*) FROM resources GROUP BY category;
sqlite> SELECT * FROM resources LIMIT 5;
sqlite> .quit
```

## Sample Data Structure

### Categories Available
1. **FOOD** - Food & Groceries
   - Canned Vegetables (45 units)
   - Canned Fruits (38 units)
   - Pasta & Rice (60 units)
   - Peanut Butter (25 units)
   - Baby Formula (30 units)

2. **CLOTHING** - Clothing & Textiles
   - Winter Coats (28 units)
   - Warm Sweaters (35 units)
   - Thermal Socks (100 units)
   - Gloves & Scarves (50 units)
   - Children Clothing (75 units)

3. **SHELTER** - Shelter & Housing
   - Emergency Blankets (120 units)
   - Sleeping Bags (20 units)
   - Tents (10 units)
   - Pillows & Bedding (40 units)

4. **MEDICAL** - Medical Supplies
   - First Aid Kits (18 units)
   - Prescription Assistance (999 units)
   - Medical Masks (500 units)
   - Vitamins & Supplements (50 units)
   - Medical Equipment (15 units)

5. **EDUCATION** - Educational Resources
   - School Supplies (80 units)
   - Textbooks (45 units)
   - Computers & Tablets (12 units)
   - Educational Software (25 units)
   - Tutoring Services (999 units)

6. **OTHER** - Miscellaneous
   - Hygiene Products (60 units)
   - Cleaning Supplies (40 units)
   - Pet Supplies (35 units)
   - Tools & Equipment (20 units)
   - Furniture (15 units)

### Sample Users
- **Admin:** admin / admin@communityhub.org
- **Volunteers:** volunteer1, volunteer2
- **Requesters:** requester1, requester2, requester3

### Sample Requests
- 8 sample requests with various statuses (PENDING, ASSIGNED)
- Different urgency levels (LOW, MEDIUM, HIGH, CRITICAL)
- Linked to resources and users

## Step 4: Restart Application

1. Stop the running application
2. Delete the database file (if not already done)
3. Restart the application
4. The application will:
   - Create the database schema
   - Populate sample data
   - Display resources on the Resources page

## Troubleshooting

### Issue: "No Resources Found" on Resources Page
**Solution:**
1. Verify database file exists: `community_hub.db`
2. Check database has data:
   ```bash
   sqlite3 community_hub.db "SELECT COUNT(*) FROM resources;"
   ```
3. If count is 0, run sample data script:
   ```bash
   sqlite3 community_hub.db < src/main/resources/sql/sample_data.sql
   ```
4. Restart application and hard refresh browser (Ctrl+Shift+R)

### Issue: Category Filtering Not Working
**Solution:**
1. Verify resources have correct category values:
   ```bash
   sqlite3 community_hub.db "SELECT DISTINCT category FROM resources;"
   ```
2. Expected categories: FOOD, CLOTHING, SHELTER, MEDICAL, EDUCATION, OTHER
3. If categories are different, update sample_data.sql and re-run

### Issue: Database Locked Error
**Solution:**
1. Close all SQLite connections
2. Stop the application
3. Delete `community_hub.db`
4. Restart application

## Database Schema Overview

### Users Table
- user_id (PRIMARY KEY)
- username (UNIQUE)
- email (UNIQUE)
- password_hash
- role (ADMIN, VOLUNTEER, REQUESTER)
- created_at, updated_at

### Resources Table
- resource_id (PRIMARY KEY)
- name
- description
- category (FOOD, CLOTHING, SHELTER, MEDICAL, EDUCATION, OTHER)
- quantity
- location
- contact_info
- created_by (FOREIGN KEY to users)
- created_at, updated_at

### Requests Table
- request_id (PRIMARY KEY)
- requester_id (FOREIGN KEY to users)
- resource_id (FOREIGN KEY to resources)
- volunteer_id (FOREIGN KEY to users, nullable)
- status (PENDING, ASSIGNED, IN_PROGRESS, COMPLETED, CANCELLED)
- description
- urgency_level (LOW, MEDIUM, HIGH, CRITICAL)
- created_at, updated_at

### Feedback Table
- feedback_id (PRIMARY KEY)
- user_id (FOREIGN KEY to users)
- request_id (FOREIGN KEY to requests, nullable)
- rating (1-5)
- comments
- feedback_type (GENERAL, REQUEST_SPECIFIC, SYSTEM_IMPROVEMENT)
- created_at

## Quick Reset Script

### Windows Batch Script (reset_db.bat)
```batch
@echo off
echo Deleting existing database...
del community_hub.db

echo Creating new database with schema...
sqlite3 community_hub.db < src/main/resources/sql/schema_sqlite.sql

echo Populating sample data...
sqlite3 community_hub.db < src/main/resources/sql/sample_data.sql

echo Database reset complete!
echo Restart the application to see changes.
pause
```

### Mac/Linux Bash Script (reset_db.sh)
```bash
#!/bin/bash
echo "Deleting existing database..."
rm -f community_hub.db

echo "Creating new database with schema..."
sqlite3 community_hub.db < src/main/resources/sql/schema_sqlite.sql

echo "Populating sample data..."
sqlite3 community_hub.db < src/main/resources/sql/sample_data.sql

echo "Database reset complete!"
echo "Restart the application to see changes."
```

## Verification Checklist

After resetting the database:

- [ ] Database file exists: `community_hub.db`
- [ ] Users table has 6 records
- [ ] Resources table has 25 records
- [ ] Resources distributed across 6 categories
- [ ] Requests table has 8 records
- [ ] Application starts without errors
- [ ] Resources page displays all resources
- [ ] Category filtering works (click buttons)
- [ ] Search functionality works
- [ ] Combined filtering works (category + search)

## Next Steps

1. **Test Filtering:**
   - Click category buttons to filter resources
   - Type in search box to search resources
   - Combine both filters

2. **Create New Resources:**
   - Login as admin
   - Click "Add Resource" button
   - Fill in form with proper category

3. **Create Requests:**
   - Login as requester
   - Click "Request" button on resource card
   - Fill in request form

4. **Manage Requests:**
   - Login as volunteer or admin
   - View and manage pending requests
   - Assign requests to volunteers

## Support

For issues or questions:
1. Check the troubleshooting section above
2. Verify database file permissions
3. Check application logs in `logs/` directory
4. Ensure SQLite is installed and accessible
