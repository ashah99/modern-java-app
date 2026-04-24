package com.developer.java.repository;

import com.developer.java.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    // Spring automatically provides methods like .save(), .findAll(), .deleteById()
}
