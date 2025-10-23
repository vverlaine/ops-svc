package com.visits.service;

import com.visits.model.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Define las operaciones de negocio disponibles para gestionar el ciclo de vida
 * de una visita técnica, incluyendo planificación, ejecución, notas y eventos.
 */
public interface VisitService {

    /**
     * Crea una visita en estado planificado con los metadatos principales.
     */
    Visit createPlanned(UUID customerId, UUID siteId, UUID technicianId,
                        OffsetDateTime start, OffsetDateTime end,
                        VisitPriority priority, String purpose, String notesPlanned);

    /**
     * Actualiza una visita planificada antes de que inicie, permitiendo mover horarios,
     * reasignar técnicos o ajustar estado y prioridad.
     */
    Visit updatePlanned(UUID visitId, OffsetDateTime start, OffsetDateTime end,
                    UUID technicianId, VisitPriority priority, String purpose, String notesPlanned,
                    VisitState state);

    /**
     * Marca una visita como iniciada y registra su evento de check-in.
     */
    Visit checkIn(UUID visitId, UUID actorId, OffsetDateTime when, Double lat, Double lng);

    /**
     * Marca una visita como completada registrando el check-out y el resumen de trabajo.
     */
    Visit checkOut(UUID visitId, UUID actorId, OffsetDateTime when, Double lat, Double lng, String workSummary);

    /**
     * Cancela una visita y produce el evento correspondiente.
     */
    void cancel(UUID visitId, UUID actorId);

    /**
     * Lista visitas aplicando filtros opcionales y paginación.
     */
    Page<Visit> list(UUID customerId, UUID technicianId, VisitState state,
                     OffsetDateTime from, OffsetDateTime to, Pageable pageable);

    /**
     * Recupera las visitas asignadas a un técnico para la fecha indicada.
     */
    List<Visit> myVisitsToday(UUID technicianId, LocalDate today);

    /**
     * Obtiene los eventos históricos asociados a una visita.
     */
    List<VisitEvent> events(UUID visitId);

    /**
     * Registra una nueva nota y devuelve la lista consolidada de notas de la visita.
     */
    List<VisitNote> addNote(UUID visitId, UUID authorId, NoteVisibility visibility, String body);

    /**
     * Recupera una visita individual por su identificador.
     */
    Visit getById(UUID visitId);
}
