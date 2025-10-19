/*
 * -----------------------------------------------------------------------------
 * WorkOrder.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Representa la entidad de "Orden de Trabajo" (Work Order) dentro del microservicio
 *   "work-orders-svc". Cada instancia modela una tarea asignada a un técnico que
 *   está asociada a un ticket de servicio.
 *
 * Contexto de uso:
 *   - Mapeada a la tabla "app.work_orders" en la base de datos.
 *   - Utilizada para registrar el ciclo de vida completo de una orden:
 *       • Asignación de ticket
 *       • Programación
 *       • Inicio y finalización
 *       • Notas de ejecución
 *
 * Diseño:
 *   - Anotada con @Entity y @Table para habilitar el mapeo JPA.
 *   - Usa UUID como identificador único con generación automática (@UuidGenerator).
 *   - Incluye campos de trazabilidad temporal (createdAt, scheduledAt, startedAt, endedAt).
 *
 * Mantenibilidad:
 *   - La clase está preparada para integrarse con otros microservicios (como tickets-svc)
 *     mediante referencias externas (ticketId, technicianId).
 *   - Puede ampliarse para incluir auditoría o relaciones bidireccionales en el futuro.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.workorders.model;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad JPA que representa una orden de trabajo (Work Order) asociada a un ticket.
 *
 * Contiene información sobre programación, ejecución, técnico asignado y estado actual.
 */
@Entity
@Table(schema = "app", name = "work_orders")
public class WorkOrder {

    // Identificador único de la orden de trabajo (UUID generado automáticamente).
  @Id
  @UuidGenerator
  @JdbcTypeCode(SqlTypes.UUID)
  private UUID id;

    // Identificador del ticket asociado a esta orden de trabajo (referencia externa).
  @JdbcTypeCode(SqlTypes.UUID)
  @Column(name = "ticket_id", nullable = false)
  private UUID ticketId;

    // Identificador del técnico asignado a la orden (puede ser nulo si aún no se asigna).
  @JdbcTypeCode(SqlTypes.UUID)
  @Column(name = "technician_id")
  private UUID technicianId;

    // Estado actual de la orden (por defecto: PENDING). Se almacena como enumeración en base de datos.
  @Enumerated(EnumType.STRING)
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  @Column(name = "status", columnDefinition = "app.wo_status", nullable = false)
  private WoStatus status = WoStatus.PENDING;

    // Fecha y hora programadas para que el técnico ejecute la orden.
  @Column(name = "scheduled_at", columnDefinition = "timestamptz")
  private OffsetDateTime scheduledAt;

    // Fecha y hora en que el técnico inició la orden.
  @Column(name = "started_at", columnDefinition = "timestamptz")
  private OffsetDateTime startedAt;

    // Fecha y hora en que el técnico completó la orden.
  @Column(name = "ended_at", columnDefinition = "timestamptz")
  private OffsetDateTime endedAt;

    // Campo de texto libre para registrar observaciones o comentarios del técnico.
  @Column(columnDefinition = "text")
  private String notes;

    // Fecha y hora de creación del registro (asignada automáticamente por la base de datos).
  @Column(name = "created_at", nullable = false, updatable = false, insertable = false, columnDefinition = "timestamptz")
  private OffsetDateTime createdAt;

      // Métodos de acceso (getters y setters) generados para manipular los campos de la entidad.
      // Permiten la serialización/deserialización automática en las operaciones de persistencia.
  // getters/setters
  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }

  public UUID getTicketId() { return ticketId; }
  public void setTicketId(UUID ticketId) { this.ticketId = ticketId; }

  public UUID getTechnicianId() { return technicianId; }
  public void setTechnicianId(UUID technicianId) { this.technicianId = technicianId; }

  public WoStatus getStatus() { return status; }
  public void setStatus(WoStatus status) { this.status = status; }

  public OffsetDateTime getScheduledAt() { return scheduledAt; }
  public void setScheduledAt(OffsetDateTime scheduledAt) { this.scheduledAt = scheduledAt; }

  public OffsetDateTime getStartedAt() { return startedAt; }
  public void setStartedAt(OffsetDateTime startedAt) { this.startedAt = startedAt; }

  public OffsetDateTime getEndedAt() { return endedAt; }
  public void setEndedAt(OffsetDateTime endedAt) { this.endedAt = endedAt; }

  public String getNotes() { return notes; }
  public void setNotes(String notes) { this.notes = notes; }

  public OffsetDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}