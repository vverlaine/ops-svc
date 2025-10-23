package com.proyecto.ops.customers.web;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.ops.customers.model.CustomerBasic;
import com.proyecto.ops.customers.repo.CustomerJdbcRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerJdbcRepository repo;

    public CustomerController(CustomerJdbcRepository repo) {
        this.repo = repo;
    }

    private Map<String, Object> toPage(List<CustomerBasic> content, int page, int size, long total) {
        int safeSize = Math.max(size, 1);
        int totalPages = (int) Math.ceil((double) total / safeSize);

        Map<String, Object> pageable = new LinkedHashMap<>();
        pageable.put("pageNumber", page);
        pageable.put("pageSize", size);
        pageable.put("sort", Map.of("sorted", false, "unsorted", true, "empty", true));
        pageable.put("offset", page * size);
        pageable.put("paged", true);
        pageable.put("unpaged", false);

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("content", content);
        out.put("pageable", pageable);
        out.put("totalPages", totalPages);
        out.put("totalElements", total);
        out.put("last", page >= totalPages - 1);
        out.put("first", page == 0);
        out.put("numberOfElements", content.size());
        out.put("size", size);
        out.put("number", page);
        out.put("sort", Map.of("sorted", false, "unsorted", true, "empty", true));
        out.put("empty", content.isEmpty());
        return out;
    }

    @GetMapping
    public ResponseEntity<?> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        List<CustomerBasic> data = repo.list(page, size);
        long total = repo.countAll();
        return ResponseEntity.ok(toPage(data, page, size, total));
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        List<CustomerBasic> data = repo.search(q, page, size);
        long total = repo.countSearch(q);
        return ResponseEntity.ok(toPage(data, page, size, total));
    }

    @PostMapping
    public ResponseEntity<?> create(
            @Valid @RequestBody CreateCustomerRequest req
    ) {
        CustomerBasic saved = repo.create(
                req.name(),
                req.taxId(),
                req.email(),
                req.phone(),
                req.address()
        );
        return ResponseEntity.ok(saved);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable UUID id,
            @RequestBody CreateCustomerRequest req
    ) {
        return repo.updatePartial(
                id,
                req.name(),
                req.taxId(),
                req.email(),
                req.phone(),
                req.address()
        )
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable UUID id
    ) {
        boolean deleted = repo.delete(id);
        return deleted ? ResponseEntity.noContent().build()
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerBasic> getById(@PathVariable UUID id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

}
