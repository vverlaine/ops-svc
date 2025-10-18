package com.proyecto.ops.contacts.web;

import java.net.URI;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.ops.contacts.model.Contact;
import com.proyecto.ops.contacts.repo.ContactRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/contacts")
public class ContactController {

    private final ContactRepository repo;

    public ContactController(ContactRepository repo) {
        this.repo = repo;
    }

    @PostMapping
    public ResponseEntity<ContactResponse> create(@Valid @RequestBody CreateContactRequest req) {
        Contact c = new Contact();
        c.setCustomerId(req.customerId());
        c.setName(req.name());
        c.setEmail(req.email());
        c.setPhone(req.phone());
        c.setRole(req.role());

        Contact saved = repo.save(c);
        return ResponseEntity
                .created(URI.create("/contacts/" + saved.getId()))
                .body(toResponse(saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactResponse> getOne(@PathVariable UUID id) {
        return repo.findById(id)
                .map(c -> ResponseEntity.ok(toResponse(c)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public Page<ContactResponse> list(
            @RequestParam(required = false) UUID customerId,
            Pageable pageable
    ) {
        Page<Contact> page = (customerId == null)
                ? repo.findAll(pageable)
                : repo.findByCustomerId(customerId, pageable);

        return page.map(this::toResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContactResponse> update(@PathVariable UUID id,
                                                  @Valid @RequestBody UpdateContactRequest req) {
        return repo.findById(id).map(c -> {
            c.setName(req.name());
            c.setEmail(req.email());
            c.setPhone(req.phone());
            c.setRole(req.role());
            Contact saved = repo.save(c);
            return ResponseEntity.ok(toResponse(saved));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        return repo.findById(id).map(c -> {
            repo.delete(c);
            return ResponseEntity.noContent().<Void>build();
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    private ContactResponse toResponse(Contact c) {
        return new ContactResponse(
                c.getId(),
                c.getCustomerId(),
                c.getName(),
                c.getEmail(),
                c.getPhone(),
                c.getRole(),
                c.getCreatedAt()
        );
    }
}