# Assets Folder - Community Resource Hub

This folder contains custom icons, images, and other visual assets for the Community Resource Hub application.

## 📁 Folder Structure

```
assets/
├── icons/          # Custom icons (SVG, PNG, JPG)
│   ├── status-*    # Status indicator icons
│   ├── action-*    # Action button icons
│   ├── category-*  # Resource category icons
│   └── urgency-*   # Urgency level icons
└── images/         # Application images and graphics
```

## 🎨 Icon Guidelines

### File Formats
- **Preferred**: SVG (scalable, crisp at any size)
- **Supported**: PNG (24x24, 32x32, 48x48), JPG

### Naming Conventions

#### Status Icons
- `status-pending.svg` - Pending requests
- `status-assigned.svg` - Assigned requests
- `status-in-progress.svg` - In progress requests
- `status-completed.svg` - Completed requests
- `status-cancelled.svg` - Cancelled requests

#### Action Icons
- `action-edit.svg` - Edit button
- `action-delete.svg` - Delete button
- `action-view.svg` - View details button
- `action-add.svg` - Add new item
- `refresh.svg` - Refresh/reload
- `search.svg` - Search functionality
- `filter.svg` - Filter options

#### Category Icons
- `category-food-and-groceries.svg`
- `category-clothing-and-textiles.svg`
- `category-medical-supplies.svg`
- `category-shelter-and-housing.svg`
- `category-transportation.svg`
- `category-educational-resources.svg`

#### Urgency Icons
- `urgency-low.svg` - Low priority
- `urgency-medium.svg` - Medium priority
- `urgency-high.svg` - High priority
- `urgency-critical.svg` - Critical priority

### Design Standards

#### Colors
- **Primary**: #0096D6 (Blue)
- **Success**: #10B981 (Green)
- **Warning**: #F59E0B (Orange)
- **Danger**: #EF4444 (Red)
- **Info**: #3B82F6 (Blue)
- **Secondary**: #64748B (Gray)

#### Sizes
- **Small**: 16x16px
- **Medium**: 24x24px (default)
- **Large**: 32x32px
- **XLarge**: 48x48px

#### Style Guidelines
- Use clean, minimal designs
- Ensure icons are readable at small sizes
- Maintain consistent stroke width (2px recommended)
- Use rounded corners and smooth curves
- Avoid overly complex details

## 🔧 How to Add Custom Icons

### Method 1: Replace Existing Icons
1. Create your icon using the naming convention above
2. Save it in the appropriate format (SVG preferred)
3. Place it in the `/icons/` folder
4. The application will automatically use your custom icon

### Method 2: Add New Icons
1. Design your icon following the guidelines above
2. Save with a descriptive name (e.g., `custom-notification.svg`)
3. Place in `/icons/` folder
4. Use the IconManager to load it in code:

```java
IconManager iconManager = IconManager.getInstance();
ImageView customIcon = iconManager.getIcon("custom-notification", IconManager.MEDIUM_ICON_SIZE);
```

## 📝 Icon Creation Tools

### Recommended Tools
- **Free**: GIMP, Inkscape, Figma (free tier)
- **Paid**: Adobe Illustrator, Sketch, Figma Pro

### Online Icon Resources
- **Heroicons**: https://heroicons.com/ (MIT License)
- **Feather Icons**: https://feathericons.com/ (MIT License)
- **Tabler Icons**: https://tabler-icons.io/ (MIT License)
- **Phosphor Icons**: https://phosphoricons.com/ (MIT License)

### SVG Template
Use this template for creating custom SVG icons:

```svg
<svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
  <!-- Your icon content here -->
  <path d="..." stroke="#0096D6" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
</svg>
```

## 🎯 Usage Examples

### In Controllers
```java
// Get an icon with specific size
IconManager iconManager = IconManager.getInstance();
ImageView editIcon = iconManager.getEditIcon(IconManager.SMALL_ICON_SIZE);
button.setGraphic(editIcon);

// Get status-specific icon
ImageView statusIcon = iconManager.getStatusIcon("pending", IconManager.MEDIUM_ICON_SIZE);

// Get category icon
ImageView categoryIcon = iconManager.getCategoryIcon("Food & Groceries", IconManager.LARGE_ICON_SIZE);
```

### In FXML (using CSS classes)
```xml
<Button styleClass="icon-button" text="Edit">
  <graphic>
    <!-- Will be loaded via IconManager -->
  </graphic>
</Button>
```

## 🔄 Icon Fallbacks

The `IconManager` provides automatic fallbacks:

1. **Primary**: Looks for custom SVG icon
2. **Secondary**: Looks for PNG version
3. **Tertiary**: Looks for JPG version
4. **Fallback**: Uses default colored square or emoji

## 📱 High DPI Support

For crisp icons on high-DPI displays:

1. **SVG**: Automatically scalable (recommended)
2. **PNG**: Provide @2x and @3x versions:
   - `icon.png` (24x24)
   - `icon@2x.png` (48x48)
   - `icon@3x.png` (72x72)

## 🎨 Color Themes

Icons adapt to the application's color scheme. Use these CSS variables in SVG:

```svg
<svg>
  <path stroke="var(--fx-primary)" fill="var(--fx-primary)"/>
</svg>
```

## 📸 Screenshots

Include screenshots of your custom icons in action:

1. Take screenshots of your customized UI
2. Place in `assets/images/screenshots/`
3. Update this README with examples

## 🤝 Contributing Icons

If you create great icons for the community:

1. Follow the design guidelines above
2. Test icons at different sizes
3. Ensure proper licensing (MIT preferred)
4. Submit via pull request with this folder updated

## 📄 License

Custom icons should be licensed under MIT License or compatible.
Default icons included are from Heroicons (MIT License).

---

**Need Help?**

- Check the IconManager.java class for implementation details
- Review EnhancedTableCellFactory.java for usage examples
- Contact the development team for custom icon requests

**Version**: 1.0.0  
**Last Updated**: November 2024