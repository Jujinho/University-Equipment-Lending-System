/**
 * @author Group 9
 */
package org.example.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.stage.Stage;

import java.io.File;
import org.example.Main;
import org.example.model.*;
import org.example.service.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

/**
 * Controller for the administrator view.
 */
public class AdministratorController {

    // Services
    private AuthenticationService authenticationService;
    private UserService userService;
    private EquipmentService equipmentService;
    private LendingService lendingService;
    private CourseService courseService;
    private NotificationService notificationService;

    // Current administrator
    private Administrator currentAdmin;

    // Timer for auto-refresh
    private Timer refreshTimer;

    // Search suggestions for auto-complete
    private ObservableList<String> searchUserSuggestions;
    private ObservableList<String> searchCourseSuggestions;
    private ObservableList<String> searchEquipmentSuggestions;
    private ObservableList<String> searchBorrowerSuggestions;

    // Personal Information Tab
    @FXML
    private Label welcomeLabel;
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private DatePicker dobPicker;
    @FXML
    private TextField addressField;
    @FXML
    private TextField adminIdField;
    @FXML
    private TextField departmentField;
    @FXML
    private TextField positionField;
    @FXML
    private TextField accessLevelField;
    @FXML
    private Button updateInfoButton;
    @FXML
    private Button changePasswordButton;

    // Manage Users Tab
    @FXML
    private ComboBox<String> userTypeComboBox;
    @FXML
    private TextField searchUserField;
    @FXML
    private Button searchUserButton;
    @FXML
    private Button addUserButton;
    @FXML
    private TableView<User> usersTable;
    @FXML
    private TableColumn<User, Integer> userIdColumn;
    @FXML
    private TableColumn<User, String> userNameColumn;
    @FXML
    private TableColumn<User, String> userEmailColumn;
    @FXML
    private TableColumn<User, String> userRoleColumn;
    @FXML
    private TableColumn<User, String> userDepartmentColumn;
    @FXML
    private TableColumn<User, HBox> userActionsColumn;

    // Manage Courses Tab
    @FXML
    private TextField searchCourseField;
    @FXML
    private Button searchCourseButton;
    @FXML
    private Button addCourseButton;
    @FXML
    private TableView<Course> coursesTable;
    @FXML
    private TableColumn<Course, Integer> courseIdColumn;
    @FXML
    private TableColumn<Course, String> courseCodeColumn;
    @FXML
    private TableColumn<Course, String> courseNameColumn;
    @FXML
    private TableColumn<Course, Integer> courseSemesterColumn;
    @FXML
    private TableColumn<Course, Integer> courseYearColumn;
    @FXML
    private TableColumn<Course, String> courseInstructorColumn;
    @FXML
    private TableColumn<Course, HBox> courseActionsColumn;

    // Manage Equipment Tab
    @FXML
    private TextField searchEquipmentField;
    @FXML
    private ComboBox<String> categoryComboBox;
    @FXML
    private Button searchEquipmentButton;
    @FXML
    private Button addEquipmentButton;
    @FXML
    private TableView<Equipment> equipmentTable;
    @FXML
    private TableColumn<Equipment, Integer> eqIdColumn;
    @FXML
    private TableColumn<Equipment, String> eqNameColumn;
    @FXML
    private TableColumn<Equipment, String> eqCategoryColumn;
    @FXML
    private TableColumn<Equipment, String> eqConditionColumn;
    @FXML
    private TableColumn<Equipment, Boolean> eqAvailableColumn;
    @FXML
    private TableColumn<Equipment, HBox> eqActionsColumn;

    // Manage Lending Tab
    @FXML
    private ComboBox<String> lendingStatusComboBox;
    @FXML
    private TextField borrowerField;
    @FXML
    private Button searchLendingButton;
    @FXML
    private Button createLendingButton;
    @FXML
    private TableView<LendingRecord> lendingTable;
    @FXML
    private TableColumn<LendingRecord, Integer> lendingIdColumn;
    @FXML
    private TableColumn<LendingRecord, String> lendingBorrowerColumn;
    @FXML
    private TableColumn<LendingRecord, String> lendingEquipmentColumn;
    @FXML
    private TableColumn<LendingRecord, String> lendingBorrowDateColumn;
    @FXML
    private TableColumn<LendingRecord, String> lendingDueDateColumn;
    @FXML
    private TableColumn<LendingRecord, String> lendingStatusColumn;
    @FXML
    private TableColumn<LendingRecord, Button> lendingActionColumn;    // System Statistics Tab
    @FXML
    private DatePicker statsFromDatePicker;
    @FXML
    private DatePicker statsToDatePicker;
    @FXML
    private Button refreshStatsButton;
    @FXML
    private PieChart statusPieChart;
    @FXML
    private BarChart<String, Number> equipmentBarChart;
    @FXML
    private Label totalUsersLabel;
    @FXML
    private Label totalEquipmentLabel;
    @FXML
    private Label totalLendingsLabel;
    @FXML
    private Label overdueRateLabel;
    @FXML
    private ProgressIndicator statsProgressIndicator;

    // Bottom
    @FXML
    private Button logoutButton;

    // Notifications tab
    @FXML
    private TableView<NotificationService.Notification> notificationsTable;
    @FXML
    private TableColumn<NotificationService.Notification, String> notificationTimestampColumn;
    @FXML
    private TableColumn<NotificationService.Notification, String> notificationRecipientColumn;
    @FXML
    private TableColumn<NotificationService.Notification, String> notificationSubjectColumn;
    @FXML
    private TableColumn<NotificationService.Notification, Button> notificationViewColumn;
    @FXML
    private Button refreshNotificationsButton;
    @FXML
    private Button clearNotificationsButton;

    // Date formatter
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Initialize the controller
     */
    @FXML
    public void initialize() {
        // Get services
        authenticationService = Main.getAuthenticationService();
        userService = Main.getUserService();
        equipmentService = Main.getEquipmentService();
        lendingService = Main.getLendingService();
        courseService = Main.getCourseService();
        notificationService = Main.getNotificationService();

        // Get current administrator
        if (authenticationService.isAdministrator()) {
            currentAdmin = authenticationService.getCurrentAdministrator();
            welcomeLabel.setText("Welcome, " + currentAdmin.getFullName());

            // Initialize personal information
            initializePersonalInfo();

            // Initialize other tabs
            initializeUserManagement();
            initializeCourseManagement();
            initializeEquipmentManagement();
            initializeLendingManagement();
            initializeStatistics();
            initializeNotifications();

            // Set up auto-refresh for lending management
            refreshTimer = new Timer(true); // true makes it a daemon timer
            refreshTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    // Run on JavaFX thread
                    javafx.application.Platform.runLater(() -> {
                        loadLendingRecords();
                    });
                }   
            }, 10000, 10000); // 10 seconds delay, 10 seconds period
        } else {
            showAlert("Error", "Not logged in as administrator", Alert.AlertType.ERROR);
        }
    }

    /**
     * Initialize personal information tab
     */
    private void initializePersonalInfo() {
        // Set personal information fields
        firstNameField.setText(currentAdmin.getFirstName());
        lastNameField.setText(currentAdmin.getLastName());
        emailField.setText(currentAdmin.getEmail());
        phoneField.setText(currentAdmin.getPhoneNumber());
        if (currentAdmin.getDateOfBirth() != null) {
            dobPicker.setValue(currentAdmin.getDateOfBirth());
        }
        addressField.setText(currentAdmin.getAddress());
        adminIdField.setText(currentAdmin.getAdminId());
        departmentField.setText(currentAdmin.getDepartment());
        positionField.setText(currentAdmin.getPosition());
        accessLevelField.setText(currentAdmin.getAccessLevel());
    }

    /**
     * Initialize user management tab
     */
    private void initializeUserManagement() {
        // Set up user type combo box
        userTypeComboBox.setItems(FXCollections.observableArrayList(
                "All", "Student", "Academic Staff", "Professional Staff", "Administrator"));
        userTypeComboBox.getSelectionModel().selectFirst();

        // Initialize search suggestions list for users
        searchUserSuggestions = FXCollections.observableArrayList();

        // Initialize auto-complete for user search field
        setupUserSearchAutoComplete();

        // Set up table columns
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        userNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFullName()));
        userEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        userRoleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        userDepartmentColumn.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            String department = "";

            if (user instanceof AcademicStaff) {
                department = ((AcademicStaff) user).getDepartment();
            } else if (user instanceof ProfessionalStaff) {
                department = ((ProfessionalStaff) user).getDepartment();
            } else if (user instanceof Administrator) {
                department = ((Administrator) user).getDepartment();
            }

            return new SimpleStringProperty(department);
        });        // Set up actions column
        userActionsColumn.setCellFactory(param -> new TableCell<>() {
            private final HBox actionBox = new HBox(5);
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");
                actionBox.getChildren().addAll(editButton, deleteButton);
            }

            @Override
            protected void updateItem(HBox item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    return;
                }

                User user = getTableView().getItems().get(getIndex());
                editButton.setOnAction(event -> handleEditUser(user));
                deleteButton.setOnAction(event -> handleDeleteUser(user));
                setGraphic(actionBox);
            }
        });

        // Load users
        loadUsers();
    }

    /**
     * Initialize course management tab
     */
    private void initializeCourseManagement() {
        // Initialize search suggestions list for courses
        searchCourseSuggestions = FXCollections.observableArrayList();
        // Initialize auto-complete for course search field
        setupCourseSearchAutoComplete();

        // Set up table columns
        courseIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        courseCodeColumn.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        courseNameColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        courseSemesterColumn.setCellValueFactory(new PropertyValueFactory<>("semester"));
        courseYearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));

        courseInstructorColumn.setCellValueFactory(cellData -> {
            Course course = cellData.getValue();
            if (course.getInstructor() != null) {
                return new SimpleStringProperty(course.getInstructor().getFullName());
            } else {
                return new SimpleStringProperty("Not assigned");
            }
        });
          // Set up actions column
        courseActionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final Button manageStudentsButton = new Button("Manage Students");
            private final HBox pane = new HBox(5);

            {
                deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

                editButton.setOnAction(event -> {
                    Course course = getTableView().getItems().get(getIndex());
                    handleEditCourse(course);
                });

                deleteButton.setOnAction(event -> {
                    Course course = getTableView().getItems().get(getIndex());
                    handleDeleteCourse(course);
                });

                manageStudentsButton.setOnAction(event -> {
                    Course course = getTableView().getItems().get(getIndex());
                    handleManageCourseStudents(course);
                });

                pane.getChildren().addAll(editButton, manageStudentsButton, deleteButton);
                pane.setSpacing(5);
            }

            @Override
            protected void updateItem(HBox item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(pane);
                }
            }
        });

        // Load courses
        loadCourses();
    }

    /**
     * Initialize equipment management tab
     */
    private void initializeEquipmentManagement() {
        // Initialize search suggestions list for equipment
        searchEquipmentSuggestions = FXCollections.observableArrayList();

        // Initialize auto-complete for equipment search field
        setupEquipmentSearchAutoComplete();

        // Set up category combo box
        List<String> categories = equipmentService.getAllCategories();
        categories.add(0, "All");
        categoryComboBox.setItems(FXCollections.observableArrayList(categories));
        categoryComboBox.getSelectionModel().selectFirst();

        // Set up table columns
        eqIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        eqNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        eqCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        eqConditionColumn.setCellValueFactory(new PropertyValueFactory<>("condition"));
        eqAvailableColumn.setCellValueFactory(new PropertyValueFactory<>("available"));
          // Set up actions column
        eqActionsColumn.setCellFactory(param -> new TableCell<>() {
            private final HBox actionBox = new HBox(5);
            private final Button viewButton = new Button("View");
            private final Button imagesButton = new Button("Images");
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");
                actionBox.getChildren().addAll(viewButton, imagesButton, editButton, deleteButton);
            }

            @Override
            protected void updateItem(HBox item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    return;
                }

                Equipment equipment = getTableView().getItems().get(getIndex());
                viewButton.setOnAction(event -> handleViewEquipment(equipment));
                imagesButton.setOnAction(event -> handleManageEquipmentImages(equipment));
                editButton.setOnAction(event -> handleEditEquipment(equipment));
                deleteButton.setOnAction(event -> handleDeleteEquipment(equipment));
                setGraphic(actionBox);
            }
        });

        // Load equipment
        loadEquipment();
    }

    /**
     * Initialize lending management tab
     */
    private void initializeLendingManagement() {
        // Initialize search suggestions list for borrowers
        searchBorrowerSuggestions = FXCollections.observableArrayList();

        // Initialize auto-complete for borrower search field
        setupBorrowerSearchAutoComplete();

        // Set up status combo box
        lendingStatusComboBox.setItems(FXCollections.observableArrayList(
                "All", "Pending", "Approved", "Borrowed", "Returned", "Overdue", "Rejected"));
        lendingStatusComboBox.getSelectionModel().selectFirst();

        // Set up table columns
        lendingIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        lendingBorrowerColumn.setCellValueFactory(cellData -> {
            User borrower = cellData.getValue().getBorrower();
            return new SimpleStringProperty(borrower != null ? borrower.getFullName() : "");
        });

        lendingEquipmentColumn.setCellValueFactory(cellData -> {
            Equipment equipment = cellData.getValue().getEquipment();
            return new SimpleStringProperty(equipment != null ? equipment.getName() : "");
        });

        lendingBorrowDateColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getBorrowDate().format(dateFormatter)));

        lendingDueDateColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getDueDate().format(dateFormatter)));

        lendingStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Set up action column
        lendingActionColumn.setCellFactory(param -> new TableCell<>() {
            private final HBox actionBox = new HBox(5);
            private final Button viewButton = new Button("View");
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");
                actionBox.getChildren().addAll(viewButton, editButton, deleteButton);
            }

            @Override
            protected void updateItem(Button item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    return;
                }

                LendingRecord record = getTableView().getItems().get(getIndex());
                viewButton.setOnAction(event -> handleViewLendingRecord(record));
                editButton.setOnAction(event -> handleEditLendingRecord(record));
                deleteButton.setOnAction(event -> handleDeleteLendingRecord(record));
                setGraphic(actionBox);
            }
        });

        // Load lending records
        loadLendingRecords();
    }
    
    /**
     * Initialize statistics tab
     */
    private void initializeStatistics() {
        // Set up date pickers
        statsFromDatePicker.setValue(LocalDate.now().minusMonths(1));
        statsToDatePicker.setValue(LocalDate.now());
        
        // Make sure progress indicator is initially hidden
        statsProgressIndicator.setVisible(false);
        statsProgressIndicator.setProgress(-1.0); // indeterminate progress

        // Load statistics
        loadStatistics();
    }

    /**
     * Initialize notifications tab
     */
    private void initializeNotifications() {
        // Set up table columns
        notificationTimestampColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getFormattedTimestamp()));

        notificationRecipientColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getRecipient()));

        notificationSubjectColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getSubject()));

        // Set up view button column
        notificationViewColumn.setCellFactory(param -> new TableCell<>() {
            private final Button viewButton = new Button("View");

            {
                viewButton.setOnAction(event -> {
                    NotificationService.Notification notification = getTableView().getItems().get(getIndex());
                    handleViewNotification(notification);
                });
            }

            @Override
            protected void updateItem(Button item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(viewButton);
                }
            }
        });

        // Set up refresh button
        refreshNotificationsButton.setOnAction(event -> loadNotifications());

        // Set up clear button
        clearNotificationsButton.setOnAction(event -> {
            notificationService.clearNotifications();
            loadNotifications();
        });

        // Load notifications
        loadNotifications();

        // Add a test notification if there are no notifications
        if (notificationsTable.getItems().isEmpty()) {
            notificationService.addTestNotification(currentAdmin.getEmail());
            loadNotifications();
        }
    }

    /**
     * Load notifications from the notification service
     */
    private void loadNotifications() {
        List<NotificationService.Notification> notifications = notificationService.getAllNotifications();
        notificationsTable.setItems(FXCollections.observableArrayList(notifications));
    }

    /**
     * Handle viewing a notification
     * 
     * @param notification The notification to view
     */
    private void handleViewNotification(NotificationService.Notification notification) {
        // Create a dialog to display the notification
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Notification Details");
        dialog.setHeaderText(notification.getSubject());

        // Create a grid pane for the dialog content
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Add notification details to the grid
        grid.add(new Label("Timestamp:"), 0, 0);
        grid.add(new Label(notification.getFormattedTimestamp()), 1, 0);

        grid.add(new Label("Recipient:"), 0, 1);
        grid.add(new Label(notification.getRecipient()), 1, 1);

        grid.add(new Label("Subject:"), 0, 2);
        grid.add(new Label(notification.getSubject()), 1, 2);

        grid.add(new Label("Message:"), 0, 3);

        TextArea messageArea = new TextArea(notification.getMessage());
        messageArea.setEditable(false);
        messageArea.setWrapText(true);
        messageArea.setPrefWidth(400);
        messageArea.setPrefHeight(200);
        grid.add(messageArea, 0, 4, 2, 1);

        dialog.getDialogPane().setContent(grid);

        // Add close button
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        // Show the dialog
        dialog.showAndWait();
    }

    /**
     * Handle update information button click
     *
     * @param event The action event
     */
    @FXML
    public void handleUpdateInfo(ActionEvent event) {
        // Update admin information
        currentAdmin.setFirstName(firstNameField.getText());
        currentAdmin.setLastName(lastNameField.getText());
        currentAdmin.setEmail(emailField.getText());
        currentAdmin.setPhoneNumber(phoneField.getText());
        currentAdmin.setDateOfBirth(dobPicker.getValue());
        currentAdmin.setAddress(addressField.getText());

        // Save changes
        boolean success = userService.updatePersonalInfo(currentAdmin);
        if (success) {
            showAlert("Success", "Personal information updated successfully", Alert.AlertType.INFORMATION);
            welcomeLabel.setText("Welcome, " + currentAdmin.getFullName());
        } else {
            showAlert("Error", "Failed to update personal information", Alert.AlertType.ERROR);
        }
    }

    /**
     * Handle change password button click
     *
     * @param event The action event
     */
    @FXML
    public void handleChangePassword(ActionEvent event) {
        // Create a dialog for changing password
        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle("Change Password");
        dialog.setHeaderText("Enter your current password and new password");

        // Set the button types
        ButtonType changeButtonType = new ButtonType("Change", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(changeButtonType, ButtonType.CANCEL);

        // Create the password fields
        PasswordField oldPasswordField = new PasswordField();
        oldPasswordField.setPromptText("Current Password");
        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New Password");
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm New Password");

        // Layout the dialog
        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
        grid.add(new Label("Current Password:"), 0, 0);
        grid.add(oldPasswordField, 1, 0);
        grid.add(new Label("New Password:"), 0, 1);
        grid.add(newPasswordField, 1, 1);
        grid.add(new Label("Confirm New Password:"), 0, 2);
        grid.add(confirmPasswordField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Convert the result to a password-pair when the change button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == changeButtonType) {
                return new String[]{oldPasswordField.getText(), newPasswordField.getText(), confirmPasswordField.getText()};
            }
            return null;
        });

        Optional<String[]> result = dialog.showAndWait();

        result.ifPresent(passwords -> {
            String oldPassword = passwords[0];
            String newPassword = passwords[1];
            String confirmPassword = passwords[2];

            // Validate passwords
            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                showAlert("Error", "Passwords cannot be empty", Alert.AlertType.ERROR);
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                showAlert("Error", "New passwords do not match", Alert.AlertType.ERROR);
                return;
            }

            // Change password
            boolean success = userService.changePassword(currentAdmin.getId(), oldPassword, newPassword);
            if (success) {
                showAlert("Success", "Password changed successfully", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Error", "Failed to change password. Check your current password.", Alert.AlertType.ERROR);
            }
        });;
    }    

    /**
     * Handle search user button click
     *
     * @param event The action event
     */
    @FXML
    public void handleSearchUser(ActionEvent event) {
        loadUsers();
    }

    /**
     * Handle add user button click
     *
     * @param event The action event
     */
    @FXML
    public void handleAddUser(ActionEvent event) {
        try {
            // Create a dialog for adding a new user
            Dialog<User> dialog = new Dialog<>();
            dialog.setTitle("Add New User");
            dialog.setHeaderText("Enter information for the new user");

            // Set the button types
            ButtonType saveButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

            // Create the user type selection
            ComboBox<String> userTypeCombo = new ComboBox<>();
            userTypeCombo.setItems(FXCollections.observableArrayList(
                    "Student", "Academic Staff", "Professional Staff", "Administrator"));
            userTypeCombo.getSelectionModel().selectFirst();

            // Create the form fields for basic user information
            TextField usernameField = new TextField();
            usernameField.setPromptText("Username");

            PasswordField passwordField = new PasswordField();
            passwordField.setPromptText("Password");

            TextField firstNameField = new TextField();
            firstNameField.setPromptText("First Name");

            TextField lastNameField = new TextField();
            lastNameField.setPromptText("Last Name");

            TextField emailField = new TextField();
            emailField.setPromptText("Email");

            TextField phoneField = new TextField();
            phoneField.setPromptText("Phone Number");

            DatePicker dobPicker = new DatePicker();
            dobPicker.setPromptText("Date of Birth");

            TextField addressField = new TextField();
            addressField.setPromptText("Address");

            // Create fields for specific user types
            TextField studentIdField = new TextField();
            studentIdField.setPromptText("Student ID");

            ComboBox<String> programComboBox = new ComboBox<>();
            programComboBox.setPromptText("Program");
            programComboBox.setItems(FXCollections.observableArrayList(
                    "Computer Science", "Engineering", "Business", "Arts", "Science", "Medicine", "Law"));

            TextField yearOfStudyField = new TextField();
            yearOfStudyField.setPromptText("Year of Study");

            TextField departmentField = new TextField();
            departmentField.setPromptText("Department");

            TextField positionField = new TextField();
            positionField.setPromptText("Position");

            TextField specialtyField = new TextField();
            specialtyField.setPromptText("Specialty");

            TextField officeField = new TextField();
            officeField.setPromptText("Office");

            TextField adminIdField = new TextField();
            adminIdField.setPromptText("Admin ID");

            TextField accessLevelField = new TextField();
            accessLevelField.setPromptText("Access Level");

            // Layout the dialog
            VBox userSpecificFields = new VBox(10);

            userTypeCombo.setOnAction(e -> {
                userSpecificFields.getChildren().clear();
                String selectedType = userTypeCombo.getValue();

                switch (selectedType) {
                    case "Student":
                        userSpecificFields.getChildren().addAll(
                                new Label("Student-specific Information"),
                                new Label("Student ID:"), studentIdField,
                                new Label("Program:"), programComboBox,
                                new Label("Year of Study:"), yearOfStudyField
                        );
                        break;
                    case "Academic Staff":
                        userSpecificFields.getChildren().addAll(
                                new Label("Academic Staff-specific Information"),
                                new Label("Department:"), departmentField,
                                new Label("Position:"), positionField,
                                new Label("Specialty:"), specialtyField
                        );
                        break;
                    case "Professional Staff":
                        userSpecificFields.getChildren().addAll(
                                new Label("Professional Staff-specific Information"),
                                new Label("Department:"), departmentField,
                                new Label("Position:"), positionField,
                                new Label("Office:"), officeField
                        );
                        break;
                    case "Administrator":
                        userSpecificFields.getChildren().addAll(
                                new Label("Administrator-specific Information"),
                                new Label("Admin ID:"), adminIdField,
                                new Label("Department:"), departmentField,
                                new Label("Position:"), positionField,
                                new Label("Access Level:"), accessLevelField
                        );
                        break;
                }
            });

            // Trigger the action to initialize the fields
            userTypeCombo.getSelectionModel().selectFirst();

            // Create main layout
            GridPane basicInfoGrid = new GridPane();
            basicInfoGrid.setHgap(10);
            basicInfoGrid.setVgap(10);
            basicInfoGrid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

            basicInfoGrid.add(new Label("User Type:"), 0, 0);
            basicInfoGrid.add(userTypeCombo, 1, 0);
            basicInfoGrid.add(new Label("Username:"), 0, 1);
            basicInfoGrid.add(usernameField, 1, 1);
            basicInfoGrid.add(new Label("Password:"), 0, 2);
            basicInfoGrid.add(passwordField, 1, 2);
            basicInfoGrid.add(new Label("First Name:"), 0, 3);
            basicInfoGrid.add(firstNameField, 1, 3);
            basicInfoGrid.add(new Label("Last Name:"), 0, 4);
            basicInfoGrid.add(lastNameField, 1, 4);
            basicInfoGrid.add(new Label("Email:"), 0, 5);
            basicInfoGrid.add(emailField, 1, 5);
            basicInfoGrid.add(new Label("Phone:"), 0, 6);
            basicInfoGrid.add(phoneField, 1, 6);
            basicInfoGrid.add(new Label("Date of Birth:"), 0, 7);
            basicInfoGrid.add(dobPicker, 1, 7);
            basicInfoGrid.add(new Label("Address:"), 0, 8);
            basicInfoGrid.add(addressField, 1, 8);

            VBox mainLayout = new VBox(20);
            mainLayout.getChildren().addAll(
                    basicInfoGrid,
                    new Separator(),
                    userSpecificFields
            );

            ScrollPane scrollPane = new ScrollPane(mainLayout);
            scrollPane.setFitToWidth(true);
            scrollPane.setPrefHeight(500);

            dialog.getDialogPane().setContent(scrollPane);

            // Convert the result when the create button is clicked
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == saveButtonType) {
                    try {
                        User newUser = null;
                        String userType = userTypeCombo.getValue();
                        String username = usernameField.getText();
                        String password = passwordField.getText();

                        // Validate required fields
                        if (username.isEmpty() || password.isEmpty() || firstNameField.getText().isEmpty() || 
                            lastNameField.getText().isEmpty() || emailField.getText().isEmpty()) {
                            showAlert("Error", "Please fill out all required fields", Alert.AlertType.ERROR);
                            return null;
                        }

                        switch (userType) {
                            case "Student":
                                if (studentIdField.getText().isEmpty() || programComboBox.getValue() == null) {
                                    showAlert("Error", "Please fill out all student fields", Alert.AlertType.ERROR);
                                    return null;
                                }                                Student student = new Student();
                                student.setStudentId(studentIdField.getText());
                                student.setMajor(programComboBox.getValue());
                                if (!yearOfStudyField.getText().isEmpty()) {
                                    try {
                                        student.setYear(Integer.parseInt(yearOfStudyField.getText()));
                                    } catch (NumberFormatException e) {
                                        showAlert("Error", "Year of Study must be a number", Alert.AlertType.ERROR);
                                        return null;
                                    }
                                }
                                newUser = student;
                                break;

                            case "Academic Staff":
                                if (departmentField.getText().isEmpty() || positionField.getText().isEmpty()) {
                                    showAlert("Error", "Please fill out all academic staff fields", Alert.AlertType.ERROR);
                                    return null;
                                }                                AcademicStaff academicStaff = new AcademicStaff();
                                academicStaff.setStaffId(specialtyField.getText()); // Using specialtyField for staffId
                                academicStaff.setDepartment(departmentField.getText());
                                academicStaff.setPosition(positionField.getText());
                                newUser = academicStaff;
                                break;

                            case "Professional Staff":
                                if (departmentField.getText().isEmpty() || positionField.getText().isEmpty()) {
                                    showAlert("Error", "Please fill out all professional staff fields", Alert.AlertType.ERROR);
                                    return null;
                                }                                ProfessionalStaff professionalStaff = new ProfessionalStaff();
                                professionalStaff.setStaffId(officeField.getText()); // Using officeField for staffId
                                professionalStaff.setDepartment(departmentField.getText());
                                professionalStaff.setPosition(positionField.getText());
                                professionalStaff.setSpecialization(officeField.getText()); // Using officeField for specialization
                                newUser = professionalStaff;
                                break;

                            case "Administrator":
                                if (adminIdField.getText().isEmpty() || departmentField.getText().isEmpty() || 
                                    positionField.getText().isEmpty() || accessLevelField.getText().isEmpty()) {
                                    showAlert("Error", "Please fill out all administrator fields", Alert.AlertType.ERROR);
                                    return null;
                                }
                                Administrator administrator = new Administrator();
                                administrator.setAdminId(adminIdField.getText());
                                administrator.setDepartment(departmentField.getText());
                                administrator.setPosition(positionField.getText());
                                administrator.setAccessLevel(accessLevelField.getText());
                                newUser = administrator;
                                break;
                        }

                        if (newUser != null) {
                            newUser.setUsername(username);
                            newUser.setPassword(password); // In a real app, this should be hashed
                            newUser.setFirstName(firstNameField.getText());
                            newUser.setLastName(lastNameField.getText());
                            newUser.setEmail(emailField.getText());
                            newUser.setPhoneNumber(phoneField.getText());
                            newUser.setDateOfBirth(dobPicker.getValue());
                            newUser.setAddress(addressField.getText());
                        }

                        return newUser;
                    } catch (Exception e) {
                        e.printStackTrace();
                        showAlert("Error", "Error creating user: " + e.getMessage(), Alert.AlertType.ERROR);
                        return null;
                    }
                }
                return null;
            });

            Optional<User> result = dialog.showAndWait();            result.ifPresent(newUser -> {
                boolean success = userService.createUser(newUser);
                if (success) {
                    showAlert("Success", "User created successfully", Alert.AlertType.INFORMATION);
                    loadUsers(); // Refresh the table
                } else {
                    showAlert("Error", "Failed to create user. Username may already exist.", Alert.AlertType.ERROR);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while creating the dialog: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Handle search course button click
     *
     * @param event The action event
     */
    @FXML
    public void handleSearchCourse(ActionEvent event) {
            loadCourses();
    }

    /**
     * Handle add course button click
     *
     * @param event The action event
     */
    @FXML
    public void handleAddCourse(ActionEvent event) {
        try {
            // Create a dialog for adding a new course
            Dialog<Course> dialog = new Dialog<>();
            dialog.setTitle("Add New Course");
            dialog.setHeaderText("Enter information for the new course");

            // Set the button types
            ButtonType saveButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

            // Create the form fields
            TextField courseCodeField = new TextField();
            courseCodeField.setPromptText("Course Code");

            TextField courseNameField = new TextField();
            courseNameField.setPromptText("Course Name");

            TextField descriptionField = new TextField();
            descriptionField.setPromptText("Description");

            TextField semesterField = new TextField();
            semesterField.setPromptText("Semester (1 or 2)");

            TextField yearField = new TextField();
            yearField.setPromptText("Year (e.g., 2023)");

            // Get all academic staff for instructor selection
            List<AcademicStaff> academicStaffList = userService.getAllAcademicStaff();
            ComboBox<AcademicStaff> instructorComboBox = new ComboBox<>();
            instructorComboBox.setItems(FXCollections.observableArrayList(academicStaffList));
            instructorComboBox.setPromptText("Select Instructor (Optional)");
            instructorComboBox.setCellFactory(param -> new ListCell<AcademicStaff>() {
                @Override
                protected void updateItem(AcademicStaff item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getFullName());
                    }
                }
            });
            instructorComboBox.setButtonCell(new ListCell<AcademicStaff>() {
                @Override
                protected void updateItem(AcademicStaff item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText("Select Instructor (Optional)");
                    } else {
                        setText(item.getFullName());
                    }
                }
            });

            // Layout the dialog
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

            grid.add(new Label("Course Code:"), 0, 0);
            grid.add(courseCodeField, 1, 0);
            grid.add(new Label("Course Name:"), 0, 1);
            grid.add(courseNameField, 1, 1);
            grid.add(new Label("Description:"), 0, 2);
            grid.add(descriptionField, 1, 2);
            grid.add(new Label("Semester:"), 0, 3);
            grid.add(semesterField, 1, 3);
            grid.add(new Label("Year:"), 0, 4);
            grid.add(yearField, 1, 4);
            grid.add(new Label("Instructor:"), 0, 5);
            grid.add(instructorComboBox, 1, 5);

            dialog.getDialogPane().setContent(grid);

            // Convert the result to a course when the create button is clicked
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == saveButtonType) {
                    try {
                        // Validate required fields
                        if (courseCodeField.getText().isEmpty() || courseNameField.getText().isEmpty()) {
                            showAlert("Error", "Course code and name are required", Alert.AlertType.ERROR);
                            return null;
                        }

                        // Validate numeric fields
                        int semester;
                        int year;
                        try {
                            semester = Integer.parseInt(semesterField.getText());
                            if (semester != 1 && semester != 2) {
                                showAlert("Error", "Semester must be 1 or 2", Alert.AlertType.ERROR);
                                return null;
                            }
                        } catch (NumberFormatException e) {
                            showAlert("Error", "Semester must be a number", Alert.AlertType.ERROR);
                            return null;
                        }

                        try {
                            year = Integer.parseInt(yearField.getText());
                            if (year < 2000 || year > 2100) { // Basic validation for reasonable year range
                                showAlert("Error", "Please enter a valid year", Alert.AlertType.ERROR);
                                return null;
                            }
                        } catch (NumberFormatException e) {
                            showAlert("Error", "Year must be a number", Alert.AlertType.ERROR);
                            return null;
                        }

                        // Create new course object
                        Course newCourse = new Course();
                        newCourse.setCourseCode(courseCodeField.getText());
                        newCourse.setCourseName(courseNameField.getText());
                        newCourse.setDescription(descriptionField.getText());
                        newCourse.setSemester(semester);
                        newCourse.setYear(year);
                        newCourse.setInstructor(instructorComboBox.getValue());

                        return newCourse;
                    } catch (Exception e) {
                        e.printStackTrace();
                        showAlert("Error", "Error creating course: " + e.getMessage(), Alert.AlertType.ERROR);
                        return null;
                    }
                }
                return null;
            });

            // Show the dialog and process the result
            Optional<Course> result = dialog.showAndWait();
            result.ifPresent(newCourse -> {
                boolean success = courseService.createCourse(newCourse);
                if (success) {
                    showAlert("Success", "Course created successfully", Alert.AlertType.INFORMATION);
                    loadCourses(); // Refresh the table
                } else {
                    showAlert("Error", "Failed to create course. Course code may already exist.", Alert.AlertType.ERROR);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while creating the dialog: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Handle search equipment button click
     *
     * @param event The action event
     */
    @FXML
    public void handleSearchEquipment(ActionEvent event) {
        loadEquipment();
    }

    /**
     * Handle add equipment button click
     *
     * @param event The action event
     */
    @FXML
    public void handleAddEquipment(ActionEvent event) {
        try {
            // Create a dialog for adding equipment
            Dialog<Equipment> dialog = new Dialog<>();
            dialog.setTitle("Add Equipment");
            dialog.setHeaderText("Enter information for new equipment");

            // Set the button types
            ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

            // Create the form fields
            TextField nameField = new TextField();
            nameField.setPromptText("Name");

            TextField descriptionField = new TextField();
            descriptionField.setPromptText("Description");

            // Create category dropdown with existing categories
            ComboBox<String> categoryCombo = new ComboBox<>();
            categoryCombo.setEditable(true);
            categoryCombo.setPromptText("Category");
            List<String> categories = equipmentService.getAllCategories();
            categoryCombo.setItems(FXCollections.observableArrayList(categories));

            // Create condition dropdown with common conditions
            ComboBox<String> conditionCombo = new ComboBox<>();
            conditionCombo.setEditable(true);
            conditionCombo.setPromptText("Condition");
            List<String> conditions = equipmentService.getAllConditions();
            conditionCombo.setItems(FXCollections.observableArrayList(conditions));
            if (conditions.isEmpty()) {
                // Add default values if no conditions exist
                conditionCombo.getItems().addAll("Excellent", "Good", "Fair", "Poor", "Damaged");
            }
            conditionCombo.getSelectionModel().select("Good");

            DatePicker purchaseDatePicker = new DatePicker(LocalDate.now());
            purchaseDatePicker.setPromptText("Purchase Date");

            TextField purchasePriceField = new TextField();
            purchasePriceField.setPromptText("Purchase Price");

            TextField manufacturerField = new TextField();
            manufacturerField.setPromptText("Manufacturer");

            TextField modelField = new TextField();
            modelField.setPromptText("Model");

            TextField serialNumberField = new TextField();
            serialNumberField.setPromptText("Serial Number");

            TextField locationField = new TextField();
            locationField.setPromptText("Location");

            CheckBox availableCheckBox = new CheckBox("Available");
            availableCheckBox.setSelected(true);

            // Image management section
            Label imageLabel = new Label("Images:");
            Button addImageButton = new Button("Add Images");

            // List to track new images to upload
            List<File> newImageFiles = new ArrayList<>();

            // Create an HBox for image display and management
            javafx.scene.layout.HBox imagesBox = new javafx.scene.layout.HBox(10);
            imagesBox.setPadding(new javafx.geometry.Insets(10));
            imagesBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            // Add action for add image button
            addImageButton.setOnAction(e -> {
                javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
                fileChooser.setTitle("Select Image Files");
                fileChooser.getExtensionFilters().addAll(
                    new javafx.stage.FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
                );

                // Open file chooser dialog with multiple selection
                List<File> selectedFiles = fileChooser.showOpenMultipleDialog(dialog.getOwner());

                if (selectedFiles != null && !selectedFiles.isEmpty()) {
                    for (File selectedFile : selectedFiles) {
                        try {
                            // Add to list of files to upload
                            newImageFiles.add(selectedFile);

                            // Display the new image in UI
                            VBox imageVBox = new VBox(5);
                            imageVBox.setAlignment(javafx.geometry.Pos.CENTER);

                            // Create image preview
                            javafx.scene.image.ImageView imageView = new javafx.scene.image.ImageView();
                            imageView.setFitHeight(100);
                            imageView.setFitWidth(100);
                            imageView.setPreserveRatio(true);

                            // Load image from file
                            javafx.scene.image.Image image = new javafx.scene.image.Image(selectedFile.toURI().toString());
                            imageView.setImage(image);

                            // Add remove button
                            Button removeButton = new Button("Remove");
                            final File fileRef = selectedFile; // Create final reference for lambda
                            removeButton.setOnAction(evt -> {
                                // Remove from list and UI
                                newImageFiles.remove(fileRef);
                                imagesBox.getChildren().remove(imageVBox);
                            });

                            // Add image and button to VBox
                            imageVBox.getChildren().addAll(imageView, removeButton);

                            // Add to images container
                            imagesBox.getChildren().add(imageVBox);

                        } catch (Exception ex) {
                            showAlert("Error", "Failed to load image: " + ex.getMessage(), Alert.AlertType.ERROR);
                        }
                    }
                }
            });

            // Create a scroll pane for images
            ScrollPane imageScrollPane = new ScrollPane(imagesBox);
            imageScrollPane.setFitToWidth(true);
            imageScrollPane.setPrefHeight(150);
            imageScrollPane.setMaxHeight(200);

            // Layout the dialog
            javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

            grid.add(new Label("Name:*"), 0, 0);
            grid.add(nameField, 1, 0);
            grid.add(new Label("Description:"), 0, 1);
            grid.add(descriptionField, 1, 1);
            grid.add(new Label("Category:*"), 0, 2);
            grid.add(categoryCombo, 1, 2);
            grid.add(new Label("Condition:*"), 0, 3);
            grid.add(conditionCombo, 1, 3);
            grid.add(new Label("Purchase Date:"), 0, 4);
            grid.add(purchaseDatePicker, 1, 4);
            grid.add(new Label("Purchase Price:"), 0, 5);
            grid.add(purchasePriceField, 1, 5);
            grid.add(new Label("Manufacturer:"), 0, 6);
            grid.add(manufacturerField, 1, 6);
            grid.add(new Label("Model:"), 0, 7);
            grid.add(modelField, 1, 7);
            grid.add(new Label("Serial Number:"), 0, 8);
            grid.add(serialNumberField, 1, 8);
            grid.add(new Label("Location:*"), 0, 9);
            grid.add(locationField, 1, 9);
            grid.add(new Label("Available:"), 0, 10);
            grid.add(availableCheckBox, 1, 10);
            grid.add(imageLabel, 0, 11);
            grid.add(imageScrollPane, 1, 11);
            grid.add(addImageButton, 1, 12);

            // Add a note about required fields
            Label requiredNote = new Label("* Required fields");
            requiredNote.setStyle("-fx-font-size: 11px; -fx-text-fill: gray;");
            grid.add(requiredNote, 0, 13, 2, 1);

            // Create a scroll pane for the entire form
            ScrollPane scrollPane = new ScrollPane(grid);
            scrollPane.setFitToWidth(true);
            scrollPane.setPrefHeight(500);
            dialog.getDialogPane().setContent(scrollPane);

            // Request focus on the name field by default
            javafx.application.Platform.runLater(() -> nameField.requestFocus());

            // Convert the result to an equipment when the save button is clicked
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == saveButtonType) {
                    // Validate required fields
                    if (nameField.getText().isEmpty()) {
                        showAlert("Error", "Name is required", Alert.AlertType.ERROR);
                        return null;
                    }

                    String category = categoryCombo.getValue();
                    if (category == null || category.isEmpty()) {
                        showAlert("Error", "Category is required", Alert.AlertType.ERROR);
                        return null;
                    }

                    String condition = conditionCombo.getValue();
                    if (condition == null || condition.isEmpty()) {
                        showAlert("Error", "Condition is required", Alert.AlertType.ERROR);
                        return null;
                    }

                    if (locationField.getText().isEmpty()) {
                        showAlert("Error", "Location is required", Alert.AlertType.ERROR);
                        return null;
                    }

                    try {
                        // Parse purchase price if provided
                        double purchasePrice = 0.0;
                        if (!purchasePriceField.getText().isEmpty()) {
                            purchasePrice = Double.parseDouble(purchasePriceField.getText());
                        }

                        // Create a new equipment object
                        Equipment newEquipment = new Equipment();
                        newEquipment.setName(nameField.getText());
                        newEquipment.setDescription(descriptionField.getText());
                        newEquipment.setCategory(category);
                        newEquipment.setCondition(condition);
                        newEquipment.setPurchaseDate(purchaseDatePicker.getValue());
                        newEquipment.setPurchasePrice(purchasePrice);
                        newEquipment.setManufacturer(manufacturerField.getText());
                        newEquipment.setModel(modelField.getText());
                        newEquipment.setSerialNumber(serialNumberField.getText());
                        newEquipment.setLocation(locationField.getText());
                        newEquipment.setAvailable(availableCheckBox.isSelected());

                        return newEquipment;
                    } catch (NumberFormatException e) {
                        showAlert("Error", "Purchase price must be a number", Alert.AlertType.ERROR);
                        return null;
                    }
                }
                return null;
            });

            // Show the dialog and process the result
            Optional<Equipment> result = dialog.showAndWait();
            result.ifPresent(newEquipment -> {
                boolean success = equipmentService.createEquipment(newEquipment);
                if (success) {
                    // Now handle the image uploads if any were selected
                    if (!newImageFiles.isEmpty()) {
                        // Get the newly created equipment's ID
                        int equipmentId = newEquipment.getId();
                        int addedCount = equipmentService.addMultipleEquipmentImages(equipmentId, newImageFiles);

                        if (addedCount > 0) {
                            showAlert("Success", 
                                    "Equipment created successfully. " + addedCount + " out of " + newImageFiles.size() + " image(s) added.", 
                                    Alert.AlertType.INFORMATION);
                        } else {
                            showAlert("Warning", 
                                    "Equipment created successfully but failed to add any of the " + newImageFiles.size() + " selected images.", 
                                    Alert.AlertType.WARNING);
                        }
                    } else {
                        showAlert("Success", "Equipment created successfully", Alert.AlertType.INFORMATION);
                    }
                    loadEquipment(); // Refresh the table
                } else {
                    showAlert("Error", "Failed to create equipment", Alert.AlertType.ERROR);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while creating the dialog: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Handle search lending button click
     *
     * @param event The action event
     */
    @FXML
    public void handleSearchLending(ActionEvent event) {
        loadLendingRecords();
    }

    /**
     * Handle create lending button click
     *
     * @param event The action event
     */
    @FXML
    public void handleCreateLending(ActionEvent event) {
        try {
            // Create a dialog for creating a new lending record
            Dialog<LendingRecord> dialog = new Dialog<>();
            dialog.setTitle("Create Lending Record");
            dialog.setHeaderText("Enter information for the new lending record");

            // Set the button types
            ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

            // Create form fields
            // 1. Borrower selection
            Label borrowerLabel = new Label("Borrower:");
            ComboBox<User> borrowerComboBox = new ComboBox<>();
            List<User> allUsers = userService.getAllUsers();
            borrowerComboBox.setItems(FXCollections.observableArrayList(allUsers));
            borrowerComboBox.setCellFactory(param -> new ListCell<User>() {
                @Override
                protected void updateItem(User item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getFullName() + " (" + item.getRole() + ")");
                    }
                }
            });
            borrowerComboBox.setButtonCell(new ListCell<User>() {
                @Override
                protected void updateItem(User item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText("Select Borrower");
                    } else {
                        setText(item.getFullName() + " (" + item.getRole() + ")");
                    }
                }
            });

            // 2. Equipment selection
            Label equipmentLabel = new Label("Equipment:");
            ComboBox<Equipment> equipmentComboBox = new ComboBox<>();
            // Only show available equipment
            List<Equipment> availableEquipment = equipmentService.getAllEquipment().stream()
                    .filter(Equipment::isAvailable)
                    .collect(Collectors.toList());
            equipmentComboBox.setItems(FXCollections.observableArrayList(availableEquipment));
            equipmentComboBox.setCellFactory(param -> new ListCell<Equipment>() {
                @Override
                protected void updateItem(Equipment item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getName() + " (" + item.getCategory() + ")");
                    }
                }
            });
            equipmentComboBox.setButtonCell(new ListCell<Equipment>() {
                @Override
                protected void updateItem(Equipment item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText("Select Equipment");
                    } else {
                        setText(item.getName() + " (" + item.getCategory() + ")");
                    }
                }
            });

            // 3. Course selection (optional - required only for students)
            Label courseLabel = new Label("Course (required for students):");
            ComboBox<Course> courseComboBox = new ComboBox<>();
            List<Course> allCourses = courseService.getAllCourses();
            courseComboBox.setItems(FXCollections.observableArrayList(allCourses));
            courseComboBox.setCellFactory(param -> new ListCell<Course>() {
                @Override
                protected void updateItem(Course item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getCourseCode() + ": " + item.getCourseName());
                    }
                }
            });
            courseComboBox.setButtonCell(new ListCell<Course>() {
                @Override
                protected void updateItem(Course item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText("Select Course (optional for staff)");
                    } else {
                        setText(item.getCourseCode() + ": " + item.getCourseName());
                    }
                }
            });

            // Enable/disable course selection based on borrower type
            borrowerComboBox.setOnAction(e -> {
                User selectedUser = borrowerComboBox.getValue();
                if (selectedUser instanceof Student) {
                    courseComboBox.setDisable(false);
                    courseLabel.setText("Course (required for students):");
                } else {
                    courseComboBox.setDisable(true);
                    courseComboBox.setValue(null);
                    courseLabel.setText("Course (not applicable for staff):");
                }
            });

            // 4. Dates
            Label borrowDateLabel = new Label("Borrow Date:");
            DatePicker borrowDatePicker = new DatePicker(LocalDate.now());

            Label borrowTimeLabel = new Label("Borrow Time:");
            ComboBox<String> borrowTimeComboBox = new ComboBox<>();
            borrowTimeComboBox.setEditable(true);
            borrowTimeComboBox.setItems(FXCollections.observableArrayList(
                    "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00"));
            borrowTimeComboBox.setValue("09:00");

            Label dueDateLabel = new Label("Due Date:");
            DatePicker dueDatePicker = new DatePicker(LocalDate.now().plusDays(7)); // Default 7 days

            Label dueTimeLabel = new Label("Due Time:");
            ComboBox<String> dueTimeComboBox = new ComboBox<>();
            dueTimeComboBox.setEditable(true);
            dueTimeComboBox.setItems(FXCollections.observableArrayList(
                    "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00"));
            dueTimeComboBox.setValue("17:00");

            // 5. Status
            Label statusLabel = new Label("Status:");
            ComboBox<String> statusComboBox = new ComboBox<>();
            statusComboBox.setItems(FXCollections.observableArrayList(
                    "Pending", "Approved", "Borrowed"));
            statusComboBox.setValue("Pending");

            // 6. Other fields
            Label purposeLabel = new Label("Purpose:");
            TextField purposeField = new TextField();
            purposeField.setPromptText("Purpose of borrowing");

            Label conditionLabel = new Label("Initial Condition:");
            TextField conditionField = new TextField();
            conditionField.setPromptText("Current condition of equipment");
            // Fill condition from selected equipment
            equipmentComboBox.setOnAction(e -> {
                Equipment selectedEquipment = equipmentComboBox.getValue();
                if (selectedEquipment != null) {
                    conditionField.setText(selectedEquipment.getCondition());
                }
            });

            Label notesLabel = new Label("Notes:");
            TextArea notesArea = new TextArea();
            notesArea.setPromptText("Additional notes");
            notesArea.setPrefRowCount(3);

            // Layout the form
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new javafx.geometry.Insets(20, 10, 10, 10));

            int row = 0;
            grid.add(borrowerLabel, 0, row);
            grid.add(borrowerComboBox, 1, row);

            row++;
            grid.add(equipmentLabel, 0, row);
            grid.add(equipmentComboBox, 1, row);

            row++;
            grid.add(courseLabel, 0, row);
            grid.add(courseComboBox, 1, row);

            row++;
            HBox borrowDateTimeBox = new HBox(10);
            borrowDateTimeBox.getChildren().addAll(borrowDatePicker, borrowTimeLabel, borrowTimeComboBox);
            grid.add(borrowDateLabel, 0, row);
            grid.add(borrowDateTimeBox, 1, row);

            row++;
            HBox dueDateTimeBox = new HBox(10);
            dueDateTimeBox.getChildren().addAll(dueDatePicker, dueTimeLabel, dueTimeComboBox);
            grid.add(dueDateLabel, 0, row);
            grid.add(dueDateTimeBox, 1, row);

            row++;
            grid.add(statusLabel, 0, row);
            grid.add(statusComboBox, 1, row);

            row++;
            grid.add(purposeLabel, 0, row);
            grid.add(purposeField, 1, row);

            row++;
            grid.add(conditionLabel, 0, row);
            grid.add(conditionField, 1, row);

            row++;
            grid.add(notesLabel, 0, row);
            grid.add(notesArea, 1, row);

            // Set minimum width for fields
            borrowerComboBox.setMinWidth(300);
            equipmentComboBox.setMinWidth(300);
            courseComboBox.setMinWidth(300);
            purposeField.setMinWidth(300);
            conditionField.setMinWidth(300);
            notesArea.setMinWidth(300);

            // Create a scroll pane for the form
            ScrollPane scrollPane = new ScrollPane(grid);
            scrollPane.setFitToWidth(true);
            scrollPane.setPrefHeight(500);
            dialog.getDialogPane().setContent(scrollPane);

            // Convert the result
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == createButtonType) {
                    try {
                        // Validate required fields
                        if (borrowerComboBox.getValue() == null) {
                            showAlert("Error", "Please select a borrower", Alert.AlertType.ERROR);
                            return null;
                        }

                        if (equipmentComboBox.getValue() == null) {
                            showAlert("Error", "Please select equipment", Alert.AlertType.ERROR);
                            return null;
                        }

                        // Validate course for student
                        if (borrowerComboBox.getValue() instanceof Student && courseComboBox.getValue() == null) {
                            showAlert("Error", "Please select a course for student borrower", Alert.AlertType.ERROR);
                            return null;
                        }

                        if (borrowDatePicker.getValue() == null) {
                            showAlert("Error", "Please select borrow date", Alert.AlertType.ERROR);
                            return null;
                        }

                        if (dueDatePicker.getValue() == null) {
                            showAlert("Error", "Please select due date", Alert.AlertType.ERROR);
                            return null;
                        }

                        if (purposeField.getText().isEmpty()) {
                            showAlert("Error", "Please enter a purpose", Alert.AlertType.ERROR);
                            return null;
                        }

                        // Check if due date is after borrow date
                        if (dueDatePicker.getValue().isBefore(borrowDatePicker.getValue())) {
                            showAlert("Error", "Due date must be after borrow date", Alert.AlertType.ERROR);
                            return null;
                        }

                        // Create LendingRecord object
                        LendingRecord lendingRecord = new LendingRecord();
                        lendingRecord.setBorrower(borrowerComboBox.getValue());
                        lendingRecord.setEquipment(equipmentComboBox.getValue());
                        lendingRecord.setCourse(courseComboBox.getValue()); // May be null for staff

                        // Parse dates and times
                        LocalTime borrowTime = LocalTime.parse(borrowTimeComboBox.getValue(), DateTimeFormatter.ofPattern("HH:mm"));
                        LocalTime dueTime = LocalTime.parse(dueTimeComboBox.getValue(), DateTimeFormatter.ofPattern("HH:mm"));

                        lendingRecord.setBorrowDate(LocalDateTime.of(borrowDatePicker.getValue(), borrowTime));
                        lendingRecord.setDueDate(LocalDateTime.of(dueDatePicker.getValue(), dueTime));

                        lendingRecord.setStatus(statusComboBox.getValue());
                        lendingRecord.setPurpose(purposeField.getText());
                        lendingRecord.setCondition(conditionField.getText());
                        lendingRecord.setNotes(notesArea.getText());

                        // For pre-approved lending (when status is Approved), set approver to current admin
                        if ("Approved".equals(statusComboBox.getValue())) {
                            lendingRecord.setApprover(currentAdmin);
                        }

                        return lendingRecord;
                    } catch (Exception e) {
                        e.printStackTrace();
                        showAlert("Error", "Error creating lending record: " + e.getMessage(), Alert.AlertType.ERROR);
                        return null;
                    }
                }
                return null;
            });

            // Show dialog and process result
            Optional<LendingRecord> result = dialog.showAndWait();
            result.ifPresent(lendingRecord -> {
                boolean success = lendingService.createLendingRecord(lendingRecord);
                if (success) {
                    // Mark equipment as unavailable if status is Borrowed
                    if ("Borrowed".equals(lendingRecord.getStatus())) {
                        Equipment equipment = lendingRecord.getEquipment();
                        equipment.setAvailable(false);
                        equipmentService.updateEquipment(equipment);
                    }

                    showAlert("Success", "Lending record created successfully", Alert.AlertType.INFORMATION);
                    loadLendingRecords(); // Refresh the table
                } else {
                    showAlert("Error", "Failed to create lending record", Alert.AlertType.ERROR);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while creating the dialog: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Handle refresh statistics button click
     *
     * @param event The action event
     */
    @FXML
    public void handleRefreshStats(ActionEvent event) {
        // Reload statistics with the selected date range
        loadStatistics();
    }

    /**
     * Handle logout button click
     *
     * @param event The action event
     */
    @FXML
    public void handleLogout(ActionEvent event) {
        // Log out the user
        authenticationService.logout();

        try {
            // Navigate back to login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage) logoutButton.getScene().getWindow();

            // Set up the scene
            Scene scene = new Scene(root, 800, 600);

            // Set up the stage
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Error loading login view: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Load users
     */
    private void loadUsers() {
        // Get the search term from the search field
        String searchTerm = searchUserField.getText().trim();

        // Get all users
        List<User> users = userService.getAllUsers();

        // Filter by user type if needed
        String userType = userTypeComboBox.getValue();
        if (userType != null && !userType.equals("All")) {
            users = users.stream()
                    .filter(user -> user.getRole().equals(userType))
                    .collect(Collectors.toList());
        }

        // Filter by search term (case-insensitive search in name, email, ID fields)
        if (!searchTerm.isEmpty()) {
            final String searchTermLower = searchTerm.toLowerCase();
            users = users.stream()
                    .filter(user -> 
                            user.getFullName().toLowerCase().contains(searchTermLower) || 
                            String.valueOf(user.getId()).contains(searchTermLower))
                    .collect(Collectors.toList());
        }

        // Update the auto-complete suggestions with any new search results
        if (users != null && !users.isEmpty()) {
            // Create a temporary list of all user names from search results
            List<String> userNames = users.stream()
                .map(User::getFullName)
                .distinct()
                .collect(Collectors.toList());

            // Find new names that aren't in our suggestions yet
            List<String> newSuggestions = userNames.stream()
                .filter(name -> !searchUserSuggestions.contains(name))
                .collect(Collectors.toList());

            if (!newSuggestions.isEmpty()) {
                // Add new names to suggestions
                searchUserSuggestions.addAll(newSuggestions);
            }
        }

        // Update the table view
        usersTable.setItems(FXCollections.observableArrayList(users));
    }

    /**
     * Handle edit user
     *
     * @param user The user to edit
     */
    private void handleEditUser(User user) {
        // Create a dialog for editing user
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Edit User");
        dialog.setHeaderText("Edit user information for " + user.getFullName());

        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create the form fields
        TextField firstNameField = new TextField(user.getFirstName());
        TextField lastNameField = new TextField(user.getLastName());
        TextField emailField = new TextField(user.getEmail());
        TextField phoneField = new TextField(user.getPhoneNumber());
        DatePicker dobPicker = new DatePicker(user.getDateOfBirth());
        TextField addressField = new TextField(user.getAddress());

        // Layout the dialog
        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        grid.add(new Label("First Name:"), 0, 0);
        grid.add(firstNameField, 1, 0);
        grid.add(new Label("Last Name:"), 0, 1);
        grid.add(lastNameField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(new Label("Phone:"), 0, 3);
        grid.add(phoneField, 1, 3);
        grid.add(new Label("Date of Birth:"), 0, 4);
        grid.add(dobPicker, 1, 4);
        grid.add(new Label("Address:"), 0, 5);
        grid.add(addressField, 1, 5);

        dialog.getDialogPane().setContent(grid);

        // Convert the result to a user when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                user.setFirstName(firstNameField.getText());
                user.setLastName(lastNameField.getText());
                user.setEmail(emailField.getText());
                user.setPhoneNumber(phoneField.getText());
                user.setDateOfBirth(dobPicker.getValue());
                user.setAddress(addressField.getText());
                return user;
            }
            return null;
        });

        Optional<User> result = dialog.showAndWait();

        result.ifPresent(updatedUser -> {
            boolean success = userService.updateUser(updatedUser);
            if (success) {
                showAlert("Success", "User updated successfully", Alert.AlertType.INFORMATION);
                loadUsers(); // Refresh the table
            } else {
                showAlert("Error", "Failed to update user", Alert.AlertType.ERROR);
            }
        });
    }

    /**
     * Handle delete user
     *
     * @param user The user to delete
     */
    private void handleDeleteUser(User user) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete User");
        confirmAlert.setContentText("Are you sure you want to delete user " + user.getFullName() + "?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = userService.deleteUser(user.getId());
            if (success) {
                showAlert("Success", "User deleted successfully", Alert.AlertType.INFORMATION);
                loadUsers(); // Refresh the table
            } else {
                showAlert("Error", "Failed to delete user", Alert.AlertType.ERROR);
            }
        }
    }

    /**
     * Load courses
     */
    private void loadCourses() {
        List<Course> courses = courseService.getAllCourses();
        String searchTerm = searchCourseField.getText().trim();

        // Filter by search term if provided
        if (searchTerm != null && !searchTerm.isEmpty()) {
            String searchTermLower = searchTerm.toLowerCase();
            courses = courses.stream()
                    .filter(course -> 
                            course.getCourseCode().toLowerCase().contains(searchTermLower) ||
                            course.getCourseName().toLowerCase().contains(searchTermLower))
                    .collect(Collectors.toList());
        }

        // Update the auto-complete suggestions with any new search results
        if (courses != null && !courses.isEmpty()) {

            // Create a temporary list of all course codes and course names from search results
            List<String> courseCodes = courses.stream()
                .map(Course::getCourseCode)
                .distinct()
                .collect(Collectors.toList());

            List<String> courseNames = courses.stream()
                .map(Course::getCourseName)
                .distinct()
                .collect(Collectors.toList());

            // Find new codes that aren't in our suggestions yet
            List<String> newCodeSuggestions = courseCodes.stream()
                .filter(code -> !searchCourseSuggestions.contains(code))
                .collect(Collectors.toList());

            // Find new names that aren't in our suggestions yet
            List<String> newNameSuggestions = courseNames.stream()
                .filter(name -> !searchCourseSuggestions.contains(name))
                .collect(Collectors.toList());

            // Combine all new suggestions
            List<String> allNewSuggestions = new ArrayList<>();
            allNewSuggestions.addAll(newCodeSuggestions);
            allNewSuggestions.addAll(newNameSuggestions);

            if (!allNewSuggestions.isEmpty()) {
                // Add new suggestions to the list
                searchCourseSuggestions.addAll(allNewSuggestions);
            }
        }

        // Update the courses table
        coursesTable.setItems(FXCollections.observableArrayList(courses));

        // Force the table to refresh all cells so that edit and delete buttons are displayed correctly
        coursesTable.refresh();
    }

    /**
     * Handle edit course
     *
     * @param course The course to edit
     */
    private void handleEditCourse(Course course) {
        // Create a dialog for editing course
        Dialog<Course> dialog = new Dialog<>();
        dialog.setTitle("Edit Course");
        dialog.setHeaderText("Edit course information for " + course.getCourseCode());

        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create the form fields
        TextField courseCodeField = new TextField(course.getCourseCode());
        TextField courseNameField = new TextField(course.getCourseName());
        TextField descriptionField = new TextField(course.getDescription());
        TextField semesterField = new TextField(String.valueOf(course.getSemester()));
        TextField yearField = new TextField(String.valueOf(course.getYear()));

        // Get all academic staff for instructor selection
        List<AcademicStaff> academicStaffList = userService.getAllAcademicStaff();
        ComboBox<AcademicStaff> instructorComboBox = new ComboBox<>();
        instructorComboBox.setItems(FXCollections.observableArrayList(academicStaffList));
        instructorComboBox.setCellFactory(param -> new ListCell<AcademicStaff>() {
            @Override
            protected void updateItem(AcademicStaff item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getFullName());
                }
            }
        });
        instructorComboBox.setButtonCell(new ListCell<AcademicStaff>() {
            @Override
            protected void updateItem(AcademicStaff item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getFullName());
                }
            }
        });

        // Set the current instructor if available
        if (course.getInstructor() != null) {
            for (AcademicStaff staff : academicStaffList) {
                if (staff.getId() == course.getInstructor().getId()) {
                    instructorComboBox.getSelectionModel().select(staff);
                    break;
                }
            }
        }

        // Layout the dialog
        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        grid.add(new Label("Course Code:"), 0, 0);
        grid.add(courseCodeField, 1, 0);
        grid.add(new Label("Course Name:"), 0, 1);
        grid.add(courseNameField, 1, 1);
        grid.add(new Label("Description:"), 0, 2);
        grid.add(descriptionField, 1, 2);
        grid.add(new Label("Semester:"), 0, 3);
        grid.add(semesterField, 1, 3);
        grid.add(new Label("Year:"), 0, 4);
        grid.add(yearField, 1, 4);
        grid.add(new Label("Instructor:"), 0, 5);
        grid.add(instructorComboBox, 1, 5);

        dialog.getDialogPane().setContent(grid);

        // Convert the result to a course when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    course.setCourseCode(courseCodeField.getText());
                    course.setCourseName(courseNameField.getText());
                    course.setDescription(descriptionField.getText());
                    course.setSemester(Integer.parseInt(semesterField.getText()));
                    course.setYear(Integer.parseInt(yearField.getText()));
                    course.setInstructor(instructorComboBox.getValue());
                    return course;
                } catch (NumberFormatException e) {
                    showAlert("Error", "Semester and year must be numbers", Alert.AlertType.ERROR);
                    return null;
                }
            }
            return null;
        });

        Optional<Course> result = dialog.showAndWait();

        result.ifPresent(updatedCourse -> {
            boolean success = courseService.updateCourse(updatedCourse);
            if (success) {
                showAlert("Success", "Course updated successfully", Alert.AlertType.INFORMATION);
                loadCourses(); // Refresh the table
            } else {
                showAlert("Error", "Failed to update course", Alert.AlertType.ERROR);
            }
        });
    }

    /**
     * Handle delete course
     *
     * @param course The course to delete
     */
    private void handleDeleteCourse(Course course) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete Course");
        confirmAlert.setContentText("Are you sure you want to delete course " + course.getCourseCode() + "?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = courseService.deleteCourse(course.getId());
            if (success) {
                showAlert("Success", "Course deleted successfully", Alert.AlertType.INFORMATION);
                loadCourses(); // Refresh the table
            } else {
                showAlert("Error", "Failed to delete course", Alert.AlertType.ERROR);
            }
        }
    }

    /**
     * Load equipment
     */
    private void loadEquipment() {
        final String searchTerm = searchEquipmentField.getText().trim();
        final String category = categoryComboBox.getValue();

        // Create a task to load equipment in a background thread
        Task<List<Equipment>> loadTask = new Task<List<Equipment>>() {
            @Override
            protected List<Equipment> call() throws Exception {
                // Get all equipment from the service
                List<Equipment> equipmentList = equipmentService.getAllEquipment();

                // Filter by search term if provided
                if (searchTerm != null && !searchTerm.isEmpty()) {
                    String searchTermLower = searchTerm.toLowerCase();
                    equipmentList = equipmentList.stream()
                            .filter(equipment -> 
                                    equipment.getName().toLowerCase().contains(searchTermLower))
                            .collect(Collectors.toList());
                }

                // Filter by category if needed
                if (category != null && !category.equals("All")) {
                    equipmentList = equipmentList.stream()
                            .filter(equipment -> equipment.getCategory().equals(category))
                            .collect(Collectors.toList());
                }

                return equipmentList;
            }
        };

        // Handle the task completion
        loadTask.setOnSucceeded(e -> {
            List<Equipment> equipmentList = loadTask.getValue();

            // Update the auto-complete suggestions with any new search results
            if (equipmentList != null && !equipmentList.isEmpty()) {
                // Create a temporary list of all equipment names from search results
                List<String> equipmentNames = equipmentList.stream()
                    .map(Equipment::getName)
                    .distinct()
                    .collect(Collectors.toList());

                // Find new names that aren't in our suggestions yet
                List<String> newSuggestions = equipmentNames.stream()
                    .filter(name -> !searchEquipmentSuggestions.contains(name))
                    .collect(Collectors.toList());

                if (!newSuggestions.isEmpty()) {
                    // Add new names to suggestions
                    searchEquipmentSuggestions.addAll(newSuggestions);
                }
            }

            // Update the UI on the JavaFX Application Thread
            javafx.application.Platform.runLater(() -> {
                // Update the equipment table with search results
                equipmentTable.setItems(FXCollections.observableArrayList(equipmentList));

                // Force the table to refresh all cells so that edit and delete buttons are displayed correctly
                equipmentTable.refresh();
            });
        });

        loadTask.setOnFailed(e -> {
            Throwable exception = loadTask.getException();
            javafx.application.Platform.runLater(() -> {
                showAlert("Error", "Failed to load equipment: " + exception.getMessage(), Alert.AlertType.ERROR);
            });
        });

        // Start the task in a new thread
        new Thread(loadTask).start();
    }

    /**
     * Handle manage equipment images
     *
     * @param equipment The equipment to manage images for
     */
    private void handleManageEquipmentImages(Equipment equipment) {
        // Create a dialog for managing equipment images
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Manage Images");
        dialog.setHeaderText("Manage images for " + equipment.getName());

        // Set the button types
        ButtonType addButtonType = new ButtonType("Add Image(s)", ButtonBar.ButtonData.LEFT);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CLOSE);

        // Create main container
        VBox mainContainer = new VBox(10);

        // Create title label
        Label titleLabel = new Label("Current Images");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        mainContainer.getChildren().add(titleLabel);

        // Create a list view for images
        ListView<HBox> imagesListView = new ListView<>();
        imagesListView.setPrefWidth(400);
        imagesListView.setPrefHeight(300);
        mainContainer.getChildren().add(imagesListView);

        // Add a loading indicator
        ProgressIndicator loadingIndicator = new ProgressIndicator();
        loadingIndicator.setMaxSize(50, 50);
        mainContainer.getChildren().add(loadingIndicator);

        // Set the content before loading images
        dialog.getDialogPane().setContent(mainContainer);

        // Create a task to load images in a background thread
        Task<Map<Integer, byte[]>> loadImagesTask = new Task<Map<Integer, byte[]>>() {
            @Override
            protected Map<Integer, byte[]> call() throws Exception {
                return equipmentService.getEquipmentImagesWithIds(equipment.getId());
            }
        };

        // Handle the task completion
        loadImagesTask.setOnSucceeded(e -> {
            // Get the loaded images
            Map<Integer, byte[]> imagesWithIds = loadImagesTask.getValue();

            // Update the UI on the JavaFX Application Thread
            javafx.application.Platform.runLater(() -> {
                // Remove the loading indicator
                mainContainer.getChildren().remove(loadingIndicator);

                // Add images to list view
                for (Map.Entry<Integer, byte[]> entry : imagesWithIds.entrySet()) {
                    int imageId = entry.getKey();
                    byte[] imageData = entry.getValue();

                    // Create image view
                    javafx.scene.image.ImageView imageView = new javafx.scene.image.ImageView();
                    imageView.setFitHeight(100);
                    imageView.setFitWidth(100);
                    imageView.setPreserveRatio(true);

                    // Convert byte array to image
                    javafx.scene.image.Image image = new javafx.scene.image.Image(new java.io.ByteArrayInputStream(imageData));
                    imageView.setImage(image);

                    // Create delete button
                    Button deleteButton = new Button("Delete");
                    deleteButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");

                    deleteButton.setOnAction(event -> {
                        // Create a task to delete the image in a background thread
                        Task<Boolean> deleteTask = new Task<Boolean>() {
                            @Override
                            protected Boolean call() throws Exception {
                                return equipmentService.deleteEquipmentImage(imageId);
                            }
                        };

                        // Handle the task completion
                        deleteTask.setOnSucceeded(evt -> {
                            boolean success = deleteTask.getValue();
                            if (success) {
                                showAlert("Success", "Image deleted successfully", Alert.AlertType.INFORMATION);
                                handleManageEquipmentImages(equipment); // Refresh the dialog
                                dialog.close();
                            } else {
                                showAlert("Error", "Failed to delete image", Alert.AlertType.ERROR);
                            }
                        });

                        deleteTask.setOnFailed(evt -> {
                            Throwable exception = deleteTask.getException();
                            showAlert("Error", "Failed to delete image: " + exception.getMessage(), Alert.AlertType.ERROR);
                        });

                        // Start the task in a new thread
                        new Thread(deleteTask).start();
                    });

                    // Create HBox for image and button
                    HBox imageBox = new HBox(10);
                    imageBox.getChildren().addAll(imageView, deleteButton);

                    imagesListView.getItems().add(imageBox);
                }
            });
        });

        loadImagesTask.setOnFailed(e -> {
            Throwable exception = loadImagesTask.getException();
            javafx.application.Platform.runLater(() -> {
                // Remove the loading indicator
                mainContainer.getChildren().remove(loadingIndicator);
                // Show error message
                showAlert("Error", "Failed to load images: " + exception.getMessage(), Alert.AlertType.ERROR);
            });
        });

        // Start the task in a new thread
        new Thread(loadImagesTask).start();

        // Handle add image button
        Button addButton = (Button) dialog.getDialogPane().lookupButton(addButtonType);
        addButton.addEventFilter(ActionEvent.ACTION, event -> {
            event.consume(); // Prevent dialog from closing

            // Create file chooser with multi-select enabled
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Select Image(s)");
            fileChooser.getExtensionFilters().addAll(
                    new javafx.stage.FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );

            // Show file chooser with multiple selection
            List<java.io.File> files = fileChooser.showOpenMultipleDialog(dialog.getOwner());
            if (files != null && !files.isEmpty()) {
                // Show progress indicator if there are multiple files
                if (files.size() > 1) {
                    // Create a progress dialog
                    Dialog<Void> progressDialog = new Dialog<>();
                    progressDialog.setTitle("Uploading Images");
                    progressDialog.setHeaderText("Uploading " + files.size() + " images...");

                    ProgressBar progressBar = new ProgressBar();
                    progressBar.setPrefWidth(300);
                    progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);

                    VBox progressContent = new VBox(10);
                    progressContent.getChildren().add(progressBar);
                    Label statusLabel = new Label("Processing images...");
                    progressContent.getChildren().add(statusLabel);

                    progressDialog.getDialogPane().setContent(progressContent);
                    progressDialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

                    // Start a background task to upload images
                    Task<Integer> uploadTask = new Task<Integer>() {
                        @Override
                        protected Integer call() {
                            return equipmentService.addMultipleEquipmentImages(equipment.getId(), files);
                        }
                    };

                    uploadTask.setOnSucceeded(e -> {
                        progressDialog.close();
                        int successCount = uploadTask.getValue();
                        showAlert("Success", successCount + " of " + files.size() + " images uploaded successfully", Alert.AlertType.INFORMATION);
                        handleManageEquipmentImages(equipment); // Refresh the dialog
                        dialog.close();
                    });

                    Thread uploadThread = new Thread(uploadTask);
                    uploadThread.setDaemon(true);
                    uploadThread.start();

                    // Show progress dialog
                    progressDialog.showAndWait();
                } else {
                    // Single file upload - use a background thread
                    java.io.File file = files.get(0);

                    // Create a progress dialog for single file upload
                    Dialog<Void> progressDialog = new Dialog<>();
                    progressDialog.setTitle("Uploading Image");
                    progressDialog.setHeaderText("Uploading image...");

                    ProgressBar progressBar = new ProgressBar();
                    progressBar.setPrefWidth(300);
                    progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);

                    VBox progressContent = new VBox(10);
                    progressContent.getChildren().add(progressBar);
                    Label statusLabel = new Label("Processing image...");
                    progressContent.getChildren().add(statusLabel);

                    progressDialog.getDialogPane().setContent(progressContent);
                    progressDialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

                    // Create a task to upload the image in a background thread
                    Task<Boolean> uploadTask = new Task<Boolean>() {
                        @Override
                        protected Boolean call() throws Exception {
                            return equipmentService.addEquipmentImage(equipment.getId(), file);
                        }
                    };

                    uploadTask.setOnSucceeded(e -> {
                        progressDialog.close();
                        boolean success = uploadTask.getValue();
                        if (success) {
                            showAlert("Success", "Image added successfully", Alert.AlertType.INFORMATION);
                            handleManageEquipmentImages(equipment); // Refresh the dialog
                            dialog.close();
                        } else {
                            showAlert("Error", "Failed to add image", Alert.AlertType.ERROR);
                        }
                    });

                    uploadTask.setOnFailed(e -> {
                        progressDialog.close();
                        Throwable exception = uploadTask.getException();
                        showAlert("Error", "Failed to add image: " + exception.getMessage(), Alert.AlertType.ERROR);
                    });

                    // Start the task in a background thread
                    Thread uploadThread = new Thread(uploadTask);
                    uploadThread.setDaemon(true);
                    uploadThread.start();

                    // Show progress dialog
                    progressDialog.showAndWait();
                }
            }
        });

        dialog.showAndWait();
    }

    /**
     * Handle edit equipment
     *
     * @param equipment The equipment to edit
     */
    private void handleEditEquipment(Equipment equipment) {
        // Create a dialog for editing equipment
        Dialog<Equipment> dialog = new Dialog<>();
        dialog.setTitle("Edit Equipment");
        dialog.setHeaderText("Edit equipment information for " + equipment.getName());

        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create the form fields
        TextField nameField = new TextField(equipment.getName());
        TextField descriptionField = new TextField(equipment.getDescription());
        TextField categoryField = new TextField(equipment.getCategory());
        TextField conditionField = new TextField(equipment.getCondition());
        DatePicker purchaseDatePicker = new DatePicker(equipment.getPurchaseDate());
        TextField purchasePriceField = new TextField(String.valueOf(equipment.getPurchasePrice()));
        TextField manufacturerField = new TextField(equipment.getManufacturer());
        TextField modelField = new TextField(equipment.getModel());
        TextField serialNumberField = new TextField(equipment.getSerialNumber());
        TextField locationField = new TextField(equipment.getLocation());
        CheckBox availableCheckBox = new CheckBox();
        availableCheckBox.setSelected(equipment.isAvailable());

        // Layout the dialog
        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionField, 1, 1);
        grid.add(new Label("Category:"), 0, 2);
        grid.add(categoryField, 1, 2);
        grid.add(new Label("Condition:"), 0, 3);
        grid.add(conditionField, 1, 3);
        grid.add(new Label("Purchase Date:"), 0, 4);
        grid.add(purchaseDatePicker, 1, 4);
        grid.add(new Label("Purchase Price:"), 0, 5);
        grid.add(purchasePriceField, 1, 5);
        grid.add(new Label("Manufacturer:"), 0, 6);
        grid.add(manufacturerField, 1, 6);
        grid.add(new Label("Model:"), 0, 7);
        grid.add(modelField, 1, 7);
        grid.add(new Label("Serial Number:"), 0, 8);
        grid.add(serialNumberField, 1, 8);
        grid.add(new Label("Location:"), 0, 9);
        grid.add(locationField, 1, 9);
        grid.add(new Label("Available:"), 0, 10);
        grid.add(availableCheckBox, 1, 10);

        dialog.getDialogPane().setContent(grid);

        // Convert the result to an equipment when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    equipment.setName(nameField.getText());
                    equipment.setDescription(descriptionField.getText());
                    equipment.setCategory(categoryField.getText());
                    equipment.setCondition(conditionField.getText());
                    equipment.setPurchaseDate(purchaseDatePicker.getValue());
                    equipment.setPurchasePrice(Double.parseDouble(purchasePriceField.getText()));
                    equipment.setManufacturer(manufacturerField.getText());
                    equipment.setModel(modelField.getText());
                    equipment.setSerialNumber(serialNumberField.getText());
                    equipment.setLocation(locationField.getText());
                    equipment.setAvailable(availableCheckBox.isSelected());
                    return equipment;
                } catch (NumberFormatException e) {
                    showAlert("Error", "Purchase price must be a number", Alert.AlertType.ERROR);
                    return null;
                }
            }
            return null;
        });

        Optional<Equipment> result = dialog.showAndWait();

        result.ifPresent(updatedEquipment -> {
            // Create a task to update the equipment in a background thread
            Task<Boolean> updateTask = new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    return equipmentService.updateEquipment(updatedEquipment);
                }
            };

            // Handle the task completion
            updateTask.setOnSucceeded(e -> {
                boolean success = updateTask.getValue();
                if (success) {
                    showAlert("Success", "Equipment updated successfully", Alert.AlertType.INFORMATION);
                    loadEquipment(); // Refresh the table
                } else {
                    showAlert("Error", "Failed to update equipment", Alert.AlertType.ERROR);
                }
            });

            updateTask.setOnFailed(e -> {
                Throwable exception = updateTask.getException();
                showAlert("Error", "Failed to update equipment: " + exception.getMessage(), Alert.AlertType.ERROR);
            });

            // Start the task in a new thread
            new Thread(updateTask).start();
        });
    }

    /**
     * Handle delete equipment
     *
     * @param equipment The equipment to delete
     */
    private void handleDeleteEquipment(Equipment equipment) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete Equipment");
        confirmAlert.setContentText("Are you sure you want to delete equipment " + equipment.getName() + "?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Create a task to delete the equipment in a background thread
            Task<Boolean> deleteTask = new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    return equipmentService.deleteEquipment(equipment.getId());
                }
            };

            // Handle the task completion
            deleteTask.setOnSucceeded(e -> {
                boolean success = deleteTask.getValue();
                if (success) {
                    showAlert("Success", "Equipment deleted successfully", Alert.AlertType.INFORMATION);
                    loadEquipment(); // Refresh the table
                } else {
                    showAlert("Error", "Failed to delete equipment", Alert.AlertType.ERROR);
                }
            });

            deleteTask.setOnFailed(e -> {
                Throwable exception = deleteTask.getException();
                showAlert("Error", "Failed to delete equipment: " + exception.getMessage(), Alert.AlertType.ERROR);
            });

            // Start the task in a new thread
            new Thread(deleteTask).start();
        }
    }

    /**
     * Load lending records
     */
    private void loadLendingRecords() {
        // Create a task to load lending records in a background thread
        Task<List<LendingRecord>> loadTask = new Task<List<LendingRecord>>() {
            @Override
            protected List<LendingRecord> call() throws Exception {
                // Get all lending records from the service
                List<LendingRecord> records = lendingService.getAllLendingRecords();

                // Filter by status if needed
                String status = lendingStatusComboBox.getValue();
                if (status != null && !status.equals("All")) {
                    records = records.stream()
                            .filter(record -> record.getStatus().equals(status))
                            .collect(Collectors.toList());
                }

                // Filter by borrower if provided
                String borrowerName = borrowerField.getText();
                if (borrowerName != null && !borrowerName.isEmpty()) {
                    String borrowerNameLower = borrowerName.toLowerCase();
                    records = records.stream()
                            .filter(record -> record.getBorrower() != null && 
                                    record.getBorrower().getFullName().toLowerCase().contains(borrowerNameLower))
                            .collect(Collectors.toList());
                }

                return records;
            }
        };

        // Handle the task completion
        loadTask.setOnSucceeded(e -> {
            List<LendingRecord> lendingRecords = loadTask.getValue();

            // Update the auto-complete suggestions with any new search results
            if (lendingRecords != null && !lendingRecords.isEmpty()) {
                // Create a temporary list of all borrower names from search results
                List<String> borrowerNames = lendingRecords.stream()
                    .map(record -> record.getBorrower() != null ? record.getBorrower().getFullName() : "")
                    .filter(name -> !name.isEmpty())
                    .distinct()
                    .collect(Collectors.toList());

                // Find new names that aren't in our suggestions yet
                List<String> newSuggestions = borrowerNames.stream()
                    .filter(name -> !searchBorrowerSuggestions.contains(name))
                    .collect(Collectors.toList());

                if (!newSuggestions.isEmpty()) {
                    // Add new names to suggestions
                    searchBorrowerSuggestions.addAll(newSuggestions);
                }
            }

            // Update the UI on the JavaFX Application Thread
            javafx.application.Platform.runLater(() -> {
                lendingTable.setItems(FXCollections.observableArrayList(lendingRecords));

                // Force the table to refresh all cells so that edit and delete buttons are displayed correctly
                lendingTable.refresh();
            });
        });

        loadTask.setOnFailed(e -> {
            Throwable exception = loadTask.getException();
            javafx.application.Platform.runLater(() -> {
                showAlert("Error", "Failed to load lending records: " + exception.getMessage(), Alert.AlertType.ERROR);
            });
        });

        // Start the task in a new thread
        new Thread(loadTask).start();
    }

    /**
     * Handle view lending record
     *
     * @param record The lending record to view
     */
    private void handleViewLendingRecord(LendingRecord record) {
        // Create a dialog for viewing lending record
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("View Lending Record");
        dialog.setHeaderText("Lending Record Details");

        // Set the button types
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        // Create the form fields (read-only)
        TextField idField = new TextField(String.valueOf(record.getId()));
        idField.setEditable(false);

        TextField borrowerField = new TextField(record.getBorrower() != null ? record.getBorrower().getFullName() : "");
        borrowerField.setEditable(false);

        TextField equipmentField = new TextField(record.getEquipment() != null ? record.getEquipment().getName() : "");
        equipmentField.setEditable(false);

        TextField courseField = new TextField(record.getCourse() != null ? record.getCourse().getCourseCode() : "");
        courseField.setEditable(false);

        TextField borrowDateField = new TextField(record.getBorrowDate().format(dateFormatter));
        borrowDateField.setEditable(false);

        TextField dueDateField = new TextField(record.getDueDate().format(dateFormatter));
        dueDateField.setEditable(false);

        TextField returnDateField = new TextField(record.getReturnDate() != null ? record.getReturnDate().format(dateFormatter) : "");
        returnDateField.setEditable(false);

        TextField statusField = new TextField(record.getStatus());
        statusField.setEditable(false);

        TextField purposeField = new TextField(record.getPurpose());
        purposeField.setEditable(false);

        TextField conditionField = new TextField(record.getCondition());
        conditionField.setEditable(false);

        TextArea notesArea = new TextArea(record.getNotes());
        notesArea.setEditable(false);
        notesArea.setPrefRowCount(3);

        TextField approverField = new TextField(record.getApprover() != null ? record.getApprover().getFullName() : "");
        approverField.setEditable(false);

        // Layout the dialog
        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        grid.add(new Label("ID:"), 0, 0);
        grid.add(idField, 1, 0);
        grid.add(new Label("Borrower:"), 0, 1);
        grid.add(borrowerField, 1, 1);
        grid.add(new Label("Equipment:"), 0, 2);
        grid.add(equipmentField, 1, 2);
        grid.add(new Label("Course:"), 0, 3);
        grid.add(courseField, 1, 3);
        grid.add(new Label("Borrow Date:"), 0, 4);
        grid.add(borrowDateField, 1, 4);
        grid.add(new Label("Due Date:"), 0, 5);
        grid.add(dueDateField, 1, 5);
        grid.add(new Label("Return Date:"), 0, 6);
        grid.add(returnDateField, 1, 6);
        grid.add(new Label("Status:"), 0, 7);
        grid.add(statusField, 1, 7);
        grid.add(new Label("Purpose:"), 0, 8);
        grid.add(purposeField, 1, 8);
        grid.add(new Label("Condition:"), 0, 9);
        grid.add(conditionField, 1, 9);
        grid.add(new Label("Notes:"), 0, 10);
        grid.add(notesArea, 1, 10);
        grid.add(new Label("Approver:"), 0, 11);
        grid.add(approverField, 1, 11);

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait();
    }

    /**
     * Handle edit lending record
     *
     * @param record The lending record to edit
     */
    private void handleEditLendingRecord(LendingRecord record) {
        // Create a dialog for editing lending record
        Dialog<LendingRecord> dialog = new Dialog<>();
        dialog.setTitle("Edit Lending Record");
        dialog.setHeaderText("Edit Lending Record");

        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create the form fields
        ComboBox<String> statusComboBox = new ComboBox<>();
        statusComboBox.setItems(FXCollections.observableArrayList(
                "Pending", "Approved", "Borrowed", "Returned", "Overdue", "Rejected"));
        statusComboBox.setValue(record.getStatus());

        DatePicker dueDatePicker = new DatePicker();
        if (record.getDueDate() != null) {
            dueDatePicker.setValue(record.getDueDate().toLocalDate());
        }

        TextField purposeField = new TextField(record.getPurpose());
        TextField conditionField = new TextField(record.getCondition());
        TextArea notesArea = new TextArea(record.getNotes());
        notesArea.setPrefRowCount(3);

        // Layout the dialog
        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        grid.add(new Label("Status:"), 0, 0);
        grid.add(statusComboBox, 1, 0);
        grid.add(new Label("Due Date:"), 0, 1);
        grid.add(dueDatePicker, 1, 1);
        grid.add(new Label("Purpose:"), 0, 2);
        grid.add(purposeField, 1, 2);
        grid.add(new Label("Condition:"), 0, 3);
        grid.add(conditionField, 1, 3);
        grid.add(new Label("Notes:"), 0, 4);
        grid.add(notesArea, 1, 4);

        dialog.getDialogPane().setContent(grid);

        // Convert the result to a lending record when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                record.setStatus(statusComboBox.getValue());

                // Update due date if provided
                if (dueDatePicker.getValue() != null) {
                    LocalDateTime dueDateTime = dueDatePicker.getValue().atTime(23, 59);
                    record.setDueDate(dueDateTime);
                }

                record.setPurpose(purposeField.getText());
                record.setCondition(conditionField.getText());
                record.setNotes(notesArea.getText());

                return record;
            }
            return null;
        });

        Optional<LendingRecord> result = dialog.showAndWait();

        result.ifPresent(updatedRecord -> {
            // Create a task to update the record in a background thread
            Task<Boolean> updateTask = new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    return lendingService.updateLendingRecord(updatedRecord);
                }
            };

            // Handle the task completion
            updateTask.setOnSucceeded(e -> {
                boolean success = updateTask.getValue();
                if (success) {
                    showAlert("Success", "Lending record updated successfully", Alert.AlertType.INFORMATION);
                    loadLendingRecords(); // Refresh the table
                } else {
                    showAlert("Error", "Failed to update lending record", Alert.AlertType.ERROR);
                }
            });

            updateTask.setOnFailed(e -> {
                Throwable exception = updateTask.getException();
                showAlert("Error", "Failed to update lending record: " + exception.getMessage(), Alert.AlertType.ERROR);
            });

            // Start the task in a new thread
            new Thread(updateTask).start();
        });
    }

    /**
     * Handle delete lending record
     *
     * @param record The lending record to delete
     */
    private void handleDeleteLendingRecord(LendingRecord record) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete Lending Record");
        confirmAlert.setContentText("Are you sure you want to delete this lending record?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Create a task to delete the record in a background thread
            Task<Boolean> deleteTask = new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    return lendingService.deleteLendingRecord(record.getId());
                }
            };

            // Handle the task completion
            deleteTask.setOnSucceeded(e -> {
                boolean success = deleteTask.getValue();
                if (success) {
                    showAlert("Success", "Lending record deleted successfully", Alert.AlertType.INFORMATION);
                    loadLendingRecords(); // Refresh the table
                } else {
                    showAlert("Error", "Failed to delete lending record", Alert.AlertType.ERROR);
                }
            });

            deleteTask.setOnFailed(e -> {
                Throwable exception = deleteTask.getException();
                showAlert("Error", "Failed to delete lending record: " + exception.getMessage(), Alert.AlertType.ERROR);
            });

            // Start the task in a new thread
            new Thread(deleteTask).start();
        }
    }    /**
     * Load statistics
     */
    private void loadStatistics() {
        // Get date range
        LocalDate fromDate = statsFromDatePicker.getValue();
        LocalDate toDate = statsToDatePicker.getValue();

        if (fromDate == null || toDate == null) {
            showAlert("Error", "Please select both from and to dates", Alert.AlertType.ERROR);
            return;
        }

        if (fromDate.isAfter(toDate)) {
            showAlert("Error", "From date cannot be after to date", Alert.AlertType.ERROR);
            return;
        }

        // Convert LocalDate to LocalDateTime (start of day for fromDate, end of day for toDate)
        final LocalDateTime startDateTime = fromDate.atStartOfDay();
        final LocalDateTime endDateTime = toDate.atTime(LocalTime.MAX);

        // Show a loading indicator
        refreshStatsButton.setDisable(true);
        statsProgressIndicator.setVisible(true);
        statusPieChart.setOpacity(0.5);
        equipmentBarChart.setOpacity(0.5);
        
        totalUsersLabel.setText("Loading...");
        totalEquipmentLabel.setText("Loading...");
        totalLendingsLabel.setText("Loading...");
        overdueRateLabel.setText("Loading...");

        // Create a task to load statistics in a background thread
        Task<Map<String, Object>> statsTask = new Task<Map<String, Object>>() {
            @Override
            protected Map<String, Object> call() throws Exception {
                Map<String, Object> result = new HashMap<>();

                updateMessage("Getting statistics...");
                // Use optimized statistics retrieval method
                Map<String, Object> rawStats = lendingService.getOptimizedLendingStatistics(startDateTime, endDateTime);
                LendingService.LendingStatistics stats = lendingService.convertToLendingStatistics(rawStats);
                
                updateMessage("Getting user counts...");
                // Get total users and equipment counts
                int totalUsers = userService.getAllUsers().size();
                int totalEquipment = equipmentService.getAllEquipment().size();

                // Store results in map
                result.put("stats", stats);
                result.put("totalUsers", totalUsers);
                result.put("totalEquipment", totalEquipment);
                
                updateMessage("Processing complete");
                return result;
            }
        };        // Handle the task completion
        statsTask.setOnSucceeded(e -> {
            Map<String, Object> result = statsTask.getValue();
            LendingService.LendingStatistics stats = (LendingService.LendingStatistics) result.get("stats");
            int totalUsers = (int) result.get("totalUsers");
            int totalEquipment = (int) result.get("totalEquipment");

            // Update UI on JavaFX Application Thread
            javafx.application.Platform.runLater(() -> {
                // Restore UI state
                refreshStatsButton.setDisable(false);
                statsProgressIndicator.setVisible(false);
                statusPieChart.setOpacity(1.0);
                equipmentBarChart.setOpacity(1.0);
                
                // Update summary labels
                totalUsersLabel.setText(String.valueOf(totalUsers));
                totalEquipmentLabel.setText(String.valueOf(totalEquipment));
                totalLendingsLabel.setText(String.valueOf(stats.totalLendings));        

                // Calculate overdue rate
                double overdueRate = stats.totalLendings > 0 ? 
                        (double) stats.overdueLendings / stats.totalLendings * 100 : 0;
                overdueRateLabel.setText(String.format("%.1f%%", overdueRate));

                // Update pie chart
                statusPieChart.getData().clear();
                for (Map.Entry<String, Integer> entry : stats.lendingsPerBorrowerType.entrySet()) {
                    statusPieChart.getData().add(new PieChart.Data(entry.getKey(), entry.getValue()));
                }

                // Update bar chart
                equipmentBarChart.getData().clear();
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName("Borrowing Frequency");

                // The equipment data is already sorted from our optimized query
                int count = 0;
                for (Map.Entry<String, Integer> entry : stats.lendingsPerEquipment.entrySet()) {
                    series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
                    count++;
                    if (count >= 10) break; // Limit to top 10
                }

                equipmentBarChart.getData().add(series);
            });
        });        statsTask.setOnFailed(e -> {
            Throwable exception = statsTask.getException();
            javafx.application.Platform.runLater(() -> {
                // Restore UI state
                refreshStatsButton.setDisable(false);
                statsProgressIndicator.setVisible(false);
                statusPieChart.setOpacity(1.0);
                equipmentBarChart.setOpacity(1.0);
                
                // Reset labels
                totalUsersLabel.setText("N/A");
                totalEquipmentLabel.setText("N/A");
                totalLendingsLabel.setText("N/A");
                overdueRateLabel.setText("N/A");
                
                showAlert("Error", "Failed to load statistics: " + exception.getMessage(), Alert.AlertType.ERROR);
            });
        });

        // Start the task in a new thread
        new Thread(statsTask).start();
    }    // This method is no longer used as we're using the optimized implementation

    /**
     * Show an alert dialog
     *
     * @param title   The title of the alert
     * @param message The message to display
     * @param type    The type of alert
     */
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Handle viewing equipment details
     *
     * @param equipment The equipment to view
     */
    private void handleViewEquipment(Equipment equipment) {
        try {
            // Create a dialog
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Equipment Details");
            dialog.setHeaderText("Details for " + equipment.getName());

            // Create a grid pane for the content
            javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

            // Add the equipment details
            grid.add(new Label("ID:"), 0, 0);
            grid.add(new Label(String.valueOf(equipment.getId())), 1, 0);

            grid.add(new Label("Name:"), 0, 1);
            grid.add(new Label(equipment.getName()), 1, 1);

            grid.add(new Label("Description:"), 0, 2);
            grid.add(new Label(equipment.getDescription() != null ? equipment.getDescription() : ""), 1, 2);

            grid.add(new Label("Category:"), 0, 3);
            grid.add(new Label(equipment.getCategory()), 1, 3);

            grid.add(new Label("Condition:"), 0, 4);
            grid.add(new Label(equipment.getCondition()), 1, 4);

            grid.add(new Label("Manufacturer:"), 0, 5);
            grid.add(new Label(equipment.getManufacturer() != null ? equipment.getManufacturer() : ""), 1, 5);

            grid.add(new Label("Model:"), 0, 6);
            grid.add(new Label(equipment.getModel() != null ? equipment.getModel() : ""), 1, 6);

            grid.add(new Label("Serial Number:"), 0, 7);
            grid.add(new Label(equipment.getSerialNumber() != null ? equipment.getSerialNumber() : ""), 1, 7);

            grid.add(new Label("Location:"), 0, 8);
            grid.add(new Label(equipment.getLocation()), 1, 8);

            grid.add(new Label("Available:"), 0, 9);
            grid.add(new Label(equipment.isAvailable() ? "Yes" : "No"), 1, 9);

            grid.add(new Label("Purchase Date:"), 0, 10);
            grid.add(new Label(equipment.getPurchaseDate() != null ? equipment.getPurchaseDate().toString() : ""), 1, 10);

            grid.add(new Label("Purchase Price:"), 0, 11);
            grid.add(new Label(String.format("$%.2f", equipment.getPurchasePrice())), 1, 11);

            // Add a loading indicator for images
            ProgressIndicator loadingIndicator = new ProgressIndicator();
            loadingIndicator.setMaxSize(50, 50);
            grid.add(new Label("Images:"), 0, 12);
            grid.add(loadingIndicator, 1, 12);

            // Create a horizontal box for images (will be populated later)
            javafx.scene.layout.HBox imagesBox = new javafx.scene.layout.HBox(10);
            imagesBox.setPadding(new javafx.geometry.Insets(10));

            // Create a task to load images in a background thread
            Task<List<byte[]>> loadImagesTask = new Task<List<byte[]>>() {
                @Override
                protected List<byte[]> call() throws Exception {
                    return equipmentService.getEquipmentImages(equipment.getId());
                }
            };

            // Handle the task completion
            loadImagesTask.setOnSucceeded(e -> {
                List<byte[]> images = loadImagesTask.getValue();

                // Update the UI on the JavaFX Application Thread
                javafx.application.Platform.runLater(() -> {
                    // Remove the loading indicator
                    grid.getChildren().remove(loadingIndicator);

                    if (!images.isEmpty()) {
                        // Add each image to the box
                        for (byte[] imageData : images) {
                            javafx.scene.image.ImageView imageView = new javafx.scene.image.ImageView();
                            imageView.setFitHeight(100);
                            imageView.setFitWidth(100);
                            imageView.setPreserveRatio(true);

                            // Convert byte array to image
                            javafx.scene.image.Image image = new javafx.scene.image.Image(new java.io.ByteArrayInputStream(imageData));
                            imageView.setImage(image);

                            imagesBox.getChildren().add(imageView);
                        }

                        // Add the images box to the grid
                        grid.add(imagesBox, 1, 12);
                    } else {
                        // No images available
                        grid.add(new Label("No images available"), 1, 12);
                    }
                });
            });

            loadImagesTask.setOnFailed(e -> {
                Throwable exception = loadImagesTask.getException();
                javafx.application.Platform.runLater(() -> {
                    // Remove the loading indicator
                    grid.getChildren().remove(loadingIndicator);
                    // Show error message
                    grid.add(new Label("Failed to load images: " + exception.getMessage()), 1, 12);
                });
            });

            // Start the task in a new thread
            new Thread(loadImagesTask).start();

            // Add scrolling support for the dialog content
            ScrollPane scrollPane = new ScrollPane(grid);
            scrollPane.setFitToWidth(true);
            scrollPane.setPrefHeight(500);
            dialog.getDialogPane().setContent(scrollPane);

            // Add a close button
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

            // Show the dialog
            dialog.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Error showing equipment details: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Set up auto-complete functionality for the user search field
     */
    private void setupUserSearchAutoComplete() {
        try {
            // Create auto-complete popup with an empty list initially
            ListView<String> suggestionList = new ListView<>();
            javafx.stage.Popup popup = new javafx.stage.Popup();
            popup.getContent().add(suggestionList);

            // Set up listener for text changes
            searchUserField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == null || newValue.isEmpty()) {
                    popup.hide();
                    return;
                }

                // Filter suggestions based on input
                String lowerCaseInput = newValue.toLowerCase();
                ObservableList<String> filteredList = FXCollections.observableArrayList(
                        searchUserSuggestions.stream()
                                .filter(name -> name.toLowerCase().contains(lowerCaseInput))
                                .collect(Collectors.toList())
                );

                // Update list with filtered items
                suggestionList.setItems(filteredList);

                // Select the first item by default
                if (!filteredList.isEmpty()) {
                    suggestionList.getSelectionModel().select(0);
                }

                // Show popup if there are suggestions
                if (!filteredList.isEmpty() && searchUserField.isFocused()) {
                    javafx.geometry.Bounds bounds = searchUserField.localToScreen(searchUserField.getBoundsInLocal());
                    if (bounds != null) {
                        popup.show(searchUserField, bounds.getMinX(), bounds.getMaxY());
                        suggestionList.setPrefWidth(searchUserField.getWidth());
                        suggestionList.setPrefHeight(Math.min(filteredList.size() * 24, 200));
                    }
                } else {
                    popup.hide();
                }
            });

            // Handle selection from suggestion list
            suggestionList.setOnMouseClicked(event -> {
                String selectedItem = suggestionList.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    searchUserField.setText(selectedItem);
                    popup.hide();
                }
            });

            // Handle keyboard navigation
            suggestionList.setOnKeyPressed(event -> {
                switch (event.getCode()) {
                    case ENTER:
                        String selectedItem = suggestionList.getSelectionModel().getSelectedItem();
                            searchUserField.setText(selectedItem);
                            popup.hide();
                            // Auto-trigger search when an item is selected with Enter
                            searchUserButton.fire();
                            event.consume();
                        break;
                    case ESCAPE:
                        popup.hide();
                        event.consume();
                        break;
                    default:
                        break;
                }
            });

            // Hide popup when focus is lost, with a small delay to allow for mouse clicks
            searchUserField.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue) {
                    javafx.application.Platform.runLater(() -> {
                        try {
                            Thread.sleep(200); // Small delay to allow for mouse clicks
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        popup.hide();
                    });
                }
            });
        } catch (Exception e) {
            System.err.println("Error initializing auto-complete: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Error initializing auto-complete functionality: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Set up auto-complete functionality for the equipment search field
     */
    private void setupEquipmentSearchAutoComplete() {
        try {
            // Create auto-complete popup with an empty list initially
            ListView<String> suggestionList = new ListView<>();
            javafx.stage.Popup popup = new javafx.stage.Popup();
            popup.getContent().add(suggestionList);

            // Set up listener for text changes
            searchEquipmentField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == null || newValue.isEmpty()) {
                    popup.hide();
                    return;
                }

                // Filter suggestions based on input
                String lowerCaseInput = newValue.toLowerCase();
                ObservableList<String> filteredList = FXCollections.observableArrayList(
                        searchEquipmentSuggestions.stream()
                                .filter(name -> name.toLowerCase().contains(lowerCaseInput))
                                .collect(Collectors.toList())
                );

                // Update list with filtered items
                suggestionList.setItems(filteredList);

                // Select the first item by default
                if (!filteredList.isEmpty()) {
                    suggestionList.getSelectionModel().select(0);
                }

                // Show popup if there are suggestions
                if (!filteredList.isEmpty() && searchEquipmentField.isFocused()) {
                    javafx.geometry.Bounds bounds = searchEquipmentField.localToScreen(searchEquipmentField.getBoundsInLocal());
                    if (bounds != null) {
                        popup.show(searchEquipmentField, bounds.getMinX(), bounds.getMaxY());
                        suggestionList.setPrefWidth(searchEquipmentField.getWidth());
                        suggestionList.setPrefHeight(Math.min(filteredList.size() * 24, 200));
                    }
                } else {
                    popup.hide();
                }
            });

            // Handle selection from suggestion list
            suggestionList.setOnMouseClicked(event -> {
                String selectedItem = suggestionList.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    searchEquipmentField.setText(selectedItem);
                    popup.hide();
                }
            });

            // Handle keyboard navigation
            suggestionList.setOnKeyPressed(event -> {
                switch (event.getCode()) {
                    case ENTER:
                        String selectedItem = suggestionList.getSelectionModel().getSelectedItem();
                            searchEquipmentField.setText(selectedItem);
                            popup.hide();
                            // Auto-trigger search when an item is selected with Enter
                            searchEquipmentButton.fire();
                            event.consume();
                        break;
                    case ESCAPE:
                        popup.hide();
                        event.consume();
                        break;
                    default:
                        break;
                }
            });

            // Hide popup when focus is lost, with a small delay to allow for mouse clicks
            searchEquipmentField.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue) {
                    javafx.application.Platform.runLater(() -> {
                        try {
                            Thread.sleep(200); // Small delay to allow for mouse clicks
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        popup.hide();
                    });
                }
            });
        } catch (Exception e) {
            System.err.println("Error initializing auto-complete: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Error initializing auto-complete functionality: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Set up auto-complete functionality for the borrower search field
     */
    private void setupBorrowerSearchAutoComplete() {
        try {
            // Create auto-complete popup with an empty list initially
            ListView<String> suggestionList = new ListView<>();
            javafx.stage.Popup popup = new javafx.stage.Popup();
            popup.getContent().add(suggestionList);

            // Set up listener for text changes
            borrowerField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == null || newValue.isEmpty()) {
                    popup.hide();
                    return;
                }

                // Filter suggestions based on input
                String lowerCaseInput = newValue.toLowerCase();
                ObservableList<String> filteredList = FXCollections.observableArrayList(
                        searchBorrowerSuggestions.stream()
                                .filter(name -> name.toLowerCase().contains(lowerCaseInput))
                                .collect(Collectors.toList())
                );

                // Update list with filtered items
                suggestionList.setItems(filteredList);

                // Select the first item by default
                if (!filteredList.isEmpty()) {
                    suggestionList.getSelectionModel().select(0);
                }

                // Show popup if there are suggestions
                if (!filteredList.isEmpty() && borrowerField.isFocused()) {
                    javafx.geometry.Bounds bounds = borrowerField.localToScreen(borrowerField.getBoundsInLocal());
                    if (bounds != null) {
                        popup.show(borrowerField, bounds.getMinX(), bounds.getMaxY());
                        suggestionList.setPrefWidth(borrowerField.getWidth());
                        suggestionList.setPrefHeight(Math.min(filteredList.size() * 24, 200));
                    }
                } else {
                    popup.hide();
                }
            });

            // Handle selection from suggestion list
            suggestionList.setOnMouseClicked(event -> {
                String selectedItem = suggestionList.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    borrowerField.setText(selectedItem);
                    popup.hide();
                }
            });

            // Handle keyboard navigation
            suggestionList.setOnKeyPressed(event -> {
                switch (event.getCode()) {
                    case ENTER:
                        String selectedItem = suggestionList.getSelectionModel().getSelectedItem();
                            borrowerField.setText(selectedItem);
                            popup.hide();
                            // Auto-trigger search when an item is selected with Enter
                            searchLendingButton.fire();
                            event.consume();
                        break;
                    case ESCAPE:
                        popup.hide();
                        event.consume();
                        break;
                    default:
                        break;
                }
            });

            // Hide popup when focus is lost, with a small delay to allow for mouse clicks
            borrowerField.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue) {
                    javafx.application.Platform.runLater(() -> {
                        try {
                            Thread.sleep(200); // Small delay to allow for mouse clicks
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        popup.hide();
                    });
                }
            });
        } catch (Exception e) {
            System.err.println("Error initializing auto-complete: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Error initializing auto-complete functionality: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Set up auto-complete functionality for the course search field
     */
    private void setupCourseSearchAutoComplete() {
        try {
            // Create auto-complete popup with an empty list initially
            ListView<String> suggestionList = new ListView<>();
            javafx.stage.Popup popup = new javafx.stage.Popup();
            popup.getContent().add(suggestionList);

            // Set up listener for text changes
            searchCourseField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == null || newValue.isEmpty()) {
                    popup.hide();
                    return;
                }

                // Filter suggestions based on input
                String lowerCaseInput = newValue.toLowerCase();
                ObservableList<String> filteredList = FXCollections.observableArrayList(
                        searchCourseSuggestions.stream()
                                .filter(name -> name.toLowerCase().contains(lowerCaseInput))
                                .collect(Collectors.toList())
                );

                // Update list with filtered items
                suggestionList.setItems(filteredList);

                // Select the first item by default
                if (!filteredList.isEmpty()) {
                    suggestionList.getSelectionModel().select(0);
                }

                // Show popup if there are suggestions
                if (!filteredList.isEmpty() && searchCourseField.isFocused()) {
                    javafx.geometry.Bounds bounds = searchCourseField.localToScreen(searchCourseField.getBoundsInLocal());
                    if (bounds != null) {
                        popup.show(searchCourseField, bounds.getMinX(), bounds.getMaxY());
                        suggestionList.setPrefWidth(searchCourseField.getWidth());
                        suggestionList.setPrefHeight(Math.min(filteredList.size() * 24, 200));
                    }
                } else {
                    popup.hide();
                }
            });
              // Handle selection from suggestion list
            suggestionList.setOnMouseClicked(event -> {
                String selectedItem = suggestionList.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    searchCourseField.setText(selectedItem);
                    popup.hide();
                    // Auto-trigger search when an item is selected with mouse
                    searchCourseButton.fire();
                }
            });

            // Handle keyboard navigation
            suggestionList.setOnKeyPressed(event -> {
                switch (event.getCode()) {
                    case ENTER:
                        String selectedItem = suggestionList.getSelectionModel().getSelectedItem();                            searchCourseField.setText(selectedItem);
                            popup.hide();
                            // Auto-trigger search when an item is selected with Enter
                            searchCourseButton.fire();
                            event.consume();
                        break;
                    case ESCAPE:
                        popup.hide();
                        event.consume();
                        break;
                    default:
                        break;
                }
            });

            // Hide popup when focus is lost, with a small delay to allow for mouse clicks
            searchCourseField.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue) {
                    javafx.application.Platform.runLater(() -> {
                        try {
                            Thread.sleep(200); // Small delay to allow for mouse clicks
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        popup.hide();
                    });
                }
            });
        } catch (Exception e) {
            System.err.println("Error initializing auto-complete: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Error initializing auto-complete functionality: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Handle managing students for a course
     *
     * @param course The course to manage students for
     */
    private void handleManageCourseStudents(Course course) {
        // Create a dialog for managing course students
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Manage Course Students");
        dialog.setHeaderText("Manage students for " + course.getCourseCode() + ": " + course.getCourseName());

        // Set the button types
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);

        // Create tables for enrolled students and available students
        TableView<Student> enrolledStudentsTable = new TableView<>();
        enrolledStudentsTable.setMaxHeight(200);
        TableView<Student> availableStudentsTable = new TableView<>();
        availableStudentsTable.setMaxHeight(200);

        // Add row hover effect to both tables
        String tableHoverStyle = "-fx-background-color: #f4f4f4;";

        enrolledStudentsTable.setRowFactory(tv -> {
            TableRow<Student> row = new TableRow<>();
            row.setOnMouseEntered(event -> {
                if (!row.isEmpty()) {
                    row.setStyle(tableHoverStyle);
                }
            });
            row.setOnMouseExited(event -> {
                if (!row.isEmpty()) {
                    row.setStyle("");
                }
            });
            return row;
        });

        availableStudentsTable.setRowFactory(tv -> {
            TableRow<Student> row = new TableRow<>();
            row.setOnMouseEntered(event -> {
                if (!row.isEmpty()) {
                    row.setStyle(tableHoverStyle);
                }
            });
            row.setOnMouseExited(event -> {
                if (!row.isEmpty()) {
                    row.setStyle("");
                }
            });
            return row;
        });

        // Set up columns for enrolled students table
        TableColumn<Student, Integer> enrolledIdColumn = new TableColumn<>("ID");
        enrolledIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Student, String> enrolledNameColumn = new TableColumn<>("Name");
        enrolledNameColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getFullName()));

        TableColumn<Student, String> enrolledEmailColumn = new TableColumn<>("Email");
        enrolledEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<Student, String> enrolledStudentIdColumn = new TableColumn<>("Student ID");
        enrolledStudentIdColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));

        TableColumn<Student, Button> enrolledActionColumn = new TableColumn<>("Action");
        enrolledActionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button removeButton = new Button("Remove");

            {
                removeButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                removeButton.setOnAction(event -> {
                    Student student = getTableView().getItems().get(getIndex());
                    boolean success = courseService.removeStudent(student.getId(), course.getId());
                    if (success) {
                        // Refresh the tables
                        refreshStudentTables(course, enrolledStudentsTable, availableStudentsTable);
                    } else {
                        showAlert("Error", "Failed to remove student from course", Alert.AlertType.ERROR);
                    }
                });
            }

            @Override
            protected void updateItem(Button item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(removeButton);
                }
            }
        });

        // Add columns to enrolled students table
        enrolledStudentsTable.getColumns().add(enrolledIdColumn);
        enrolledStudentsTable.getColumns().add(enrolledNameColumn);
        enrolledStudentsTable.getColumns().add(enrolledEmailColumn);
        enrolledStudentsTable.getColumns().add(enrolledStudentIdColumn);
        enrolledStudentsTable.getColumns().add(enrolledActionColumn);

        // Set up columns for available students table
        TableColumn<Student, Integer> availableIdColumn = new TableColumn<>("ID");
        availableIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Student, String> availableNameColumn = new TableColumn<>("Name");
        availableNameColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getFullName()));

        TableColumn<Student, String> availableEmailColumn = new TableColumn<>("Email");
        availableEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<Student, String> availableStudentIdColumn = new TableColumn<>("Student ID");
        availableStudentIdColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));

        TableColumn<Student, Button> availableActionColumn = new TableColumn<>("Action");
        availableActionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button enrollButton = new Button("Enroll");

            {
                enrollButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                enrollButton.setOnAction(event -> {
                    Student student = getTableView().getItems().get(getIndex());
                    boolean success = courseService.enrollStudent(student.getId(), course.getId());
                    if (success) {
                        // Refresh the tables
                        refreshStudentTables(course, enrolledStudentsTable, availableStudentsTable);
                    } else {
                        showAlert("Error", "Failed to enroll student in course", Alert.AlertType.ERROR);
                    }
                });
            }

            @Override
            protected void updateItem(Button item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(enrollButton);
                }
            }
        });

        // Add columns to available students table
        availableStudentsTable.getColumns().add(availableIdColumn);
        availableStudentsTable.getColumns().add(availableNameColumn);
        availableStudentsTable.getColumns().add(availableEmailColumn);
        availableStudentsTable.getColumns().add(availableStudentIdColumn);
        availableStudentsTable.getColumns().add(availableActionColumn);        // Add search field for available students with auto-complete functionality
        TextField searchAvailableStudentField = new TextField();
        searchAvailableStudentField.setPromptText("Search available students...");

        // Add real-time search as user types
        searchAvailableStudentField.textProperty().addListener((observable, oldValue, newValue) -> {
            refreshAvailableStudents(course, availableStudentsTable, newValue.trim());
        });

        HBox searchAvailableBox = new HBox(5, searchAvailableStudentField);
        searchAvailableBox.setPadding(new javafx.geometry.Insets(5));

        // Add search field for enrolled students
        TextField searchEnrolledStudentField = new TextField();
        searchEnrolledStudentField.setPromptText("Search enrolled students...");

        // Add real-time search as user types
        searchEnrolledStudentField.textProperty().addListener((observable, oldValue, newValue) -> {
            refreshEnrolledStudents(course, enrolledStudentsTable, newValue.trim());
        });

        HBox searchEnrolledBox = new HBox(5, searchEnrolledStudentField);
        searchEnrolledBox.setPadding(new javafx.geometry.Insets(5));        // Create layout
        VBox enrolledBox = new VBox(5);
        enrolledBox.getChildren().addAll(new Label("Enrolled Students:"), searchEnrolledBox, enrolledStudentsTable);

        VBox availableBox = new VBox(5);
        availableBox.getChildren().addAll(new Label("Available Students:"), searchAvailableBox, availableStudentsTable);

        VBox mainBox = new VBox(10);
        mainBox.getChildren().addAll(enrolledBox, availableBox);
        mainBox.setPadding(new javafx.geometry.Insets(10));

        // Set content
        dialog.getDialogPane().setContent(mainBox);

        // Initial load of student data
        refreshStudentTables(course, enrolledStudentsTable, availableStudentsTable);

        // Show the dialog
        dialog.showAndWait();

        // Refresh the main course table after managing students
        loadCourses();
    }

    /**
     * Refresh both student tables (enrolled and available) for a course
     * 
     * @param course The course
     * @param enrolledTable The table of enrolled students
     * @param availableTable The table of available students
     */
    private void refreshStudentTables(Course course, TableView<Student> enrolledTable, TableView<Student> availableTable) {
        // Load enrolled students without filtering
        refreshEnrolledStudents(course, enrolledTable, null);

        // Load available students (not enrolled) without filtering
        refreshAvailableStudents(course, availableTable, null);
    }

    /**
     * Refresh the enrolled students table with optional filtering
     * 
     * @param course The course
     * @param enrolledTable The table of enrolled students
     * @param searchTerm Optional search term to filter students
     */
    private void refreshEnrolledStudents(Course course, TableView<Student> enrolledTable, String searchTerm) {
        // Get all enrolled students for the course
        List<Student> enrolledStudents = courseService.getStudentsInCourse(course.getId());

        // Apply search filter if provided
        if (searchTerm != null && !searchTerm.isEmpty()) {
            String searchLower = searchTerm.toLowerCase();
            List<Student> filteredStudents = new ArrayList<>();
            for (Student student : enrolledStudents) {
                boolean nameMatch = student.getFullName().toLowerCase().contains(searchLower);
                boolean idMatch = student.getStudentId() != null && student.getStudentId().toLowerCase().contains(searchLower);
                boolean emailMatch = student.getEmail() != null && student.getEmail().toLowerCase().contains(searchLower);

                if (nameMatch || idMatch || emailMatch) {
                    filteredStudents.add(student);
                }
            }
            enrolledStudents = filteredStudents;
        }

        enrolledTable.setItems(FXCollections.observableArrayList(enrolledStudents));
    }

    /**
     * Refresh the available students table
     * 
     * @param course The course
     * @param availableTable The table of available students
     * @param searchTerm Optional search term to filter students
     */
    private void refreshAvailableStudents(Course course, TableView<Student> availableTable, String searchTerm) {
        // Get all students
        List<Student> allStudents = userService.getAllStudents();

        // Get enrolled students to exclude them
        List<Student> enrolledStudents = courseService.getStudentsInCourse(course.getId());

        // Filter out enrolled students
        HashSet<Integer> enrolledIds = new HashSet<>();
        for (Student student : enrolledStudents) {
            enrolledIds.add(student.getId());
        }

        List<Student> availableStudents = new ArrayList<>();
        for (Student student : allStudents) {
            if (!enrolledIds.contains(student.getId())) {
                availableStudents.add(student);
            }
        }

        // Apply search filter if provided
        if (searchTerm != null && !searchTerm.isEmpty()) {
            String searchLower = searchTerm.toLowerCase();
            List<Student> filteredStudents = new ArrayList<>();
            for (Student student : availableStudents) {
                boolean nameMatch = student.getFullName().toLowerCase().contains(searchLower);
                boolean idMatch = student.getStudentId() != null && student.getStudentId().toLowerCase().contains(searchLower);
                boolean emailMatch = student.getEmail() != null && student.getEmail().toLowerCase().contains(searchLower);

                if (nameMatch || idMatch || emailMatch) {
                    filteredStudents.add(student);
                }
            }
            availableStudents = filteredStudents;
        }

        availableTable.setItems(FXCollections.observableArrayList(availableStudents));
    }
}
