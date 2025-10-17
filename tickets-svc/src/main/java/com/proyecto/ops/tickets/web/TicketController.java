package com.proyecto.ops.tickets.web;

import java.net.URI;      // <- model, no domain
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.ops.tickets.model.Ticket;
import com.proyecto.ops.tickets.repo.TicketRepository;
import com.proyecto.ops.tickets.model.TicketStatus;
import com.proyecto.ops.tickets.model.TicketPriority;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    private final TicketRepository repo;

    public TicketController(TicketRepository repo) {
        this.repo = repo;
    }

    @PostMapping
    public ResponseEntity<Ticket> create(@Valid @RequestBody CreateTicketRequest req) {
        var t = new Ticket();
        t.setTitle(req.title());
        t.setDescription(req.description());
        t.setStatus(req.status() == null ? TicketStatus.OPEN : TicketStatus.valueOf(req.status()));
        t.setPriority(req.priority() == null ? TicketPriority.MEDIUM : TicketPriority.valueOf(req.priority()));
        t.setCustomerId(req.customerId());
        t.setAssetId(req.assetId());
        t.setCreatedBy(req.createdBy());

        var saved = repo.save(t);
        return ResponseEntity.created(URI.create("/tickets/" + saved.getId())).body(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> get(@PathVariable UUID id) {
        return repo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public Page<Ticket> list(@RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size) {
        return repo.findAll(PageRequest.of(page, size));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Ticket> updateStatus(@PathVariable UUID id,
                                               @RequestBody CreateTicketRequest req) {
        return repo.findById(id)
                .map(t -> {
                    if (req.status() != null) {
                        t.setStatus(TicketStatus.valueOf(req.status()));
                    }
                    return ResponseEntity.ok(repo.save(t));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}