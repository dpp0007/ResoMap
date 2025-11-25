package com.communityhub.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Password Utilities - BULLETPROOF VERSION
 * Handles password hashing and verification with salt
 * 
 * @author ResoMap Team
 * @version 3.0 - FIXED TRUNCATION ISSUE
 */
public final class PasswordUtils {
    
    private static final Logger logger = Logger.getLogger(PasswordUtils.class.getName());
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;
    private static final String SEPARATOR = ":";
    
    // Private constructor prevents instantiation
    private PasswordUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    /**
     * Hashes a password with a random salt
     * Format: salt:hash
     * 
     * @param password Plain text password
     * @return Salted hash in format "salt:hash"
     */
    public static String hashPassword(String password) {
        try {
            logger.fine("Hashing password (length: " + password.length() + ")");
            
            // Generate random salt
            byte[] salt = generateSalt();
            String saltString = Base64.getEncoder().encodeToString(salt);
            
            // Hash password with salt
            String hash = hashWithSalt(password, salt);
            
            // Combine salt and hash
            String result = saltString + SEPARATOR + hash;
            
            logger.fine("Password hashed successfully");
            logger.fine("Salt length: " + saltString.length());
            logger.fine("Hash length: " + hash.length());
            logger.fine("Total length: " + result.length());
            
            return result;
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error hashing password", e);
            throw new RuntimeException("Password hashing failed", e);
        }
    }
    
    /**
     * Verifies a password against a stored hash
     * Supports both salted (salt:hash) and legacy plain hash formats
     * 
     * @param password Plain text password to verify
     * @param storedHash Stored hash (either "salt:hash" or plain hash)
     * @return true if password matches
     */
    public static boolean verifyPassword(String password, String storedHash) {
        try {
            logger.fine("Verifying password (length: " + password.length() + ")");
            logger.fine("Stored hash length: " + storedHash.length());
            logger.fine("Stored hash format: " + (storedHash.contains(SEPARATOR) ? "SALTED" : "PLAIN"));
            
            if (storedHash.contains(SEPARATOR)) {
                // New salted format: salt:hash
                return verifySaltedPassword(password, storedHash);
            } else {
                // Legacy plain hash format (for backward compatibility)
                return verifyPlainHash(password, storedHash);
            }
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error verifying password", e);
            return false;
        }
    }
    
    /**
     * Verifies password against salted hash
     * 
     * @param password Plain text password
     * @param saltedHash Hash in format "salt:hash"
     * @return true if password matches
     */
    private static boolean verifySaltedPassword(String password, String saltedHash) {
        try {
            // Split salt and hash
            String[] parts = saltedHash.split(SEPARATOR, 2);
            if (parts.length != 2) {
                logger.warning("Invalid salted hash format - expected 2 parts, got " + parts.length);
                return false;
            }
            
            String saltString = parts[0];
            String expectedHash = parts[1];
            
            logger.fine("Extracted salt: " + saltString + " (length: " + saltString.length() + ")");
            logger.fine("Expected hash: " + expectedHash + " (length: " + expectedHash.length() + ")");
            
            // Decode salt
            byte[] salt = Base64.getDecoder().decode(saltString);
            
            // Hash the provided password with the same salt
            String actualHash = hashWithSalt(password, salt);
            
            logger.fine("Actual hash: " + actualHash + " (length: " + actualHash.length() + ")");
            
            // Compare hashes
            boolean matches = expectedHash.equals(actualHash);
            logger.fine("Password verification result: " + matches);
            
            return matches;
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error verifying salted password", e);
            return false;
        }
    }
    
    /**
     * Verifies password against plain hash (legacy support)
     * 
     * @param password Plain text password
     * @param plainHash Plain hash without salt
     * @return true if password matches
     */
    private static boolean verifyPlainHash(String password, String plainHash) {
        try {
            logger.fine("Verifying against plain hash (legacy mode)");
            
            // Hash password without salt
            String actualHash = hashPlain(password);
            
            boolean matches = plainHash.equals(actualHash);
            logger.fine("Plain hash verification result: " + matches);
            
            return matches;
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error verifying plain hash", e);
            return false;
        }
    }
    
    /**
     * Generates a random salt
     * 
     * @return Random salt bytes
     */
    private static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }
    
    /**
     * Hashes password with salt
     * 
     * @param password Plain text password
     * @param salt Salt bytes
     * @return Base64 encoded hash
     */
    private static String hashWithSalt(String password, byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
        
        // Add salt to digest
        digest.update(salt);
        
        // Hash password
        byte[] hashedBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        
        // Encode to Base64
        return Base64.getEncoder().encodeToString(hashedBytes);
    }
    
    /**
     * Hashes password without salt (legacy support)
     * 
     * @param password Plain text password
     * @return Base64 encoded hash
     */
    private static String hashPlain(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
        byte[] hashedBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hashedBytes);
    }
    
    /**
     * Checks if a hash is in the new salted format
     * 
     * @param hash Hash to check
     * @return true if hash is salted
     */
    public static boolean isSaltedHash(String hash) {
        return hash != null && hash.contains(SEPARATOR);
    }
}
