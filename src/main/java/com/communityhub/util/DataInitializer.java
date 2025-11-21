package com.communityhub.util;

import com.communityhub.exception.DatabaseException;
import com.communityhub.exception.InvalidInputException;
import com.communityhub.model.*;
import com.communityhub.service.AuthenticationService;
import com.communityhub.service.ResourceService;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Utility class to initialize sample data for testing
 */
public class DataInitializer {
    
    private static final Logger logger = Logger.getLogger(DataInitializer.class.getName());
    
    /**
     * Initializes sample data if database is empty
     */
    public static void initializeSampleData() {
        try {
            AuthenticationService authService = new AuthenticationService();
            ResourceService resourceService = new ResourceService();
            
            // Check if we already have users
            if (resourceService.getAllResources().isEmpty()) {
                logger.info("Initializing sample data...");
                
                // Create sample admin user
                try {
                    authService.register("admin", "admin@communityhub.com", "Admin123!", "Admin123!", UserRole.ADMIN);
                    logger.info("Sample admin user created: admin / Admin123!");
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Admin user may already exist", e);
                }
                
                // Create sample volunteer
                try {
                    authService.register("volunteer1", "volunteer@communityhub.com", "Volunteer123!", "Volunteer123!", UserRole.VOLUNTEER);
                    logger.info("Sample volunteer user created: volunteer1 / Volunteer123!");
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Volunteer user may already exist", e);
                }
                
                // Create sample requester
                try {
                    authService.register("user1", "user@communityhub.com", "User123!", "User123!", UserRole.REQUESTER);
                    logger.info("Sample requester user created: user1 / User123!");
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Requester user may already exist", e);
                }
                
                // Create sample resources
                try {
                    Resource food1 = new Resource("Canned Food", "Food", "Non-perishable canned goods for emergency relief");
                    food1.setQuantity(50);
                    food1.setLocation("Community Center - Storage Room A");
                    food1.setContactInfo("contact@communitycenter.org");
                    resourceService.addResource(food1);
                    
                    Resource clothing1 = new Resource("Winter Coats", "Clothing", "Warm winter coats for adults and children");
                    clothing1.setQuantity(25);
                    clothing1.setLocation("Donation Center - Building B");
                    clothing1.setContactInfo("donations@communityhub.com");
                    resourceService.addResource(clothing1);
                    
                    Resource medical1 = new Resource("First Aid Kits", "Medical", "Basic first aid supplies and emergency medical kits");
                    medical1.setQuantity(15);
                    medical1.setLocation("Health Center - Supply Room");
                    medical1.setContactInfo("health@communityhub.com");
                    resourceService.addResource(medical1);
                    
                    Resource shelter1 = new Resource("Emergency Blankets", "Shelter", "Thermal emergency blankets for temporary shelter");
                    shelter1.setQuantity(100);
                    shelter1.setLocation("Emergency Services - Warehouse");
                    shelter1.setContactInfo("emergency@communityhub.com");
                    resourceService.addResource(shelter1);
                    
                    logger.info("Sample resources created successfully");
                    
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Error creating sample resources", e);
                }
                
                logger.info("Sample data initialization completed");
            }
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to initialize sample data", e);
        }
    }
}