/**
 * @author Group 9
 */
package org.example.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.Main;
import org.example.model.Equipment;
import org.example.service.EquipmentService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for the visitor view.
 */
public class VisitorController {

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> categoryComboBox;

    @FXML
    private ComboBox<String> conditionComboBox;

    @FXML
    private Button searchButton;

    @FXML
    private TableView<Equipment> equipmentTable;

    @FXML
    private TableColumn<Equipment, String> nameColumn;

    @FXML
    private TableColumn<Equipment, String> categoryColumn;

    @FXML
    private TableColumn<Equipment, String> conditionColumn;

    @FXML
    private TableColumn<Equipment, String> manufacturerColumn;

    @FXML
    private TableColumn<Equipment, String> modelColumn;

    @FXML
    private TableColumn<Equipment, String> availableColumn;

    @FXML
    private TableColumn<Equipment, Button> detailsColumn;

    @FXML
    private Button backButton;
    
    private EquipmentService equipmentService;
    private ObservableList<Equipment> equipmentList;
    
    // Auto-complete suggestions for equipment search
    private ObservableList<String> searchEquipmentSuggestions;

    /**
     * Initialize the controller
     */
    @FXML
    public void initialize() {
        try {
            // Get the equipment service
            equipmentService = Main.getEquipmentService();

            // Initialize the equipment list
            equipmentList = FXCollections.observableArrayList();

            // Set up the table columns
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
            conditionColumn.setCellValueFactory(new PropertyValueFactory<>("condition"));
            manufacturerColumn.setCellValueFactory(new PropertyValueFactory<>("manufacturer"));
            modelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));

            // Set up the available column
            availableColumn.setCellValueFactory(cellData -> 
                    new SimpleStringProperty(cellData.getValue().isAvailable() ? "Yes" : "No"));

            // Set up the details column
            detailsColumn.setCellFactory(param -> new TableCell<>() {
                private final Button detailsButton = new Button("View");

                @Override
                protected void updateItem(Button item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(detailsButton);
                        detailsButton.setOnAction(event -> {
                            Equipment equipment = getTableView().getItems().get(getIndex());
                            showEquipmentDetails(equipment);
                        });
                    }
                }
            });

            // Set the items in the table
            equipmentTable.setItems(equipmentList);

            // Load the categories and conditions
            loadCategories();
            loadConditions();
            
            // Initialize auto-complete functionality
            initializeAutoComplete();

            // Load all equipment
            loadAllEquipment();
        } catch (Exception e) {
            System.err.println("Error initializing visitor view: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Error loading visitor view: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Handle search button click
     *
     * @param event The action event
     */
    @FXML
    public void handleSearch(ActionEvent event) {
        try {
            // Get the search criteria
            String name = searchField.getText();
            String category = categoryComboBox.getValue();
            String condition = conditionComboBox.getValue();

            // Search for equipment
            List<Equipment> searchResults = equipmentService.searchEquipment(name, category, condition);
            
            // Update the table
            equipmentList.clear();
            equipmentList.addAll(searchResults);

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
        } catch (Exception e) {
            System.err.println("Error searching equipment: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Error searching equipment: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Handle back button click
     *
     * @param event The action event
     */
    @FXML
    public void handleBack(ActionEvent event) {
        try {
            // Load the login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage) backButton.getScene().getWindow();

            // Set up the scene
            Scene scene = new Scene(root, 800, 600);

            // Set up the stage
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Error loading login screen: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Load all equipment
     */
    private void loadAllEquipment() {
        try {
            // Get all equipment
            List<Equipment> allEquipment = equipmentService.getAllEquipment();

            // Update the table
            equipmentList.clear();
            equipmentList.addAll(allEquipment);
        } catch (Exception e) {
            System.err.println("Error loading equipment: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Error loading equipment: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Load the categories
     */
    private void loadCategories() {
        try {
            // Get all categories
            List<String> categories = equipmentService.getAllCategories();

            // Add an empty option
            categories.add(0, "");

            // Update the combo box
            categoryComboBox.getItems().clear();
            categoryComboBox.getItems().addAll(categories);
        } catch (Exception e) {
            System.err.println("Error loading categories: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Error loading categories: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Load the conditions
     */
    private void loadConditions() {
        try {
            // Get all conditions
            List<String> conditions = equipmentService.getAllConditions();

            // Add an empty option
            conditions.add(0, "");

            // Update the combo box
            conditionComboBox.getItems().clear();
            conditionComboBox.getItems().addAll(conditions);
        } catch (Exception e) {
            System.err.println("Error loading conditions: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Error loading conditions: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Show equipment details
     *
     * @param equipment The equipment to show details for
     */
    private void showEquipmentDetails(Equipment equipment) {
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
            grid.add(new Label("Name:"), 0, 0);
            grid.add(new Label(equipment.getName()), 1, 0);

            grid.add(new Label("Description:"), 0, 1);
            grid.add(new Label(equipment.getDescription()), 1, 1);

            grid.add(new Label("Category:"), 0, 2);
            grid.add(new Label(equipment.getCategory()), 1, 2);

            grid.add(new Label("Condition:"), 0, 3);
            grid.add(new Label(equipment.getCondition()), 1, 3);

            grid.add(new Label("Manufacturer:"), 0, 4);
            grid.add(new Label(equipment.getManufacturer()), 1, 4);

            grid.add(new Label("Model:"), 0, 5);
            grid.add(new Label(equipment.getModel()), 1, 5);

            grid.add(new Label("Serial Number:"), 0, 6);
            grid.add(new Label(equipment.getSerialNumber()), 1, 6);

            grid.add(new Label("Location:"), 0, 7);
            grid.add(new Label(equipment.getLocation()), 1, 7);

            grid.add(new Label("Available:"), 0, 8);
            grid.add(new Label(equipment.isAvailable() ? "Yes" : "No"), 1, 8);

            // Add equipment images if available
            List<byte[]> images = equipmentService.getEquipmentImages(equipment.getId());
            if (!images.isEmpty()) {
                grid.add(new Label("Images:"), 0, 9);

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
                grid.add(imagesBox, 1, 9);
            }

            // Set the content
            dialog.getDialogPane().setContent(grid);

            // Add a close button
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

            // Show the dialog
            dialog.showAndWait();
        } catch (Exception e) {
            System.err.println("Error showing equipment details: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Error showing equipment details: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Show an alert
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
     * Initialize auto-complete functionality for equipment search field
     */
    private void initializeAutoComplete() {
        try {
            // Get all equipment names for auto-complete
            List<Equipment> allEquipment = equipmentService.getAllEquipment();
            searchEquipmentSuggestions = FXCollections.observableArrayList(
                    allEquipment.stream()
                            .map(Equipment::getName)
                            .distinct()
                            .collect(Collectors.toList())
            );
    
            // Create auto-complete popup with an empty list initially
            ListView<String> suggestionList = new ListView<>();
            javafx.stage.Popup popup = new javafx.stage.Popup();
            popup.getContent().add(suggestionList);
    
            // Set up listener for text changes
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
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
                if (!filteredList.isEmpty() && suggestionList.getSelectionModel().getSelectedIndex() < 0) {
                    suggestionList.getSelectionModel().select(0);
                }
    
                // Show popup if there are suggestions
                if (!filteredList.isEmpty() && searchField.isFocused()) {
                    javafx.geometry.Bounds bounds = searchField.localToScreen(searchField.getBoundsInLocal());
                    if (bounds != null) {
                        popup.show(searchField, bounds.getMinX(), bounds.getMaxY());
                        suggestionList.setPrefWidth(searchField.getWidth());
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
                    searchField.setText(selectedItem);
                    popup.hide();
                }
            });
            
            // Handle keyboard navigation in the suggestion list
            suggestionList.setOnKeyPressed(event -> {
                switch (event.getCode()) {
                    case ENTER:
                        String selectedItem = suggestionList.getSelectionModel().getSelectedItem();
                        if (selectedItem != null) {
                            searchField.setText(selectedItem);
                            popup.hide();
                            // Auto-trigger search when an item is selected with Enter
                            searchButton.fire();
                            event.consume();
                        }
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
            searchField.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue) {
                    popup.hide();
                }
            });
        } catch (Exception e) {
            System.err.println("Error initializing auto-complete: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Error initializing auto-complete functionality: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
