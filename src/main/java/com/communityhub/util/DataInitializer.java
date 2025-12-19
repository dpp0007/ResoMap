package com.communityhub.util;

import com.communityhub.dao.*;
import com.communityhub.exception.DatabaseException;
import com.communityhub.model.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Initializer - COMPREHENSIVE SAMPLE DATA
 * Creates realistic test data for all entities
 */
public final class DataInitializer {
    
    private static final Logger logger = Logger.getLogger(DataInitializer.class.getName());
    private static final Random random = new Random();
    
    private DataInitializer() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    public static void initializeSampleData() {
        logger.info("=== INITIALIZING COMPREHENSIVE SAMPLE DATA ===");
        
        try {
            createSampleUsers();
            createSampleResources();
            createSampleRequests();
            
            logger.info("=== SAMPLE DATA INITIALIZATION COMPLETE ===");
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to initialize sample data", e);
        }
    }
    
    private static void createSampleUsers() {
        logger.info("Creating sample users...");
        
        try {
            UserDAO userDAO = new UserDAO();
            
            // Admin Users (3)
            createUser(userDAO, "admin", "admin@resomap.com", "Admin123!", UserRole.ADMIN);
            createUser(userDAO, "superadmin", "super@resomap.com", "Super123!", UserRole.ADMIN);
            createUser(userDAO, "manager", "manager@resomap.com", "Manager123!", UserRole.ADMIN);
            
            // Volunteer Users (5)
            createUser(userDAO, "volunteer1", "vol1@resomap.com", "Volunteer123!", UserRole.VOLUNTEER);
            createUser(userDAO, "volunteer2", "vol2@resomap.com", "Volunteer123!", UserRole.VOLUNTEER);
            createUser(userDAO, "volunteer3", "vol3@resomap.com", "Volunteer123!", UserRole.VOLUNTEER);
            createUser(userDAO, "volunteer4", "vol4@resomap.com", "Volunteer123!", UserRole.VOLUNTEER);
            createUser(userDAO, "volunteer5", "vol5@resomap.com", "Volunteer123!", UserRole.VOLUNTEER);
            
            // Requester Users (7)
            createUser(userDAO, "user1", "user1@resomap.com", "User123!", UserRole.REQUESTER);
            createUser(userDAO, "user2", "user2@resomap.com", "User123!", UserRole.REQUESTER);
            createUser(userDAO, "user3", "user3@resomap.com", "User123!", UserRole.REQUESTER);
            createUser(userDAO, "user4", "user4@resomap.com", "User123!", UserRole.REQUESTER);
            createUser(userDAO, "user5", "user5@resomap.com", "User123!", UserRole.REQUESTER);
            createUser(userDAO, "user6", "user6@resomap.com", "User123!", UserRole.REQUESTER);
            createUser(userDAO, "user7", "user7@resomap.com", "User123!", UserRole.REQUESTER);
            
            logger.info("Sample users created successfully");
            
        } catch (DatabaseException e) {
            logger.log(Level.SEVERE, "Failed to create sample users", e);
        }
    }
    
    private static void createUser(UserDAO userDAO, String username, String email, String password, UserRole role) {
        try {
            User existingUser = userDAO.findByUsername(username);
            if (existingUser != null) {
                logger.info("User already exists: " + username);
                return;
            }
            
            String passwordHash = PasswordUtils.hashPassword(password);
            
            User user;
            switch (role) {
                case ADMIN:
                    user = new Admin(username, email, passwordHash);
                    break;
                case VOLUNTEER:
                    user = new Volunteer(username, email, passwordHash);
                    break;
                case REQUESTER:
                    user = new Requester(username, email, passwordHash);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown role: " + role);
            }
            
            userDAO.create(user);
            logger.info("✓ Created user: " + username + " / " + password + " (Role: " + role + ")");
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to create user: " + username, e);
        }
    }
    
    private static void createSampleResources() {
        logger.info("Creating sample resources...");
        
        try {
            ResourceDAO resourceDAO = new ResourceDAO();
            
            // Food & Groceries
            createResource(resourceDAO, "Canned Beans", "Food & Groceries", "Protein-rich canned beans", 50);
            createResource(resourceDAO, "Rice Bags (5kg)", "Food & Groceries", "Long grain white rice", 25);
            createResource(resourceDAO, "Pasta Boxes", "Food & Groceries", "Whole wheat pasta", 40);
            createResource(resourceDAO, "Cooking Oil", "Food & Groceries", "Vegetable cooking oil", 30);
            createResource(resourceDAO, "Baby Formula", "Food & Groceries", "Infant formula powder", 15);
            createResource(resourceDAO, "Canned Vegetables", "Food & Groceries", "Mixed canned vegetables", 60);
            
            // Medical Supplies
            createResource(resourceDAO, "First Aid Kits", "Medical Supplies", "Complete first aid kits", 20);
            createResource(resourceDAO, "Bandages", "Medical Supplies", "Sterile bandages assorted sizes", 100);
            createResource(resourceDAO, "Pain Relievers", "Medical Supplies", "Over-the-counter pain medication", 60);
            createResource(resourceDAO, "Thermometers", "Medical Supplies", "Digital thermometers", 25);
            createResource(resourceDAO, "Hand Sanitizer", "Medical Supplies", "Alcohol-based hand sanitizer", 80);
            createResource(resourceDAO, "Face Masks", "Medical Supplies", "Disposable face masks", 200);
            
            // Clothing & Textiles
            createResource(resourceDAO, "Winter Coats", "Clothing & Textiles", "Warm winter coats various sizes", 35);
            createResource(resourceDAO, "Blankets", "Clothing & Textiles", "Warm fleece blankets", 45);
            createResource(resourceDAO, "Children's Clothes", "Clothing & Textiles", "Kids clothing mixed sizes", 60);
            createResource(resourceDAO, "Shoes", "Clothing & Textiles", "Various shoes and boots", 40);
            createResource(resourceDAO, "Socks & Underwear", "Clothing & Textiles", "New socks and underwear", 100);
            createResource(resourceDAO, "Rain Jackets", "Clothing & Textiles", "Waterproof rain jackets", 30);
            
            // Tools & Equipment
            createResource(resourceDAO, "Flashlights", "Tools & Equipment", "LED flashlights with batteries", 30);
            createResource(resourceDAO, "Tool Sets", "Tools & Equipment", "Basic tool sets for repairs", 15);
            createResource(resourceDAO, "Generators", "Tools & Equipment", "Portable power generators", 5);
            createResource(resourceDAO, "Radios", "Tools & Equipment", "Battery-powered radios", 20);
            createResource(resourceDAO, "Batteries", "Tools & Equipment", "AA and AAA batteries", 150);
            
            // Educational Resources
            createResource(resourceDAO, "School Supplies", "Educational Resources", "Notebooks, pens, pencils", 75);
            createResource(resourceDAO, "Textbooks", "Educational Resources", "Various grade level textbooks", 50);
            createResource(resourceDAO, "Laptops", "Educational Resources", "Refurbished laptops for students", 10);
            createResource(resourceDAO, "Backpacks", "Educational Resources", "School backpacks", 40);
            
            // Technology
            createResource(resourceDAO, "Mobile Phones", "Technology", "Basic smartphones", 20);
            createResource(resourceDAO, "Tablets", "Technology", "Tablets for education/communication", 15);
            createResource(resourceDAO, "Chargers", "Technology", "Universal phone chargers", 50);
            
            logger.info("Sample resources created successfully");
            
        } catch (DatabaseException e) {
            logger.log(Level.SEVERE, "Failed to create sample resources", e);
        }
    }
    
    private static void createResource(ResourceDAO resourceDAO, String name, String category, 
                                     String description, int quantity) {
        try {
            Resource resource = new Resource(name, description, category, quantity, 
                "Community Center - Storage", "contact@resomap.com", "admin");
            
            resourceDAO.create(resource);
            logger.info("✓ Created resource: " + name + " (Qty: " + quantity + ")");
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to create resource: " + name, e);
        }
    }
    
    private static void createSampleRequests() {
        logger.info("Creating sample requests...");
        
        try {
            RequestDAO requestDAO = new RequestDAO();
            UserDAO userDAO = new UserDAO();
            ResourceDAO resourceDAO = new ResourceDAO();
            
            List<User> requesters = Arrays.asList(
                userDAO.findByUsername("user1"),
                userDAO.findByUsername("user2"),
                userDAO.findByUsername("user3"),
                userDAO.findByUsername("user4"),
                userDAO.findByUsername("user5"),
                userDAO.findByUsername("user6"),
                userDAO.findByUsername("user7")
            );
            
            List<User> volunteers = Arrays.asList(
                userDAO.findByUsername("volunteer1"),
                userDAO.findByUsername("volunteer2"),
                userDAO.findByUsername("volunteer3"),
                userDAO.findByUsername("volunteer4"),
                userDAO.findByUsername("volunteer5")
            );
            
            List<Resource> resources = resourceDAO.findAll();
            
            RequestStatus[] statuses = RequestStatus.values();
            UrgencyLevel[] urgencies = UrgencyLevel.values();
            
            String[] descriptions = {
                "Need food supplies for family of 4",
                "Require medical supplies for elderly care",
                "Winter clothing needed for children",
                "Emergency shelter supplies needed",
                "School supplies for remote learning",
                "First aid kit for community center",
                "Blankets needed for homeless shelter",
                "Baby formula urgently needed",
                "Tools needed for home repairs",
                "Warm coats for winter season",
                "Educational materials needed",
                "Technology devices for job searching",
                "Cooking supplies needed",
                "Medical equipment required",
                "Emergency food for large family",
                "Clothing donations needed",
                "Books and supplies needed",
                "Transportation assistance needed",
                "Household items needed",
                "Pet supplies needed"
            };
            
            for (int i = 0; i < descriptions.length && i < resources.size(); i++) {
                User requester = requesters.get(i % requesters.size());
                Resource resource = resources.get(i % resources.size());
                RequestStatus status = statuses[i % statuses.length];
                UrgencyLevel urgency = urgencies[i % urgencies.length];
                
                Request request = new Request();
                request.setRequesterId(requester.getUserId());
                request.setResourceId(resource.getResourceId());
                request.setDescription(descriptions[i]);
                request.setStatus(status);
                request.setUrgencyLevel(urgency);
                
                if (status == RequestStatus.ASSIGNED || status == RequestStatus.IN_PROGRESS || status == RequestStatus.COMPLETED) {
                    User volunteer = volunteers.get(i % volunteers.size());
                    request.setVolunteerId(volunteer.getUserId());
                }
                
                requestDAO.create(request);
                logger.info("✓ Created request: " + descriptions[i].substring(0, Math.min(30, descriptions[i].length())) + "...");
            }
            
            logger.info("Sample requests created successfully");
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to create sample requests", e);
        }
    }
}
