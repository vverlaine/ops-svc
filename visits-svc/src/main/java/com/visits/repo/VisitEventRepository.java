/*
 * -----------------------------------------------------------------------------
 * VisitEventRepository.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Define la capa de acceso a datos (DAO) para la entidad {@link com.visits.model.VisitEvent},
 *   la cual almacena los eventos generados durante el ciclo de vida de una visita.
 *
 * Contexto de uso:
 *   - Pertenece al microservicio "visits-svc".
 *   - Hereda de JpaRepository, lo que provee las operaciones CRUD básicas:
 *       • save()        → Crea o actualiza un evento.
 *       • findById()    → Busca un evento por su identificador.
 *       • findAll()     → Lista todos los eventos registrados.
 *       • deleteById()  → Elimina un evento específico.
 *   - Incluye un método personalizado para obtener los eventos de una visita en orden cronológico.
 *
 * Mantenibilidad:
 *   - Pueden agregarse nuevos métodos de búsqueda, por ejemplo:
 *       findByType(String type) o findByActorId(UUID actorId).
 * -----------------------------------------------------------------------------
 */
package com.visits.repo;

import com.visits.model.VisitEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio JPA para la entidad VisitEvent.
 *
 * Permite realizar operaciones CRUD y consultas personalizadas sobre los eventos
 * registrados durante las visitas.
 */
public interface VisitEventRepository extends JpaRepository<VisitEvent, UUID> {
    /**
     * Obtiene todos los eventos asociados a una visita específica,
     * ordenados cronológicamente por fecha de creación.
     *
     * @param visitId Identificador de la visita.
     * @return Lista de eventos ordenados de más antiguos a más recientes.
     */
    List<VisitEvent> findByVisitIdOrderByCreatedAtAsc(UUID visitId);
}