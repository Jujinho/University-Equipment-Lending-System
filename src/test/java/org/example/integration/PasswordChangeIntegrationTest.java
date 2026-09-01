/**
 * @author Group 9
 */
package org.example.integration;

import org.example.db.DatabaseConnection;
import org.example.db.UserRepository;
import org.example.model.Student;
import org.example.model.User;
import org.example.service.AuthenticationService;
import org.example.service.UserService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for password change functionality.
 */
public class PasswordChangeIntegrationTest {

    private static UserRepository userRepository;
    private static UserService userService;
    private static AuthenticationService authenticationService;
    private static int testUserId;
    private static final String TEST_USERNAME = "testuser_" + System.currentTimeMillis();
    private static final String INITIAL_PASSWORD = "initialPassword";
    private static final String NEW_PASSWORD = "newPassword";

    @BeforeAll
    public static void setUp() {
        try {
            // Ensure the connection pool is initialized or reinitialized if it was closed
            DatabaseConnection.reinitializeConnectionPool();

            // Initialize database
            DatabaseConnection.initializeDatabase();

            // Create repositories and services
            userRepository = new UserRepository();
            userService = new UserService(userRepository);
            authenticationService = new AuthenticationService(userRepository);

            // Create a test user
            Student testUser = new Student();
            testUser.setUsername(TEST_USERNAME);
            testUser.setPassword(INITIAL_PASSWORD);
            testUser.setFirstName("Test");
            testUser.setLastName("User");
            testUser.setEmail("testuser@example.com");
            testUser.setPhoneNumber("1234567890");
            testUser.setDateOfBirth(LocalDate.of(2000, 1, 1));
            testUser.setAddress("123 Test St");
            testUser.setStudentId("S12345");
            testUser.setMajor("Computer Science");
            testUser.setYear(2);

            boolean created = userService.createUser(testUser);
            System.out.println("[DEBUG_LOG] Test user created: " + created);

            if (!created) {
                System.out.println("[DEBUG_LOG] Failed to create user through service");
                fail("Failed to create test user through service");
            }

            // Get the ID of the created user
            Optional<User> createdUser = userRepository.getUserByUsername(TEST_USERNAME);
            if (createdUser.isPresent()) {
                testUserId = createdUser.get().getId();
                System.out.println("[DEBUG_LOG] Test user ID: " + testUserId);
            } else {
                System.out.println("[DEBUG_LOG] Could not find user by username: " + TEST_USERNAME);
                fail("Failed to create test user");
            }
        } catch (SQLException e) {
            System.err.println("[DEBUG_LOG] SQL Error: " + e.getMessage());
            e.printStackTrace();
            fail("Failed to set up test: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("[DEBUG_LOG] Unexpected error: " + e.getMessage());
            e.printStackTrace();
            fail("Failed to set up test with unexpected error: " + e.getMessage());
        }
    }

    @AfterAll
    public static void tearDown() {
        try {
            // Delete the test user
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM users WHERE username = ?");
            stmt.setString(1, TEST_USERNAME);
            stmt.executeUpdate();
            stmt.close();
            conn.close();

            // In a test environment with multiple tests, it's better not to close the connection pool
            // as it might be needed by other tests. The pool will be closed by the JVM shutdown hook.
            // DatabaseConnection.closeConnection();
        } catch (SQLException e) {
            System.err.println("Error cleaning up test: " + e.getMessage());
        }
    }

    @Test
    public void testChangePassword() {
        System.out.println("[DEBUG_LOG] Starting password change test");

        // Verify that the user can authenticate with the initial password
        boolean initialAuth = authenticationService.authenticate(TEST_USERNAME, INITIAL_PASSWORD);
        System.out.println("[DEBUG_LOG] Initial authentication: " + initialAuth);
        assertTrue(initialAuth, "User should be able to authenticate with initial password");

        // Change the password
        boolean passwordChanged = userService.changePassword(testUserId, INITIAL_PASSWORD, NEW_PASSWORD);
        System.out.println("[DEBUG_LOG] Password changed: " + passwordChanged);
        assertTrue(passwordChanged, "Password change should succeed");

        // Verify that the user can no longer authenticate with the initial password
        authenticationService.logout();
        boolean oldPasswordAuth = authenticationService.authenticate(TEST_USERNAME, INITIAL_PASSWORD);
        System.out.println("[DEBUG_LOG] Authentication with old password: " + oldPasswordAuth);
        assertFalse(oldPasswordAuth, "User should not be able to authenticate with old password");

        // Verify that the user can authenticate with the new password
        boolean newPasswordAuth = authenticationService.authenticate(TEST_USERNAME, NEW_PASSWORD);
        System.out.println("[DEBUG_LOG] Authentication with new password: " + newPasswordAuth);
        assertTrue(newPasswordAuth, "User should be able to authenticate with new password");
    }
}
