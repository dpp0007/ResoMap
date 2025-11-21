# 📖 Community Resource Hub - User Guide

> Complete guide to using the Community Resource Hub application

---

## 📋 Table of Contents

- [Getting Started](#getting-started)
- [Login & Registration](#login--registration)
- [Admin User Guide](#admin-user-guide)
- [Volunteer User Guide](#volunteer-user-guide)
- [Requester User Guide](#requester-user-guide)
- [Common Features](#common-features)
- [Troubleshooting](#troubleshooting)

---

## 🚀 Getting Started

### System Requirements
- Windows, macOS, or Linux operating system
- Java 11 or higher installed
- Minimum 4GB RAM
- 500MB free disk space

### Launching the Application
1. Double-click the application icon or run `mvn javafx:run`
2. The login screen will appear
3. Use your credentials to log in

---

## 🔐 Login & Registration

### Login Screen

#### Components

**Username Field**
- Enter your username (case-sensitive)
- Required field
- Example: `admin`, `volunteer1`, `requester1`

**Password Field**
- Enter your password
- Password is hidden for security
- Required field

**Login Button**
- Click to authenticate and access the system
- Validates credentials before proceeding
- Shows error message if credentials are invalid

**Register Link**
- Located below the login button
- Click "Don't have an account? Register here" to create new account
- Opens the registration form

**Loading Indicator**
- Appears while authenticating
- Shows spinning animation during login process

#### Login Process
1. Enter your username in the first field
2. Enter your password in the second field
3. Click the "Login" button
4. Wait for authentication
5. You'll be redirected to your role-specific dashboard

#### Error Messages
- **"Invalid credentials"** - Username or password is incorrect
- **"Account is inactive"** - Your account has been deactivated
- **"Database connection error"** - System cannot connect to database


### Registration Screen

#### Components

**Username Field**
- Choose a unique username (3-50 characters)
- Only letters, numbers, and underscores allowed
- Cannot be changed after registration

**Email Field**
- Enter a valid email address
- Must be unique in the system
- Used for notifications and account recovery

**Password Field**
- Minimum 4 characters (8+ recommended)
- Should include letters and numbers
- Password is encrypted before storage

**Confirm Password Field**
- Re-enter your password
- Must match the password field exactly
- Prevents typing errors

**Display Name Field**
- Your full name or display name
- Shown to other users in the system
- Can be updated later in profile settings

**Role Selection**
- **Volunteer**: Help fulfill community requests
- **Requester**: Submit requests for assistance
- Choose based on how you want to use the system

**Register Button**
- Creates your new account
- Validates all fields before submission
- Redirects to login screen on success

**Back to Login Link**
- Returns to the login screen
- Use if you already have an account

#### Registration Process
1. Click "Register here" from login screen
2. Fill in all required fields
3. Select your role (Volunteer or Requester)
4. Click "Register" button
5. Wait for confirmation message
6. Return to login screen and sign in

---


## 👨‍💼 Admin User Guide

**Test Credentials**: Username: `admin` | Password: `pass`

### Dashboard Overview

When you log in as an admin, you'll see the main dashboard with:

#### Header Section
- **Title**: "Admin Dashboard" - Shows your current role
- **Welcome Label**: Displays "Welcome, [Your Name]"
- **Logout Button**: Click to safely log out of the system

#### Sidebar Navigation
The left sidebar contains all main navigation buttons:

**Resource Management Button** (📦)
- Opens the resource management interface
- Manage all community resources
- Add, edit, or delete resources

**User Management Button** (👥)
- View and manage all system users
- Filter users by role
- View user statistics

**Request Overview Button** (📋)
- Monitor all help requests
- Filter by status and urgency
- Assign volunteers to requests

**System Reports Button** (📊)
- Generate comprehensive reports
- View system analytics
- Export data

**Settings Button** (⚙️)
- Configure system settings
- Manage database
- Security settings

#### Dashboard Statistics Cards

**Total Resources Card**
- Shows total number of resources in system
- Updates in real-time
- Click to view resource management

**Total Users Card**
- Displays total registered users
- Includes all roles
- Click to view user management

**Active Requests Card**
- Shows currently active requests
- Excludes completed/cancelled
- Click to view request overview

**Pending Requests Card**
- Displays requests awaiting assignment
- Requires immediate attention
- Highlighted for visibility

**Critical Requests Card**
- Shows high-priority urgent requests
- Requires immediate action
- Red highlight for urgency

**Available Resources Card**
- Shows resources currently in stock
- Quantity greater than zero
- Green highlight for availability

#### Quick Action Buttons

**Manage Resources Button**
- Quick access to resource management
- Same as sidebar button
- Primary action button (blue)

**View Requests Button**
- Quick access to request overview
- Same as sidebar button
- Primary action button (blue)

**Refresh Data Button**
- Reloads all dashboard statistics
- Updates all cards with latest data
- Secondary action button (gray)


### Resource Management Page

#### Header Controls

**Search Field**
- Search resources by name
- Real-time filtering
- Case-insensitive search

**Category Filter**
- Filter by resource category
- Options: All, Food, Clothing, Medical, Shelter, etc.
- Dropdown selection

**Add Resource Button** (+ Add Resource)
- Opens dialog to create new resource
- Primary action button
- Requires all fields to be filled

**Refresh Button** (🔄 Refresh)
- Reloads resource list
- Updates quantities and availability
- Secondary action button

#### Resources Table

**Columns:**
1. **Resource ID**: Unique identifier (first 8 characters)
2. **Name**: Resource name
3. **Category**: Resource type/category
4. **Quantity**: Available amount
5. **Location**: Where resource is stored
6. **Status**: Active/Inactive indicator
7. **Actions**: Edit and Delete buttons

**Action Buttons per Row:**
- **Edit Button**: Modify resource details
- **Delete Button**: Remove resource (with confirmation)

#### Add/Edit Resource Dialog

**Fields:**
- **Resource Name**: Required, max 100 characters
- **Description**: Detailed information about resource
- **Category**: Dropdown selection
- **Quantity**: Number available (0 or more)
- **Location**: Where to find the resource
- **Contact Info**: Phone or email for inquiries

**Buttons:**
- **Save**: Creates or updates resource
- **Cancel**: Closes dialog without saving


### User Management Page

#### Header Section

**Page Title**: "User Management"

**Search Field**
- Search users by username, email, or display name
- Real-time filtering as you type
- Clear button to reset search

**Role Filter Dropdown**
- Filter users by role
- Options: All Roles, Admin, Volunteer, Requester
- Updates table immediately

**Add User Button** (+ Add User)
- Create new user account
- Admin privilege required
- Opens user creation dialog

**Refresh Button** (🔄 Refresh)
- Reloads user list
- Updates statistics
- Fetches latest data from database

#### Statistics Cards

**Total Users Count**
- Shows total registered users
- All roles included
- Large number display

**Active Users Count**
- Users with active status
- Excludes deactivated accounts
- Green indicator

**Volunteers Count**
- Total volunteer accounts
- Active volunteers only
- Blue indicator

**Requesters Count**
- Total requester accounts
- Active requesters only
- Orange indicator

#### Users Table

**Columns:**
1. **User ID**: Unique identifier (truncated)
2. **Username**: Login username
3. **Display Name**: User's full name
4. **Email**: Contact email address
5. **Role**: Admin/Volunteer/Requester
6. **Status**: Active/Inactive badge
7. **Created**: Account creation date
8. **Actions**: Edit, Deactivate, Delete buttons

**Row Actions:**
- **Edit**: Modify user details
- **Deactivate/Activate**: Toggle account status
- **Delete**: Remove user (with confirmation)


### Request Overview Page

#### Header Controls

**Status Filter Dropdown**
- Filter by request status
- Options: All Status, Pending, Assigned, In Progress, Completed
- Updates table in real-time

**Urgency Filter Dropdown**
- Filter by urgency level
- Options: All Urgency, Low, Medium, High, Critical
- Color-coded indicators

**Refresh Button** (🔄 Refresh)
- Reloads all requests
- Updates statistics
- Fetches latest status changes

#### Statistics Cards (Grid Layout)

**Total Requests**
- All requests in system
- Includes all statuses
- Large number display

**Pending Requests**
- Awaiting volunteer assignment
- Yellow indicator
- Requires attention

**Assigned Requests**
- Currently assigned to volunteers
- Blue indicator
- In progress

**Completed Requests**
- Successfully fulfilled requests
- Green indicator
- Success metric

#### Requests Table

**Columns:**
1. **Request ID**: Unique identifier
2. **Requester**: Who submitted the request
3. **Resource**: Requested resource name
4. **Description**: Request details (truncated)
5. **Urgency**: Priority level with color
6. **Status**: Current status badge
7. **Volunteer**: Assigned volunteer (if any)
8. **Created**: Submission date/time
9. **Actions**: View Details, Assign, Update Status

**Action Buttons:**
- **View Details**: Opens full request information
- **Assign Volunteer**: Assign to available volunteer
- **Update Status**: Change request status
- **Delete**: Remove request (admin only)


### System Reports Page

#### Header Section

**Export Report Button** (📊 Export Report)
- Export current report data
- Multiple format options
- Primary action button

**Refresh Button** (🔄 Refresh)
- Reload report data
- Update all statistics
- Secondary action button

#### Report Period Selection

**Report Period Dropdown**
- Predefined periods: Today, This Week, This Month, This Year, Custom
- Quick selection for common ranges

**From Date Picker**
- Select start date for report
- Calendar popup interface
- Custom date range

**To Date Picker**
- Select end date for report
- Calendar popup interface
- Must be after start date

**Generate Button**
- Creates report for selected period
- Updates all statistics
- Processes data and displays results

#### Key Metrics Dashboard (Grid Layout)

**Total Requests Card** (📊)
- Total requests in selected period
- Large number display
- Trend indicator

**Completion Rate Card** (✅)
- Percentage of completed requests
- Success metric
- Green indicator for high rates

**Average Response Time Card** (⏱️)
- Average time to assign volunteer
- Measured in hours
- Performance metric

**Active Volunteers Card** (👥)
- Volunteers who accepted requests
- In selected period
- Engagement metric

**Resources Distributed Card** (📦)
- Total resources given out
- Quantity metric
- Impact indicator

**Average Rating Card** (⭐)
- Average user satisfaction rating
- Scale of 1-5
- Quality metric


#### Detailed Reports Tabs

**Tab 1: Request Statistics**
- Table showing statistics by category
- Columns: Category, Total, Pending, Completed, Completion %
- Sortable columns
- Export to CSV option

**Tab 2: User Activity**
- Table showing user engagement
- Columns: User, Role, Requests, Completed, Last Active
- Filter by role
- Identify most active users

**Tab 3: Resource Usage**
- Table showing resource distribution
- Columns: Resource, Category, Requests, Distributed, Available
- Track popular resources
- Inventory management insights

### System Settings Page

#### Header Controls

**Save Settings Button** (💾 Save Settings)
- Saves all configuration changes
- Validates input before saving
- Shows success/error message
- Primary action button

**Reset Button** (🔄 Reset)
- Resets all settings to defaults
- Confirmation dialog required
- Secondary action button

#### Settings Tabs

**Tab 1: General Settings**

**Application Settings Section:**
- **Application Name Field**: System name (editable)
- **Organization Field**: Organization name
- **Contact Email Field**: Support email address
- **Support Phone Field**: Contact phone number

**System Preferences Section:**
- **Enable Email Notifications Checkbox**: Toggle email alerts
- **Auto-assign Requests Checkbox**: Automatic volunteer assignment
- **Require Admin Approval Checkbox**: New user approval required
- **Enable Feedback System Checkbox**: Allow user feedback


**Tab 2: Request Settings**

**Request Management Section:**
- **Max Requests per User Spinner**: Limit requests (1-50)
- **Request Timeout Spinner**: Hours before timeout (1-168)
- **Priority Levels Dropdown**: Number of urgency levels (3-5)

**Urgency Thresholds Section:**
- **Enable Urgency Alerts Checkbox**: Alert for urgent requests
- **Auto-escalate Critical Checkbox**: Automatic escalation
- **Critical Alert Time Spinner**: Hours before alert (1-48)

**Tab 3: Database Settings**

**Database Configuration Section:**
- **Database Type Dropdown**: SQLite, MySQL, PostgreSQL
- **Connection Pool Size Spinner**: Concurrent connections (5-100)
- **Backup Frequency Dropdown**: Daily, Weekly, Monthly, Manual

**Maintenance Section:**
- **Backup Now Button**: Create immediate database backup
  - Opens confirmation dialog
  - Shows progress indicator
  - Updates last backup timestamp
  - Opens backup folder on completion

- **Optimize Database Button**: Optimize tables and indexes
  - Confirmation required
  - May take several moments
  - Shows success message

- **Clear Cache Button**: Clear application cache
  - Confirmation required
  - Improves performance
  - Quick operation

**Status Labels:**
- **Database Status**: Shows connection status
- **Last Backup**: Displays last backup date/time


**Tab 4: Security Settings**

**Authentication Settings Section:**
- **Min Password Length Spinner**: Minimum characters (4-32)
- **Session Timeout Spinner**: Minutes of inactivity (5-480)
- **Require Strong Passwords Checkbox**: Enforce complexity
- **Enable Two-Factor Authentication Checkbox**: 2FA requirement
- **Lock Account Checkbox**: Lock after failed attempts

**Data Privacy Section:**
- **Enable Audit Logging Checkbox**: Track all system activities
- **Anonymize Feedback Checkbox**: Hide user identity in feedback
- **GDPR Compliance Mode Checkbox**: European data protection

**Privacy Tools:**
- **View Audit Log Button**: Opens audit log viewer
  - Shows last 10 system activities
  - Table with: Timestamp, User, Action, Details
  - Sortable columns
  - Close button to exit

- **Export User Data Button**: GDPR data export
  - Opens export dialog
  - **User ID/Username Field**: Enter user to export
  - **Export Format Dropdown**: PDF (HTML), JSON, CSV, XML
  - **Include Checkboxes**:
    - Include Requests
    - Include Feedback
    - Include Activity Log
  - **Export Button**: Generates and saves file
  - **Cancel Button**: Closes dialog

**Export Process:**
1. Enter user ID or username
2. Select export format (PDF recommended)
3. Choose data to include
4. Click Export button
5. File is saved to: `C:\Users\[YourName]\CommunityHub_Exports\`
6. Folder opens automatically
7. For PDF: Open HTML file in browser, Print → Save as PDF

**Status Label**
- Shows success/error messages
- Green for success, red for errors
- Displays operation results

---


## 🙋 Volunteer User Guide

**Test Credentials**: Username: `volunteer1` | Password: `pass`

### Dashboard Overview

#### Header Section
- **Title**: "Community Resource Hub"
- **Welcome Label**: "Welcome, [Your Name]"
- **Logout Button**: Sign out safely

#### Sidebar Navigation

**Dashboard Button** (🏠 Dashboard)
- Returns to main dashboard view
- Shows statistics overview
- Default landing page

**Available Requests Button** (📋 Available Requests)
- View all pending help requests
- Requests awaiting volunteers
- Accept new assignments

**My Assignments Button** (📝 My Assignments)
- View your accepted requests
- Update request status
- Track your work

**Resources Button** (📦 Resources)
- Browse available resources
- Search and filter
- View resource details

**Profile Button** (👤 Profile)
- View your volunteer profile
- Update personal information
- View statistics and history

#### Dashboard Statistics Cards

**Available Requests Card** (📋)
- Number of requests awaiting volunteers
- Updates in real-time
- Click to view available requests

**My Assignments Card** (📝)
- Your currently assigned requests
- Active assignments only
- Click to view assignments

**Completed Requests Card** (✅)
- Requests you've completed
- Success metric
- Shows your contribution


### Available Requests Page

#### Header Controls

**Page Title**: "Available Requests"

**Urgency Filter Dropdown**
- Filter by urgency level
- Options: All Urgency, Low, Medium, High, Critical
- Color-coded display

**Refresh Button** (🔄 Refresh)
- Reload available requests
- Get latest pending requests
- Updates table immediately

#### Requests Table

**Columns:**
1. **ID**: Request identifier (truncated)
2. **Resource**: Requested resource name
3. **Requester**: Person who needs help
4. **Urgency**: Priority level with color indicator
5. **Description**: Request details
6. **Date**: When request was submitted
7. **Action**: Accept button

**Accept Button** (per row)
- Click to accept the request
- Opens confirmation dialog
- Shows request details:
  - Resource ID
  - Urgency level
  - Description
- **Confirm**: Assigns request to you
- **Cancel**: Returns to list

**After Accepting:**
- Request moves to "My Assignments"
- Status changes to "Assigned to Volunteer"
- Requester is notified
- You can now update status

### My Assignments Page

#### Header Controls

**Status Filter Dropdown**
- Filter by request status
- Options: All Status, Assigned, In Progress, Completed
- View specific stages

**Refresh Button** (🔄 Refresh)
- Reload your assignments
- Update status changes
- Fetch latest data


#### My Assignments Table

**Columns:**
1. **ID**: Request identifier
2. **Resource**: Resource being provided
3. **Requester**: Person you're helping
4. **Status**: Current request status
5. **Urgency**: Priority level
6. **Date**: Assignment date
7. **Action**: Update Status button

**Update Status Button** (per row)
- Opens status update dialog
- Shows current status
- **New Status Dropdown**:
  - Assigned to Volunteer
  - In Progress
  - Completed
- **Update Button**: Saves new status
- **Cancel Button**: Closes without changes

**Status Workflow:**
1. **Assigned** → Initial state after accepting
2. **In Progress** → When you start working on it
3. **Completed** → When request is fulfilled

### Resources Page

#### Header Section

**Search Bar**
- Search by name, category, or location
- Real-time filtering
- Clear button to reset

**Category Filter Dropdown**
- Filter by resource type
- Options: All Categories, Food, Clothing, Medical, Shelter, etc.
- Updates grid immediately

**Refresh Button** (🔄 Refresh)
- Reload resource list
- Update availability
- Fetch latest quantities


#### Resource Cards Grid

**Card Layout**: 2 columns, responsive grid

**Each Resource Card Shows:**
- **Icon/Label**: Resource type indicator (e.g., "FOOD")
- **Resource Name**: Title (e.g., "Food Bank")
- **Category**: Type (e.g., "Food & Groceries")
- **Availability Badge**: Quantity available (e.g., "150 Items")
- **Description**: Detailed information about resource
- **Details Section**:
  - 📍 **Location**: Where to find it
  - 📞 **Contact**: Phone or email
  - ⏰ **Hours**: Availability times
- **Status Indicator**: Available/Limited/Out of Stock

**Card Themes:**
- **Food Theme**: Warm orange/yellow colors
- **Clothing Theme**: Blue/purple colors
- **Medical Theme**: Red/pink colors
- **Shelter Theme**: Green/teal colors

**Interaction:**
- Hover over card for highlight effect
- Click for more details (if enabled)
- Visual feedback on interaction

### Profile Page

#### Profile Overview Section

**Profile Icon**: Large volunteer icon (🤝)

**Display Name**: Your full name

**Statistics Cards** (3 cards in row):

**Active Assignments Card** (📝)
- Number of current assignments
- Requests in progress
- Large number display

**Completed Requests Card** (✅)
- Total completed requests
- Your contribution count
- Success metric

**Impact Score Card** (⭐)
- Calculated score based on activity
- Formula: (Completed × 10) + (Active × 5)
- Gamification element


#### Account Information Section

**Member Since Label**
- Shows account creation date
- Format: "Member since [Date]"

**Profile Form Fields:**
- **Username Field**: Display only (cannot change)
- **Email Field**: Editable email address
- **Role Field**: Display only (Volunteer)

**Update Profile Button**
- Saves changes to email
- Validates email format
- Shows success/error message

#### Volunteer Preferences Section

**Availability Checkboxes:**
- Weekdays
- Weekends
- Evenings
- Emergency Response

**Preferred Categories** (Multi-select):
- Food & Groceries
- Clothing & Textiles
- Medical Supplies
- Shelter & Housing
- Educational Resources
- Transportation

**Save Preferences Button**
- Stores your preferences
- Used for request matching
- Optional settings

#### Activity History Section

**Recent Activity List:**
- Last 10 activities
- Format: "[Date] - [Action]"
- Examples:
  - "2024-11-18 - Accepted request #REQ-001"
  - "2024-11-17 - Completed request #REQ-002"
  - "2024-11-16 - Updated profile information"

**View Full History Button**
- Opens detailed activity log
- Paginated view
- Export option

---


## 📝 Requester User Guide

**Test Credentials**: Username: `requester1` | Password: `pass`

### Dashboard Overview

#### Header Section
- **Title**: "Community Resource Hub"
- **Welcome Label**: "Welcome, [Your Name]"
- **Logout Button**: Sign out of system

#### Sidebar Navigation

**Home Button** (🏠 Home)
- Main dashboard view
- Statistics overview
- Quick actions

**Resources Button** (📋 Resources)
- Browse available resources
- Search and filter
- Check availability

**My Requests Button** (📝 My Requests)
- View your submitted requests
- Track status
- View history

**New Request Button** (➕ New Request)
- Submit new help request
- Quick access to request form
- Primary action

**Donate Button** (💝 Donate)
- Information about donations
- How to contribute
- Contact details

**Feedback Button** (💬 Feedback)
- Submit feedback
- Rate services
- Provide suggestions

**Profile Button** (👤 Profile)
- View your profile
- Update information
- Account settings

#### Dashboard Statistics Cards

**Total Requests Card**
- All your submitted requests
- Lifetime count
- Click to view all

**Pending Requests Card**
- Requests awaiting assignment
- Yellow indicator
- Requires attention

**Completed Requests Card**
- Successfully fulfilled requests
- Green indicator
- Success metric


### Resources Page

#### Header Controls

**Search Field**
- Search by resource name
- Real-time filtering
- Case-insensitive

**Category Filter Dropdown**
- Filter by category
- Options: All, Food, Clothing, Medical, etc.
- Updates table immediately

**Refresh Button** (🔄 Refresh)
- Reload resource list
- Update availability
- Fetch latest data

#### Resources Table

**Columns:**
1. **Name**: Resource name
2. **Category**: Resource type
3. **Description**: Brief details
4. **Quantity**: Available amount
5. **Location**: Where to find it
6. **Contact**: Contact information
7. **Status**: Available/Limited/Out

**Row Interaction:**
- Click row to view full details
- Hover for highlight
- Double-click to request (if enabled)

### My Requests Page

#### Header Controls

**Request Count Label**
- Shows total number of your requests
- Updates automatically

**Refresh Button** (🔄 Refresh)
- Reload your requests
- Update statuses
- Fetch latest changes


#### Requests Table

**Columns:**
1. **Request ID**: Unique identifier (truncated)
2. **Resource**: Requested resource name
3. **Description**: Your request details
4. **Urgency**: Priority level with color
5. **Status**: Current status badge
6. **Volunteer**: Assigned volunteer (if any)
7. **Date**: Submission date
8. **Actions**: View Details, Cancel

**Status Badges:**
- **⏳ Pending Review**: Awaiting assignment (Yellow)
- **👤 Assigned to Volunteer**: Volunteer assigned (Blue)
- **🔄 In Progress**: Being worked on (Orange)
- **✅ Completed**: Successfully fulfilled (Green)
- **❌ Rejected**: Not approved (Red)

**Action Buttons:**
- **View Details**: Opens full request information
- **Cancel Request**: Cancel pending request (confirmation required)

### New Request Page

#### Request Form

**Resource Selection Dropdown**
- Choose from available resources
- Shows resource name and category
- Required field
- Only active resources shown

**Description Text Area**
- Explain your need in detail
- Why you need this resource
- Any special requirements
- Maximum 500 characters
- Required field

**Urgency Level Dropdown**
- Select priority level
- Options:
  - **Low**: When convenient
  - **Medium**: Within a few days
  - **High**: Urgent, within 24 hours
  - **Critical**: Immediate attention needed
- Required field
- Affects assignment priority

**Quantity Field**
- How many units needed
- Number input (1 or more)
- Default: 1
- Optional field


**Submit Request Button**
- Creates new request
- Validates all required fields
- Shows confirmation message
- Redirects to My Requests page

**Clear Form Button**
- Resets all fields
- Confirmation dialog
- Returns to empty form

**Request Submission Process:**
1. Select a resource from dropdown
2. Enter detailed description
3. Choose urgency level
4. Specify quantity (optional)
5. Click "Submit Request"
6. Wait for confirmation
7. Request appears in "My Requests"
8. Admin/Volunteer will review and assign

### Feedback Page

#### Feedback Form

**Feedback Type Dropdown**
- Select feedback category
- Options:
  - **General**: Overall experience
  - **Request Specific**: About a specific request
  - **System Improvement**: Suggestions for improvement
- Required field

**Request Selection Dropdown** (if Request Specific)
- Choose from your completed requests
- Only shows completed requests
- Optional for other types

**Rating Selector**
- Rate your experience (1-5 stars)
- 1 = Poor, 5 = Excellent
- Click stars to select
- Visual feedback
- Required field

**Comments Text Area**
- Detailed feedback
- What went well
- What could improve
- Maximum 1000 characters
- Required field

**Anonymous Checkbox**
- Submit feedback anonymously
- Your identity hidden from reports
- Optional setting


**Submit Feedback Button**
- Sends your feedback
- Validates required fields
- Shows success message
- Clears form after submission

**Clear Button**
- Resets all fields
- Confirmation required
- Returns to empty form

### Profile Page

#### Profile Information Section

**Display Name Field**
- Your full name
- Editable
- Shown to volunteers

**Username Field**
- Your login username
- Display only (cannot change)

**Email Field**
- Contact email address
- Editable
- Used for notifications

**Role Field**
- Shows "Requester"
- Display only

**Update Profile Button**
- Saves changes
- Validates email format
- Shows success/error message

#### Account Statistics Section

**Member Since Label**
- Account creation date
- Format: "Member since [Date]"

**Statistics Display:**
- **Total Requests**: All submitted requests
- **Pending**: Awaiting assignment
- **Completed**: Successfully fulfilled
- **Success Rate**: Completion percentage

#### Change Password Section

**Current Password Field**
- Enter your current password
- Required for security
- Password hidden

**New Password Field**
- Enter new password
- Minimum 4 characters
- Password hidden

**Confirm New Password Field**
- Re-enter new password
- Must match new password
- Password hidden

**Change Password Button**
- Updates your password
- Validates all fields
- Shows success/error message
- Requires re-login

---


## 🔧 Common Features

### Navigation

**Sidebar Navigation**
- Always visible on left side
- Click any button to switch pages
- Active page highlighted
- Smooth transitions

**Header Bar**
- Always visible at top
- Shows current page title
- Welcome message with your name
- Logout button always accessible

### Logout Process

**Logout Button** (in header)
1. Click "Logout" button
2. Session is terminated
3. Redirected to login screen
4. All unsaved changes are lost
5. Must login again to access system

### Data Refresh

**Refresh Buttons** (🔄)
- Available on most pages
- Reloads data from database
- Updates all displayed information
- Shows loading indicator
- Completes in 1-2 seconds

### Search and Filter

**Search Fields**
- Type to search in real-time
- Case-insensitive matching
- Searches multiple fields
- Clear button (X) to reset

**Filter Dropdowns**
- Click to open options
- Select to apply filter
- "All" option to show everything
- Combines with search

### Tables

**Common Features:**
- Sortable columns (click header)
- Hover row for highlight
- Pagination (if many rows)
- Responsive width
- Scroll for overflow

**Action Buttons:**
- Located in last column
- Icon or text buttons
- Hover for tooltip
- Click for action


### Dialogs and Popups

**Confirmation Dialogs**
- Appear for destructive actions
- "Are you sure?" message
- **OK Button**: Proceed with action
- **Cancel Button**: Abort action
- Click outside to cancel

**Information Dialogs**
- Show success/error messages
- Single OK button
- Auto-close after reading
- Icon indicates type (✓ or ✗)

**Form Dialogs**
- Input fields for data entry
- **Save/Submit Button**: Confirm
- **Cancel Button**: Discard
- Validation before submission

### Notifications

**Success Messages**
- Green color
- Checkmark icon (✓)
- "Operation successful" text
- Auto-dismiss after 3 seconds

**Error Messages**
- Red color
- X icon (✗)
- Describes the error
- Manual dismiss required

**Warning Messages**
- Yellow/Orange color
- Warning icon (⚠)
- Caution information
- Manual dismiss

### Loading Indicators

**Spinner**
- Rotating circle animation
- Appears during data loading
- Blocks interaction
- Disappears when complete

**Progress Bar**
- Shows percentage complete
- For long operations
- Estimated time remaining
- Can be cancelled


### Keyboard Shortcuts

**Global Shortcuts:**
- **Enter**: Submit form/dialog
- **Escape**: Close dialog/cancel
- **Tab**: Navigate between fields
- **Shift+Tab**: Navigate backwards
- **Ctrl+R**: Refresh current page (if available)

**Text Fields:**
- **Ctrl+A**: Select all text
- **Ctrl+C**: Copy selected text
- **Ctrl+V**: Paste text
- **Ctrl+X**: Cut selected text
- **Ctrl+Z**: Undo last change

### Accessibility Features

**Keyboard Navigation**
- All features accessible via keyboard
- Tab order follows logical flow
- Focus indicators visible
- No mouse required

**Screen Reader Support**
- Labels for all form fields
- Alt text for images
- ARIA labels for buttons
- Semantic HTML structure

**Visual Indicators**
- Color-coded status badges
- Icons supplement text
- High contrast mode available
- Adjustable font sizes

---

## 🔍 Troubleshooting

### Login Issues

**Problem**: "Invalid credentials" error
- **Solution**: Check username and password spelling
- Ensure Caps Lock is off
- Try test credentials: admin/pass

**Problem**: "Account is inactive"
- **Solution**: Contact administrator
- Account may need reactivation
- Check email for notifications

**Problem**: Application won't start
- **Solution**: Verify Java 11+ is installed
- Check system requirements
- Review error logs in logs/ folder


### Data Not Loading

**Problem**: Tables show no data
- **Solution**: Click Refresh button (🔄)
- Check internet connection
- Verify database file exists
- Restart application

**Problem**: Statistics show zero
- **Solution**: Refresh the page
- Check if data exists in system
- Review database connection
- Contact administrator

### Form Submission Errors

**Problem**: "Required field" error
- **Solution**: Fill in all required fields
- Look for red asterisks (*)
- Check field validation messages

**Problem**: "Invalid email format"
- **Solution**: Use format: name@domain.com
- No spaces allowed
- Check for typos

**Problem**: Form won't submit
- **Solution**: Check all validations pass
- Ensure network connection
- Try refreshing page
- Check error messages

### Performance Issues

**Problem**: Application is slow
- **Solution**: Close unused windows
- Clear cache (Admin → Settings → Database → Clear Cache)
- Restart application
- Check system resources

**Problem**: Pages take long to load
- **Solution**: Click Refresh to reload
- Check database size
- Optimize database (Admin only)
- Reduce data range in reports


### Export Issues

**Problem**: Export file won't open
- **Solution**: 
  - For PDF exports: Open HTML file in browser first
  - Use Print → Save as PDF in browser
  - Check file isn't corrupted
  - Try different export format

**Problem**: Can't find exported file
- **Solution**: 
  - Check: `C:\Users\[YourName]\CommunityHub_Exports\`
  - Look for filename with timestamp
  - Use Windows Search
  - Try exporting again

### Database Issues

**Problem**: "Database connection error"
- **Solution**: 
  - Check community_hub.db file exists
  - Verify file permissions
  - Restart application
  - Contact administrator

**Problem**: Data not saving
- **Solution**: 
  - Check disk space available
  - Verify write permissions
  - Check database isn't locked
  - Review error logs

### Getting Help

**Contact Support:**
- **Email**: admin@communityhub.org
- **Phone**: +1-555-0100
- **Hours**: Monday-Friday, 9 AM - 5 PM

**Before Contacting Support:**
1. Note the exact error message
2. Check logs/ folder for error details
3. Try restarting the application
4. Document steps to reproduce issue
5. Note your username and role

**Include in Support Request:**
- Your username (not password!)
- What you were trying to do
- Error message received
- Steps to reproduce
- Screenshots (if applicable)
- Log files from logs/ folder

---

## 📚 Additional Resources

### Video Tutorials
- Coming soon: Video walkthroughs for each role
- Step-by-step guides
- Common workflows

### FAQ
- Frequently asked questions
- Quick answers
- Common scenarios

### Best Practices
- Tips for efficient use
- Workflow recommendations
- Security guidelines

---

<div align="center">

**Need More Help?**

Contact your system administrator or email support@communityhub.org

**Version**: 1.0.0 | **Last Updated**: November 2024

[⬆ Back to Top](#-community-resource-hub---user-guide)

</div>
