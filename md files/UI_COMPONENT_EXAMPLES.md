# ResoMap UI - Component Examples & Code Snippets

**Copy-paste ready examples for common UI patterns**

---

## Dashboard Layout

### Complete Dashboard Structure
```html
<div class="main-layout">
    <main class="main-content">
        <!-- Page Header -->
        <div class="page-header">
            <h1 class="page-title">Dashboard</h1>
        </div>
        
        <!-- Primary Metrics (3 cards) -->
        <div class="grid grid-3 mb-lg">
            <div class="stat-card primary-metric">
                <div class="stat-card-label">📊 Active Requests</div>
                <div class="stat-card-value">8</div>
                <div class="stat-card-change">requests in progress</div>
            </div>
            <div class="stat-card primary-metric success-variant">
                <div class="stat-card-label">✅ Completed</div>
                <div class="stat-card-value">23</div>
                <div class="stat-card-change">requests finished</div>
            </div>
            <div class="stat-card primary-metric warning-variant">
                <div class="stat-card-label">📦 Total Resources</div>
                <div class="stat-card-value">42</div>
                <div class="stat-card-change">available items</div>
            </div>
        </div>
        
        <!-- Secondary Metrics Section -->
        <div class="section mb-lg">
            <div class="section-header">
                <h2 class="section-title">System Overview</h2>
            </div>
            <div class="grid grid-2">
                <div class="stat-card secondary-metric">
                    <div class="stat-card-label">👥 Total Volunteers</div>
                    <div class="stat-card-value">15</div>
                    <div class="stat-card-change">active volunteers</div>
                </div>
                <div class="stat-card secondary-metric">
                    <div class="stat-card-label">📈 System Health</div>
                    <div class="stat-card-value">100%</div>
                    <div class="stat-card-change">all systems operational</div>
                </div>
            </div>
        </div>
        
        <!-- Quick Actions -->
        <div class="section mb-lg">
            <div class="section-header">
                <h2 class="section-title">Quick Actions</h2>
            </div>
            <div class="quick-actions-grid">
                <a href="#" class="action-card action-primary">
                    <span class="action-icon">➕</span>
                    <span class="action-text">Create Request</span>
                </a>
                <a href="#" class="action-card action-secondary">
                    <span class="action-icon">📦</span>
                    <span class="action-text">Browse Resources</span>
                </a>
            </div>
        </div>
        
        <!-- Recent Activity -->
        <div class="section">
            <div class="section-header">
                <h2 class="section-title">Recent Activity</h2>
            </div>
            <ul class="activity-timeline">
                <li class="activity-item">
                    <div class="activity-dot"></div>
                    <div class="activity-content">
                        <div class="activity-time">2 hours ago</div>
                        <div class="activity-description">User created request #42</div>
                    </div>
                </li>
                <li class="activity-item">
                    <div class="activity-dot"></div>
                    <div class="activity-content">
                        <div class="activity-time">4 hours ago</div>
                        <div class="activity-description">Volunteer accepted assignment</div>
                    </div>
                </li>
            </ul>
        </div>
    </main>
</div>
```

---

## Sidebar Navigation

### Complete Sidebar
```html
<aside class="sidebar">
    <!-- Brand Section -->
    <div class="sidebar-brand">
        <span class="sidebar-brand-icon">🗺️</span>
        <span class="sidebar-brand-text">ResoMap</span>
    </div>
    
    <!-- Navigation Menu -->
    <ul class="sidebar-menu">
        <li class="sidebar-menu-item">
            <a href="/dashboard" class="sidebar-menu-link active">
                <span class="sidebar-menu-icon">📊</span>
                <span class="sidebar-menu-label">Dashboard</span>
            </a>
        </li>
        <li class="sidebar-menu-item">
            <a href="/resources" class="sidebar-menu-link">
                <span class="sidebar-menu-icon">📦</span>
                <span class="sidebar-menu-label">Resources</span>
            </a>
        </li>
        <li class="sidebar-menu-item">
            <a href="/requests" class="sidebar-menu-link">
                <span class="sidebar-menu-icon">📋</span>
                <span class="sidebar-menu-label">Requests</span>
            </a>
        </li>
        <li class="sidebar-menu-item">
            <a href="/admin" class="sidebar-menu-link">
                <span class="sidebar-menu-icon">⚙️</span>
                <span class="sidebar-menu-label">Admin Panel</span>
            </a>
        </li>
    </ul>
    
    <!-- Footer with Role & Logout -->
    <div class="sidebar-footer">
        <div class="sidebar-footer-text">Current Role</div>
        <div class="sidebar-footer-content">
            <span class="sidebar-footer-role">ADMIN</span>
            <a href="/logout" class="sidebar-logout-btn">🚪 Logout</a>
        </div>
    </div>
</aside>
```

---

## Stat Cards

### Primary Metric Card (Blue)
```html
<div class="stat-card primary-metric">
    <div class="stat-card-label">📊 Active Requests</div>
    <div class="stat-card-value">8</div>
    <div class="stat-card-change">requests in progress</div>
</div>
```

### Primary Metric Card (Green - Success)
```html
<div class="stat-card primary-metric success-variant">
    <div class="stat-card-label">✅ Completed</div>
    <div class="stat-card-value">23</div>
    <div class="stat-card-change">requests finished</div>
</div>
```

### Primary Metric Card (Orange - Warning)
```html
<div class="stat-card primary-metric warning-variant">
    <div class="stat-card-label">📦 Total Resources</div>
    <div class="stat-card-value">42</div>
    <div class="stat-card-change">available items</div>
</div>
```

### Primary Metric Card (Red - Danger)
```html
<div class="stat-card primary-metric danger-variant">
    <div class="stat-card-label">❌ Cancelled</div>
    <div class="stat-card-value">3</div>
    <div class="stat-card-change">cancelled requests</div>
</div>
```

### Secondary Metric Card
```html
<div class="stat-card secondary-metric">
    <div class="stat-card-label">👥 Total Volunteers</div>
    <div class="stat-card-value">15</div>
    <div class="stat-card-change">active volunteers</div>
</div>
```

---

## Buttons

### Primary Button
```html
<a href="#" class="btn btn-primary">Primary Action</a>
```

### Secondary Button
```html
<a href="#" class="btn btn-secondary">Secondary Action</a>
```

### Success Button
```html
<a href="#" class="btn btn-success">Success Action</a>
```

### Danger Button
```html
<a href="#" class="btn btn-danger">Delete</a>
```

### Warning Button
```html
<a href="#" class="btn btn-warning">Warning</a>
```

### Outline Button
```html
<a href="#" class="btn btn-outline">Outline</a>
```

### Ghost Button
```html
<a href="#" class="btn btn-ghost">Ghost</a>
```

### Button Sizes
```html
<a href="#" class="btn btn-primary btn-sm">Small</a>
<a href="#" class="btn btn-primary">Medium (default)</a>
<a href="#" class="btn btn-primary btn-lg">Large</a>
```

### Button Block (Full Width)
```html
<a href="#" class="btn btn-primary btn-block">Full Width</a>
```

---

## Quick Action Cards

### Primary Action Card
```html
<a href="#" class="action-card action-primary">
    <span class="action-icon">➕</span>
    <span class="action-text">Create Request</span>
</a>
```

### Secondary Action Card
```html
<a href="#" class="action-card action-secondary">
    <span class="action-icon">📦</span>
    <span class="action-text">Browse Resources</span>
</a>
```

### Action Cards Grid
```html
<div class="quick-actions-grid">
    <a href="#" class="action-card action-primary">
        <span class="action-icon">➕</span>
        <span class="action-text">Create Request</span>
    </a>
    <a href="#" class="action-card action-primary">
        <span class="action-icon">👀</span>
        <span class="action-text">View Requests</span>
    </a>
    <a href="#" class="action-card action-secondary">
        <span class="action-icon">📦</span>
        <span class="action-text">Browse Resources</span>
    </a>
</div>
```

---

## Sections

### Basic Section
```html
<div class="section">
    <div class="section-header">
        <h2 class="section-title">Section Title</h2>
    </div>
    <!-- Content here -->
</div>
```

### Section with Actions
```html
<div class="section">
    <div class="section-header">
        <h2 class="section-title">Section Title</h2>
        <div class="section-actions">
            <a href="#" class="btn btn-sm btn-primary">Action</a>
        </div>
    </div>
    <!-- Content here -->
</div>
```

### Section with Grid
```html
<div class="section mb-lg">
    <div class="section-header">
        <h2 class="section-title">System Overview</h2>
    </div>
    <div class="grid grid-2">
        <div class="stat-card secondary-metric">
            <!-- Card content -->
        </div>
        <div class="stat-card secondary-metric">
            <!-- Card content -->
        </div>
    </div>
</div>
```

---

## Alerts

### Success Alert
```html
<div class="alert alert-success">
    ✅ Operation completed successfully!
</div>
```

### Error Alert
```html
<div class="alert alert-error">
    ❌ An error occurred. Please try again.
</div>
```

### Warning Alert
```html
<div class="alert alert-warning">
    ⚠️ Please review before proceeding.
</div>
```

### Info Alert
```html
<div class="alert alert-info">
    ℹ️ This is an informational message.
</div>
```

---

## Badges

### Primary Badge
```html
<span class="badge badge-primary">Primary</span>
```

### Success Badge
```html
<span class="badge badge-success">Success</span>
```

### Danger Badge
```html
<span class="badge badge-danger">Danger</span>
```

### Warning Badge
```html
<span class="badge badge-warning">Warning</span>
```

### Info Badge
```html
<span class="badge badge-info">Info</span>
```

### Gray Badge
```html
<span class="badge badge-gray">Gray</span>
```

---

## Status Badges

### Pending Status
```html
<span class="status-badge status-pending">PENDING</span>
```

### Assigned Status
```html
<span class="status-badge status-assigned">ASSIGNED</span>
```

### In Progress Status
```html
<span class="status-badge status-in-progress">IN PROGRESS</span>
```

### Completed Status
```html
<span class="status-badge status-completed">COMPLETED</span>
```

### Cancelled Status
```html
<span class="status-badge status-cancelled">CANCELLED</span>
```

---

## Forms

### Basic Form Group
```html
<div class="form-group">
    <label for="name">Name</label>
    <input type="text" id="name" name="name" placeholder="Enter name">
</div>
```

### Form Group with Required
```html
<div class="form-group">
    <label for="email">Email <span class="required">*</span></label>
    <input type="email" id="email" name="email" placeholder="Enter email">
</div>
```

### Form Group with Helper Text
```html
<div class="form-group">
    <label for="password">Password</label>
    <input type="password" id="password" name="password">
    <small>Must be at least 8 characters</small>
</div>
```

### Form Group with Error
```html
<div class="form-group">
    <label for="username">Username</label>
    <input type="text" id="username" name="username">
    <span class="error-text">Username is already taken</span>
</div>
```

### Select Dropdown
```html
<div class="form-group">
    <label for="role">Role</label>
    <select id="role" name="role">
        <option value="">Select a role</option>
        <option value="admin">Admin</option>
        <option value="volunteer">Volunteer</option>
        <option value="requester">Requester</option>
    </select>
</div>
```

### Textarea
```html
<div class="form-group">
    <label for="message">Message</label>
    <textarea id="message" name="message" placeholder="Enter your message"></textarea>
</div>
```

### Complete Form
```html
<form>
    <div class="form-group">
        <label for="name">Name <span class="required">*</span></label>
        <input type="text" id="name" name="name" required>
    </div>
    
    <div class="form-group">
        <label for="email">Email <span class="required">*</span></label>
        <input type="email" id="email" name="email" required>
    </div>
    
    <div class="form-group">
        <label for="role">Role <span class="required">*</span></label>
        <select id="role" name="role" required>
            <option value="">Select a role</option>
            <option value="admin">Admin</option>
            <option value="volunteer">Volunteer</option>
        </select>
    </div>
    
    <div class="form-group">
        <label for="message">Message</label>
        <textarea id="message" name="message"></textarea>
    </div>
    
    <button type="submit" class="btn btn-primary">Submit</button>
    <button type="reset" class="btn btn-secondary">Reset</button>
</form>
```

---

## Activity Timeline

### Single Activity Item
```html
<li class="activity-item">
    <div class="activity-dot"></div>
    <div class="activity-content">
        <div class="activity-time">2 hours ago</div>
        <div class="activity-description">User created request #42</div>
    </div>
</li>
```

### Complete Timeline
```html
<ul class="activity-timeline">
    <li class="activity-item">
        <div class="activity-dot"></div>
        <div class="activity-content">
            <div class="activity-time">2 hours ago</div>
            <div class="activity-description">User created request #42</div>
        </div>
    </li>
    <li class="activity-item">
        <div class="activity-dot"></div>
        <div class="activity-content">
            <div class="activity-time">4 hours ago</div>
            <div class="activity-description">Volunteer accepted assignment</div>
        </div>
    </li>
    <li class="activity-item">
        <div class="activity-dot"></div>
        <div class="activity-content">
            <div class="activity-time">1 day ago</div>
            <div class="activity-description">Request #41 marked completed</div>
        </div>
    </li>
</ul>
```

---

## Grids

### 2-Column Grid
```html
<div class="grid grid-2">
    <div class="stat-card secondary-metric"><!-- Card 1 --></div>
    <div class="stat-card secondary-metric"><!-- Card 2 --></div>
</div>
```

### 3-Column Grid
```html
<div class="grid grid-3">
    <div class="stat-card primary-metric"><!-- Card 1 --></div>
    <div class="stat-card primary-metric"><!-- Card 2 --></div>
    <div class="stat-card primary-metric"><!-- Card 3 --></div>
</div>
```

### 4-Column Grid
```html
<div class="grid grid-4">
    <div class="stat-card"><!-- Card 1 --></div>
    <div class="stat-card"><!-- Card 2 --></div>
    <div class="stat-card"><!-- Card 3 --></div>
    <div class="stat-card"><!-- Card 4 --></div>
</div>
```

---

## Utility Classes

### Spacing Examples
```html
<!-- Margin Top -->
<div class="mt-lg">Large margin top</div>

<!-- Margin Bottom -->
<div class="mb-md">Medium margin bottom</div>

<!-- Padding -->
<div class="p-lg">Large padding</div>
```

### Flexbox Examples
```html
<!-- Flex Container -->
<div class="flex gap-md">
    <div>Item 1</div>
    <div>Item 2</div>
</div>

<!-- Centered Flex -->
<div class="flex-center">
    <span>Centered content</span>
</div>

<!-- Space Between -->
<div class="flex-between">
    <span>Left</span>
    <span>Right</span>
</div>
```

### Text Examples
```html
<p class="text-center">Centered text</p>
<p class="text-right">Right aligned text</p>
<p class="text-muted">Muted gray text</p>
<p class="text-small">Small text</p>
<p class="text-large">Large text</p>
```

---

## Empty States

### Empty State
```html
<div class="empty-state">
    <div class="empty-state-icon">📭</div>
    <div class="empty-state-title">No Activity Yet</div>
    <div class="empty-state-text">Your recent activity will appear here</div>
</div>
```

---

## Cards

### Basic Card
```html
<div class="card">
    <div class="card-header">
        <h3>Card Title</h3>
    </div>
    <div class="card-body">
        Card content goes here
    </div>
    <div class="card-footer">
        <a href="#" class="btn btn-sm btn-primary">Action</a>
    </div>
</div>
```

---

**Version:** 1.0  
**Last Updated:** December 19, 2025  
**Status:** Complete & Ready to Use
