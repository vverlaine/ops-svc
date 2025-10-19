/*
 * -----------------------------------------------------------------------------
 * VisitEmailRepository.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Define la capa de acceso a datos (DAO) para la entidad {@link com.visits.model.VisitEmail},
 *   encargada de registrar los correos electrónicos enviados o pendientes
 *   dentro del microservicio "visits-svc".
 *
 * Contexto de uso:
 *   - Hereda de JpaRepository, lo que provee operaciones CRUD estándar:
 *       • save()        → Crear o actualizar registros.
 *       • findById()    → Consultar un correo por su ID.
 *       • findAll()     → Listar todos los registros.
 *       • deleteById()  → Eliminar un registro.
 *   - Utiliza UUID como tipo de identificador primario.
 *
 * Mantenibilidad:
 *   - Métodos de consulta personalizados pueden agregarse según sea necesario,
 *     por ejemplo: findByVisitId(UUID visitId) o findByStatus(String status).
 * -----------------------------------------------------------------------------
 */
package com.visits.repo;

import com.visits.model.VisitEmail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repositorio JPA para la entidad VisitEmail.
 *
 * Permite realizar operaciones CRUD y futuras consultas personalizadas
 * sobre los registros de correos asociados a visitas.
 */
public interface VisitEmailRepository extends JpaRepository<VisitEmail, UUID> {
    // Este repositorio hereda todos los métodos CRUD de JpaRepository.
    // Ejemplo de uso:
    //   visitEmailRepository.save(visitEmail);      // Guarda o actualiza un correo
    //   visitEmailRepository.findById(uuid);        // Busca un correo por su UUID
    //   visitEmailRepository.deleteById(uuid);      // Elimina un correo por su UUID
    //
    // Métodos personalizados pueden ser definidos aquí según la necesidad del negocio.
}