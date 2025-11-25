package com.communityhub.core;

/**
 * Application Constants
 * Centralized location for all application-wide constants
 * 
 * Benefits:
 * - Single source of truth
 * - Easy to maintain
 * - Prevents magic numbers/strings
 * - Type-safe constants
 * 
 * @author ResoMap Team
 * @version 2.0
 */
public final class Constants {
    
    // Private constructor prevents instantiation
    private Constants() {
        throw new UnsupportedOperationException("Constants class cannot be instantiated");
    }
    
    // ========== Application Info ==========
    public static final String APP_NAME = "ResoMap";
    public static final String APP_VERSION = "2.0.0";
    public static final String APP_DESCRIPTION = "Community Resource Management Platform";
    
    // ========== Window Dimensions ==========
    public static final class Window {
        public static final int LOGIN_WIDTH = 800;
        public static final int LOGIN_HEIGHT = 600;
        public static final int DASHBOARD_WIDTH = 1200;
        public static final int DASHBOARD_HEIGHT = 800;
        public static final int DIALOG_WIDTH = 600;
        public static final int DIALOG_HEIGHT = 400;
        
        private Window() {}
    }
    
    // ========== FXML Paths ==========
    public static final class FXML {
        public static final String LOGIN = "/fxml/login.fxml";
        public static final String REGISTER = "/fxml/register.fxml";
        public static final String ADMIN_DASHBOARD = "/fxml/admin-dashboard.fxml";
        public static final String VOLUNTEER_DASHBOARD = "/fxml/volunteer-dashboard.fxml";
        public static final String REQUESTER_DASHBOARD = "/fxml/requester-dashboard.fxml";
        public static final String USER_MANAGEMENT = "/fxml/user-management.fxml";
        public static final String RESOURCE_MANAGEMENT = "/fxml/resource-management.fxml";
        public static final String REQUEST_OVERVIEW = "/fxml/request-overview.fxml";
        public static final String SYSTEM_REPORTS = "/fxml/system-reports.fxml";
        public static final String SYSTEM_SETTINGS = "/fxml/system-settings.fxml";
        public static final String NEW_REQUEST = "/fxml/new-request.fxml";
        public static final String RESOURCES = "/fxml/resources.fxml";
        public static final String PLACEHOLDER_CONTENT = "/fxml/placeholder-content.fxml";
        public static final String VOLUNTEER_AVAILABLE_REQUESTS = "/fxml/volunteer-available-requests.fxml";
        public static final String VOLUNTEER_MY_ASSIGNMENTS = "/fxml/volunteer-my-assignments.fxml";
        
        private FXML() {}
    }
    
    // ========== CSS Paths ==========
    public static final class CSS {
        public static final String MAIN_STYLESHEET = "/css/styles.css";
        
        private CSS() {}
    }
    
    // ========== Database Configuration ==========
    public static final class Database {
        public static final String DRIVER = "org.sqlite.JDBC";
        public static final String URL = "jdbc:sqlite:community_hub.db";
        public static final int CONNECTION_TIMEOUT = 30;
        public static final int MAX_POOL_SIZE = 10;
        public static final int MIN_POOL_SIZE = 2;
        
        private Database() {}
    }
    
    // ========== Session Configuration ==========
    public static final class Session {
        public static final int TIMEOUT_HOURS = 8;
        public static final int REFRESH_INTERVAL_MINUTES = 30;
        
        private Session() {}
    }
    
    // ========== Security Configuration ==========
    public static final class Security {
        public static final int MAX_LOGIN_ATTEMPTS = 5;
        public static final long LOCKOUT_DURATION_MS = 15 * 60 * 1000; // 15 minutes
        public static final int PASSWORD_MIN_LENGTH = 8;
        public static final int PASSWORD_MAX_LENGTH = 128;
        public static final int USERNAME_MIN_LENGTH = 3;
        public static final int USERNAME_MAX_LENGTH = 50;
        public static final String PASSWORD_HASH_ALGORITHM = "SHA-256";
        public static final int SALT_LENGTH = 16;
        
        private Security() {}
    }
    
    // ========== Validation Patterns ==========
    public static final class Validation {
        public static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        public static final String USERNAME_PATTERN = "^[a-zA-Z0-9_-]{3,50}$";
        public static final String PHONE_PATTERN = "^[+]?[0-9]{10,15}$";
        
        private Validation() {}
    }
    
    // ========== UI Messages ==========
    public static final class Messages {
        // Success messages
        public static final String LOGIN_SUCCESS = "Login successful!";
        public static final String LOGOUT_SUCCESS = "Logged out successfully.";
        public static final String REGISTRATION_SUCCESS = "Registration successful! Please log in.";
        public static final String SAVE_SUCCESS = "Changes saved successfully.";
        public static final String DELETE_SUCCESS = "Item deleted successfully.";
        
        // Error messages
        public static final String LOGIN_FAILED = "Invalid username or password.";
        public static final String REGISTRATION_FAILED = "Registration failed. Please try again.";
        public static final String DATABASE_ERROR = "Database error occurred. Please try again.";
        public static final String NETWORK_ERROR = "Network error. Please check your connection.";
        public static final String VALIDATION_ERROR = "Please correct the errors and try again.";
        public static final String ACCESS_DENIED = "You don't have permission to perform this action.";
        public static final String SESSION_EXPIRED = "Your session has expired. Please log in again.";
        
        // Confirmation messages
        public static final String CONFIRM_DELETE = "Are you sure you want to delete this item?";
        public static final String CONFIRM_LOGOUT = "Are you sure you want to logout?";
        public static final String CONFIRM_CANCEL = "Are you sure you want to cancel? Unsaved changes will be lost.";
        
        private Messages() {}
    }
    
    // ========== Table Configuration ==========
    public static final class Table {
        public static final int DEFAULT_PAGE_SIZE = 20;
        public static final int MAX_PAGE_SIZE = 100;
        public static final int REFRESH_INTERVAL_SECONDS = 30;
        
        private Table() {}
    }
    
    // ========== Cache Configuration ==========
    public static final class Cache {
        public static final int RESOURCE_CACHE_SIZE = 100;
        public static final int REQUEST_CACHE_SIZE = 200;
        public static final int USER_CACHE_SIZE = 50;
        public static final long CACHE_EXPIRY_MINUTES = 15;
        
        private Cache() {}
    }
    
    // ========== Logging Configuration ==========
    public static final class Logging {
        public static final String LOG_DIRECTORY = "logs";
        public static final String LOG_FILE_PATTERN = "community-hub-%g.log";
        public static final int LOG_FILE_COUNT = 5;
        public static final int LOG_FILE_SIZE_MB = 10;
        
        private Logging() {}
    }
    
    // ========== Date/Time Formats ==========
    public static final class DateFormat {
        public static final String DATE_TIME = "yyyy-MM-dd HH:mm:ss";
        public static final String DATE_ONLY = "yyyy-MM-dd";
        public static final String TIME_ONLY = "HH:mm:ss";
        public static final String DISPLAY_DATE = "MMM dd, yyyy";
        public static final String DISPLAY_DATE_TIME = "MMM dd, yyyy HH:mm";
        
        private DateFormat() {}
    }
    
    // ========== Resource Categories ==========
    public static final class ResourceCategory {
        public static final String FOOD = "Food & Groceries";
        public static final String CLOTHING = "Clothing & Textiles";
        public static final String MEDICAL = "Medical Supplies";
        public static final String SHELTER = "Shelter & Housing";
        public static final String TRANSPORTATION = "Transportation";
        public static final String EDUCATION = "Educational Resources";
        public static final String TOOLS = "Tools & Equipment";
        public static final String TECHNOLOGY = "Technology";
        public static final String OTHER = "Other";
        
        private ResourceCategory() {}
    }
    
    // ========== Request Status Icons ==========
    public static final class Icons {
        public static final String PENDING = "⏳";
        public static final String ASSIGNED = "👤";
        public static final String IN_PROGRESS = "🔄";
        public static final String COMPLETED = "✅";
        public static final String CANCELLED = "❌";
        public static final String CRITICAL = "🔴";
        public static final String HIGH = "🟠";
        public static final String MEDIUM = "🟡";
        public static final String LOW = "🟢";
        
        private Icons() {}
    }
}
