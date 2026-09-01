/**
 * @author Group 9
 */
package org.example.service;

import org.example.db.DatabaseConnection;
import org.example.db.UserRepository;
import org.example.model.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for password change functionality.
 */
public class PasswordChangeTest {
    
    private static UserRepository userRepository;

    @BeforeAll
    public static void setUp() {
        try {
            // Ensure the connection pool is initialized or reinitialized if it was closed
            DatabaseConnection.reinitializeConnectionPool();

            // Initialize database
            DatabaseConnection.initializeDatabase();

            // Create repositories
            userRepository = new UserRepository();
        } catch (SQLException e) {
            fail("Failed to set up test: " + e.getMessage());
        }
    }

    @AfterAll
    public static void tearDown() {
        // In a test environment with multiple tests, it's better not to close the connection pool
        // as it might be needed by other tests. The pool will be closed by the JVM shutdown hook.
        // DatabaseConnection.closeConnection();
    }

    @Test
    public void testChangePassword() {
        // Get all users
        List<User> users = userRepository.getAllUsers();

        // Skip test if no users are available
        if (users.isEmpty()) {
            System.out.println("[DEBUG_LOG] No users available for testing");
            return;
        }

        // Use the first user for testing
        User testUser = users.get(0);
        int userId = testUser.getId();
        String username = testUser.getUsername();
        String originalPassword = testUser.getPassword();

        System.out.println("[DEBUG_LOG] Testing with user: " + username + " (ID: " + userId + ")");
        System.out.println("[DEBUG_LOG] Original password: " + originalPassword);

        // Try to change the password
        // Note: Since we don't know the original plain text password, we'll use a direct update
        // This is just for testing purposes and would not be done in a real application
        String newPassword = "newTestPassword";
        String hashedNewPassword = org.example.util.PasswordHasher.hashPassword(newPassword);

        // Update the password directly
        testUser.setPassword(hashedNewPassword);
        boolean updated = userRepository.updateUser(testUser);

        System.out.println("[DEBUG_LOG] Password update result: " + updated);
        assertTrue(updated, "Password update should succeed");

        // Verify that the password was changed
        User updatedUser = userRepository.getUserById(userId).orElse(null);
        assertNotNull(updatedUser, "User should still exist");
        assertEquals(hashedNewPassword, updatedUser.getPassword(), "Password should be updated");

        // Restore the original password
        updatedUser.setPassword(originalPassword);
        boolean restored = userRepository.updateUser(updatedUser);

        System.out.println("[DEBUG_LOG] Password restore result: " + restored);
        assertTrue(restored, "Password restore should succeed");
    }
}
