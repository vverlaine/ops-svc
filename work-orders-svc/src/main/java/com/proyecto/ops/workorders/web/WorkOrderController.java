package com.proyecto.ops.workorders.web;

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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.proyecto.ops.workorders.clients.TicketsClient;
import com.proyecto.ops.workorders.model.WoStatus;
import com.proyecto.ops.workorders.model.WorkOrder;
import com.proyecto.ops.workorders.repo.WorkOrderRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/work-orders")
public class WorkOrderController {

    private final WorkOrderRepository repo;
    private final TicketsClient ticketsClient;

    public WorkOrderController(WorkOrderRepository repo, TicketsClient ticketsClient) {
        this.repo = repo;
        this.ticketsClient = ticketsClient;
    }

    @PostMapping
    public ResponseEntity<WorkOrderResponse> create(@Valid @RequestBody CreateWorkOrderRequest req) {
        // ticket requerido + validación contra tickets-svc
        try {
            if (!ticketsClient.exists(req.ticketId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ticketId inválido o no existe en tickets-svc");
            }
        } catch (org.springframework.web.client.ResourceAccessException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "tickets-svc no disponible", e);
        }

        WorkOrder w = new WorkOrder();
        w.setTicketId(req.ticketId());
        w.setTechnicianId(req.technicianId());
        w.setScheduledAt(req.scheduledAt());
        w.setNotes(req.notes());
        w.setStatus(WoStatus.PENDING);

        WorkOrder saved = repo.save(w);
        return ResponseEntity.created(URI.create("/work-orders/" + saved.getId()))
                .body(toResponse(saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkOrderResponse> getOne(@PathVariable UUID id) {
        return repo.findById(id)
                .map(w -> ResponseEntity.ok(toResponse(w)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public Page<WorkOrderResponse> list(
            @RequestParam(required = false) UUID ticketId,
            @RequestParam(required = false) WoStatus status,
            Pageable pageable
    ) {
        Page<WorkOrder> page = repo.search(ticketId, status, pageable);
        return page.map(this::toResponse);
    }

    @PatchMapping("/{id}/assign")
    public ResponseEntity<WorkOrderResponse> assign(@PathVariable UUID id, @Valid @RequestBody AssignRequest req) {
        return repo.findById(id).map(w -> {
            w.setTechnicianId(req.technicianId());
            w.setStatus(WoStatus.ASSIGNED);
            WorkOrder saved = repo.save(w);
            return ResponseEntity.ok(toResponse(saved));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<WorkOrderResponse> updateStatus(@PathVariable UUID id, @Valid @RequestBody UpdateStatusRequest req) {
        return repo.findById(id).map(w -> {
            WoStatus newStatus = req.status();
            if (newStatus == WoStatus.STARTED && w.getStartedAt() == null) {
                w.setStartedAt(OffsetDateTime.now());
            }
            if (newStatus == WoStatus.DONE && w.getEndedAt() == null) {
                w.setEndedAt(OffsetDateTime.now());
            }
            w.setStatus(newStatus);
            if (req.notes() != null && !req.notes().isBlank()) {
                w.setNotes(req.notes());
            }
            WorkOrder saved = repo.save(w);
            return ResponseEntity.ok(toResponse(saved));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        return repo.findById(id)
                .map(w -> {
                    repo.delete(w);
                    return ResponseEntity.noContent().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // --- errores comunes (ajusta mensajes si quieres) ---
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> integrity(DataIntegrityViolationException ex, HttpServletRequest req) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", OffsetDateTime.now().toString());
        body.put("status", HttpStatus.CONFLICT.value());
        body.put("error", "Conflict");
        body.put("message", "Violación de integridad");
        body.put("path", req.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    private WorkOrderResponse toResponse(WorkOrder w) {
        return new WorkOrderResponse(
                w.getId(),
                w.getTicketId(),
                w.getTechnicianId(),
                w.getStatus(),
                w.getScheduledAt(),
                w.getStartedAt(),
                w.getEndedAt(),
                w.getNotes(),
                w.getCreatedAt()
        );
    }
}
