/*
 * -----------------------------------------------------------------------------
 * WorkOrderRepository.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Define la capa de acceso a datos (DAO) para la entidad {@link com.proyecto.ops.workorders.model.WorkOrder},
 *   utilizada dentro del microservicio "work-orders-svc".
 *
 * Contexto de uso:
 *   - Extiende JpaRepository para heredar operaciones CRUD básicas (save, findById, findAll, deleteById).
 *   - Permite realizar búsquedas personalizadas mediante consultas JPQL.
 *   - Utilizada por el servicio principal de órdenes de trabajo para listar o filtrar órdenes.
 *
 * Diseño:
 *   - Se emplea una consulta personalizada (`@Query`) para filtrar órdenes por ticket y estado.
 *   - Los filtros usan la función COALESCE para permitir parámetros opcionales.
 *     Si el parámetro es nulo, el filtro se ignora.
 *
 * Mantenibilidad:
 *   - Se pueden agregar nuevos métodos derivados o consultas personalizadas para necesidades futuras,
 *     por ejemplo: findByTechnicianId(UUID technicianId).
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.workorders.repo;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.proyecto.ops.workorders.model.WoStatus;
import com.proyecto.ops.workorders.model.WorkOrder;

/**
 * Repositorio JPA para la entidad WorkOrder.
 *
 * Proporciona acceso a operaciones CRUD y consultas personalizadas.
 */
public interface WorkOrderRepository extends JpaRepository<WorkOrder, UUID> {

    /**
     * Realiza una búsqueda paginada de órdenes de trabajo, filtrando opcionalmente
     * por el identificador del ticket o por estado.
     *
     * @param ticketId Identificador del ticket asociado (puede ser nulo para omitir el filtro).
     * @param status Estado de la orden (puede ser nulo para omitir el filtro).
     * @param pageable Parámetros de paginación (número de página, tamaño, orden).
     * @return Página de resultados que cumplen los criterios de búsqueda.
     */
    // Consulta JPQL que filtra dinámicamente por ticketId y estado usando COALESCE.
    // Si el parámetro es nulo, el filtro no se aplica (se comporta como un filtro opcional).
    @Query("""
           select w
           from WorkOrder w
           where w.ticketId = coalesce(:ticketId, w.ticketId)
             and w.status = coalesce(:status, w.status)
           order by w.createdAt desc
           """)
    Page<WorkOrder> search(
        @Param("ticketId") UUID ticketId,
        @Param("status") WoStatus status,
        Pageable pageable
    );
}