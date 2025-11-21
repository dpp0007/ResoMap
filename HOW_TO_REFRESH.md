# 🔄 How to Refresh App Changes Without Recompiling

## ✨ Quick Refresh Options

### 1. **CSS Changes Only** (Fastest)
If you only modify `.css` files:
```bash
# Just copy the CSS file to target directory
copy "src\main\resources\css\styles.css" "target\classes\css\styles.css"
```
- CSS changes are often applied immediately in JavaFX
- Sometimes you may need to minimize/restore the window to see changes

### 2. **FXML Changes Only**
If you only modify `.fxml` files:
```bash
# Copy FXML files to target directory
copy "src\main\resources\fxml\*.fxml" "target\classes\fxml\"
```

### 3. **Resource Files (Images, Icons)**
If you add/modify images or icons:
```bash
# Copy entire assets directory
xcopy "src\main\resources\assets" "target\classes\assets" /E /Y
```

### 4. **Quick Compile (No Full Restart)**
For Java code changes, use incremental compile:
```bash
mvn compile -q
```
This is faster than full `mvn javafx:run`

## 🚀 Development Workflow

### Hot Reload Setup (Recommended)
1. Keep your app running
2. Make changes to CSS/FXML files
3. Run the appropriate copy command above
4. In your running app, minimize and restore window to trigger refresh

### IDE Integration
- **IntelliJ IDEA**: Enable "Build project automatically"
- **Eclipse**: Auto-build is usually enabled by default
- **VS Code**: Use Java extension with auto-compile

## 📁 File-Specific Refresh Commands

### CSS Changes
```bash
# Windows
copy "src\main\resources\css\styles.css" "target\classes\css\styles.css"

# PowerShell
Copy-Item "src\main\resources\css\styles.css" "target\classes\css\styles.css"
```

### FXML Changes
```bash
# Windows
copy "src\main\resources\fxml\requester-dashboard.fxml" "target\classes\fxml\requester-dashboard.fxml"

# PowerShell  
Copy-Item "src\main\resources\fxml\requester-dashboard.fxml" "target\classes\fxml\requester-dashboard.fxml"
```

### Icon/Image Changes
```bash
# Windows
xcopy "src\main\resources\assets\icons\resources.png" "target\classes\assets\icons\" /Y

# PowerShell
Copy-Item "src\main\resources\assets\icons\resources.png" "target\classes\assets\icons\resources.png"
```

## ⚡ Pro Tips

1. **Use File Watchers**: Set up your IDE to automatically copy changed files to target directory
2. **CSS Live Reload**: JavaFX supports CSS hot reload - changes often apply instantly
3. **Window Refresh Trick**: Minimize/maximize window to force UI refresh
4. **Development Profile**: Create a Maven profile for faster compilation
5. **Background Compile**: Keep `mvn compile -q` running in a separate terminal

## 🛠 Batch Script for Quick Refresh

Create `refresh.bat` in your project root:
```batch
@echo off
echo Refreshing application resources...
copy "src\main\resources\css\*.css" "target\classes\css\" >nul 2>&1
copy "src\main\resources\fxml\*.fxml" "target\classes\fxml\" >nul 2>&1
xcopy "src\main\resources\assets" "target\classes\assets" /E /Y >nul 2>&1
echo ✅ Resources refreshed! Minimize/restore app window to see changes.
```

Then just run `refresh.bat` after making changes!

## 🔧 Alternative: Use Maven Spring Boot DevTools Pattern

For even faster development, consider:
1. Maven Exec plugin with auto-restart
2. JRebel for Java hot reload
3. JavaFX Scene Builder for FXML live editing

---
*This setup dramatically speeds up your development workflow by avoiding full application restarts!*