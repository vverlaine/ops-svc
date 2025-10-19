/*
 * -----------------------------------------------------------------------------
 * ContactRepository.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Repositorio JPA para la entidad Contact. Permite realizar operaciones CRUD
 *   y consultas personalizadas sobre la tabla `app.contacts`.
 *
 * Contexto de uso:
 *   - Pertenece al microservicio contacts-svc.
 *   - Es utilizado por los controladores y servicios de dominio para acceder
 *     y gestionar los contactos asociados a clientes.
 *
 * Diseño:
 *   - Extiende JpaRepository<Contact, UUID>, lo que proporciona métodos
 *     estándar de persistencia como save(), findById(), findAll(), delete().
 *   - Incluye una consulta personalizada findByCustomerId() para obtener
 *     los contactos asociados a un cliente en particular.
 *
 * Mantenibilidad:
 *   - Spring Data JPA genera automáticamente la implementación.
 *   - Nuevas consultas pueden definirse simplemente añadiendo más métodos
 *     siguiendo las convenciones de nomenclatura.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.contacts.repo;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.proyecto.ops.contacts.model.Contact;

/**
 * Repositorio de acceso a datos para la entidad {@link com.proyecto.ops.contacts.model.Contact}.
 *
 * Proporciona operaciones CRUD y consultas específicas relacionadas con contactos.
 */
public interface ContactRepository extends JpaRepository<Contact, UUID> {
    /**
     * Busca los contactos asociados a un cliente específico.
     *
     * @param customerId UUID del cliente.
     * @param pageable   Parámetros de paginación (página, tamaño, orden).
     * @return Página con los contactos correspondientes al cliente indicado.
     */
    Page<Contact> findByCustomerId(UUID customerId, Pageable pageable);
}