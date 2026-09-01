/**
 * @author Group 9
 */
package org.example.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.layout.HBox;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import org.example.Main;
import org.example.model.*;
import org.example.service.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

/**
 * Controller for the academic staff view.
 */
public class AcademicStaffController {

    // Services
    private AuthenticationService authenticationService;
    private UserService userService;
    private EquipmentService equipmentService;
    private LendingService lendingService;
    private CourseService courseService;
    
    // Current academic staff
    private AcademicStaff currentStaff;

    // Timer for auto-refresh
    private Timer refreshTimer;

    // Auto-complete suggestions for equipment search
    private javafx.collections.ObservableList<String> searchEquipmentSuggestions;

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
    private TextField staffIdField;
    @FXML
    private TextField departmentField;
    @FXML
    private TextField positionField;
    @FXML
    private Button updateInfoButton;
    @FXML
    private Button changePasswordButton;

    // My Courses Tab
    @FXML
    private TableView<Course> coursesTable;
    @FXML
    private TableColumn<Course, String> courseCodeColumn;
    @FXML
    private TableColumn<Course, String> courseNameColumn;
    @FXML
    private TableColumn<Course, Integer> semesterColumn;
    @FXML
    private TableColumn<Course, Integer> yearColumn;
    @FXML
    private TableColumn<Course, Integer> studentsCountColumn;
    @FXML
    private TableColumn<Course, Button> courseActionColumn;

    // Lending Requests Tab
    @FXML
    private ComboBox<Course> filterCourseComboBox;
    @FXML
    private ComboBox<String> filterStatusComboBox;
    @FXML
    private Button filterRequestsButton;
    @FXML
    private TableView<LendingRecord> lendingRequestsTable;
    @FXML
    private TableColumn<LendingRecord, String> requestStudentColumn;
    @FXML
    private TableColumn<LendingRecord, String> requestEquipmentColumn;
    @FXML
    private TableColumn<LendingRecord, String> requestCourseColumn;
    @FXML
    private TableColumn<LendingRecord, String> requestBorrowDateColumn;
    @FXML
    private TableColumn<LendingRecord, String> requestDueDateColumn;
    @FXML
    private TableColumn<LendingRecord, String> requestStatusColumn;
    @FXML
    private TableColumn<LendingRecord, Button> requestActionColumn;

    // Borrow Equipment Tab
    @FXML
    private TextField searchEquipmentField;
    @FXML
    private ComboBox<String> categoryComboBox;
    @FXML
    private ComboBox<String> conditionComboBox;
    @FXML
    private Button searchEquipmentButton;
    @FXML
    private TableView<Equipment> equipmentTable;
    @FXML
    private TableColumn<Equipment, String> eqNameColumn;
    @FXML
    private TableColumn<Equipment, String> eqCategoryColumn;
    @FXML
    private TableColumn<Equipment, String> eqConditionColumn;
    @FXML
    private TableColumn<Equipment, String> eqManufacturerColumn;
    @FXML
    private TableColumn<Equipment, String> eqModelColumn;
    @FXML
    private TableColumn<Equipment, Boolean> eqAvailableColumn;
    @FXML
    private TableColumn<Equipment, Button> eqActionColumn;
    @FXML
    private TextField purposeField;

    // My Lending History Tab
    @FXML
    private DatePicker fromDatePicker;
    @FXML
    private DatePicker toDatePicker;
    @FXML
    private ComboBox<String> statusComboBox;
    @FXML
    private Button filterHistoryButton;
    @FXML
    private TableView<LendingRecord> lendingHistoryTable;
    @FXML
    private TableColumn<LendingRecord, String> equipmentNameColumn;
    @FXML
    private TableColumn<LendingRecord, String> borrowDateColumn;
    @FXML
    private TableColumn<LendingRecord, String> dueDateColumn;
    @FXML
    private TableColumn<LendingRecord, String> returnDateColumn;
    @FXML
    private TableColumn<LendingRecord, String> statusColumn;
    @FXML
    private TableColumn<LendingRecord, Button> actionColumn;

    // Statistics Tab
    @FXML
    private ComboBox<Course> statsCourseComboBox;
    @FXML
    private Button refreshStatsButton;
    @FXML
    private PieChart statusPieChart;
    @FXML
    private BarChart<String, Number> borrowingBarChart;
    @FXML
    private Label totalBorrowedLabel;
    @FXML
    private Label currentlyBorrowedLabel;
    @FXML
    private Label overdueItemsLabel;
    @FXML
    private Label pendingRequestsLabel;

    // Bottom
    @FXML
    private Button logoutButton;

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

        // Get current academic staff
        if (authenticationService.isAcademicStaff()) {
            currentStaff = authenticationService.getCurrentAcademicStaff();
            welcomeLabel.setText("Welcome, " + currentStaff.getFullName());

            initializePersonalInfo();
            initializeMyCourses();
            initializeLendingRequests();
            initializeEquipmentBorrowing();
            initializeLendingHistory();
            initializeStatistics();

            // Set up auto-refresh timer (refresh every 10 seconds)
            refreshTimer = new Timer(true); // true makes it a daemon timer
            refreshTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    // Run on JavaFX thread
                    javafx.application.Platform.runLater(() -> {
                        loadLendingRequests();
                        loadLendingHistory();
                    });
                }
            }, 10000, 10000); // 10 seconds delay, 10 seconds period
        } else {
            showAlert("Error", "Not logged in as academic staff", Alert.AlertType.ERROR);
        }
    }

    /**
     * Initialize personal information tab
     */
    private void initializePersonalInfo() {
        // Set personal information fields
        firstNameField.setText(currentStaff.getFirstName());
        lastNameField.setText(currentStaff.getLastName());
        emailField.setText(currentStaff.getEmail());
        phoneField.setText(currentStaff.getPhoneNumber());
        if (currentStaff.getDateOfBirth() != null) {
            dobPicker.setValue(currentStaff.getDateOfBirth());
        }
        addressField.setText(currentStaff.getAddress());
        staffIdField.setText(currentStaff.getStaffId());
        departmentField.setText(currentStaff.getDepartment());
        positionField.setText(currentStaff.getPosition());
    }

    /**
     * Initialize my courses tab
     */
    private void initializeMyCourses() {
        // Set up table columns
        courseCodeColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getCourseCode()));

        courseNameColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getCourseName()));

        semesterColumn.setCellValueFactory(cellData -> 
                new SimpleIntegerProperty(cellData.getValue().getSemester()).asObject());

        yearColumn.setCellValueFactory(cellData -> 
                new SimpleIntegerProperty(cellData.getValue().getYear()).asObject());

        studentsCountColumn.setCellValueFactory(cellData -> 
                new SimpleIntegerProperty(cellData.getValue().getEnrolledStudents().size()).asObject());

        // Set up action column
        courseActionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button viewButton = new Button("View Students");

            @Override
            protected void updateItem(Button item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    return;
                }

                Course course = getTableView().getItems().get(getIndex());
                viewButton.setOnAction(event -> handleViewCourseStudents(course));
                setGraphic(viewButton);
            }
        });

        // Load courses
        loadMyCourses();
    }

    /**
     * Initialize lending requests tab
     */
    private void initializeLendingRequests() {
        // Set up course combo box
        List<Course> courses = courseService.getCoursesByInstructor(currentStaff.getId());
        ObservableList<Course> coursesList = FXCollections.observableArrayList(courses);
        filterCourseComboBox.setItems(coursesList);
        filterCourseComboBox.setCellFactory(param -> new ListCell<Course>() {
            @Override
            protected void updateItem(Course item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getCourseCode() + " - " + item.getCourseName());
                }
            }
        });
        filterCourseComboBox.setButtonCell(new ListCell<Course>() {
            @Override
            protected void updateItem(Course item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getCourseCode() + " - " + item.getCourseName());
                }
            }
        });

        // Set up status combo box
        filterStatusComboBox.setItems(FXCollections.observableArrayList(
                "All", "Pending", "Approved", "Borrowed", "Returned", "Overdue", "Rejected"));
        filterStatusComboBox.getSelectionModel().selectFirst();

        // Set up table columns
        requestStudentColumn.setCellValueFactory(cellData -> {
            User borrower = cellData.getValue().getBorrower();
            return new SimpleStringProperty(borrower != null ? borrower.getFullName() : "");
        });

        requestEquipmentColumn.setCellValueFactory(cellData -> {
            Equipment equipment = cellData.getValue().getEquipment();
            return new SimpleStringProperty(equipment != null ? equipment.getName() : "");
        });

        requestCourseColumn.setCellValueFactory(cellData -> {
            Course course = cellData.getValue().getCourse();
            return new SimpleStringProperty(course != null ? course.getCourseCode() : "");
        });

        requestBorrowDateColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getBorrowDate().format(dateFormatter)));

        requestDueDateColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getDueDate().format(dateFormatter)));

        requestStatusColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getStatus()));

        // Set up action column
        requestActionColumn.setCellFactory(param -> new TableCell<>() {
            private final HBox actionBox = new HBox(5);
            private final Button approveButton = new Button("Approve");
            private final Button rejectButton = new Button("Reject");

            {
                approveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                rejectButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");
                actionBox.getChildren().addAll(approveButton, rejectButton);
            }

            @Override
            protected void updateItem(Button item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    return;
                }

                LendingRecord record = getTableView().getItems().get(getIndex());
                if ("Pending".equals(record.getStatus())) {
                    approveButton.setOnAction(event -> handleApproveLendingRequest(record));
                    rejectButton.setOnAction(event -> handleRejectLendingRequest(record));
                    setGraphic(actionBox);
                } else {
                    setGraphic(null);
                }
            }
        });

        // Load lending requests
        loadLendingRequests();
    }

    /**
     * Initialize equipment borrowing tab
     */
    private void initializeEquipmentBorrowing() {
        // Set up category and condition combo boxes
        loadCategories();
        loadConditions();
        
        // Initialize auto-complete for equipment search
        initializeSearchEquipmentAutoComplete();

        // Set up table columns
        eqNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        eqCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        eqConditionColumn.setCellValueFactory(new PropertyValueFactory<>("condition"));
        eqManufacturerColumn.setCellValueFactory(new PropertyValueFactory<>("manufacturer"));
        eqModelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));
        eqAvailableColumn.setCellValueFactory(new PropertyValueFactory<>("available"));

        // Set up action column
        eqActionColumn.setCellFactory(param -> new TableCell<>() {
            private final HBox actionBox = new HBox(5);
            private final Button viewButton = new Button("View");
            private final Button borrowButton = new Button("Borrow");

            {
                actionBox.getChildren().addAll(viewButton, borrowButton);
            }

            @Override
            protected void updateItem(Button item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    return;
                }

                Equipment equipment = getTableView().getItems().get(getIndex());
                // View button is always shown
                viewButton.setOnAction(event -> handleViewEquipment(equipment));
                
                // Borrow button only shown for available equipment
                if (equipment.isAvailable()) {
                    borrowButton.setOnAction(event -> handleBorrowEquipment(equipment));
                    setGraphic(actionBox);
                } else {
                    // If equipment is not available, only show view button
                    setGraphic(viewButton);
                }
            }
        });

        // Load equipment
        loadAllEquipment();
    }

    /**
     * Initialize lending history tab
     */
    private void initializeLendingHistory() {
        // Set up status combo box
        statusComboBox.setItems(FXCollections.observableArrayList(
                "All", "Pending", "Approved", "Borrowed", "Returned", "Overdue", "Rejected"));
        statusComboBox.getSelectionModel().selectFirst();

        // Set up date pickers
        fromDatePicker.setValue(LocalDate.now().minusMonths(1));
        toDatePicker.setValue(LocalDate.now());

        // Set up table columns
        equipmentNameColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getEquipment().getName()));

        borrowDateColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getBorrowDate().format(dateFormatter)));

        dueDateColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getDueDate().format(dateFormatter)));

        returnDateColumn.setCellValueFactory(cellData -> {
            LocalDateTime returnDate = cellData.getValue().getReturnDate();
            return new SimpleStringProperty(returnDate != null ? returnDate.format(dateFormatter) : "");
        });

        statusColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getStatus()));

        // Set up action column
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button returnButton = new Button("Return");

            @Override
            protected void updateItem(Button item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    return;
                }

                LendingRecord record = getTableView().getItems().get(getIndex());
                if ("Borrowed".equals(record.getStatus())) {
                    returnButton.setOnAction(event -> handleReturnEquipment(record));
                    setGraphic(returnButton);
                } else {
                    setGraphic(null);
                }
            }
        });

        // Load lending history
        loadLendingHistory();
    }

    /**
     * Initialize statistics tab
     */
    private void initializeStatistics() {
        // Set up course combo box
        List<Course> courses = courseService.getCoursesByInstructor(currentStaff.getId());
        courses.add(0, null); // Add null option for "All Courses"
        ObservableList<Course> coursesList = FXCollections.observableArrayList(courses);
        statsCourseComboBox.setItems(coursesList);
        statsCourseComboBox.setCellFactory(param -> new ListCell<Course>() {
            @Override
            protected void updateItem(Course item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("All Courses");
                } else {
                    setText(item.getCourseCode() + " - " + item.getCourseName());
                }
            }
        });
        statsCourseComboBox.setButtonCell(new ListCell<Course>() {
            @Override
            protected void updateItem(Course item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("All Courses");
                } else {
                    setText(item.getCourseCode() + " - " + item.getCourseName());
                }
            }
        });
        statsCourseComboBox.getSelectionModel().selectFirst();

        // Add change listener to automatically update statistics when course selection changes
        statsCourseComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            loadStatistics();
        });

        // Load statistics
        loadStatistics();
    }

    /**
     * Handle update information button click
     *
     * @param event The action event
     */
    @FXML
    public void handleUpdateInfo(ActionEvent event) {
        // Update staff information
        currentStaff.setFirstName(firstNameField.getText());
        currentStaff.setLastName(lastNameField.getText());
        currentStaff.setEmail(emailField.getText());
        currentStaff.setPhoneNumber(phoneField.getText());
        currentStaff.setDateOfBirth(dobPicker.getValue());
        currentStaff.setAddress(addressField.getText());

        // Save changes
        boolean success = userService.updatePersonalInfo(currentStaff);
        if (success) {
            showAlert("Success", "Personal information updated successfully", Alert.AlertType.INFORMATION);
            welcomeLabel.setText("Welcome, " + currentStaff.getFullName());
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
            boolean success = userService.changePassword(currentStaff.getId(), oldPassword, newPassword);
            if (success) {
                showAlert("Success", "Password changed successfully", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Error", "Failed to change password. Check your current password.", Alert.AlertType.ERROR);
            }
        });
    }

    /**
     * Handle filter requests button click
     *
     * @param event The action event
     */
    @FXML
    public void handleFilterRequests(ActionEvent event) {
        loadLendingRequests();
    }
    
    /**
     * Handle search equipment button click
     *
     * @param event The action event
     */
    @FXML
    public void handleSearchEquipment(ActionEvent event) {
        String name = searchEquipmentField.getText();
        String category = categoryComboBox.getValue();
        String condition = conditionComboBox.getValue();

        // If "All" is selected, set to null for the search
        if ("All".equals(category)) {
            category = null;
        }
        if ("All".equals(condition)) {
            condition = null;
        }

        // Search equipment
        List<Equipment> searchResults = equipmentService.searchEquipment(name, category, condition);
        equipmentTable.setItems(FXCollections.observableArrayList(searchResults));
        equipmentTable.refresh(); // Refresh the table to ensure it displays the latest data on time
        
        // Update the auto-complete suggestions with any new search results
        if (searchResults != null && !searchResults.isEmpty()) {
            // Create a temporary list of all equipment names from search results
            List<String> searchNames = searchResults.stream()
                .map(Equipment::getName)
                .distinct()
                .collect(Collectors.toList());
                
            // Find new names that aren't in our suggestions yet
            List<String> newSuggestions = searchNames.stream()
                .filter(equipName -> !searchEquipmentSuggestions.contains(equipName))
                .collect(Collectors.toList());
                
            if (!newSuggestions.isEmpty()) {
                // Add new names to suggestions
                searchEquipmentSuggestions.addAll(newSuggestions);
            }
        }
    }

    /**
     * Handle filter history button click
     *
     * @param event The action event
     */
    @FXML
    public void handleFilterHistory(ActionEvent event) {
        loadLendingHistory();
    }

    /**
     * Handle refresh statistics button click
     *
     * @param event The action event
     */
    @FXML
    public void handleRefreshStats(ActionEvent event) {
        loadStatistics();
    }

    /**
     * Handle logout button click
     *
     * @param event The action event
     */
    @FXML
    public void handleLogout(ActionEvent event) {
        // Cancel the refresh timer
        if (refreshTimer != null) {
            refreshTimer.cancel();
            refreshTimer = null;
        }

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
     * Load my courses
     */
    private void loadMyCourses() {
        List<Course> courses = courseService.getCoursesByInstructor(currentStaff.getId());
        coursesTable.setItems(FXCollections.observableArrayList(courses));
    }

    /**
     * Handle view course students button click
     *
     * @param course The course to view students for
     */
    private void handleViewCourseStudents(Course course) {
        // Create a dialog to display students
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Students in " + course.getCourseCode());
        dialog.setHeaderText("Students enrolled in " + course.getCourseCode() + " - " + course.getCourseName());

        // Set the button types
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        // Create a table view for students
        TableView<Student> studentsTable = new TableView<>();
        studentsTable.setPrefWidth(500);
        studentsTable.setPrefHeight(400);

        // Create table columns
        TableColumn<Student, String> idColumn = new TableColumn<>("Student ID");
        idColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStudentId()));

        TableColumn<Student, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFullName()));

        TableColumn<Student, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));

        // Add columns to table
        studentsTable.getColumns().add(idColumn);
        studentsTable.getColumns().add(nameColumn);
        studentsTable.getColumns().add(emailColumn);

        // Set column resize policy
        studentsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Add students to table
        studentsTable.setItems(FXCollections.observableArrayList(course.getEnrolledStudents()));

        // Set the content
        dialog.getDialogPane().setContent(studentsTable);

        // Show the dialog
        dialog.showAndWait();
    }

    /**
     * Load lending requests
     */
    private void loadLendingRequests() {
        // Get all courses taught by the academic staff
        List<Course> courses = courseService.getCoursesByInstructor(currentStaff.getId());

        // Get all lending records for these courses
        List<LendingRecord> allRequests = new ArrayList<>();
        for (Course course : courses) {
            allRequests.addAll(lendingService.getLendingRecordsByCourse(course.getId()));
        }

        // Filter by status if needed
        String status = filterStatusComboBox.getValue();
        if (status != null && !status.equals("All")) {
            allRequests = allRequests.stream()
                    .filter(record -> record.getStatus().equals(status))
                    .collect(Collectors.toList());
        }

        // Filter by course if needed
        Course selectedCourse = filterCourseComboBox.getValue();
        if (selectedCourse != null) {
            allRequests = allRequests.stream()
                    .filter(record -> record.getCourse() != null && record.getCourse().getId() == selectedCourse.getId())
                    .collect(Collectors.toList());
        }

        lendingRequestsTable.setItems(FXCollections.observableArrayList(allRequests));
        lendingRequestsTable.refresh(); // Refresh the table to ensure it displays the latest data on time
    }

    /**
     * Handle approve lending request button click
     *
     * @param lendingRecord The lending record to approve
     */
    private void handleApproveLendingRequest(LendingRecord lendingRecord) {
        boolean success = lendingService.approveLendingRequest(lendingRecord.getId(), currentStaff.getId());
        if (success) {
            showAlert("Success", "Lending request approved successfully", Alert.AlertType.INFORMATION);
            loadLendingRequests(); // Refresh the table
        } else {
            showAlert("Error", "Failed to approve lending request", Alert.AlertType.ERROR);
        }
    }

    /**
     * Handle reject lending request button click
     *
     * @param lendingRecord The lending record to reject
     */
    private void handleRejectLendingRequest(LendingRecord lendingRecord) {
        // Create a dialog for rejection reason
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Reject Lending Request");
        dialog.setHeaderText("Provide a reason for rejection");
        dialog.setContentText("Reason:");

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(reason -> {    
            boolean success = lendingService.rejectLendingRequest(lendingRecord.getId(), currentStaff.getId(), reason);
            if (success) {
                showAlert("Success", "Lending request rejected successfully", Alert.AlertType.INFORMATION);
                loadLendingRequests(); // Refresh the table
            } else {
                showAlert("Error", "Failed to reject lending request", Alert.AlertType.ERROR);
            }
        });
    }

    /**
     * Load categories for combo box
     */
    private void loadCategories() {
        List<String> categories = equipmentService.getAllCategories();
        categories.add(0, "All");
        categoryComboBox.setItems(FXCollections.observableArrayList(categories));
        categoryComboBox.getSelectionModel().selectFirst();
    }

    /**
     * Load conditions for combo box
     */
    private void loadConditions() {
        List<String> conditions = equipmentService.getAllConditions();
        conditions.add(0, "All");
        conditionComboBox.setItems(FXCollections.observableArrayList(conditions));
        conditionComboBox.getSelectionModel().selectFirst();
    }

    /**
     * Load all equipment
     */
    private void loadAllEquipment() {
        List<Equipment> equipmentList = equipmentService.getAllEquipment();
        equipmentTable.setItems(FXCollections.observableArrayList(equipmentList));
        equipmentTable.refresh(); // Refresh the table to ensure it displays the latest data on time
    }

    /**
     * Handle borrow equipment button click
     *
     * @param equipment The equipment to borrow
     */
    private void handleBorrowEquipment(Equipment equipment) {
        String purpose = purposeField.getText();

        if (purpose.isEmpty()) {
            showAlert("Error", "Please enter a purpose", Alert.AlertType.ERROR);
            return;
        }

        // Create lending request
        LocalDateTime borrowDate = LocalDateTime.now();
        LocalDateTime dueDate = borrowDate.plusWeeks(2); // Maximum borrowing period is 2 weeks

        boolean success = lendingService.createStaffLendingRequest(
                currentStaff, equipment, borrowDate, dueDate, purpose, "");

        if (success) {
            showAlert("Success", "Borrowing request submitted successfully", Alert.AlertType.INFORMATION);
            loadAllEquipment(); // Refresh equipment list
            purposeField.clear(); // Clear purpose field
            loadLendingHistory(); // Refresh lending history
        } else {
            showAlert("Error", "Failed to submit borrowing request", Alert.AlertType.ERROR);
        }
    }

    /**
     * Handle view equipment details
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

            // Add equipment images if available
            List<byte[]> images = equipmentService.getEquipmentImages(equipment.getId());
            if (!images.isEmpty()) {
                grid.add(new Label("Images:"), 0, 12);

                // Create a horizontal box for images
                javafx.scene.layout.HBox imagesBox = new javafx.scene.layout.HBox(10);
                imagesBox.setPadding(new javafx.geometry.Insets(10));

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
            }

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
     * Load lending history
     */
    private void loadLendingHistory() {
        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();
        String status = statusComboBox.getValue();

        if (fromDate == null || toDate == null) {
            showAlert("Error", "Please select both from and to dates", Alert.AlertType.ERROR);
            return;
        }

        if (fromDate.isAfter(toDate)) {
            showAlert("Error", "From date cannot be after to date", Alert.AlertType.ERROR);
            return;
        }

        // Get lending records
        List<LendingRecord> allRecords = lendingService.getLendingRecordsByBorrower(currentStaff.getId());

        // Filter by date and status
        LocalDateTime fromDateTime = fromDate.atStartOfDay();
        LocalDateTime toDateTime = toDate.plusDays(1).atStartOfDay();

        ObservableList<LendingRecord> lendingRecords = allRecords.stream()
                .filter(record -> {
                    boolean dateMatch = record.getBorrowDate().isAfter(fromDateTime) && 
                                       record.getBorrowDate().isBefore(toDateTime);
                    boolean statusMatch = "All".equals(status) || record.getStatus().equals(status);
                    return dateMatch && statusMatch;
                })
                .collect(FXCollections::observableArrayList, ObservableList::add, ObservableList::addAll);
                
        lendingHistoryTable.setItems(FXCollections.observableArrayList(lendingRecords));
        lendingHistoryTable.refresh(); // Refresh the table to ensure it displays the latest data on time
    }

    /**
     * Handle return equipment button click
     *
     * @param lendingRecord The lending record to return
     */
    private void handleReturnEquipment(LendingRecord lendingRecord) {
        // Create a dialog for returning equipment
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Return Equipment");
        dialog.setHeaderText("Enter condition and notes for returning " + lendingRecord.getEquipment().getName());

        // Set the button types
        ButtonType returnButtonType = new ButtonType("Return", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(returnButtonType, ButtonType.CANCEL);

        // Create the fields
        ComboBox<String> conditionCombo = new ComboBox<>();
        conditionCombo.setItems(FXCollections.observableArrayList("Excellent", "Good", "Fair", "Poor", "Damaged"));
        conditionCombo.getSelectionModel().selectFirst();

        TextArea notesArea = new TextArea();
        notesArea.setPromptText("Enter any notes about the equipment condition");

        // Layout the dialog
        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
        grid.add(new Label("Condition:"), 0, 0);
        grid.add(conditionCombo, 1, 0);
        grid.add(new Label("Notes:"), 0, 1);
        grid.add(notesArea, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Convert the result to a string when the return button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == returnButtonType) {
                return conditionCombo.getValue() + "|" + notesArea.getText();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(returnInfo -> {
            String[] parts = returnInfo.split("\\|");
            String condition = parts[0];
            String notes = parts.length > 1 ? parts[1] : "";

            // Return equipment
            boolean success = lendingService.returnEquipment(
                    lendingRecord.getId(), LocalDateTime.now(), condition, notes);

            if (success) {
                showAlert("Success", "Equipment returned successfully", Alert.AlertType.INFORMATION);
                loadLendingHistory(); // Refresh lending history
                loadAllEquipment(); // Refresh equipment list
            } else {
                showAlert("Error", "Failed to return equipment", Alert.AlertType.ERROR);
            }
        });
    }
    
    /**
     * Load statistics
     */
    private void loadStatistics() {
        // Get selected course
        Course selectedCourse = statsCourseComboBox.getValue();

        // Get lending statistics
        LendingService.LendingStatistics stats = lendingService.getAcademicStaffLendingStatistics(currentStaff.getId());

        // Update summary labels
        totalBorrowedLabel.setText(String.valueOf(stats.totalLendings));
        currentlyBorrowedLabel.setText(String.valueOf(stats.currentLendings));
        overdueItemsLabel.setText(String.valueOf(stats.overdueLendings));

        // Count pending requests
        int pendingRequests = 0;
        List<Course> courses = courseService.getCoursesByInstructor(currentStaff.getId());
        for (Course course : courses) {
            // If a specific course is selected, only count requests for that course
            if (selectedCourse != null && selectedCourse.getId() != course.getId()) {
                continue;
            }
            
            List<LendingRecord> records = lendingService.getLendingRecordsByCourse(course.getId());
            pendingRequests += records.stream()
                    .filter(record -> "Pending".equals(record.getStatus()))
                    .count();
        }
        pendingRequestsLabel.setText(String.valueOf(pendingRequests));

        // Update pie chart
        statusPieChart.getData().clear();
        Map<String, Integer> statusCounts = new HashMap<>();

        // Count records by status
        for (Course course : courses) {
            // If a specific course is selected, only count records for that course
            if (selectedCourse != null && selectedCourse.getId() != course.getId()) {
                continue;
            }
            
            List<LendingRecord> records = lendingService.getLendingRecordsByCourse(course.getId());
            for (LendingRecord record : records) {
                String status = record.getStatus();
                statusCounts.put(status, statusCounts.getOrDefault(status, 0) + 1);
            }
        }

        // Add data to pie chart
        for (Map.Entry<String, Integer> entry : statusCounts.entrySet()) {
            statusPieChart.getData().add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }

        // Update bar chart
        borrowingBarChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Borrowing Frequency");

        // Add data to bar chart
        if (selectedCourse == null) {
            // Show all courses
            for (Map.Entry<String, Integer> entry : stats.lendingsPerCourse.entrySet()) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }
        } else {
            // Show only the selected course
            String courseCode = selectedCourse.getCourseCode();
            int count = stats.lendingsPerCourse.getOrDefault(courseCode, 0);
            series.getData().add(new XYChart.Data<>(courseCode, count));
        }

        borrowingBarChart.getData().add(series);
    }

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
     * Initialize auto-complete functionality for search equipment field
     */
    private void initializeSearchEquipmentAutoComplete() {
        // Get all equipment names for auto-complete
        List<Equipment> allEquipment = equipmentService.getAllEquipment();
        searchEquipmentSuggestions = FXCollections.observableArrayList(
                allEquipment.stream()
                        .map(Equipment::getName)
                        .distinct()
                        .collect(Collectors.toList())
        );

        // Create auto-complete popup with an empty list initially
        javafx.scene.control.ListView<String> suggestionList = new javafx.scene.control.ListView<>();
        javafx.stage.Popup popup = new javafx.stage.Popup();
        popup.getContent().add(suggestionList);

        // Set up listener for text changes
        searchEquipmentField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Hide popup if text is empty
            if (newValue == null || newValue.trim().isEmpty()) {
                popup.hide();
                return;
            }
            
            // Filter suggestions based on input
            String lowerCaseInput = newValue.toLowerCase();
            ObservableList<String> filteredList = FXCollections.observableArrayList(
                    searchEquipmentSuggestions.stream()
                            .filter(equipName -> equipName.toLowerCase().contains(lowerCaseInput))
                            .collect(Collectors.toList())
            );

            // Update list with filtered items
            suggestionList.setItems(filteredList);
            
            // Select the first item by default
            if (!filteredList.isEmpty() && suggestionList.getSelectionModel().getSelectedIndex() < 0) {
                suggestionList.getSelectionModel().select(0);
            }
            
            // Adjust dimensions
            suggestionList.setPrefWidth(searchEquipmentField.getWidth());
            int height = Math.min(filteredList.size() * 24, 200);
            suggestionList.setPrefHeight(height > 0 ? height : 24); // Ensure minimum height

            // Show popup if there are suggestions and field is focused
            if (!filteredList.isEmpty() && searchEquipmentField.isFocused()) {
                // Position popup below the text field
                javafx.geometry.Bounds bounds = searchEquipmentField.localToScreen(searchEquipmentField.getBoundsInLocal());
                if (bounds != null) {
                    popup.show(searchEquipmentField, bounds.getMinX(), bounds.getMaxY());
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
                
                // Auto-trigger search when an item is selected
                searchEquipmentButton.fire();
            }
        });

        // Handle keyboard navigation in the suggestion list
        suggestionList.setOnKeyPressed(event -> {
                switch (event.getCode()) {
                    case ENTER:
                        String selectedItem = suggestionList.getSelectionModel().getSelectedItem();
                            searchEquipmentField.setText(selectedItem);
                            popup.hide();
                            // Auto-trigger search when an item is selected with Enter
                            javafx.application.Platform.runLater(() -> searchEquipmentButton.fire());
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

        // Hide popup when focus is lost
        searchEquipmentField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // If focus is lost
                // Add a small delay before hiding to allow for selection
                javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.millis(200));
                pause.setOnFinished(e -> popup.hide());
                pause.play();
            }
        });
    }
}
