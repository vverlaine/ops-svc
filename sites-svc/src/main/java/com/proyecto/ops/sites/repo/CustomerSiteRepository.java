/*
 * -----------------------------------------------------------------------------
 * CustomerSiteRepository.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Repositorio JPA para la entidad {@link com.proyecto.ops.sites.model.CustomerSite}.
 *   Permite realizar operaciones CRUD y consultas personalizadas sobre la tabla
 *   `app.customer_sites` del microservicio "sites-svc".
 *
 * Contexto de uso:
 *   - Se utiliza por los controladores y servicios del módulo "sites-svc" para
 *     acceder y gestionar los sitios asociados a clientes.
 *   - Extiende JpaRepository para aprovechar los métodos automáticos de Spring Data JPA.
 *
 * Diseño:
 *   - La interfaz define un método adicional:
 *       • findByCustomerId(UUID customerId, Pageable pageable)
 *         → Retorna una lista paginada de sitios asociados a un cliente específico.
 *
 * Mantenibilidad:
 *   - Spring Data JPA implementa automáticamente la interfaz.
 *   - Si se requieren consultas más complejas (por ejemplo, búsqueda por nombre o ciudad),
 *     pueden agregarse siguiendo las convenciones de nomenclatura de Spring.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.sites.repo;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.proyecto.ops.sites.model.CustomerSite;

/**
 * Repositorio JPA para la entidad CustomerSite.
 *
 * Proporciona métodos CRUD y una consulta personalizada por customerId.
 */
public interface CustomerSiteRepository extends JpaRepository<CustomerSite, UUID> {
    /**
     * Obtiene una lista paginada de sitios asociados a un cliente específico.
     *
     * @param customerId UUID del cliente propietario de los sitios.
     * @param pageable   Parámetros de paginación (página, tamaño, orden).
     * @return Página de resultados con los sitios correspondientes.
     */
    Page<CustomerSite> findByCustomerId(UUID customerId, Pageable pageable);
}