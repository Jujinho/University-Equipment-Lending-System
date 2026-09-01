/**
 * @author Group 9
 */
package org.example.db;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * JUnit test class for verifying the database connection.
 */
public class DatabaseConnectionJUnitTest {

    /**
     * Test that the database connection can be established.
     */
    @Test
    public void testDatabaseConnection() {
        System.out.println("Testing database connection...");

        try {
            // Attempt to get a connection
            Connection connection = DatabaseConnection.getConnection();

            // Check if the connection is valid
            assertNotNull(connection, "Connection should not be null");
            assertFalse(connection.isClosed(), "Connection should not be closed");

            System.out.println("Database connection successful!");
            System.out.println("Connected to: " + connection.getMetaData().getURL());
            System.out.println("Database product name: " + connection.getMetaData().getDatabaseProductName());
            System.out.println("Database product version: " + connection.getMetaData().getDatabaseProductVersion());
            
            // Close only this connection, not the entire pool
            connection.close();
            System.out.println("Connection closed.");

        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());

            // Print the full stack trace
            e.printStackTrace();

            // Print the cause if available
            if (e.getCause() != null) {
                System.err.println("Caused by: " + e.getCause().getMessage());
                e.getCause().printStackTrace();
            }

            fail("Database connection failed: " + e.getMessage());
        }
    }
}
