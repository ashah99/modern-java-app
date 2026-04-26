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
        TableColumn<Contact, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setMinWidth(150);

        TableColumn<Contact, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setMinWidth(200);

        table.getColumns().addAll(nameCol, emailCol);

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
            if (!nameInput.getText().isEmpty()) {
                Contact c = new Contact(nameInput.getText(), emailInput.getText());
                repository.save(c);
                contactData.add(c);
                nameInput.clear();
                emailInput.clear();
            }
        });

        Button deleteBtn = new Button("Delete");
        deleteBtn.setOnAction(e -> {
            Contact selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                repository.delete(selected);
                contactData.remove(selected);
            }
        });

        Button exitBtn = new Button("Exit");
        exitBtn.setOnAction(e -> javafx.application.Platform.exit());

        // --- UI LAYOUT ---
        HBox searchBox = new HBox(10, new Label("Search:"), searchField);
        HBox inputBox = new HBox(10, nameInput, emailInput);
        HBox actionBox = new HBox(10, saveBtn, deleteBtn, exitBtn);

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