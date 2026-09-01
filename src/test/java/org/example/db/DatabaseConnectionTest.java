/**
 * @author Group 9
 */
package org.example.db;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Test class for verifying the database connection.
 */
public class DatabaseConnectionTest {

    /**
     * Main method to test the database connection.
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("Testing database connection...");
        
        try {
            // Attempt to get a connection
            Connection connection = DatabaseConnection.getConnection();
            
            // Check if the connection is valid
            if (connection != null && !connection.isClosed()) {
                System.out.println("Database connection successful!");
                System.out.println("Connected to: " + connection.getMetaData().getURL());
                System.out.println("Database product name: " + connection.getMetaData().getDatabaseProductName());
                System.out.println("Database product version: " + connection.getMetaData().getDatabaseProductVersion());
            } else {
                System.err.println("Failed to establish database connection.");
            }
            
            // Close the connection
            DatabaseConnection.closeConnection();
            System.out.println("Connection closed.");
            
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}