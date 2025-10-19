/*
 * -----------------------------------------------------------------------------
 * VisitController.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Controlador REST principal del microservicio "visits-svc" encargado de exponer
 *   los endpoints para gestionar el ciclo de vida completo de las visitas técnicas.
 *
 * Contexto de uso:
 *   - Interactúa con el servicio {@link com.visits.service.VisitService} para realizar
 *     operaciones de creación, actualización, inicio, finalización, cancelación,
 *     registro de eventos y notas asociadas a visitas.
 *   - Es utilizado tanto por supervisores como por técnicos en campo.
 *
 * Diseño:
 *   - Anotado con @RestController y @RequestMapping("/visits").
 *   - Expone endpoints RESTful bajo los métodos HTTP: POST, PATCH y GET.
 *   - Utiliza clases DTO internas para recibir solicitudes JSON de los clientes.
 *
 * Endpoints principales:
 *   • POST /visits                → Crea una nueva visita planificada.
 *   • PATCH /visits/{id}          → Actualiza una visita existente.
 *   • POST /visits/{id}/check-in  → Marca el inicio de la visita (check-in).
 *   • POST /visits/{id}/check-out → Marca la finalización (check-out).
 *   • POST /visits/{id}/cancel    → Cancela una visita planificada.
 *   • GET  /visits                → Lista las visitas filtradas por criterios.
 *   • GET  /visits/me/today       → Lista las visitas asignadas al técnico actual.
 *   • GET  /visits/{id}/events    → Devuelve los eventos de una visita.
 *   • POST /visits/{id}/notes     → Agrega una nota asociada a una visita.
 *
 * Mantenibilidad:
 *   - Este controlador delega toda la lógica de negocio a la capa de servicio.
 *   - Los DTOs internos deben mantenerse sincronizados con las entidades del dominio.
 * -----------------------------------------------------------------------------
 */
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
/**
 * Controlador REST que gestiona las operaciones relacionadas con visitas técnicas.
 *
 * Proporciona endpoints para crear, actualizar, iniciar, finalizar y documentar visitas.
 */
public class VisitController {

    private final VisitService service;

    /**
     * Constructor con inyección del servicio principal de visitas.
     *
     * @param service Servicio de negocio que gestiona las operaciones de visitas.
     */
    public VisitController(VisitService service) {
        this.service = service;
    }

    // Crear visita planificada
    /**
     * Crea una nueva visita planificada a partir de los datos proporcionados en la solicitud.
     *
     * @param req Objeto con los datos necesarios para planificar la visita.
     * @return La visita creada.
     */
    @PostMapping
    public Visit create(@RequestBody CreateVisitRequest req) {
        // Delegación al servicio para crear una nueva visita en estado PLANNED.
        return service.createPlanned(
                req.customerId, req.siteId, req.technicianId,
                req.scheduledStartAt, req.scheduledEndAt,
                req.priority, req.purpose, req.notesPlanned
        );
    }

    // Editar visita planificada
    /**
     * Actualiza los datos de una visita planificada existente.
     *
     * @param id Identificador de la visita a actualizar.
     * @param req Datos actualizados de la visita.
     * @return La visita modificada.
     */
    @PatchMapping("/{id}")
    public Visit update(@PathVariable UUID id, @RequestBody UpdateVisitRequest req) {
        // Delegación al servicio para actualizar una visita que aún no ha iniciado.
        return service.updatePlanned(
                id, req.scheduledStartAt, req.scheduledEndAt,
                req.technicianId, req.priority, req.purpose, req.notesPlanned
        );
    }

    // Check-in
    /**
     * Marca el inicio real de una visita (check-in) y actualiza su estado a STARTED.
     *
     * @param id Identificador de la visita.
     * @param req Objeto con los datos de ubicación, técnico y momento del check-in.
     * @return La visita actualizada.
     */
    @PostMapping("/{id}/check-in")
    public Visit checkIn(@PathVariable UUID id, @RequestBody CheckRequest req) {
        // Delegación al servicio para registrar el evento de inicio de visita.
        return service.checkIn(id, req.actorId, req.when, req.lat, req.lng);
    }

    // Check-out
    /**
     * Marca la finalización real de una visita (check-out) con resumen del trabajo.
     *
     * @param id Identificador de la visita.
     * @param req Objeto con los datos de ubicación, técnico, hora y resumen.
     * @return La visita completada.
     */
    @PostMapping("/{id}/check-out")
    public Visit checkOut(@PathVariable UUID id, @RequestBody CheckOutRequest req) {
        // Delegación al servicio para registrar el evento de finalización de la visita.
        return service.checkOut(id, req.actorId, req.when, req.lat, req.lng, req.workSummary);
    }

    // Cancelar
    /**
     * Cancela una visita planificada antes de que inicie.
     *
     * @param id Identificador de la visita.
     * @param actorId Identificador del usuario que realiza la cancelación.
     */
    @PostMapping("/{id}/cancel")
    public void cancel(@PathVariable UUID id, @RequestParam UUID actorId) {
        // Llama al servicio para cambiar el estado de la visita a CANCELLED.
        service.cancel(id, actorId);
    }

    // Listar con filtros simples
    /**
     * Lista las visitas filtradas opcionalmente por cliente, técnico, estado o fechas.
     *
     * @param customerId Identificador del cliente (opcional).
     * @param technicianId Identificador del técnico (opcional).
     * @param state Estado de la visita (opcional).
     * @param from Fecha inicial del rango (opcional).
     * @param to Fecha final del rango (opcional).
     * @param page Número de página.
     * @param size Tamaño de página.
     * @return Página de visitas que cumplen los criterios.
     */
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

    // Mis visitas de hoy
    /**
     * Devuelve las visitas asignadas al técnico para el día actual.
     *
     * @param technicianId Identificador del técnico autenticado.
     * @param dateIso Fecha opcional en formato ISO (por defecto, hoy).
     * @return Lista de visitas del día actual.
     */
    @GetMapping("/me/today")
    public List<Visit> myToday(@RequestParam UUID technicianId,
                               @RequestParam(required = false) String dateIso) {
        // Si no se envía fecha, usa la fecha actual del sistema.
        LocalDate day = (dateIso == null || dateIso.isBlank()) ? LocalDate.now() : LocalDate.parse(dateIso);
        return service.myVisitsToday(technicianId, day);
    }

    // Eventos de una visita
    /**
     * Devuelve todos los eventos registrados para una visita.
     *
     * @param id Identificador de la visita.
     * @return Lista de eventos asociados a la visita.
     */
    @GetMapping("/{id}/events")
    public List<VisitEvent> events(@PathVariable UUID id) {
        // Obtiene del servicio los eventos asociados a la visita solicitada.
        return service.events(id);
    }

    // Agregar nota
    /**
     * Agrega una nota a una visita existente y devuelve todas las notas actualizadas.
     *
     * @param id Identificador de la visita.
     * @param req Objeto con los datos de la nota (autor, visibilidad, cuerpo).
     * @return Lista de notas actualizadas.
     */
    @PostMapping("/{id}/notes")
    public List<VisitNote> addNote(@PathVariable UUID id, @RequestBody AddNoteRequest req) {
        // Delegación al servicio para registrar una nueva nota asociada a la visita.
        return service.addNote(id, req.authorId, req.visibility, req.body);
    }

    // =========================================================================
    // Clases internas DTO utilizadas para recibir los cuerpos de solicitud (request)
    // desde los clientes en formato JSON. Estas clases simplifican la comunicación
    // entre el frontend y el backend, evitando exponer las entidades del dominio.
    // =========================================================================
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