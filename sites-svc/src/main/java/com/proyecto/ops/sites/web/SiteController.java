package com.proyecto.ops.sites.web;

import java.net.URI;
import java.time.OffsetDateTime;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.ops.sites.model.CustomerSite;
import com.proyecto.ops.sites.repo.CustomerSiteRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/sites")
public class SiteController {

    private final CustomerSiteRepository repo;

    public SiteController(CustomerSiteRepository repo) {
        this.repo = repo;
    }

    @PostMapping
    public ResponseEntity<SiteResponse> create(@Valid @RequestBody CreateSiteRequest req) {
        CustomerSite s = new CustomerSite();
        s.setCustomerId(req.customerId());
        s.setName(req.name());
        s.setAddress(req.address());
        s.setCity(req.city());
        s.setState(req.state());
        s.setCountry(req.country());

        CustomerSite saved = repo.save(s);
        return ResponseEntity.created(URI.create("/sites/" + saved.getId()))
                .body(toResponse(saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SiteResponse> getOne(@PathVariable UUID id) {
        return repo.findById(id)
                .map(s -> ResponseEntity.ok(toResponse(s)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public Page<SiteResponse> list(@RequestParam(required = false) UUID customerId,
                                   Pageable pageable) {
        Page<CustomerSite> page = (customerId == null)
                ? repo.findAll(pageable)
                : repo.findByCustomerId(customerId, pageable);

        return page.map(this::toResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SiteResponse> update(@PathVariable UUID id,
                                               @Valid @RequestBody UpdateSiteRequest req) {
        return repo.findById(id).map(s -> {
            s.setName(req.name());
            s.setAddress(req.address());
            s.setCity(req.city());
            s.setState(req.state());
            s.setCountry(req.country());
            CustomerSite saved = repo.save(s);
            return ResponseEntity.ok(toResponse(saved));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        return repo.findById(id).map(s -> {
            repo.delete(s);
            return ResponseEntity.noContent().<Void>build();
        }).orElseGet(() -> ResponseEntity.notFound().<Void>build());
    }

    // FK inválida (customer_id) => 409
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleIntegrity(DataIntegrityViolationException ex,
                                                               HttpServletRequest req) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", OffsetDateTime.now().toString());
        body.put("status", HttpStatus.CONFLICT.value());
        body.put("error", "Conflict");
        body.put("message", "FK inválida (customer_id) o conflicto de integridad");
        body.put("path", req.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    private SiteResponse toResponse(CustomerSite s) {
        return new SiteResponse(
                s.getId(),
                s.getCustomerId(),
                s.getName(),
                s.getAddress(),
                s.getCity(),
                s.getState(),
                s.getCountry(),
                s.getCreatedAt()
        );
    }
}