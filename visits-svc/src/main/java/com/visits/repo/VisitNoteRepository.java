/*
 * -----------------------------------------------------------------------------
 * VisitNoteRepository.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Define la capa de acceso a datos (DAO) para la entidad {@link com.visits.model.VisitNote},
 *   utilizada para almacenar y consultar las notas asociadas a las visitas dentro
 *   del microservicio "visits-svc".
 *
 * Contexto de uso:
 *   - Hereda de JpaRepository, lo que proporciona operaciones CRUD estándar:
 *       • save()        → Guarda o actualiza una nota.
 *       • findById()    → Busca una nota por su identificador.
 *       • findAll()     → Lista todas las notas registradas.
 *       • deleteById()  → Elimina una nota específica.
 *   - Incluye un método personalizado para obtener las notas de una visita
 *     ordenadas cronológicamente.
 *
 * Mantenibilidad:
 *   - Pueden añadirse nuevos métodos de búsqueda según necesidades futuras,
 *     como findByAuthorId(UUID authorId) o findByVisibility(NoteVisibility visibility).
 * -----------------------------------------------------------------------------
 */
package com.visits.repo;

import com.visits.model.VisitNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio JPA para la entidad VisitNote.
 *
 * Permite realizar operaciones CRUD y consultas personalizadas sobre las notas
 * asociadas a las visitas.
 */
public interface VisitNoteRepository extends JpaRepository<VisitNote, UUID> {
    /**
     * Obtiene todas las notas asociadas a una visita específica,
     * ordenadas por fecha de creación de forma ascendente.
     *
     * @param visitId Identificador único de la visita.
     * @return Lista de notas ordenadas cronológicamente.
     */
    List<VisitNote> findByVisitIdOrderByCreatedAtAsc(UUID visitId);
}