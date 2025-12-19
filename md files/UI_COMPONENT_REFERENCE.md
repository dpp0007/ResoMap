# ResoMap UI Component Reference

## Quick Start

### CSS Import
All pages should link to the main stylesheet:
```html
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
```

This automatically imports all CSS modules (base, components, layout, pages).

## Component Usage Guide

### Buttons

#### Primary Button
```html
<button class="btn btn-primary">Click Me</button>
<a href="#" class="btn btn-primary">Link Button</a>
```

#### Button Variants
```html
<button class="btn btn-primary">Primary</button>
<button class="btn btn-secondary">Secondary</button>
<button class="btn btn-success">Success</button>
<button class="btn btn-danger">Danger</button>
<button class="btn btn-warning">Warning</button>
<button class="btn btn-outline">Outline</button>
<button class="btn btn-ghost">Ghost</button>
```

#### Button Sizes
```html
<button class="btn btn-sm btn-primary">Small</button>
<button class="btn btn-primary">Default</button>
<button class="btn btn-lg btn-primary">Large</button>
```

#### Button States
```html
<button class="btn btn-primary">Normal</button>
<button class="btn btn-primary" disabled>Disabled</button>
<button class="btn btn-primary btn-loading">Loading</button>
<button class="btn btn-primary btn-block">Full Width</button>
```

### Forms

#### Form Group
```html
<div class="form-group">
    <label for="username">Username</label>
    <input type="text" id="username" name="username" placeholder="Enter username" required>
    <small>Help text goes here</small>
    <span class="error-text" id="usernameError"></span>
</div>
```

#### Form Row (Multiple Columns)
```html
<div class="form-row">
    <div class="form-group">
        <label for="firstName">First Name</label>
        <input type="text" id="firstName" name="firstName">
    </div>
    <div class="form-group">
        <label for="lastName">Last Name</label>
        <input type="text" id="lastName" name="lastName">
    </div>
</div>
```

#### Select Input
```html
<div class="form-group">
    <label for="role">Role</label>
    <select id="role" name="role" required>
        <option value="">Select a role</option>
        <option value="ADMIN">Admin</option>
        <option value="VOLUNTEER">Volunteer</option>
        <option value="REQUESTER">Requester</option>
    </select>
</div>
```

#### Textarea
```html
<div class="form-group">
    <label for="description">Description</label>
    <textarea id="description" name="description" placeholder="Enter description" rows="4"></textarea>
</div>
```

### Cards

#### Basic Card
```html
<div class="card">
    <div class="card-body">
        <h3>Card Title</h3>
        <p>Card content goes here</p>
    </div>
</div>
```

#### Card with Header and Footer
```html
<div class="card">
    <div class="card-header">
        <h3>Card Title</h3>
    </div>
    <div class="card-body">
        <p>Card content goes here</p>
    </div>
    <div class="card-footer">
        <button class="btn btn-primary">Action</button>
    </div>
</div>
```

### Badges

#### Badge Variants
```html
<span class="badge badge-primary">Primary</span>
<span class="badge badge-success">Success</span>
<span class="badge badge-danger">Danger</span>
<span class="badge badge-warning">Warning</span>
<span class="badge badge-info">Info</span>
<span class="badge badge-gray">Gray</span>
```

#### Status Badges
```html
<span class="status-badge status-pending">Pending</span>
<span class="status-badge status-assigned">Assigned</span>
<span class="status-badge status-in-progress">In Progress</span>
<span class="status-badge status-completed">Completed</span>
<span class="status-badge status-cancelled">Cancelled</span>
```

### Alerts

#### Alert Types
```html
<div class="alert alert-success">
    <span>Success message</span>
</div>

<div class="alert alert-error">
    <span>Error message</span>
</div>

<div class="alert alert-warning">
    <span>Warning message</span>
</div>

<div class="alert alert-info">
    <span>Info message</span>
</div>
```

#### Alert with Close Button
```html
<div class="alert alert-success">
    <span>Success message</span>
    <button class="alert-close" onclick="this.parentElement.style.display='none';">&times;</button>
</div>
```

### Tables

#### Basic Table
```html
<table class="table">
    <thead>
        <tr>
            <th>Column 1</th>
            <th>Column 2</th>
            <th>Column 3</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td>Data 1</td>
            <td>Data 2</td>
            <td>Data 3</td>
        </tr>
    </tbody>
</table>
```

### Modals

#### Modal Structure
```html
<div class="modal" id="myModal">
    <div class="modal-content">
        <div class="modal-header">
            <h2>Modal Title</h2>
            <button class="modal-close" onclick="closeModal()">&times;</button>
        </div>
        <div class="modal-body">
            <p>Modal content goes here</p>
        </div>
        <div class="modal-footer">
            <button class="btn btn-secondary" onclick="closeModal()">Cancel</button>
            <button class="btn btn-primary">Confirm</button>
        </div>
    </div>
</div>
```

#### Modal JavaScript
```javascript
function openModal() {
    document.getElementById('myModal').classList.add('active');
}

function closeModal() {
    document.getElementById('myModal').classList.remove('active');
}

// Close on Escape key
document.addEventListener('keydown', function(event) {
    if (event.key === 'Escape') {
        closeModal();
    }
});
```

### Stat Cards

#### Stat Card
```html
<div class="stat-card">
    <div class="stat-card-label">Total Resources</div>
    <div class="stat-card-value">42</div>
    <div class="stat-card-change">+5 this week</div>
</div>
```

#### Stat Card with Color
```html
<div class="stat-card" style="border-left-color: var(--success);">
    <div class="stat-card-label">Completed</div>
    <div class="stat-card-value" style="color: var(--success);">28</div>
</div>
```

### Grids

#### 2-Column Grid
```html
<div class="grid grid-2">
    <div class="card">Item 1</div>
    <div class="card">Item 2</div>
</div>
```

#### 3-Column Grid
```html
<div class="grid grid-3">
    <div class="card">Item 1</div>
    <div class="card">Item 2</div>
    <div class="card">Item 3</div>
</div>
```

#### 4-Column Grid
```html
<div class="grid grid-4">
    <div class="card">Item 1</div>
    <div class="card">Item 2</div>
    <div class="card">Item 3</div>
    <div class="card">Item 4</div>
</div>
```

### Sections

#### Section with Header
```html
<div class="section">
    <div class="section-header">
        <h2 class="section-title">Section Title</h2>
        <div class="section-actions">
            <button class="btn btn-sm btn-primary">Action</button>
        </div>
    </div>
    <p>Section content goes here</p>
</div>
```

### Empty States

#### Empty State
```html
<div class="empty-state">
    <div class="empty-state-icon">📭</div>
    <div class="empty-state-title">No Items</div>
    <div class="empty-state-text">There are no items to display</div>
</div>
```

### Utility Classes

#### Spacing
```html
<div class="mt-lg">Margin top large</div>
<div class="mb-md">Margin bottom medium</div>
<div class="p-lg">Padding large</div>
```

#### Flexbox
```html
<div class="flex gap-md">
    <div>Item 1</div>
    <div>Item 2</div>
</div>

<div class="flex-center">Centered content</div>

<div class="flex-between">
    <div>Left</div>
    <div>Right</div>
</div>
```

#### Text
```html
<p class="text-center">Centered text</p>
<p class="text-muted">Muted text</p>
<p class="text-small">Small text</p>
<p class="text-large">Large text</p>
```

#### Visibility
```html
<div class="hidden">Hidden element</div>
<div class="sr-only">Screen reader only</div>
```

### Animations

#### Animation Classes
```html
<div class="animate-fade-in">Fade in</div>
<div class="animate-slide-in-down">Slide in down</div>
<div class="animate-slide-in-up">Slide in up</div>
<div class="animate-slide-in-left">Slide in left</div>
<div class="animate-slide-in-right">Slide in right</div>
<div class="animate-scale-in">Scale in</div>
```

## CSS Variables

### Colors
```css
--primary: #2563eb
--primary-dark: #1e40af
--primary-light: #3b82f6
--secondary: #10b981
--success: #10b981
--warning: #f59e0b
--danger: #ef4444
--info: #3b82f6
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

## Responsive Design

### Mobile-First Approach
```css
/* Mobile (default) */
.grid { grid-template-columns: 1fr; }

/* Tablet and up */
@media (min-width: 768px) {
    .grid { grid-template-columns: repeat(2, 1fr); }
}

/* Desktop and up */
@media (min-width: 1200px) {
    .grid { grid-template-columns: repeat(4, 1fr); }
}
```

### Breakpoints
- **Mobile**: < 480px
- **Tablet**: 480px - 767px
- **Desktop**: 768px - 1199px
- **Large Desktop**: 1200px+

## Accessibility

### Focus States
All interactive elements have visible focus states:
```css
:focus-visible {
    outline: 2px solid var(--primary);
    outline-offset: 2px;
}
```

### Color Contrast
- Normal text: 4.5:1 minimum
- Large text: 3:1 minimum
- UI components: 3:1 minimum

### Keyboard Navigation
- Tab order is logical
- All buttons are keyboard accessible
- Modals trap focus
- Escape key closes modals

## Best Practices

### Do's
✅ Use semantic HTML
✅ Use CSS variables for colors
✅ Use utility classes for spacing
✅ Use proper heading hierarchy
✅ Test on mobile devices
✅ Use focus-visible for keyboard nav
✅ Provide alt text for images
✅ Use proper form labels

### Don'ts
❌ Don't use inline styles
❌ Don't hardcode colors
❌ Don't skip form labels
❌ Don't use color alone for status
❌ Don't remove focus outlines
❌ Don't use too many animations
❌ Don't forget mobile testing
❌ Don't break semantic HTML

## Support

For questions or issues with the UI components, refer to:
- `UI_REFACTOR_SUMMARY.md` - Complete refactor documentation
- `src/main/webapp/css/` - CSS source files
- `src/main/webapp/jsp/` - JSP template examples
