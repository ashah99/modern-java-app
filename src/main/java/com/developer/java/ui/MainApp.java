// New Code
package com.developer.java.ui;

import com.developer.java.model.Contact;
import com.developer.java.repository.ContactRepository;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {

    private ContactRepository repository;

    // 1. DATA LISTS
    private final ObservableList<Contact> contactData = FXCollections.observableArrayList();
    private FilteredList<Contact> filteredData; // The "View" that handles searching

    // 2. UI COMPONENTS
    private TableView<Contact> table = new TableView<>();
    private TextField searchField = new TextField();
    private TextField nameInput = new TextField();
    private TextField emailInput = new TextField();
    private ChoiceBox<String> categoryInput = new ChoiceBox<>();

    public void setRepository(ContactRepository repository) {
        this.repository = repository;
        refreshGrid();
    }

    public void refreshGrid() {
        if (repository != null) {
            contactData.setAll(repository.findAll());
        }
    }

    @Override
    public void start(Stage primaryStage) {
// --- TABLE SETUP ---
// --- UPDATED TABLE COLUMNS ---
        TableColumn<Contact, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setMinWidth(120);

        TableColumn<Contact, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setMinWidth(180);

// NEW: Category Column
        TableColumn<Contact, String> catCol = new TableColumn<>("Category");
        catCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        catCol.setMinWidth(120);

        categoryInput.getItems().addAll("Work", "Family", "Friends", "Table Tennis", "Other");
        categoryInput.setValue("Other"); // Default value

// Add all three to the table
        table.getColumns().setAll(nameCol, emailCol, catCol);

        // --- SEARCH LOGIC (The FilteredList) ---
        // Wrap contactData in filteredData. The 'p -> true' means show all by default.
        filteredData = new FilteredList<>(contactData, p -> true);

        searchField.setPromptText("Search by name...");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(contact -> {
                if (newValue == null || newValue.isEmpty()) return true;

                String lowerCaseFilter = newValue.toLowerCase();
                return contact.getName().toLowerCase().contains(lowerCaseFilter);
            });
        });

        // Set the table to use the Filtered List
        table.setItems(filteredData);

        // --- BUTTONS & ACTIONS ---
        Button saveBtn = new Button("Add");
        saveBtn.setOnAction(e -> {
            String name = nameInput.getText().trim();
            String email = emailInput.getText().trim();
            String category = categoryInput.getValue();

            // Safety Logic: Check for empty fields
            if (name.isEmpty() || email.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Validation Error");
                alert.setContentText("Name and Email cannot be empty!");
                alert.showAndWait();
                return;
            }

            // Safety Logic: Basic Email Validation
            if (!email.contains("@")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Please enter a valid email address.");
                alert.showAndWait();
                return;
            }

            // Create and Save
            Contact c = new Contact(name, email);
            c.setCategory(category); // Set the new category

            repository.save(c);
            contactData.add(c);

            // Clear inputs
            nameInput.clear();
            emailInput.clear();
            categoryInput.setValue("Other");
        });

        Button deleteBtn = new Button("Delete");
        deleteBtn.setOnAction(e -> {
            Contact selected = table.getSelectionModel().getSelectedItem();

            if (selected != null) {
                // Create the Alert
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Delete Confirmation");
                alert.setHeaderText("Removing: " + selected.getName());
                alert.setContentText("Are you sure you want to delete this contact?");

                // Show the dialog and wait for the user to click a button
                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        repository.delete(selected);
                        contactData.remove(selected);
                    }
                });
            } else {
                // Warning if nothing is selected
                Alert warn = new Alert(Alert.AlertType.WARNING);
                warn.setTitle("No Selection");
                warn.setContentText("Please select a contact from the table first.");
                warn.showAndWait();
            }
        });

        Button exitBtn = new Button("Exit");
        exitBtn.setOnAction(e -> javafx.application.Platform.exit());

        // --- UI LAYOUT ---
        HBox searchBox = new HBox(10, new Label("Search:"), searchField);
       // HBox inputBox = new HBox(10, nameInput, emailInput);
        HBox actionBox = new HBox(10, saveBtn, deleteBtn, exitBtn);
        HBox inputBox = new HBox(10,
                new Label("Name:"), nameInput,
                new Label("Email:"), emailInput,
                new Label("Category:"), categoryInput
        );
        inputBox.setPadding(new Insets(10, 0, 10, 0));
        VBox mainLayout = new VBox(15);
        mainLayout.setPadding(new Insets(20));
        mainLayout.getChildren().addAll(
                new Label("Contact Management System"),
                searchBox,
                table,
                new Label("Manage Contacts:"),
                inputBox,
                actionBox
        );

        primaryStage.setTitle("Contact Manager v1.0");
        primaryStage.setScene(new Scene(mainLayout, 550, 600));
        primaryStage.show();
    }
}