/*
 * -----------------------------------------------------------------------------
 * TechnicianRepository.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Repositorio JPA responsable de gestionar las operaciones de acceso a datos
 *   para la entidad {@link com.proyecto.ops.technicians.model.Technician}.
 *
 * Contexto de uso:
 *   - Forma parte del microservicio "technicians-svc".
 *   - Proporciona consultas personalizadas y operaciones CRUD para técnicos.
 *
 * Diseño:
 *   - Extiende JpaRepository, lo que le proporciona operaciones básicas
 *     como guardar, eliminar, buscar por ID, etc.
 *   - Define una consulta personalizada mediante SQL nativo para filtrar técnicos
 *     según su estado (activo/inactivo) y habilidades.
 *
 * Métodos:
 *   • search(Boolean active, String skill, Pageable pageable)
 *       → Retorna una lista paginada de técnicos filtrados por estado o habilidad.
 *   • findByUserId(UUID userId)
 *       → Busca un técnico por el identificador del usuario asociado.
 *   • existsByUserId(UUID userId)
 *       → Verifica si existe un técnico asociado a un determinado usuario.
 *
 * Mantenibilidad:
 *   - Si se agregan nuevos criterios de filtrado, pueden incorporarse fácilmente
 *     en la consulta personalizada utilizando parámetros opcionales.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.technicians.repo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.proyecto.ops.technicians.model.Technician;

/**
 * Repositorio JPA para la entidad Technician.
 *
 * Proporciona operaciones CRUD y consultas personalizadas sobre técnicos registrados.
 */
public interface TechnicianRepository extends JpaRepository<Technician, UUID> {

  @Query(
    value = """
            select *
            from app.technicians t
            where (:active is null or t.active = :active)
              and (:skill  is null or :skill = any (t.skills))
            order by t.created_at desc
            """,
    countQuery = """
            select count(*)
            from app.technicians t
            where (:active is null or t.active = :active)
              and (:skill  is null or :skill = any (t.skills))
            """,
    nativeQuery = true
  )
    /**
     * Realiza una búsqueda paginada de técnicos según su estado y/o habilidad.
     *
     * @param active  Si es true, filtra solo técnicos activos; si es false, inactivos; si es null, no filtra por estado.
     * @param skill   Filtra los técnicos que posean la habilidad especificada (puede ser null).
     * @param pageable Parámetros de paginación (página, tamaño y orden).
     * @return Página con los resultados de técnicos encontrados.
     */
    Page<Technician> search(
        @Param("active") Boolean active,
        @Param("skill")  String skill,
        Pageable pageable
    );

    /**
     * Busca un técnico asociado a un usuario específico.
     *
     * @param userId UUID del usuario.
     * @return Optional con el técnico correspondiente si existe.
     */
    Optional<Technician> findByUserId(UUID userId);

    /**
     * Verifica si existe un técnico asociado a un determinado usuario.
     *
     * @param userId UUID del usuario a verificar.
     * @return true si existe, false en caso contrario.
     */
    boolean existsByUserId(UUID userId);
}