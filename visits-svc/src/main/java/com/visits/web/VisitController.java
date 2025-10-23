package com.visits.web;

import com.visits.model.*;
import com.visits.service.VisitService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/visits")
/**
 * Controlador REST que gestiona las operaciones relacionadas con visitas
 * técnicas.
 *
 * Proporciona endpoints para crear, actualizar, iniciar, finalizar y documentar
 * visitas.
 */
public class VisitController {

    private final VisitService service;

    public VisitController(VisitService service) {
        this.service = service;
    }

    @PostMapping
    public Visit create(@RequestBody CreateVisitRequest req) {
        // Delegación al servicio para crear una nueva visita en estado PLANNED.
        return service.createPlanned(
                req.customerId, req.siteId, req.technicianId,
                req.scheduledStartAt, req.scheduledEndAt,
                req.priority, req.purpose, req.notesPlanned
        );
    }

    @PatchMapping("/{id}")
    public Visit update(@PathVariable UUID id, @RequestBody UpdateVisitRequest req) {
        // Delegación al servicio para actualizar una visita que aún no ha iniciado.
        return service.updatePlanned(
                id, req.scheduledStartAt, req.scheduledEndAt,
                req.technicianId, req.priority, req.purpose, req.notesPlanned, req.state
        );
    }

    @PostMapping("/{id}/check-in")
    public Visit checkIn(@PathVariable UUID id, @RequestBody CheckRequest req) {

        OffsetDateTime checkInAt = req.when != null ? req.when : OffsetDateTime.now(ZoneOffset.UTC);
        return service.checkIn(id, req.actorId, checkInAt, req.lat, req.lng);
    }

    @PostMapping("/{id}/check-out")
    public Visit checkOut(@PathVariable UUID id, @RequestBody CheckOutRequest req) {

        OffsetDateTime checkOutAt = req.when != null ? req.when : OffsetDateTime.now(ZoneOffset.UTC);
        return service.checkOut(id, req.actorId, checkOutAt, req.lat, req.lng, req.workSummary);
    }

    @PostMapping("/{id}/cancel")
    public void cancel(@PathVariable UUID id, @RequestParam UUID actorId) {
        // Llama al servicio para cambiar el estado de la visita a CANCELLED.
        service.cancel(id, actorId);
    }

    @GetMapping
    public Page<Visit> list(@RequestParam(required = false) UUID customerId,
            @RequestParam(required = false) UUID technicianId,
            @RequestParam(required = false) VisitState state,
            @RequestParam(required = false) OffsetDateTime from,
            @RequestParam(required = false) OffsetDateTime to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        // Solicita al servicio las visitas filtradas y paginadas según los parámetros.
        return service.list(customerId, technicianId, state, from, to, PageRequest.of(page, size));
    }

    @GetMapping("/me/today")
    public List<Visit> myToday(@RequestParam UUID technicianId,
            @RequestParam(required = false) String dateIso) {
        // Si no se envía fecha, usa la fecha actual del sistema.
        LocalDate day = (dateIso == null || dateIso.isBlank()) ? LocalDate.now() : LocalDate.parse(dateIso);
        return service.myVisitsToday(technicianId, day);
    }

    @GetMapping("/{id}/events")
    public List<VisitEvent> events(@PathVariable UUID id) {
        // Obtiene del servicio los eventos asociados a la visita solicitada.
        return service.events(id);
    }

    @PostMapping("/{id}/notes")
    public List<VisitNote> addNote(@PathVariable UUID id, @RequestBody AddNoteRequest req) {
        // Delegación al servicio para registrar una nueva nota asociada a la visita.
        return service.addNote(id, req.authorId, req.visibility, req.body);
    }

    @GetMapping("/{id}")
    public Visit getById(@PathVariable UUID id) {
        return service.getById(id);
    }

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
        public VisitState state;
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
