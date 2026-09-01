/**
 * @author Group 9
 */
package org.example.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Handles the connection to the PostgreSQL database.
 * This class is responsible for establishing a connection to the database,
 * creating the necessary tables if they don't exist, and providing methods
 * to access the database.
 * Uses HikariCP for connection pooling to improve performance.
 */
public class DatabaseConnection {
    // Database connection properties should be loaded from environment variables or a configuration file
    // for better security. This is a temporary solution.
    private static final String DB_URL = System.getenv("DB_URL") != null ? 
            System.getenv("DB_URL") : "jdbc:postgresql://aws-0-ap-southeast-1.pooler.supabase.com:6543/postgres?prepareThreshold=0";
    private static final String DB_USER = System.getenv("DB_USER") != null ? 
            System.getenv("DB_USER") : "postgres.weaplidrrnnmjwkdpglw";
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD") != null ? 
            System.getenv("DB_PASSWORD") : "RMIT@2025yta";

    // Disable prepared statement caching to prevent "prepared statement already exists" errors
    private static final boolean PREPARED_STATEMENT_CACHE_ENABLED = false;

    private static HikariDataSource dataSource;

    /**
     * Private constructor to prevent instantiation
     */
    private DatabaseConnection() {
    }

    /**
     * Initialize the connection pool
     */
    private static void initializeConnectionPool() {
        if (dataSource == null) {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(DB_URL);
            config.setUsername(DB_USER);
            config.setPassword(DB_PASSWORD);
            config.addDataSourceProperty("sslmode", "require");

            // Disable prepared statement caching to prevent "prepared statement already exists" errors
            config.addDataSourceProperty("preparedStatementCacheQueries", "0");
            config.addDataSourceProperty("prepStmtCacheSize", "0");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "0");
            config.addDataSourceProperty("cachePrepStmts", String.valueOf(PREPARED_STATEMENT_CACHE_ENABLED));

            // Additional settings to prevent prepared statement issues
            config.addDataSourceProperty("useServerPrepStmts", "false");
            config.addDataSourceProperty("rewriteBatchedStatements", "true");
            config.addDataSourceProperty("useLocalSessionState", "true");
            config.addDataSourceProperty("useLocalTransactionState", "true");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("useUnicode", "true");

            // Auto-commit
            config.setAutoCommit(true);

            // Connection pool settings
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(5);
            config.setIdleTimeout(30000);
            config.setConnectionTimeout(30000);
            config.setMaxLifetime(1800000);

            // Add leak detection
            config.setLeakDetectionThreshold(60000); // 60 seconds

            dataSource = new HikariDataSource(config);
        }
    }

    /**
     * Get a connection to the database from the connection pool
     *
     * @return A connection to the database
     * @throws SQLException If a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            initializeConnectionPool();
        }
        return dataSource.getConnection();
    }

    /**
     * Close the database connection pool
     * 
     * Note: In a test environment with multiple tests, it's better to keep the connection
     * pool open and let it be managed by the JVM shutdown hook. This method is provided
     * for completeness but should be used with caution.
     */
    public static void closeConnection() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    /**
     * Reinitialize the connection pool if it has been closed
     * This is useful in test environments where the pool might have been closed by another test
     * 
     * @throws SQLException If a database access error occurs
     */
    public static void reinitializeConnectionPool() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            initializeConnectionPool();
        }
    }

    /**
     * Initialize the database by creating the necessary tables if they don't exist
     *
     * @throws SQLException If a database access error occurs
     */
    public static void initializeDatabase() throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Create Users table
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id SERIAL PRIMARY KEY, " +
                    "username VARCHAR(50) UNIQUE NOT NULL, " +
                    "password VARCHAR(100) NOT NULL, " +
                    "first_name VARCHAR(50) NOT NULL, " +
                    "last_name VARCHAR(50) NOT NULL, " +
                    "email VARCHAR(100) UNIQUE NOT NULL, " +
                    "phone_number VARCHAR(20), " +
                    "date_of_birth DATE, " +
                    "address TEXT, " +
                    "user_type VARCHAR(20) NOT NULL, " + // 'Student', 'AcademicStaff', 'ProfessionalStaff', 'Administrator'
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")");

            // Create Students table
            stmt.execute("CREATE TABLE IF NOT EXISTS students (" +
                    "user_id INTEGER PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE, " +
                    "student_id VARCHAR(20) UNIQUE NOT NULL, " +
                    "major VARCHAR(100), " +
                    "year INTEGER" +
                    ")");

            // Create AcademicStaff table
            stmt.execute("CREATE TABLE IF NOT EXISTS academic_staff (" +
                    "user_id INTEGER PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE, " +
                    "staff_id VARCHAR(20) UNIQUE NOT NULL, " +
                    "department VARCHAR(100), " +
                    "position VARCHAR(100)" +
                    ")");

            // Create ProfessionalStaff table
            stmt.execute("CREATE TABLE IF NOT EXISTS professional_staff (" +
                    "user_id INTEGER PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE, " +
                    "staff_id VARCHAR(20) UNIQUE NOT NULL, " +
                    "department VARCHAR(100), " +
                    "position VARCHAR(100), " +
                    "specialization VARCHAR(100)" +
                    ")");

            // Create Administrators table
            stmt.execute("CREATE TABLE IF NOT EXISTS administrators (" +
                    "user_id INTEGER PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE, " +
                    "admin_id VARCHAR(20) UNIQUE NOT NULL, " +
                    "department VARCHAR(100), " +
                    "position VARCHAR(100), " +
                    "access_level VARCHAR(20)" +
                    ")");

            // Create Courses table
            stmt.execute("CREATE TABLE IF NOT EXISTS courses (" +
                    "id SERIAL PRIMARY KEY, " +
                    "course_code VARCHAR(20) UNIQUE NOT NULL, " +
                    "course_name VARCHAR(100) NOT NULL, " +
                    "description TEXT, " +
                    "semester INTEGER, " +
                    "year INTEGER, " +
                    "instructor_id INTEGER REFERENCES academic_staff(user_id), " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")");

            // Create StudentCourses table (many-to-many relationship)
            stmt.execute("CREATE TABLE IF NOT EXISTS student_courses (" +
                    "student_id INTEGER REFERENCES students(user_id) ON DELETE CASCADE, " +
                    "course_id INTEGER REFERENCES courses(id) ON DELETE CASCADE, " +
                    "PRIMARY KEY (student_id, course_id)" +
                    ")");

            // Create Equipment table
            stmt.execute("CREATE TABLE IF NOT EXISTS equipment (" +
                    "id SERIAL PRIMARY KEY, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "description TEXT, " +
                    "category VARCHAR(50), " +
                    "condition VARCHAR(20), " +
                    "purchase_date DATE, " +
                    "purchase_price DECIMAL(10, 2), " +
                    "manufacturer VARCHAR(100), " +
                    "model VARCHAR(100), " +
                    "serial_number VARCHAR(100), " +
                    "location VARCHAR(100), " +
                    "available BOOLEAN DEFAULT TRUE, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")");

            // Create EquipmentImages table
            stmt.execute("CREATE TABLE IF NOT EXISTS equipment_images (" +
                    "id SERIAL PRIMARY KEY, " +
                    "equipment_id INTEGER REFERENCES equipment(id) ON DELETE CASCADE, " +
                    "image BYTEA NOT NULL, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")");

            // Create LendingRecords table
            stmt.execute("CREATE TABLE IF NOT EXISTS lending_records (" +
                    "id SERIAL PRIMARY KEY, " +
                    "borrower_id INTEGER REFERENCES users(id) ON DELETE CASCADE, " +
                    "equipment_id INTEGER REFERENCES equipment(id) ON DELETE CASCADE, " +
                    "course_id INTEGER REFERENCES courses(id), " +
                    "borrow_date TIMESTAMP NOT NULL, " +
                    "due_date TIMESTAMP NOT NULL, " +
                    "return_date TIMESTAMP, " +
                    "status VARCHAR(20) NOT NULL, " + // 'Pending', 'Approved', 'Borrowed', 'Returned', 'Overdue', 'Rejected'
                    "purpose TEXT, " +
                    "condition VARCHAR(20), " +
                    "notes TEXT, " +
                    "approver_id INTEGER REFERENCES users(id), " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")");

            System.out.println("Database tables created successfully");
        }
    }
}
