/*
 * -----------------------------------------------------------------------------
 * VisitService.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Define la interfaz del servicio principal de visitas dentro del microservicio
 *   "visits-svc". Establece las operaciones necesarias para gestionar el ciclo
 *   de vida completo de una visita técnica, incluyendo su planificación,
 *   ejecución, cancelación y documentación.
 *
 * Contexto de uso:
 *   - Implementada por una clase de servicio (por ejemplo, VisitServiceImpl).
 *   - Se comunica con los repositorios y entidades del dominio para realizar
 *     operaciones sobre las visitas, notas y eventos asociados.
 *
 * Diseño:
 *   - Define métodos para manejar las etapas del ciclo de vida de una visita:
 *       • createPlanned()  → Crea una nueva visita planificada.
 *       • updatePlanned()  → Modifica una visita ya planificada.
 *       • checkIn()        → Marca el inicio real de la visita.
 *       • checkOut()       → Marca la finalización real de la visita.
 *       • cancel()         → Cancela una visita planificada.
 *   - También provee métodos para listar, consultar eventos y agregar notas.
 *
 * Mantenibilidad:
 *   - Esta interfaz permite la abstracción de la lógica de negocio, facilitando
 *     pruebas unitarias y la sustitución de implementaciones.
 * -----------------------------------------------------------------------------
 */
package com.visits.service;

import com.visits.model.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Interfaz de servicio que define las operaciones de gestión de visitas.
 *
 * Permite planificar, iniciar, finalizar, cancelar y documentar visitas,
 * además de listar y consultar eventos relacionados.
 */
public interface VisitService {

    /**
     * Crea una nueva visita planificada con los datos especificados.
     *
     * @param customerId Identificador del cliente asociado.
     * @param siteId Identificador del sitio donde se realizará la visita.
     * @param technicianId Identificador del técnico asignado.
     * @param start Fecha y hora planificadas de inicio.
     * @param end Fecha y hora planificadas de finalización.
     * @param priority Nivel de prioridad de la visita.
     * @param purpose Propósito o motivo de la visita.
     * @param notesPlanned Notas adicionales o detalles planificados.
     * @return La visita planificada creada.
     */
    Visit createPlanned(UUID customerId, UUID siteId, UUID technicianId,
                        OffsetDateTime start, OffsetDateTime end,
                        VisitPriority priority, String purpose, String notesPlanned);

    /**
     * Actualiza los datos de una visita planificada.
     *
     * @param visitId Identificador de la visita a actualizar.
     * @param start Nueva fecha y hora de inicio planificada.
     * @param end Nueva fecha y hora de finalización planificada.
     * @param technicianId Nuevo técnico asignado.
     * @param priority Nueva prioridad.
     * @param purpose Propósito actualizado.
     * @param notesPlanned Notas o detalles actualizados.
     * @return La visita actualizada.
     */
    Visit updatePlanned(UUID visitId, OffsetDateTime start, OffsetDateTime end,
                    UUID technicianId, VisitPriority priority, String purpose, String notesPlanned,
                    VisitState state);

    /**
     * Marca el inicio real de una visita (check-in).
     *
     * @param visitId Identificador de la visita.
     * @param actorId Identificador del usuario que realiza el check-in.
     * @param when Fecha y hora del registro.
     * @param lat Latitud del punto de check-in.
     * @param lng Longitud del punto de check-in.
     * @return La visita actualizada con el estado STARTED.
     */
    Visit checkIn(UUID visitId, UUID actorId, OffsetDateTime when, Double lat, Double lng);

    /**
     * Marca la finalización de una visita (check-out).
     *
     * @param visitId Identificador de la visita.
     * @param actorId Identificador del usuario que realiza el check-out.
     * @param when Fecha y hora del registro.
     * @param lat Latitud del punto de check-out.
     * @param lng Longitud del punto de check-out.
     * @param workSummary Resumen o notas del trabajo realizado.
     * @return La visita actualizada con el estado DONE.
     */
    Visit checkOut(UUID visitId, UUID actorId, OffsetDateTime when, Double lat, Double lng, String workSummary);

    /**
     * Cancela una visita planificada antes de que inicie.
     *
     * @param visitId Identificador de la visita.
     * @param actorId Identificador del usuario que ejecuta la cancelación.
     */
    void cancel(UUID visitId, UUID actorId);

    /**
     * Lista las visitas aplicando filtros opcionales como cliente, técnico, estado o rango de fechas.
     *
     * @param customerId Identificador del cliente (opcional).
     * @param technicianId Identificador del técnico (opcional).
     * @param state Estado actual de la visita (opcional).
     * @param from Fecha y hora inicial del rango (opcional).
     * @param to Fecha y hora final del rango (opcional).
     * @param pageable Parámetros de paginación.
     * @return Página con la lista de visitas filtradas.
     */
    Page<Visit> list(UUID customerId, UUID technicianId, VisitState state,
                     OffsetDateTime from, OffsetDateTime to, Pageable pageable);

    /**
     * Obtiene la lista de visitas asignadas a un técnico para el día actual.
     *
     * @param technicianId Identificador del técnico.
     * @param today Fecha actual.
     * @return Lista de visitas programadas para el día.
     */
    List<Visit> myVisitsToday(UUID technicianId, LocalDate today);

    /**
     * Obtiene la lista de eventos registrados para una visita específica.
     *
     * @param visitId Identificador de la visita.
     * @return Lista de eventos asociados a esa visita.
     */
    List<VisitEvent> events(UUID visitId);

    /**
     * Agrega una nota a una visita existente.
     *
     * @param visitId Identificador de la visita.
     * @param authorId Identificador del autor de la nota.
     * @param visibility Nivel de visibilidad de la nota (INTERNAL o CUSTOMER).
     * @param body Contenido de la nota.
     * @return Lista actualizada de notas de la visita.
     */
    List<VisitNote> addNote(UUID visitId, UUID authorId, NoteVisibility visibility, String body);

    Visit getById(UUID visitId);
}