/*
 * -----------------------------------------------------------------------------
 * SupervisorRepository.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Repositorio JPA responsable de gestionar las operaciones de acceso a datos
 *   para la entidad {@link com.proyecto.ops.supervisors.model.Supervisor}.
 *
 * Contexto de uso:
 *   - Forma parte del microservicio "supervisors-svc".
 *   - Proporciona consultas personalizadas y operaciones CRUD para supervisores.
 *
 * Diseño:
 *   - Extiende JpaRepository, lo que le proporciona operaciones básicas
 *     como guardar, eliminar, buscar por ID, etc.
 *   - Define una consulta personalizada mediante SQL nativo para filtrar supervisores
 *     según su estado (activo/inactivo) o equipo.
 *
 * Métodos:
 *   • search(Boolean active, UUID teamId, Pageable pageable)
 *       → Retorna una lista paginada de supervisores filtrados por estado y/o equipo.
 *   • findByUserId(UUID userId)
 *       → Busca un supervisor por el identificador del usuario asociado.
 */
package com.proyecto.ops.supervisors.repo;

import com.proyecto.ops.supervisors.model.Supervisor;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repositorio JPA para la entidad Supervisor.
 *
 * Proporciona operaciones CRUD y consultas personalizadas sobre supervisores registrados.
 *
 */
public interface SupervisorRepository extends JpaRepository<Supervisor, UUID> {

  @Query(
    value = """
            select *
            from app.supervisors s
            where (:active is null or s.active = :active)
              and (:teamId is null or s.team_id = :teamId)
            order by s.created_at desc
            """,
    countQuery = """
            select count(*)
            from app.supervisors s
            where (:active is null or s.active = :active)
              and (:teamId is null or s.team_id = :teamId)
            """,
    nativeQuery = true
  )
    /**
     * Realiza una búsqueda paginada de supervisores según su estado y/o equipo.
     *
     * @param active  Si es true, filtra solo supervisores activos; si es false, inactivos; si es null, no filtra por estado.
     * @param teamId  Filtra por el identificador del equipo asignado (opcional).
     * @param pageable Parámetros de paginación (página, tamaño y orden).
     * @return Página con los resultados de supervisores encontrados.
     */
    Page<Supervisor> search(
        @Param("active") Boolean active,
        @Param("teamId")  UUID teamId,
        Pageable pageable
    );

    /**
     * Busca un supervisor asociado a un usuario específico.
     *
     * @param userId UUID del usuario.
     * @return Optional con el supervisor correspondiente si existe.
     */
    Optional<Supervisor> findByUserId(UUID userId);
}
