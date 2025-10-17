package com.proyecto.ops.tickets.web;

import java.net.URI;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.proyecto.ops.tickets.model.Ticket;
import com.proyecto.ops.tickets.model.TicketPriority;
import com.proyecto.ops.tickets.repo.TicketRepository;
import com.proyecto.ops.tickets.web.CreateTicketRequest;
import com.proyecto.ops.tickets.web.TicketResponse;
import com.proyecto.ops.tickets.web.UpdateTicketStatusRequest;

import com.proyecto.ops.tickets.clients.CustomersClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/tickets")
@Validated
public class TicketController {

    private final TicketRepository repo;
    private final CustomersClient customersClient;

    public TicketController(TicketRepository repo, CustomersClient customersClient) {
        this.repo = repo;
        this.customersClient = customersClient;
    }

    @GetMapping
    public Page<TicketResponse> list(Pageable pageable) {
        return repo.findAll(pageable).map(this::toResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> get(@PathVariable UUID id) {
        return repo.findById(id)
            .map(t -> ResponseEntity.ok(toResponse(t)))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TicketResponse> create(@Valid @RequestBody CreateTicketRequest req) {
        Ticket t = new Ticket();
        t.setTitle(req.title());
        t.setDescription(req.description());
        t.setPriority(req.priority()); // si viene null, @PrePersist pone MEDIUM
        t.setCustomerId(req.customerId());
        t.setAssetId(req.assetId());
        t.setCreatedBy(req.createdBy());
        // Validate that customer exists in customers-svc
        if (t.getCustomerId() == null || !customersClient.exists(t.getCustomerId())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "CustomerId inválido o no existe en customers-svc"
            );
        }

        Ticket saved = repo.save(t);
        return ResponseEntity
            .created(URI.create("/tickets/" + saved.getId()))
            .body(toResponse(saved));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TicketResponse> updateStatus(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateTicketStatusRequest req
    ) {
        return repo.findById(id)
            .map(t -> {
                t.setStatus(req.status());
                Ticket updated = repo.save(t);
                return ResponseEntity.ok(toResponse(updated));
            })
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ---- Update Priority DTO (local to controller to avoid creating a new file) ----
    public static record UpdatePriorityRequest(
            @jakarta.validation.constraints.NotNull TicketPriority priority
    ) {}

    @PatchMapping("/{id}/priority")
    public ResponseEntity<TicketResponse> updatePriority(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePriorityRequest req
    ) {
        return repo.findById(id)
                .map(t -> {
                    t.setPriority(req.priority());
                    Ticket updated = repo.save(t);
                    return ResponseEntity.ok(toResponse(updated));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private TicketResponse toResponse(Ticket t) {
        return new TicketResponse(
            t.getId(),
            t.getTitle(),
            t.getDescription(),
            t.getStatus(),
            t.getPriority(),
            t.getCustomerId(),
            t.getAssetId(),
            t.getCreatedBy(),
            t.getCreatedAt()
        );
    }
}