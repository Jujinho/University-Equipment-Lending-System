/**
 * @author Group 9
 */
package org.example.util;

import java.security.SecureRandom;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Utility class for hashing and verifying passwords.
 * Uses BCrypt-like algorithm with salting for secure password storage.
 */
public class PasswordHasher {
    
    private static final int SALT_LENGTH = 16; // 16 bytes = 128 bits
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final int ITERATIONS = 10000;
    private static final String DELIMITER = "$";
    
    /**
     * Hash a password using a secure hashing algorithm with salt.
     * 
     * @param password The password to hash
     * @return A string containing the salt and hashed password
     */
    public static String hashPassword(String password) {
        try {
            // Generate a random salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);
            
            // Hash the password with the salt
            byte[] hash = hashWithSalt(password, salt);
            
            // Encode the salt and hash as Base64 strings
            String saltStr = Base64.getEncoder().encodeToString(salt);
            String hashStr = Base64.getEncoder().encodeToString(hash);
            
            // Return the salt and hash, separated by a delimiter
            return ITERATIONS + DELIMITER + saltStr + DELIMITER + hashStr;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password: " + e.getMessage(), e);
        }
    }
    
    /**
     * Verify a password against a stored hash.
     * 
     * @param password The password to verify
     * @param storedHash The stored hash to verify against
     * @return true if the password matches the hash, false otherwise
     */
    public static boolean verifyPassword(String password, String storedHash) {
        try {
            // Split the stored hash into its components
            String[] parts = storedHash.split("\\" + DELIMITER);
            if (parts.length != 3) {
                return false; // Invalid hash format
            }
            
            int iterations = Integer.parseInt(parts[0]);
            byte[] salt = Base64.getDecoder().decode(parts[1]);
            byte[] hash = Base64.getDecoder().decode(parts[2]);
            
            // Hash the password with the same salt
            byte[] testHash = hashWithSalt(password, salt, iterations);
            
            // Compare the hashes
            return MessageDigest.isEqual(hash, testHash);
        } catch (Exception e) {
            return false; // Any error means verification fails
        }
    }
    
    /**
     * Hash a password with a salt using the specified algorithm.
     * 
     * @param password The password to hash
     * @param salt The salt to use
     * @return The hashed password
     * @throws NoSuchAlgorithmException If the algorithm is not available
     */
    private static byte[] hashWithSalt(String password, byte[] salt) throws NoSuchAlgorithmException {
        return hashWithSalt(password, salt, ITERATIONS);
    }
    
    /**
     * Hash a password with a salt using the specified algorithm and iterations.
     * 
     * @param password The password to hash
     * @param salt The salt to use
     * @param iterations The number of iterations
     * @return The hashed password
     * @throws NoSuchAlgorithmException If the algorithm is not available
     */
    private static byte[] hashWithSalt(String password, byte[] salt, int iterations) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
        digest.reset();
        digest.update(salt);
        byte[] input = digest.digest(password.getBytes());
        
        // Apply multiple iterations of the hash function
        for (int i = 0; i < iterations; i++) {
            digest.reset();
            input = digest.digest(input);
        }
        
        return input;
    }
}