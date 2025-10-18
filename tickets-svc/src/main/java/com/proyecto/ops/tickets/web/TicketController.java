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

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import com.proyecto.ops.tickets.model.TicketStatus;

import com.proyecto.ops.tickets.clients.CustomersClient;
import com.proyecto.ops.tickets.clients.ContactsClient;
import com.proyecto.ops.tickets.model.Ticket;
import com.proyecto.ops.tickets.repo.TicketRepository;
import org.springframework.web.client.ResourceAccessException;

@RestController
@RequestMapping("/tickets")
@Validated
public class TicketController {

    private final TicketRepository repo;
    private final CustomersClient customersClient;
    private final ContactsClient contactsClient;

    public TicketController(TicketRepository repo,
            CustomersClient customersClient,
            ContactsClient contactsClient) {
        this.repo = repo;
        this.customersClient = customersClient;
        this.contactsClient = contactsClient;
    }

    @GetMapping
    public Page<TicketResponse> list(
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) TicketPriority priority,
            @RequestParam(required = false) UUID customerId,
            Pageable pageable
    ) {
        return repo.search(status, priority, customerId, pageable).map(this::toResponse);
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

        if (req.status() != null) {
            t.setStatus(req.status());       // <-- sin valueOf

                }if (req.priority() != null) {
            t.setPriority(req.priority());   // <-- sin valueOf
        }
        t.setCustomerId(req.customerId());
        t.setSiteId(req.siteId());           // si lo tienes en la entidad
        t.setAssetId(req.assetId());
        t.setRequestedBy(req.requestedBy()); // <-- importante
        t.setCreatedBy(req.createdBy());

        if (t.getCustomerId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CustomerId es requerido");
        }

        try {
            if (!customersClient.exists(t.getCustomerId())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "CustomerId inválido o no existe en customers-svc"
                );
            }
        } catch (ResourceAccessException e) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE, "customers-svc no disponible", e
            );
        }

        Ticket saved = repo.save(t);
        return ResponseEntity.created(URI.create("/tickets/" + saved.getId()))
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
            ) {

    }

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
        String customerName = customersClient.getNameOrUnknown(t.getCustomerId());
        String requestedByName = (t.getRequestedBy() != null)
                ? contactsClient.getNameOrUnknown(t.getRequestedBy())
                : null;

        return new TicketResponse(
                t.getId(),
                t.getTitle(),
                t.getDescription(),
                t.getStatus().name(),
                t.getPriority().name(),
                t.getCustomerId(),
                customerName,
                t.getSiteId(),
                t.getAssetId(),
                t.getRequestedBy(),
                requestedByName,
                t.getCreatedBy(),
                t.getCreatedAt()
        );
    }
}
