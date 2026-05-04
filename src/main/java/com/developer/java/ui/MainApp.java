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
    // Add this field at the top of your class
    private Contact selectedContact = null;
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

        searchField.setPromptText("Search by name and category...");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(contact -> {
                if (newValue == null || newValue.isEmpty()) return true;

                String filter = newValue.toLowerCase();

                // Search in BOTH Name and Category
                boolean matchesName = contact.getName().toLowerCase().contains(filter);
                boolean matchesCategory = (contact.getCategory() != null) &&
                        contact.getCategory().toLowerCase().contains(filter);

                return matchesName || matchesCategory;
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

            if (name.isEmpty() || email.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Name and Email are required!").showAndWait();
                return;
            }

            // Safety Logic: Basic Email Validation
            if (!email.contains("@")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Please enter a valid email address.");
                alert.showAndWait();
                return;
            }

            if (selectedContact != null) {
                // --- UPDATE EXISTING ---
                selectedContact.setName(name);
                selectedContact.setEmail(email);
                selectedContact.setCategory(category);

                repository.save(selectedContact); // Hibernate updates the record in H2
                table.refresh();                  // Refresh the UI display
                selectedContact = null;           // Clear the selection
                saveBtn.setText("Add");           // Reset button text
            } else {
                // --- ADD NEW ---
                Contact c = new Contact(name, email);
                c.setCategory(category);
                repository.save(c);
                contactData.add(c);
            }

            // Clear the fields
            nameInput.clear();
            emailInput.clear();
            categoryInput.setValue("Other");
        });

        Button clearBtn = new Button("Clear");
        clearBtn.setOnAction(e -> {
            selectedContact = null;
            nameInput.clear();
            emailInput.clear();
            categoryInput.setValue("Other");
            saveBtn.setText("Add");
            table.getSelectionModel().clearSelection();
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

        Button refreshBtn = new Button("Refresh");
        // Use your existing refreshGrid method
        refreshBtn.setOnAction(e -> {
            refreshGrid();

            // Optional: Add a small status notification in the console
            // or a temporary label so the user knows it happened.
            System.out.println("Data manually refreshed from database.");
        });

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedContact = newSelection;
                nameInput.setText(selectedContact.getName());
                emailInput.setText(selectedContact.getEmail());
                categoryInput.setValue(selectedContact.getCategory());

                // Change button text to show we are in "Edit Mode"
                saveBtn.setText("Update");
            }
        });

        Button exitBtn = new Button("Exit");
        exitBtn.setOnAction(e -> javafx.application.Platform.exit());

        // --- UI LAYOUT ---
        HBox searchBox = new HBox(10, new Label("Search:"), searchField);
       // HBox inputBox = new HBox(10, nameInput, emailInput);
       // Add it to your actionBox
        HBox actionBox = new HBox(10, saveBtn, deleteBtn, refreshBtn, clearBtn, exitBtn);
        HBox inputBox = new HBox(10,
                new Label("Name:"), nameInput,
                new Label("Email:"), emailInput,
                new Label("Category:"), categoryInput
        );
        // Updated actionBox with the Refresh button
        actionBox.setPadding(new Insets(10, 0, 10, 0));
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