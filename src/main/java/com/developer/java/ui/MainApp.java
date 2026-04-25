package com.developer.java.ui;

import com.developer.java.model.Contact;
import com.developer.java.repository.ContactRepository;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Documentation:
 * This class handles the UI (Presentation Layer).
 * It uses an ObservableList to keep the TableView in sync with the data.
 */
public class MainApp extends Application {

    // --- CLASS LEVEL FIELDS ---
    // This allows all methods in the class to access the database repository
    private ContactRepository repository;

    // This list acts as the "live" mirror for the TableView
    private final ObservableList<Contact> contactData = FXCollections.observableArrayList();

    /**
     * This setter is called by MainLauncher to "inject" the Spring Repository.
     */
    public void setRepository(ContactRepository repository) {
        this.repository = repository;
        refreshGrid(); // Initial load happens here
        // When the repository is set, we immediately load all data from the H2 DB
        if (repository != null) {
            contactData.setAll(repository.findAll());
        }
    }

    /**
     * Fetches the latest data from the H2 database and updates the UI Table.
     */
    public void refreshGrid() {
        if (repository != null) {
            // 1. Fetch all entries from Hibernate
            var latestContacts = repository.findAll();

            // 2. Clear and update the ObservableList (which updates the TableView)
            contactData.setAll(latestContacts);

            System.out.println("Grid refreshed: " + latestContacts.size() + " entries loaded.");
        }
    }

    @Override
    public void start(Stage primaryStage) {
        // 1. CREATE THE TABLE
        TableView<Contact> table = new TableView<>();

        // Name Column - Binds to getName() in Contact.java
        TableColumn<Contact, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setMinWidth(150);

        // Email Column - Binds to getEmail() in Contact.java
        TableColumn<Contact, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setMinWidth(200);

        table.getColumns().addAll(nameCol, emailCol);
        table.setItems(contactData); // Bind the list to the table

        // 2. INPUT FIELDS
        TextField nameInput = new TextField();
        nameInput.setPromptText("Enter Name");

        TextField emailInput = new TextField();
        emailInput.setPromptText("Enter Email");

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setOnAction(e -> refreshGrid());

        Button saveBtn = new Button("Save Contact");

        // --- BUTTON ACTION ---
        saveBtn.setOnAction(e -> {
            String name = nameInput.getText();
            String email = emailInput.getText();

            if (!name.isEmpty() && repository != null) {
                // Create a new Entity object
                Contact c = new Contact(name, email);

                // SAVE to Database via Hibernate
                repository.save(c);

                // UPDATE the UI Table immediately
                contactData.add(c);

                // Clear the fields for next entry
                nameInput.clear();
                emailInput.clear();
            }
        });

        // 3. LAYOUT & SCENE
        VBox layout = new VBox(15); // 15px spacing between elements
        layout.setPadding(new Insets(20)); // Padding around the edges
        layout.getChildren().addAll(new Label("Contact Directory"), table, nameInput, emailInput, saveBtn, refreshBtn );

        Scene scene = new Scene(layout, 450, 500);
        primaryStage.setTitle("Contact Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}