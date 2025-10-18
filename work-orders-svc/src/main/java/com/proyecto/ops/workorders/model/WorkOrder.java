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

@Entity
@Table(schema = "app", name = "work_orders")
public class WorkOrder {

  @Id
  @UuidGenerator
  @JdbcTypeCode(SqlTypes.UUID)
  private UUID id;

  @JdbcTypeCode(SqlTypes.UUID)
  @Column(name = "ticket_id", nullable = false)
  private UUID ticketId;

  @JdbcTypeCode(SqlTypes.UUID)
  @Column(name = "technician_id")
  private UUID technicianId;

  @Enumerated(EnumType.STRING)
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  @Column(name = "status", columnDefinition = "app.wo_status", nullable = false)
  private WoStatus status = WoStatus.PENDING;

  @Column(name = "scheduled_at", columnDefinition = "timestamptz")
  private OffsetDateTime scheduledAt;

  @Column(name = "started_at", columnDefinition = "timestamptz")
  private OffsetDateTime startedAt;

  @Column(name = "ended_at", columnDefinition = "timestamptz")
  private OffsetDateTime endedAt;

  @Column(columnDefinition = "text")
  private String notes;

  @Column(name = "created_at", nullable = false, updatable = false, insertable = false, columnDefinition = "timestamptz")
  private OffsetDateTime createdAt;

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