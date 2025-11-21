package com.communityhub.util;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Utility class for managing application icons and images
 * Provides centralized icon loading with caching and fallback support
 */
public class IconManager {
    
    private static final Logger logger = Logger.getLogger(IconManager.class.getName());
    private static IconManager instance;
    private final Map<String, Image> imageCache;
    
    // Icon paths
    private static final String ICON_BASE_PATH = "/assets/icons/";
    private static final String IMAGE_BASE_PATH = "/assets/images/";
    
    // Default icon sizes
    public static final double SMALL_ICON_SIZE = 16.0;
    public static final double MEDIUM_ICON_SIZE = 24.0;
    public static final double LARGE_ICON_SIZE = 32.0;
    public static final double XLARGE_ICON_SIZE = 48.0;
    
    private IconManager() {
        this.imageCache = new HashMap<>();
    }
    
    /**
     * Gets the singleton instance of IconManager
     * @return IconManager instance
     */
    public static synchronized IconManager getInstance() {
        if (instance == null) {
            instance = new IconManager();
        }
        return instance;
    }
    
    /**
     * Loads an icon from the assets/icons folder
     * @param iconName Name of the icon file (without extension)
     * @param size Size of the icon
     * @return ImageView with the loaded icon
     */
    public ImageView getIcon(String iconName, double size) {
        return getIcon(iconName, size, true);
    }
    
    /**
     * Loads an icon with fallback option
     * @param iconName Name of the icon file
     * @param size Size of the icon
     * @param preserveRatio Whether to preserve aspect ratio
     * @return ImageView with the loaded icon
     */
    public ImageView getIcon(String iconName, double size, boolean preserveRatio) {
        Image image = loadImage(ICON_BASE_PATH + iconName + ".png");
        if (image == null) {
            image = loadImage(ICON_BASE_PATH + iconName + ".jpg");
        }
        if (image == null) {
            image = loadImage(ICON_BASE_PATH + iconName + ".svg");
        }
        if (image == null) {
            // Create a fallback colored square
            return createFallbackIcon(size);
        }
        
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(size);
        imageView.setFitHeight(size);
        imageView.setPreserveRatio(preserveRatio);
        imageView.setSmooth(true);
        
        return imageView;
    }
    
    /**
     * Gets a status icon based on request status
     * @param status Request status
     * @param size Icon size
     * @return ImageView with status icon
     */
    public ImageView getStatusIcon(String status, double size) {
        String iconName = "status-" + status.toLowerCase().replace(" ", "-");
        return getIcon(iconName, size);
    }
    
    /**
     * Gets an urgency icon based on urgency level
     * @param urgency Urgency level
     * @param size Icon size
     * @return ImageView with urgency icon
     */
    public ImageView getUrgencyIcon(String urgency, double size) {
        String iconName = "urgency-" + urgency.toLowerCase().replace(" ", "-");
        return getIcon(iconName, size);
    }
    
    /**
     * Gets a category icon based on resource category
     * @param category Resource category
     * @param size Icon size
     * @return ImageView with category icon
     */
    public ImageView getCategoryIcon(String category, double size) {
        String iconName = "category-" + category.toLowerCase().replace(" ", "-").replace("&", "and");
        return getIcon(iconName, size);
    }
    
    /**
     * Gets an action icon for buttons
     * @param action Action name (edit, delete, view, etc.)
     * @param size Icon size
     * @return ImageView with action icon
     */
    public ImageView getActionIcon(String action, double size) {
        String iconName = "action-" + action.toLowerCase();
        return getIcon(iconName, size);
    }
    
    /**
     * Loads an image from the assets/images folder
     * @param imageName Name of the image file
     * @return Image object or null if not found
     */
    public Image getImage(String imageName) {
        return loadImage(IMAGE_BASE_PATH + imageName);
    }
    
    /**
     * Creates commonly used icons programmatically
     */
    public ImageView getRefreshIcon(double size) {
        return getIcon("refresh", size);
    }
    
    public ImageView getEditIcon(double size) {
        return getIcon("edit", size);
    }
    
    public ImageView getDeleteIcon(double size) {
        return getIcon("delete", size);
    }
    
    public ImageView getViewIcon(double size) {
        return getIcon("view", size);
    }
    
    public ImageView getAddIcon(double size) {
        return getIcon("add", size);
    }
    
    public ImageView getSearchIcon(double size) {
        return getIcon("search", size);
    }
    
    public ImageView getFilterIcon(double size) {
        return getIcon("filter", size);
    }
    
    /**
     * Loads an image with caching
     * @param path Path to the image resource
     * @return Image object or null if not found
     */
    private Image loadImage(String path) {
        // Check cache first
        if (imageCache.containsKey(path)) {
            return imageCache.get(path);
        }
        
        try {
            InputStream inputStream = getClass().getResourceAsStream(path);
            if (inputStream != null) {
                Image image = new Image(inputStream);
                imageCache.put(path, image);
                return image;
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to load image: " + path, e);
        }
        
        return null;
    }
    
    /**
     * Creates a fallback icon when the requested icon is not found
     * @param size Size of the fallback icon
     * @return ImageView with a simple colored square
     */
    @SuppressWarnings("unused") // Parameter may be used for future fallback icon sizing
    private ImageView createFallbackIcon(double size) {
        // For now, return null - in a real implementation, you might create
        // a simple colored rectangle or use a default icon
        logger.warning("Icon not found, using fallback");
        return null;
    }
    
    /**
     * Clears the image cache
     */
    public void clearCache() {
        imageCache.clear();
    }
    
    /**
     * Gets cache size for monitoring
     * @return Number of cached images
     */
    public int getCacheSize() {
        return imageCache.size();
    }
}