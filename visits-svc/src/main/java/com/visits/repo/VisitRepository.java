/*
 * -----------------------------------------------------------------------------
 * VisitRepository.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Define la capa de acceso a datos (DAO) para la entidad {@link com.visits.model.Visit},
 *   que representa las visitas planificadas, en curso o completadas dentro del
 *   microservicio "visits-svc".
 *
 * Contexto de uso:
 *   - Hereda de JpaRepository, lo cual provee operaciones CRUD estándar:
 *       • save()        → Crea o actualiza registros de visitas.
 *       • findById()    → Busca una visita por su identificador UUID.
 *       • findAll()     → Lista todas las visitas registradas.
 *       • deleteById()  → Elimina una visita específica.
 *   - Incluye consultas personalizadas para filtrar visitas según técnico,
 *     rango de fechas o estado.
 *
 * Mantenibilidad:
 *   - Pueden agregarse nuevos métodos derivados del nombre, por ejemplo:
 *       findByCustomerId(UUID customerId)
 *       findByStateAndTechnicianId(VisitState state, UUID technicianId)
 * -----------------------------------------------------------------------------
 */
package com.visits.repo;

import com.visits.model.Visit;
import com.visits.model.VisitState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Repositorio JPA para la entidad Visit.
 *
 * Permite realizar operaciones CRUD y consultas específicas sobre las visitas.
 */
public interface VisitRepository extends JpaRepository<Visit, UUID> {
    // Consulta personalizada para obtener visitas por técnico y rango de fechas planificadas
    /**
     * Obtiene una página de visitas asignadas a un técnico específico dentro de un rango de fechas planificadas.
     *
     * @param technicianId Identificador del técnico asignado.
     * @param from Fecha y hora de inicio del rango.
     * @param to Fecha y hora de fin del rango.
     * @param pageable Parámetros de paginación (página, tamaño, orden).
     * @return Página con las visitas encontradas para ese técnico en el rango dado.
     */
    Page<Visit> findByTechnicianIdAndScheduledStartAtBetween(
            UUID technicianId, OffsetDateTime from, OffsetDateTime to, Pageable pageable);

    // Consulta personalizada para obtener visitas por estado
    /**
     * Obtiene una página de visitas filtradas por su estado actual.
     *
     * @param state Estado de la visita (PLANNED, STARTED, DONE, etc.).
     * @param pageable Parámetros de paginación (página, tamaño, orden).
     * @return Página con las visitas que coinciden con el estado especificado.
     */
    Page<Visit> findByState(VisitState state, Pageable pageable);
}