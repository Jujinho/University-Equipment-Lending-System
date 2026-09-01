/**
 * @author Group 9
 */
package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.Main;
import org.example.model.*;
import org.example.service.AuthenticationService;

import java.io.IOException;

/**
 * Controller for the login screen.
 */
public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button loginButton;

    @FXML
    private Button visitorButton;

    private AuthenticationService authenticationService;

    /**
     * Initialize the controller
     */
    @FXML
    public void initialize() {
        // Get the authentication service
        authenticationService = Main.getAuthenticationService();

        // Clear any error message
        errorLabel.setText("");
    }

    /**
     * Handle login button click
     *
     * @param event The action event
     */
    @FXML
    public void handleLogin(ActionEvent event) {
        // Get the username and password
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Validate input
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter username and password");
            return;
        }

        // Authenticate the user
        boolean authenticated = authenticationService.authenticate(username, password);

        if (authenticated) {
            // Get the current user
            User user = authenticationService.getCurrentUser();

            // Navigate to the appropriate view based on user role
            try {
                if (user instanceof Student) {
                    navigateToStudentView();
                } else if (user instanceof AcademicStaff) {
                    navigateToAcademicStaffView();
                } else if (user instanceof ProfessionalStaff) {
                    navigateToProfessionalStaffView();
                } else if (user instanceof Administrator) {
                    navigateToAdministratorView();
                } else {
                    // Fallback to visitor view if role is unknown
                    navigateToVisitorView();
                }
            } catch (IOException e) {
                errorLabel.setText("Error loading view: " + e.getMessage());
            }
        } else {
            errorLabel.setText("Invalid username or password");
        }
    }

    /**
     * Handle visitor access button click
     *
     * @param event The action event
     */
    @FXML
    public void handleVisitorAccess(ActionEvent event) {
        try {
            navigateToVisitorView();
        } catch (IOException e) {
            errorLabel.setText("Error loading visitor view: " + e.getMessage());
        }
    }

    /**
     * Navigate to the student view
     *
     * @throws IOException If an I/O error occurs
     */
    private void navigateToStudentView() throws IOException {
        navigateToView("/fxml/student_view.fxml");
    }

    /**
     * Navigate to the academic staff view
     *
     * @throws IOException If an I/O error occurs
     */
    private void navigateToAcademicStaffView() throws IOException {
        navigateToView("/fxml/academic_staff_view.fxml");
    }

    /**
     * Navigate to the professional staff view
     *
     * @throws IOException If an I/O error occurs
     */
    private void navigateToProfessionalStaffView() throws IOException {
        navigateToView("/fxml/professional_staff_view.fxml");
    }

    /**
     * Navigate to the administrator view
     *
     * @throws IOException If an I/O error occurs
     */
    private void navigateToAdministratorView() throws IOException {
        navigateToView("/fxml/administrator_view.fxml");
    }

    /**
     * Navigate to the visitor view
     *
     * @throws IOException If an I/O error occurs
     */
    private void navigateToVisitorView() throws IOException {
        navigateToView("/fxml/visitor_view.fxml");
    }

    /**
     * Navigate to a view
     *
     * @param fxmlPath The path to the FXML file
     * @throws IOException If an I/O error occurs
     */
    private void navigateToView(String fxmlPath) throws IOException {
        // Load the FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

        // Get the current stage
        Stage stage = (Stage) loginButton.getScene().getWindow();

        // Set up the scene
        Scene scene = new Scene(root, 800, 600);

        // Set up the stage
        stage.setScene(scene);
        stage.show();
    }
}
