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

    public VisitServiceImpl(VisitRepository visitRepository,
            VisitEventRepository eventRepository,
            VisitNoteRepository noteRepository,
            VisitEmailService visitEmailService) {
        this.visitRepository = visitRepository;
        this.eventRepository = eventRepository;
        this.noteRepository = noteRepository;
        this.visitEmailService = visitEmailService;
    }

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
            v.setState(state);
        }

        v.validateDates();
        Visit saved = visitRepository.save(v);

        eventRepository.save(VisitEvent.of(saved.getId(), "VisitUpdated", null, null, null, null));
        return saved;
    }

    @Override
    public Visit checkIn(UUID visitId, UUID actorId, OffsetDateTime when, Double lat, Double lng) {
        Visit v = visitRepository.findById(visitId)
                .orElseThrow(() -> new IllegalArgumentException("Visit not found"));

        v.start(when == null ? OffsetDateTime.now(ZoneOffset.UTC) : when);
        Visit saved = visitRepository.save(v);

        eventRepository.save(VisitEvent.of(saved.getId(), "VisitStarted", actorId, lat, lng, null));
        return saved;
    }

    @Override
    public Visit checkOut(UUID visitId, UUID actorId, OffsetDateTime when, Double lat, Double lng, String workSummary) {
        Visit v = visitRepository.findById(visitId)
                .orElseThrow(() -> new IllegalArgumentException("Visit not found"));

        v.complete(when == null ? OffsetDateTime.now(ZoneOffset.UTC) : when);
        Visit saved = visitRepository.save(v);

        eventRepository.save(VisitEvent.of(saved.getId(), "VisitCompleted", actorId, lat, lng, workSummary));

        // Dispara la notificación por correo electrónico al completarse la visita.
        try {
            visitEmailService.onVisitCompleted(saved);
        } catch (Exception ex) {
        }
        return saved;
    }

    @Override
    public void cancel(UUID visitId, UUID actorId) {
        Visit v = visitRepository.findById(visitId)
                .orElseThrow(() -> new IllegalArgumentException("Visit not found"));
        v.cancel();
        visitRepository.save(v);

        eventRepository.save(VisitEvent.of(v.getId(), "VisitCancelled", actorId, null, null, null));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Visit> list(UUID customerId, UUID technicianId, UUID supervisorId, VisitState state,
            OffsetDateTime from, OffsetDateTime to, Pageable pageable) {
        if (supervisorId != null) {
            return visitRepository.findBySupervisor(supervisorId, from, to, pageable);
        }
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

    @Override
    @Transactional(readOnly = true)
    public List<VisitEvent> events(UUID visitId) {
        return eventRepository.findByVisitIdOrderByCreatedAtAsc(visitId);
    }

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
