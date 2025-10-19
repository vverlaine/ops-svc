package com.visits.web;

import com.visits.model.*;
import com.visits.service.VisitService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/visits")
public class VisitController {

    private final VisitService service;

    public VisitController(VisitService service) {
        this.service = service;
    }

    // Crear visita planificada
    @PostMapping
    public Visit create(@RequestBody CreateVisitRequest req) {
        return service.createPlanned(
                req.customerId, req.siteId, req.technicianId,
                req.scheduledStartAt, req.scheduledEndAt,
                req.priority, req.purpose, req.notesPlanned
        );
    }

    // Editar visita planificada
    @PatchMapping("/{id}")
    public Visit update(@PathVariable UUID id, @RequestBody UpdateVisitRequest req) {
        return service.updatePlanned(
                id, req.scheduledStartAt, req.scheduledEndAt,
                req.technicianId, req.priority, req.purpose, req.notesPlanned
        );
    }

    // Check-in
    @PostMapping("/{id}/check-in")
    public Visit checkIn(@PathVariable UUID id, @RequestBody CheckRequest req) {
        return service.checkIn(id, req.actorId, req.when, req.lat, req.lng);
    }

    // Check-out
    @PostMapping("/{id}/check-out")
    public Visit checkOut(@PathVariable UUID id, @RequestBody CheckOutRequest req) {
        return service.checkOut(id, req.actorId, req.when, req.lat, req.lng, req.workSummary);
    }

    // Cancelar
    @PostMapping("/{id}/cancel")
    public void cancel(@PathVariable UUID id, @RequestParam UUID actorId) {
        service.cancel(id, actorId);
    }

    // Listar con filtros simples
    @GetMapping
    public Page<Visit> list(@RequestParam(required = false) UUID customerId,
                            @RequestParam(required = false) UUID technicianId,
                            @RequestParam(required = false) VisitState state,
                            @RequestParam(required = false) OffsetDateTime from,
                            @RequestParam(required = false) OffsetDateTime to,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "50") int size) {
        return service.list(customerId, technicianId, state, from, to, PageRequest.of(page, size));
    }

    // Mis visitas de hoy
    @GetMapping("/me/today")
    public List<Visit> myToday(@RequestParam UUID technicianId,
                               @RequestParam(required = false) String dateIso) {
        LocalDate day = (dateIso == null || dateIso.isBlank()) ? LocalDate.now() : LocalDate.parse(dateIso);
        return service.myVisitsToday(technicianId, day);
    }

    // Eventos de una visita
    @GetMapping("/{id}/events")
    public List<VisitEvent> events(@PathVariable UUID id) {
        return service.events(id);
    }

    // Agregar nota
    @PostMapping("/{id}/notes")
    public List<VisitNote> addNote(@PathVariable UUID id, @RequestBody AddNoteRequest req) {
        return service.addNote(id, req.authorId, req.visibility, req.body);
    }

    // ==== DTOs request simples ====
    public static class CreateVisitRequest {
        public UUID customerId;
        public UUID siteId;
        public UUID technicianId;
        public OffsetDateTime scheduledStartAt;
        public OffsetDateTime scheduledEndAt;
        public VisitPriority priority;
        public String purpose;
        public String notesPlanned;
    }

    public static class UpdateVisitRequest {
        public OffsetDateTime scheduledStartAt;
        public OffsetDateTime scheduledEndAt;
        public UUID technicianId;
        public VisitPriority priority;
        public String purpose;
        public String notesPlanned;
    }

    public static class CheckRequest {
        public UUID actorId;
        public OffsetDateTime when;
        public Double lat;
        public Double lng;
    }

    public static class CheckOutRequest extends CheckRequest {
        public String workSummary;
    }

    public static class AddNoteRequest {
        public UUID authorId;
        public NoteVisibility visibility;
        public String body;
    }
}