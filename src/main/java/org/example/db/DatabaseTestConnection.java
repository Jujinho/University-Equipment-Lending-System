/**
 * @author Group 9
 */
package org.example.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseTestConnection {
    public static void main(String[] args) {
        // Define the connection parameters with your password and updated details
        String url = "jdbc:postgresql://aws-0-ap-southeast-1.pooler.supabase.com:6543/postgres?user=postgres.weaplidrrnnmjwkdpglw&password=RMIT@2025yta&sslmode=require&pool_mode=transaction";

        // Establish the connection
        try (Connection conn = DriverManager.getConnection(url)) {
            System.out.println("Connection successful!");
        } catch (SQLException e) {
            System.out.println("Error connecting to the database: " + e.getMessage());
        }
    }
}