# ResoMap UI - Quick Reference Guide

**For developers who need to understand the UI system quickly**

---

## 30-Second Overview

ResoMap uses a **modern, light-themed design system** with:
- Clean sidebar navigation (260px, left side)
- Redesigned dashboard with color-coded metrics
- Smooth animations and hover effects
- Fully responsive (desktop, tablet, mobile)
- Pure CSS (no frameworks)

---

## File Location
`src/main/webapp/css/styles.css` (934 lines)

---

## Color System

| Color | Hex | Usage |
|-------|-----|-------|
| Primary Blue | #2563eb | Main actions, active states |
| Success Green | #10b981 | Completed, positive |
| Warning Orange | #f59e0b | Resources, caution |
| Danger Red | #ef4444 | Cancelled, errors |
| Gray (50-900) | Various | Text, backgrounds |

**Access via CSS variables:**
```css
color: var(--primary);
color: var(--success);
color: var(--warning);
color: var(--danger);
```

---

## Spacing System

| Variable | Size | Usage |
|----------|------|-------|
| --space-xs | 4px | Tiny gaps |
| --space-sm | 8px | Small gaps |
| --space-md | 16px | Standard |
| --space-lg | 24px | Large |
| --space-xl | 32px | Extra large |
| --space-2xl | 48px | Huge |

---

## Main Components

### 1. Sidebar Navigation
```html
<aside class="sidebar">
    <div class="sidebar-brand">
        <span class="sidebar-brand-icon">🗺️</span>
        <span class="sidebar-brand-text">ResoMap</span>
    </div>
    <ul class="sidebar-menu">
        <li class="sidebar-menu-item">
            <a href="#" class="sidebar-menu-link active">
                <span class="sidebar-menu-icon">📊</span>
                <span class="sidebar-menu-label">Dashboard</span>
            </a>
        </li>
    </ul>
    <div class="sidebar-footer">
        <span class="sidebar-footer-role">ADMIN</span>
        <a href="#" class="sidebar-logout-btn">🚪 Logout</a>
    </div>
</aside>
```

**Key Classes:**
- `.sidebar` - Main container
- `.sidebar-menu-link.active` - Active navigation item
- `.sidebar-footer-role` - Role badge
- `.sidebar-logout-btn` - Logout button

---

### 2. Stat Cards (Dashboard)

#### Primary Metric Card
```html
<div class="stat-card primary-metric">
    <div class="stat-card-label">📊 Active Requests</div>
    <div class="stat-card-value">8</div>
    <div class="stat-card-change">requests in progress</div>
</div>
```

#### With Color Variant
```html
<div class="stat-card primary-metric success-variant">
    <div class="stat-card-label">✅ Completed</div>
    <div class="stat-card-value">23</div>
    <div class="stat-card-change">requests finished</div>
</div>
```

**Color Variants:**
- `.success-variant` - Green
- `.warning-variant` - Orange
- `.danger-variant` - Red

**Card Types:**
- `.primary-metric` - Large, prominent (40px values)
- `.secondary-metric` - Compact, supporting (28px values)

---

### 3. Quick Action Cards
```html
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
```

**Variants:**
- `.action-primary` - Blue, main action
- `.action-secondary` - Gray, secondary action

---

### 4. Buttons
```html
<a href="#" class="btn btn-primary">Primary</a>
<a href="#" class="btn btn-secondary">Secondary</a>
<a href="#" class="btn btn-success">Success</a>
<a href="#" class="btn btn-danger">Danger</a>
<a href="#" class="btn btn-warning">Warning</a>
```

**Sizes:**
- `.btn-sm` - Small
- `.btn` - Medium (default)
- `.btn-lg` - Large

---

### 5. Sections
```html
<div class="section">
    <div class="section-header">
        <h2 class="section-title">System Overview</h2>
    </div>
    <!-- Content here -->
</div>
```

---

### 6. Alerts
```html
<div class="alert alert-success">✅ Success message</div>
<div class="alert alert-error">❌ Error message</div>
<div class="alert alert-warning">⚠️ Warning message</div>
<div class="alert alert-info">ℹ️ Info message</div>
```

---

### 7. Badges
```html
<span class="badge badge-primary">Primary</span>
<span class="badge badge-success">Success</span>
<span class="badge badge-danger">Danger</span>
```

---

### 8. Status Badges
```html
<span class="status-badge status-pending">PENDING</span>
<span class="status-badge status-assigned">ASSIGNED</span>
<span class="status-badge status-in-progress">IN PROGRESS</span>
<span class="status-badge status-completed">COMPLETED</span>
<span class="status-badge status-cancelled">CANCELLED</span>
```

---

### 9. Activity Timeline
```html
<ul class="activity-timeline">
    <li class="activity-item">
        <div class="activity-dot"></div>
        <div class="activity-content">
            <div class="activity-time">2 hours ago</div>
            <div class="activity-description">User created request</div>
        </div>
    </li>
</ul>
```

---

### 10. Forms
```html
<div class="form-group">
    <label for="name">Name <span class="required">*</span></label>
    <input type="text" id="name" name="name" placeholder="Enter name">
    <small>Helper text</small>
</div>
```

---

## Utility Classes

### Spacing
```html
<div class="mt-lg">Margin top large</div>
<div class="mb-md">Margin bottom medium</div>
<div class="p-lg">Padding large</div>
```

### Flexbox
```html
<div class="flex">Flex container</div>
<div class="flex-center">Centered flex</div>
<div class="flex-between">Space between</div>
```

### Text
```html
<p class="text-center">Centered text</p>
<p class="text-muted">Muted gray text</p>
<p class="text-small">Small text</p>
```

### Display
```html
<div class="hidden">Hidden element</div>
<span class="sr-only">Screen reader only</span>
```

---

## Responsive Breakpoints

```css
Desktop:  1200px+ (full width)
Tablet:   768px - 1199px (adjusted)
Mobile:   < 768px (single column)
```

**Example:**
```css
@media (max-width: 768px) {
    .sidebar { width: 240px; }
    .main-layout { margin-left: 240px; }
}
```

---

## Hover Effects

### Stat Cards
- Lift up: `-4px`
- Shadow: `0 8px 24px rgba(0, 0, 0, 0.12)`
- Top border animates in
- Border color changes

### Action Cards
- Icon scales: `1.15x`
- Lift up: `-4px`
- Shadow enhances
- Gradient intensifies

### Buttons
- Background darkens
- Shadow enhances
- Lift up: `-1px`

### Sidebar Links
- Background: `rgba(37, 99, 235, 0.08)`
- Icon opacity: `0.7 → 1.0`
- Icon translates: `2px right`

---

## Animation Timings

```css
--transition-fast: 150ms (quick)
--transition-base: 200ms (standard)
--transition-slow: 300ms (emphasis)
```

---

## Common Patterns

### Add New Stat Card
```html
<div class="stat-card primary-metric success-variant">
    <div class="stat-card-label">✅ Label</div>
    <div class="stat-card-value">42</div>
    <div class="stat-card-change">description</div>
</div>
```

### Add New Section
```html
<div class="section mb-lg">
    <div class="section-header">
        <h2 class="section-title">Title</h2>
    </div>
    <div class="grid grid-2">
        <!-- Cards here -->
    </div>
</div>
```

### Add New Grid
```html
<div class="grid grid-3">
    <!-- 3 columns on desktop -->
</div>
```

### Add New Alert
```html
<div class="alert alert-success">
    ✅ Success message
</div>
```

---

## CSS Variables Reference

### Colors
```css
--primary: #2563eb
--primary-dark: #1e40af
--primary-light: #3b82f6
--success: #10b981
--warning: #f59e0b
--danger: #ef4444
--gray-50 to --gray-900
```

### Spacing
```css
--space-xs: 4px
--space-sm: 8px
--space-md: 16px
--space-lg: 24px
--space-xl: 32px
--space-2xl: 48px
```

### Radius
```css
--radius-sm: 4px
--radius-md: 8px
--radius-lg: 12px
--radius-full: 9999px
```

### Shadows
```css
--shadow-sm: 0 1px 2px 0 rgba(0, 0, 0, 0.05)
--shadow-md: 0 4px 6px -1px rgba(0, 0, 0, 0.1)
--shadow-lg: 0 10px 15px -3px rgba(0, 0, 0, 0.1)
--shadow-xl: 0 20px 25px -5px rgba(0, 0, 0, 0.1)
```

### Transitions
```css
--transition-fast: 150ms cubic-bezier(0.4, 0, 0.2, 1)
--transition-base: 200ms cubic-bezier(0.4, 0, 0.2, 1)
--transition-slow: 300ms cubic-bezier(0.4, 0, 0.2, 1)
```

---

## Key Features

✅ **Modern Design**
- Light theme sidebar
- Color-coded metrics
- Smooth animations

✅ **Responsive**
- Mobile-first approach
- 3 breakpoints
- Flexible grids

✅ **Accessible**
- WCAG AA compliant
- Keyboard navigation
- Focus states

✅ **Performance**
- Pure CSS (no JS)
- GPU accelerated
- 60fps animations

---

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Underlines on hover | Already fixed in CSS |
| Sidebar not showing | Check responsive breakpoints |
| Colors wrong | Check CSS variables in `:root` |
| Animations stuttering | Use `transform` and `opacity` |
| Layout broken | Check `margin-left` on `.main-layout` |

---

## Next Steps

1. Read `COMPLETE_UI_SUMMARY.md` for detailed documentation
2. Check `src/main/webapp/jsp/dashboard.jsp` for implementation examples
3. Review CSS comments in `styles.css` for specific details
4. Test on different screen sizes (desktop, tablet, mobile)

---

**Version:** 1.0  
**Last Updated:** December 19, 2025  
**Status:** Production Ready
