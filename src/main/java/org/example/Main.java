/**
 * @author Group 9
 */
package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.db.*;
import org.example.service.*;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Main class for the University Equipment Lending System.
 * This class initializes the database, services, and GUI components.
 */
public class Main extends Application {

    // Database repositories
    private static UserRepository userRepository;
    private static CourseRepository courseRepository;
    private static EquipmentRepository equipmentRepository;
    private static LendingRecordRepository lendingRecordRepository;

    // Services
    private static AuthenticationService authenticationService;
    private static UserService userService;
    private static CourseService courseService;
    private static EquipmentService equipmentService;
    private static LendingService lendingService;
    private static NotificationService notificationService;

    /**
     * Main method
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        try {
            // Initialize database
            initializeDatabase();

            // Initialize services
            initializeServices();

            // Launch JavaFX application using the launcher pattern
            // This helps with JavaFX module system in Java 9+
            Application.launch(Main.class, args);
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Application error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Close database connection
            DatabaseConnection.closeConnection();
        }
    }

    /**
     * Initialize the database
     *
     * @throws SQLException If a database access error occurs
     */
    private static void initializeDatabase() throws SQLException {
        // Initialize database connection and tables
        DatabaseConnection.initializeDatabase();

        // Initialize repositories
        userRepository = new UserRepository();
        courseRepository = new CourseRepository(userRepository);
        equipmentRepository = new EquipmentRepository();
        lendingRecordRepository = new LendingRecordRepository(userRepository, equipmentRepository, courseRepository);
    }

    /**
     * Initialize the services
     */
    private static void initializeServices() {
        // Initialize services
        authenticationService = new AuthenticationService(userRepository);
        userService = new UserService(userRepository);
        courseService = new CourseService(courseRepository);
        equipmentService = new EquipmentService(equipmentRepository);
        notificationService = new NotificationService();
        lendingService = new LendingService(lendingRecordRepository, courseService, notificationService);
    }

    /**
     * JavaFX start method
     *
     * @param primaryStage The primary stage for this application
     * @throws IOException If an I/O error occurs
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        // Load the login screen
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Parent root = loader.load();

        // Set up the scene
        Scene scene = new Scene(root, 800, 600);

        // Set up the stage
        primaryStage.setTitle("University Equipment Lending System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Get the authentication service
     *
     * @return The authentication service
     */
    public static AuthenticationService getAuthenticationService() {
        return authenticationService;
    }

    /**
     * Get the user service
     *
     * @return The user service
     */
    public static UserService getUserService() {
        return userService;
    }

    /**
     * Get the course service
     *
     * @return The course service
     */
    public static CourseService getCourseService() {
        return courseService;
    }

    /**
     * Get the equipment service
     *
     * @return The equipment service
     */
    public static EquipmentService getEquipmentService() {
        return equipmentService;
    }

    /**
     * Get the lending service
     *
     * @return The lending service
     */
    public static LendingService getLendingService() {
        return lendingService;
    }

    /**
     * Get the notification service
     *
     * @return The notification service
     */
    public static NotificationService getNotificationService() {
        return notificationService;
    }
}
