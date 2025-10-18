package com.proyecto.ops.contacts.repo;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.proyecto.ops.contacts.model.Contact;

public interface ContactRepository extends JpaRepository<Contact, UUID> {
    Page<Contact> findByCustomerId(UUID customerId, Pageable pageable);
}