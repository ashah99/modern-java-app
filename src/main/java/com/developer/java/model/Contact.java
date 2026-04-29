package com.developer.java.model;

import jakarta.persistence.*;

@Entity
@Table(name = "contacts") // Name of the table in the DB
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String category; // <-- New Field
    // Standard no-arg constructor (required by JPA)
    public Contact() {}

    // Convenience constructor
    public Contact(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}