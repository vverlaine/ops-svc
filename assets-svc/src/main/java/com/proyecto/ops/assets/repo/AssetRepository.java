/*
 * -----------------------------------------------------------------------------
 * AssetRepository.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Repositorio JPA para la entidad Asset. Permite realizar operaciones CRUD
 *   y consultas personalizadas sobre la tabla `app.assets`.
 *
 * Contexto de uso:
 *   - Es utilizado por los servicios de dominio (por ejemplo, AssetService)
 *     o controladores para acceder a los datos de los activos.
 *
 * Diseño:
 *   - Extiende JpaRepository<Asset, UUID>, lo cual provee operaciones
 *     estándar de persistencia (findAll, findById, save, delete, etc.).
 *   - Define un método personalizado para obtener los activos de un cliente
 *     mediante su UUID.
 *
 * Mantenibilidad:
 *   - Si se requieren más consultas personalizadas, pueden declararse como
 *     métodos adicionales en esta interfaz.
 *   - Spring Data JPA genera automáticamente la implementación a partir de
 *     los nombres de los métodos.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.assets.repo;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.proyecto.ops.assets.model.Asset;

/**
 * Repositorio de acceso a datos para la entidad {@link com.proyecto.ops.assets.model.Asset}.
 *
 * Extiende {@link org.springframework.data.jpa.repository.JpaRepository} para heredar
 * las operaciones CRUD básicas y soporta paginación mediante {@link org.springframework.data.domain.Pageable}.
 */
public interface AssetRepository extends JpaRepository<Asset, UUID> {
    /**
     * Busca los activos asociados a un cliente específico.
     *
     * @param customerId UUID del cliente propietario.
     * @param pageable   Parámetros de paginación (página, tamaño, orden).
     * @return Página de resultados con los activos pertenecientes al cliente.
     */
    Page<Asset> findByCustomerId(UUID customerId, Pageable pageable);
}