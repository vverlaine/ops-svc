/*
 * -----------------------------------------------------------------------------
 * TicketRepository.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Repositorio JPA responsable del acceso y consulta de datos para la entidad
 *   {@link com.proyecto.ops.tickets.model.Ticket}, que representa los tickets
 *   registrados dentro del sistema.
 *
 * Contexto de uso:
 *   - Pertenece al microservicio "tickets-svc".
 *   - Proporciona operaciones CRUD y una consulta personalizada para filtrar
 *     tickets por estado, prioridad, cliente y usuario solicitante.
 *
 * Diseño:
 *   - Extiende JpaRepository, lo que le otorga métodos estándar como save(), findById(),
 *     deleteById(), y findAll().
 *   - Define una consulta personalizada usando JPQL con parámetros opcionales,
 *     implementando una búsqueda flexible.
 *
 * Método principal:
 *   • search(...)
 *       → Permite buscar tickets aplicando filtros condicionales.
 *         Si un parámetro es null, se ignora ese filtro en la consulta.
 *
 * Mantenibilidad:
 *   - Si se agregan nuevos filtros (por ejemplo, siteId o assetId), deben incluirse
 *     tanto en la cláusula @Query como en los parámetros del método.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.tickets.repo;

import com.proyecto.ops.tickets.model.Ticket;
import com.proyecto.ops.tickets.model.TicketPriority;
import com.proyecto.ops.tickets.model.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

/**
 * Repositorio JPA para la entidad Ticket.
 *
 * Proporciona operaciones CRUD y una consulta personalizada con filtros opcionales.
 */
public interface TicketRepository extends JpaRepository<Ticket, UUID> {

    /**
     * Busca tickets aplicando filtros opcionales de estado, prioridad, cliente o usuario solicitante.
     *
     * @param status       Estado del ticket (puede ser null para no filtrar por estado).
     * @param priority     Prioridad del ticket (puede ser null para no filtrar por prioridad).
     * @param customerId   Identificador del cliente (puede ser null para no filtrar por cliente).
     * @param requestedBy  Usuario que creó la solicitud (puede ser null para no filtrar por usuario).
     * @param pageable     Parámetros de paginación (página, tamaño y orden).
     * @return Página con la lista de tickets que cumplen los filtros especificados.
     */
    @Query("""
           // Consulta JPQL que permite aplicar filtros dinámicos usando coalesce().
           // Si un parámetro es null, se conserva el valor original de la columna (no filtra).
           select t
           from Ticket t
           where t.status     = coalesce(:status, t.status)
             and t.priority   = coalesce(:priority, t.priority)
             and t.customerId = coalesce(:customerId, t.customerId)
             and t.requestedBy = coalesce(:requestedBy, t.requestedBy)
           order by t.createdAt desc
           """)
    Page<Ticket> search(
            @Param("status") TicketStatus status,
            @Param("priority") TicketPriority priority,
            @Param("customerId") UUID customerId,
            @Param("requestedBy") UUID requestedBy,
            Pageable pageable
    );
}