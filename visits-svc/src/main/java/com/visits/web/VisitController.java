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

/**
 * Controlador REST que expone los endpoints de gestión de visitas técnicas.
 *
 * Proporciona operaciones CRUD, controles de flujo (check-in/check-out),
 * manejo de cancelaciones y registro de notas o eventos asociados a una visita.
 */
@RestController
@RequestMapping("/visits")
public class VisitController {

    private final VisitService service;

    public VisitController(VisitService service) {
        this.service = service;
    }

    /**
     * Crea una nueva visita en estado planificado.
     *
     * @param req Cuerpo de la solicitud con los datos esenciales de la visita.
     * @return Visita recién creada.
     */
    @PostMapping
    public Visit create(@RequestBody CreateVisitRequest req) {
        return service.createPlanned(
                req.customerId, req.siteId, req.technicianId,
                req.scheduledStartAt, req.scheduledEndAt,
                req.priority, req.purpose, req.notesPlanned
        );
    }

    /**
     * Actualiza una visita planificada; permite mover horarios, asignar técnicos
     * o ajustar la prioridad antes de que inicie la ejecución.
     *
     * @param id  Identificador de la visita a modificar.
     * @param req Datos a actualizar.
     * @return Visita resultante tras aplicar los cambios.
     */
    @PatchMapping("/{id}")
    public Visit update(@PathVariable UUID id, @RequestBody UpdateVisitRequest req) {
        return service.updatePlanned(
                id, req.scheduledStartAt, req.scheduledEndAt,
                req.technicianId, req.priority, req.purpose, req.notesPlanned, req.state
        );
    }

    /**
     * Registra la llegada del técnico al sitio. Marca la visita como iniciada
     * y conserva la geolocalización opcional.
     *
     * @param id  Identificador de la visita.
     * @param req Información del actor y el tiempo de registro.
     * @return Visita actualizada tras el check-in.
     */
    @PostMapping("/{id}/check-in")
    public Visit checkIn(@PathVariable UUID id, @RequestBody CheckRequest req) {

        OffsetDateTime checkInAt = req.when != null ? req.when : OffsetDateTime.now(ZoneOffset.UTC);
        return service.checkIn(id, req.actorId, checkInAt, req.lat, req.lng);
    }

    /**
     * Registra la salida del técnico del sitio, marcando la visita como completada
     * y persistiendo el resumen de trabajo realizado.
     *
     * @param id  Identificador de la visita.
     * @param req Datos de check-out, incluyendo resumen de trabajo.
     * @return Visita actualizada tras el check-out.
     */
    @PostMapping("/{id}/check-out")
    public Visit checkOut(@PathVariable UUID id, @RequestBody CheckOutRequest req) {

        OffsetDateTime checkOutAt = req.when != null ? req.when : OffsetDateTime.now(ZoneOffset.UTC);
        return service.checkOut(id, req.actorId, checkOutAt, req.lat, req.lng, req.workSummary);
    }

    /**
     * Cancela una visita pendiente o en progreso, registrando al actor que solicitó
     * la cancelación.
     *
     * @param id      Identificador de la visita.
     * @param actorId Usuario que solicita la cancelación.
     */
    @PostMapping("/{id}/cancel")
    public void cancel(@PathVariable UUID id, @RequestParam UUID actorId) {
        service.cancel(id, actorId);
    }

    /**
     * Lista visitas filtradas y paginadas por cliente, técnico, estado o rango de fechas.
     *
     * @param customerId   Filtro opcional por cliente.
     * @param technicianId Filtro opcional por técnico.
     * @param state        Filtro opcional por estado.
     * @param from         Fecha de inicio del rango.
     * @param to           Fecha fin del rango.
     * @param page         Número de página (base 0).
     * @param size         Cantidad de registros por página.
     * @return Página de visitas que cumplen los filtros.
     */
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

    /**
     * Devuelve las visitas asignadas al técnico para un día específico, dando
     * prioridad al día actual cuando no se envía parámetro.
     *
     * @param technicianId Identificador del técnico.
     * @param dateIso      Fecha opcional en formato ISO (yyyy-MM-dd).
     * @return Visitas del técnico en la fecha solicitada.
     */
    @GetMapping("/me/today")
    public List<Visit> myToday(@RequestParam UUID technicianId,
            @RequestParam(required = false) String dateIso) {
        LocalDate day = (dateIso == null || dateIso.isBlank()) ? LocalDate.now() : LocalDate.parse(dateIso);
        return service.myVisitsToday(technicianId, day);
    }

    /**
     * Recupera el histórico de eventos registrados para una visita.
     *
     * @param id Identificador de la visita.
     * @return Lista de eventos ordenados cronológicamente.
     */
    @GetMapping("/{id}/events")
    public List<VisitEvent> events(@PathVariable UUID id) {
        return service.events(id);
    }

    /**
     * Agrega una nota a la visita, respetando nivel de visibilidad y autoría.
     *
     * @param id  Identificador de la visita.
     * @param req Contenido de la nota a registrar.
     * @return Lista actualizada de notas luego de añadir la nueva entrada.
     */
    @PostMapping("/{id}/notes")
    public List<VisitNote> addNote(@PathVariable UUID id, @RequestBody AddNoteRequest req) {
        return service.addNote(id, req.authorId, req.visibility, req.body);
    }

    /**
     * Obtiene una visita específica por su identificador.
     *
     * @param id Identificador de la visita buscada.
     * @return Visita encontrada.
     */
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
