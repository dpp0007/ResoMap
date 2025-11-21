package com.communityhub.ui.util;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

/**
 * Utility class for loading and managing resource category icons
 */
public class ResourceIconLoader {
    private static final Logger logger = Logger.getLogger(ResourceIconLoader.class.getName());
    private static final Map<String, ImageView> iconCache = new HashMap<>();
    private static ResourceIconLoader instance;
    
    private ResourceIconLoader() {}
    
    public static ResourceIconLoader getInstance() {
        if (instance == null) {
            instance = new ResourceIconLoader();
        }
        return instance;
    }
    
    /**
     * Creates an icon for a resource category
     */
    public ImageView createResourceIcon(String category) {
        return createResourceIcon(category, 48);
    }
    
    /**
     * Creates an icon for a resource category with specified size
     */
    public ImageView createResourceIcon(String category, double size) {
        String cacheKey = category + "_" + size;
        
        if (iconCache.containsKey(cacheKey)) {
            ImageView cached = iconCache.get(cacheKey);
            ImageView copy = new ImageView(cached.getImage());
            copy.setFitWidth(size);
            copy.setFitHeight(size);
            copy.setPreserveRatio(true);
            copy.setSmooth(true);
            return copy;
        }
        
        try {
            String iconPath = getIconPath(category);
            InputStream iconStream = getClass().getResourceAsStream(iconPath);
            
            if (iconStream != null) {
                Image image = new Image(iconStream, size, size, true, true);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(size);
                imageView.setFitHeight(size);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                
                iconCache.put(cacheKey, imageView);
                
                // Return a copy to avoid sharing the same instance
                ImageView copy = new ImageView(image);
                copy.setFitWidth(size);
                copy.setFitHeight(size);
                copy.setPreserveRatio(true);
                copy.setSmooth(true);
                return copy;
            } else {
                logger.warning(() -> "Icon not found for category: " + category);
                return createFallbackIcon(category, size);
            }
        } catch (Exception e) {
            logger.warning(() -> "Error loading icon for category " + category + ": " + e.getMessage());
            return createFallbackIcon(category, size);
        }
    }
    
    /**
     * Maps category names to icon file paths
     */
    private String getIconPath(String category) {
        switch (category.toLowerCase().replace(" ", "")) {
            case "foodassistance":
            case "food":
                return "/assets/icons/food-assistance.svg";
            case "medicalsupport":
            case "medical":
                return "/assets/icons/medical-support.svg";
            case "shelter":
            case "housing":
                return "/assets/icons/shelter.svg";
            case "clothing":
            case "clothes":
                return "/assets/icons/clothing.svg";
            case "transportation":
            case "transport":
                return "/assets/icons/transportation.svg";
            case "education":
            case "learning":
                return "/assets/icons/education.svg";
            default:
                return "/assets/icons/default.svg";
        }
    }
    
    /**
     * Creates a fallback icon when the SVG cannot be loaded
     */
    @SuppressWarnings("unused") // Category parameter may be used for future customization
    private ImageView createFallbackIcon(String category, double size) {
        // Create a simple colored circle as fallback
        StackPane fallback = new StackPane();
        fallback.setPrefSize(size, size);
        fallback.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: " + (size/2) + "px;");
        
        // Convert to ImageView (simplified approach)
        ImageView imageView = new ImageView();
        imageView.setFitWidth(size);
        imageView.setFitHeight(size);
        return imageView;
    }
    
    /**
     * Gets the appropriate color for a resource category
     */
    public static String getCategoryColor(String category) {
        switch (category.toLowerCase().replace(" ", "")) {
            case "foodassistance":
            case "food":
                return "#F97316"; // Orange
            case "medicalsupport":
            case "medical":
                return "#EF4444"; // Red
            case "shelter":
            case "housing":
                return "#6366F1"; // Indigo
            case "clothing":
            case "clothes":
                return "#8B5CF6"; // Purple
            case "transportation":
            case "transport":
                return "#10B981"; // Green
            case "education":
            case "learning":
                return "#0EA5E9"; // Blue
            default:
                return "#6B7280"; // Gray
        }
    }
}