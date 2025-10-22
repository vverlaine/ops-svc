/*
 * -----------------------------------------------------------------------------
 * VisitServiceImpl.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Implementa la lógica principal del servicio de gestión de visitas dentro del
 *   microservicio "visits-svc".
 *
 * Contexto de uso:
 *   - Implementa la interfaz {@link com.visits.service.VisitService}.
 *   - Gestiona todo el ciclo de vida de las visitas técnicas: planificación,
 *     actualización, inicio, finalización, cancelación y documentación (notas y eventos).
 *
 * Diseño:
 *   - Utiliza la arquitectura por capas: el servicio se comunica con los repositorios
 *     JPA para acceder a la base de datos y con otros servicios (como envío de correo).
 *   - Está anotado con @Service y @Transactional para que Spring gestione
 *     automáticamente su ciclo de vida y la transaccionalidad.
 *
 * Dependencias principales:
 *   • VisitRepository       → Maneja las operaciones CRUD sobre visitas.
 *   • VisitEventRepository  → Registra eventos del ciclo de vida de la visita.
 *   • VisitNoteRepository   → Gestiona las notas asociadas a las visitas.
 *   • VisitEmailService     → Envía correos electrónicos al completar una visita.
 *
 * Flujo general:
 *   1. Se crean y actualizan visitas planificadas.
 *   2. Se ejecutan check-in y check-out con registro de ubicación y eventos.
 *   3. Se cancelan visitas si es necesario.
 *   4. Se agregan notas o se consultan eventos asociados.
 *
 * Mantenibilidad:
 *   - Cada método está documentado y segmentado para que futuras modificaciones
 *     (como agregar estados o tipos de eventos) sean fáciles de incorporar.
 * -----------------------------------------------------------------------------
 */
package com.visits.service;

import com.visits.model.*;
import com.visits.repo.*;
import com.visits.service.VisitService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

/**
 * Implementación del servicio principal de gestión de visitas.
 *
 * Controla las operaciones del ciclo de vida de las visitas y delega la
 * persistencia a los repositorios JPA correspondientes.
 */
@Service
@Transactional
public class VisitServiceImpl implements VisitService {

    private final VisitRepository visitRepository;
    private final VisitEventRepository eventRepository;
    private final VisitNoteRepository noteRepository;
    private final VisitEmailService visitEmailService;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param visitRepository Repositorio para operaciones CRUD sobre visitas.
     * @param eventRepository Repositorio para registrar eventos de visitas.
     * @param noteRepository Repositorio para notas asociadas a las visitas.
     * @param visitEmailService Servicio encargado de manejar notificaciones por
     * correo.
     */
    public VisitServiceImpl(VisitRepository visitRepository,
            VisitEventRepository eventRepository,
            VisitNoteRepository noteRepository,
            VisitEmailService visitEmailService) {
        this.visitRepository = visitRepository;
        this.eventRepository = eventRepository;
        this.noteRepository = noteRepository;
        this.visitEmailService = visitEmailService;
    }

    /**
     * Crea una nueva visita planificada y registra un evento inicial.
     *
     * @param customerId Identificador del cliente.
     * @param siteId Identificador del sitio de la visita.
     * @param technicianId Técnico asignado.
     * @param start Fecha/hora planificada de inicio.
     * @param end Fecha/hora planificada de finalización.
     * @param priority Prioridad de la visita.
     * @param purpose Propósito o motivo de la visita.
     * @param notesPlanned Notas adicionales.
     * @return Visita creada y almacenada en la base de datos.
     */
    @Override
    public Visit createPlanned(UUID customerId, UUID siteId, UUID technicianId,
            OffsetDateTime start, OffsetDateTime end,
            VisitPriority priority, String purpose, String notesPlanned) {

        Visit v = Visit.planned(customerId, siteId, technicianId, start, end, priority, purpose, notesPlanned);
        v.validateDates();
        v = visitRepository.save(v);

        // Registra un evento indicando que la visita ha sido programada.
        eventRepository.save(VisitEvent.of(v.getId(), "VisitScheduled", null, null, null, null));
        return v;
    }

    /**
     * Actualiza los datos de una visita en estado PLANNED. Solo puede
     * modificarse mientras no haya iniciado.
     *
     * @param visitId Identificador de la visita.
     * @param start Nueva fecha/hora de inicio planificada.
     * @param end Nueva fecha/hora de fin planificada.
     * @param technicianId Técnico asignado.
     * @param priority Nueva prioridad.
     * @param purpose Propósito actualizado.
     * @param notesPlanned Notas modificadas.
     * @return Visita actualizada.
     */
    @Override
    public Visit updatePlanned(UUID visitId, OffsetDateTime start, OffsetDateTime end,
            UUID technicianId, VisitPriority priority, String purpose, String notesPlanned, VisitState state) {
        Visit v = visitRepository.findById(visitId)
                .orElseThrow(() -> new IllegalArgumentException("Visit not found"));

        if (v.getState() != VisitState.PLANNED) {
            throw new IllegalStateException("Solo se puede editar una visita en estado PLANNED");
        }
        if (start != null) {
            v.setScheduledStartAt(start);
        }
        if (end != null) {
            v.setScheduledEndAt(end);
        }
        if (technicianId != null) {
            v.setTechnicianId(technicianId);
        }
        if (priority != null) {
            v.setPriority(priority);
        }
        if (purpose != null) {
            v.setPurpose(purpose);
        }
        if (notesPlanned != null) {
            v.setNotesPlanned(notesPlanned);
        }
        if (state != null) {
            v.setState(state); // ✅ ESTA LÍNEA FALTABA
        }

        v.validateDates();
        Visit saved = visitRepository.save(v);

        eventRepository.save(VisitEvent.of(saved.getId(), "VisitUpdated", null, null, null, null));
        return saved;
    }

    /**
     * Marca el inicio real de una visita (check-in) y registra el evento
     * correspondiente.
     *
     * @param visitId Identificador de la visita.
     * @param actorId Identificador del técnico que inicia la visita.
     * @param when Fecha/hora del check-in.
     * @param lat Latitud del punto de inicio.
     * @param lng Longitud del punto de inicio.
     * @return Visita actualizada en estado STARTED.
     */
    @Override
    public Visit checkIn(UUID visitId, UUID actorId, OffsetDateTime when, Double lat, Double lng) {
        Visit v = visitRepository.findById(visitId)
                .orElseThrow(() -> new IllegalArgumentException("Visit not found"));

        v.start(when == null ? OffsetDateTime.now(ZoneOffset.UTC) : when);
        Visit saved = visitRepository.save(v);

        // Registra el evento "VisitStarted" con información de ubicación.
        eventRepository.save(VisitEvent.of(saved.getId(), "VisitStarted", actorId, lat, lng, null));
        return saved;
    }

    /**
     * Marca la finalización de una visita (check-out), registra el evento y
     * dispara la notificación por correo electrónico.
     *
     * @param visitId Identificador de la visita.
     * @param actorId Identificador del técnico.
     * @param when Fecha/hora del check-out.
     * @param lat Latitud de ubicación.
     * @param lng Longitud de ubicación.
     * @param workSummary Resumen del trabajo realizado.
     * @return Visita actualizada en estado DONE.
     */
    @Override
    public Visit checkOut(UUID visitId, UUID actorId, OffsetDateTime when, Double lat, Double lng, String workSummary) {
        Visit v = visitRepository.findById(visitId)
                .orElseThrow(() -> new IllegalArgumentException("Visit not found"));

        v.complete(when == null ? OffsetDateTime.now(ZoneOffset.UTC) : when);
        Visit saved = visitRepository.save(v);
        // workSummary podría ir en payload
        eventRepository.save(VisitEvent.of(saved.getId(), "VisitCompleted", actorId, lat, lng, workSummary));

        // Dispara la notificación por correo electrónico al completarse la visita.
        try {
            visitEmailService.onVisitCompleted(saved);
        } catch (Exception ex) {
            // Captura errores de envío sin interrumpir el flujo de check-out.
            // No romper el flujo de checkout si el correo falla
        }
        return saved;
    }

    /**
     * Cancela una visita planificada y registra un evento de cancelación.
     *
     * @param visitId Identificador de la visita.
     * @param actorId Identificador del usuario que la cancela.
     */
    @Override
    public void cancel(UUID visitId, UUID actorId) {
        Visit v = visitRepository.findById(visitId)
                .orElseThrow(() -> new IllegalArgumentException("Visit not found"));
        v.cancel();
        visitRepository.save(v);

        // Registra el evento de cancelación para trazabilidad.
        eventRepository.save(VisitEvent.of(v.getId(), "VisitCancelled", actorId, null, null, null));
    }

    /**
     * Lista las visitas aplicando filtros opcionales: técnico, estado o rango
     * de fechas.
     *
     * @param customerId Identificador del cliente (opcional).
     * @param technicianId Identificador del técnico (opcional).
     * @param state Estado actual de la visita (opcional).
     * @param from Fecha inicial del rango (opcional).
     * @param to Fecha final del rango (opcional).
     * @param pageable Parámetros de paginación.
     * @return Página de visitas filtradas.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Visit> list(UUID customerId, UUID technicianId, VisitState state,
            OffsetDateTime from, OffsetDateTime to, Pageable pageable) {
        // Implementación simple: si viene technicianId + rango, usar el finder preparado
        if (technicianId != null && from != null && to != null) {
            return visitRepository.findByTechnicianIdAndScheduledStartAtBetween(technicianId, from, to, pageable);
        }
        if (state != null) {
            return visitRepository.findByState(state, pageable);
        }
        // Si no se aplican filtros, retorna todas las visitas paginadas.
        return visitRepository.findAll(pageable);
    }

    /**
     * Obtiene la lista de visitas programadas para un técnico en la fecha
     * actual.
     *
     * @param technicianId Identificador del técnico.
     * @param today Fecha actual.
     * @return Lista de visitas del día.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Visit> myVisitsToday(UUID technicianId, LocalDate today) {
        OffsetDateTime start = today.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime end = today.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC).minusNanos(1);

        // Devuelve las visitas programadas para el día, limitadas a 1000 registros.
        return visitRepository
                .findByTechnicianIdAndScheduledStartAtBetween(technicianId, start, end, PageRequest.of(0, 1000))
                .getContent();
    }

    /**
     * Obtiene la lista completa de eventos asociados a una visita, ordenados
     * cronológicamente.
     *
     * @param visitId Identificador de la visita.
     * @return Lista de eventos registrados.
     */
    @Override
    @Transactional(readOnly = true)
    public List<VisitEvent> events(UUID visitId) {
        return eventRepository.findByVisitIdOrderByCreatedAtAsc(visitId);
    }

    /**
     * Agrega una nota a una visita y retorna la lista actualizada de notas.
     *
     * @param visitId Identificador de la visita.
     * @param authorId Identificador del autor.
     * @param visibility Nivel de visibilidad de la nota (INTERNAL o CUSTOMER).
     * @param body Contenido de la nota.
     * @return Lista de notas actualizadas asociadas a la visita.
     */
    @Override
    public List<VisitNote> addNote(UUID visitId, UUID authorId, NoteVisibility visibility, String body) {
        Visit v = visitRepository.findById(visitId)
                .orElseThrow(() -> new IllegalArgumentException("Visit not found"));
        if (body == null || body.isBlank()) {
            throw new IllegalArgumentException("Nota vacía");
        }

        // Crea y guarda una nueva nota asociada a la visita.
        noteRepository.save(VisitNote.of(v.getId(), authorId, visibility == null ? NoteVisibility.INTERNAL : visibility, body));

        // Retorna todas las notas de la visita ordenadas por fecha de creación.
        return noteRepository.findByVisitIdOrderByCreatedAtAsc(visitId);
    }

    @Override
    public Visit getById(UUID visitId) {
        return visitRepository.findById(visitId)
                .orElseThrow(() -> new RuntimeException("Visita no encontrada: " + visitId));
    }
}
