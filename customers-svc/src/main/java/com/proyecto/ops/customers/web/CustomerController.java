package com.proyecto.ops.customers.web;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.ops.customers.model.CustomerBasic;
import com.proyecto.ops.customers.repo.CustomerJdbcRepository;

import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerJdbcRepository repo;

    public CustomerController(CustomerJdbcRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public Map<String, Object> list(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @RequestParam(required = false) String q
    ) {
        List<CustomerBasic> content;
        int total;

        if (q != null && !q.isBlank()) {
            content = repo.searchByName(q, page, size);
            total = repo.countByName(q);
        } else {
            content = repo.findPage(page, size);
            total = repo.countAll();
        }

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("content", content);
        resp.put("page", page);
        resp.put("size", size);
        resp.put("totalElements", total);
        resp.put("totalPages", (int) Math.ceil((double) total / size));
        resp.put("q", q);
        return resp;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerBasic> getById(@PathVariable UUID id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    public record CreateCustomerRequest(
            @jakarta.validation.constraints.NotBlank String name
            ) {

    }

// Crear cliente
    @org.springframework.web.bind.annotation.PostMapping
    public org.springframework.http.ResponseEntity<CustomerBasic> create(
            @org.springframework.web.bind.annotation.RequestBody CreateCustomerRequest req
    ) {
        UUID id = repo.create(req.name());

        return org.springframework.http.ResponseEntity
                .created(java.net.URI.create("/customers/" + id))
                .body(new CustomerBasic(id, req.name()));
    }
}
