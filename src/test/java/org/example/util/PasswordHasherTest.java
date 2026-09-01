/**
 * @author Group 9
 */
package org.example.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the PasswordHasher class.
 */
public class PasswordHasherTest {
    
    @Test
    public void testHashPassword() {
        // Hash a password
        String password = "securePassword123";
        String hashedPassword = PasswordHasher.hashPassword(password);
        
        // Verify that the hashed password is not the same as the original password
        assertNotEquals(password, hashedPassword);
        
        // Verify that the hashed password contains the delimiter
        assertTrue(hashedPassword.contains("$"));
        
        // Verify that the hashed password has three parts separated by the delimiter
        String[] parts = hashedPassword.split("\\$");
        assertEquals(3, parts.length);
        
        // Verify that the first part is a number (iterations)
        int iterations = Integer.parseInt(parts[0]);
        assertTrue(iterations > 0);
        
        // Verify that the second part is not empty (salt)
        assertFalse(parts[1].isEmpty());
        
        // Verify that the third part is not empty (hash)
        assertFalse(parts[2].isEmpty());
    }
    
    @Test
    public void testVerifyPassword() {
        // Hash a password
        String password = "securePassword123";
        String hashedPassword = PasswordHasher.hashPassword(password);
        
        // Verify that the correct password is verified
        assertTrue(PasswordHasher.verifyPassword(password, hashedPassword));
        
        // Verify that an incorrect password is not verified
        assertFalse(PasswordHasher.verifyPassword("wrongPassword", hashedPassword));
        
        // Verify that a similar password is not verified
        assertFalse(PasswordHasher.verifyPassword("securePassword124", hashedPassword));
        assertFalse(PasswordHasher.verifyPassword("securePassword", hashedPassword));
        assertFalse(PasswordHasher.verifyPassword("SecurePassword123", hashedPassword));
    }
    
    @Test
    public void testVerifyPasswordWithInvalidHash() {
        // Verify that an invalid hash format is not verified
        assertFalse(PasswordHasher.verifyPassword("password", "invalidHash"));
        assertFalse(PasswordHasher.verifyPassword("password", "invalid$hash"));
        assertFalse(PasswordHasher.verifyPassword("password", "invalid$hash$format"));
        
        // Verify that a hash with invalid iterations is not verified
        assertFalse(PasswordHasher.verifyPassword("password", "abc$salt$hash"));
    }
    
    @Test
    public void testHashingConsistency() {
        // Hash the same password multiple times
        String password = "securePassword123";
        String hashedPassword1 = PasswordHasher.hashPassword(password);
        String hashedPassword2 = PasswordHasher.hashPassword(password);
        
        // Verify that the hashed passwords are different (due to different salts)
        assertNotEquals(hashedPassword1, hashedPassword2);
        
        // Verify that both hashed passwords can be verified with the original password
        assertTrue(PasswordHasher.verifyPassword(password, hashedPassword1));
        assertTrue(PasswordHasher.verifyPassword(password, hashedPassword2));
    }
}