package com.proyecto.ops.contacts.web;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.ops.contacts.clients.CustomersClient;
import com.proyecto.ops.contacts.model.Contact;
import com.proyecto.ops.contacts.repo.ContactRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/contacts")
public class ContactController {

    private final ContactRepository repo;
    private final CustomersClient customersClient;

    public ContactController(ContactRepository repo, CustomersClient customersClient) {
        this.repo = repo;
        this.customersClient = customersClient;
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
        return ResponseEntity.created(URI.create("/contacts/" + saved.getId()))
                .body(toResponse(saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactResponse> getOne(@PathVariable UUID id) {
        return repo.findById(id)
                .map(c -> ResponseEntity.ok(toResponse(c)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public Page<ContactResponse> list(@RequestParam(required = false) UUID customerId,
            Pageable pageable) {
        Page<Contact> page = (customerId == null)
                ? repo.findAll(pageable)
                : repo.findByCustomerId(customerId, pageable);
        return page.map(this::toResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        return repo.findById(id)
                .map(c -> {
                    repo.delete(c);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.notFound().<Void>build());
    }

    private ContactResponse toResponse(Contact c) {
        String customerName = customersClient.getNameOrUnknown(c.getCustomerId());
        return new ContactResponse(
                c.getId(),
                c.getCustomerId(),
                customerName,
                c.getName(),
                c.getEmail(),
                c.getPhone(),
                c.getRole(),
                c.getCreatedAt()
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleIntegrity(DataIntegrityViolationException ex,
            HttpServletRequest req) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", HttpStatus.CONFLICT.value());
        body.put("error", "Conflict");
        body.put("message", "Violación de restricción");
        body.put("path", req.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }
}
