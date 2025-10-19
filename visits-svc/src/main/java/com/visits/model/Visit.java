package com.visits.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "visits",
    schema = "app",
    indexes = {
        @Index(name = "idx_visits_technician_start", columnList = "technician_id,scheduled_start_at"),
        @Index(name = "idx_visits_state", columnList = "state")
    }
)
public class Visit {

    // ---- Identificadores ----
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @NotNull
    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @NotNull
    @Column(name = "site_id", nullable = false)
    private UUID siteId;

    @NotNull
    @Column(name = "technician_id", nullable = false)
    private UUID technicianId;

    // ---- Estado / prioridad ----
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false, length = 20)
    private VisitState state;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 10)
    private VisitPriority priority;

    // ---- Datos planificados ----
    @Size(max = 200)
    @Column(name = "purpose", length = 200)
    private String purpose;

    @NotNull
    @Column(name = "scheduled_start_at", nullable = false)
    private OffsetDateTime scheduledStartAt;

    @NotNull
    @Column(name = "scheduled_end_at", nullable = false)
    private OffsetDateTime scheduledEndAt;

    @Size(max = 2000)
    @Column(name = "notes_planned", length = 2000)
    private String notesPlanned;

    // ---- Tiempos de ejecución ----
    @Column(name = "check_in_at")
    private OffsetDateTime checkInAt;

    @Column(name = "check_out_at")
    private OffsetDateTime checkOutAt;

    // ---- Auditoría ----
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    // ---- Constructores ----
    public Visit() {
        // JPA
    }

    // ---- Factories eficientes ----
    /** Crea una visita planificada con valores seguros por defecto. */
    public static Visit planned(
            UUID customerId,
            UUID siteId,
            UUID technicianId,
            OffsetDateTime start,
            OffsetDateTime end,
            VisitPriority priority,
            String purpose,
            String notesPlanned
    ) {
        Visit visit = new Visit();
        visit.setId(UUID.randomUUID());
        visit.setCustomerId(customerId);
        visit.setSiteId(siteId);
        visit.setTechnicianId(technicianId);
        visit.setState(VisitState.PLANNED);
        visit.setPriority(priority == null ? VisitPriority.MEDIUM : priority);
        visit.setPurpose(purpose);
        visit.setScheduledStartAt(start);
        visit.setScheduledEndAt(end);
        visit.setNotesPlanned(notesPlanned);
        return visit;
    }

    // ---- Reglas de transición básicas ----
    /** Inicia la visita (PLANNED -> STARTED) y setea checkInAt si viene null. */
    public void start(OffsetDateTime when) {
        if (this.state != VisitState.PLANNED) {
            throw new IllegalStateException("Solo se puede iniciar desde PLANNED.");
        }
        this.state = VisitState.STARTED;
        if (this.checkInAt == null) this.checkInAt = when;
    }

    /** Completa la visita (STARTED -> DONE) y setea checkOutAt si viene null. */
    public void complete(OffsetDateTime when) {
        if (this.state != VisitState.STARTED) {
            throw new IllegalStateException("Solo se puede completar desde STARTED.");
        }
        this.state = VisitState.DONE;
        if (this.checkOutAt == null) this.checkOutAt = when;
    }

    /** Cancela la visita (PLANNED -> CANCELLED). */
    public void cancel() {
        if (this.state != VisitState.PLANNED) {
            throw new IllegalStateException("Solo se puede cancelar desde PLANNED.");
        }
        this.state = VisitState.CANCELLED;
    }

    /** Marca como no presentada (PLANNED -> NO_SHOW). Útil para job nocturno. */
    public void noShow() {
        if (this.state != VisitState.PLANNED) {
            throw new IllegalStateException("Solo se puede marcar NO_SHOW desde PLANNED.");
        }
        this.state = VisitState.NO_SHOW;
    }

    // ---- Validaciones ligeras (llamar desde el servicio antes de persistir) ----
    /** Valida rangos de fechas mínimos. */
    public void validateDates() {
        if (scheduledStartAt == null || scheduledEndAt == null) {
            throw new IllegalArgumentException("Las fechas planificadas son obligatorias.");
        }
        if (!scheduledEndAt.isAfter(scheduledStartAt)) {
            throw new IllegalArgumentException("scheduled_end_at debe ser posterior a scheduled_start_at.");
        }
        if (checkInAt != null && checkOutAt != null && !checkOutAt.isAfter(checkInAt)) {
            throw new IllegalArgumentException("check_out_at debe ser posterior a check_in_at.");
        }
    }

    // ---- Getters y Setters ----
    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCustomerId() {
        return customerId;
    }
    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public UUID getSiteId() {
        return siteId;
    }
    public void setSiteId(UUID siteId) {
        this.siteId = siteId;
    }

    public UUID getTechnicianId() {
        return technicianId;
    }
    public void setTechnicianId(UUID technicianId) {
        this.technicianId = technicianId;
    }

    public VisitState getState() {
        return state;
    }
    public void setState(VisitState state) {
        this.state = state;
    }

    public VisitPriority getPriority() {
        return priority;
    }
    public void setPriority(VisitPriority priority) {
        this.priority = priority;
    }

    public String getPurpose() {
        return purpose;
    }
    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public OffsetDateTime getScheduledStartAt() {
        return scheduledStartAt;
    }
    public void setScheduledStartAt(OffsetDateTime scheduledStartAt) {
        this.scheduledStartAt = scheduledStartAt;
    }

    public OffsetDateTime getScheduledEndAt() {
        return scheduledEndAt;
    }
    public void setScheduledEndAt(OffsetDateTime scheduledEndAt) {
        this.scheduledEndAt = scheduledEndAt;
    }

    public String getNotesPlanned() {
        return notesPlanned;
    }
    public void setNotesPlanned(String notesPlanned) {
        this.notesPlanned = notesPlanned;
    }

    public OffsetDateTime getCheckInAt() {
        return checkInAt;
    }
    public void setCheckInAt(OffsetDateTime checkInAt) {
        this.checkInAt = checkInAt;
    }

    public OffsetDateTime getCheckOutAt() {
        return checkOutAt;
    }
    public void setCheckOutAt(OffsetDateTime checkOutAt) {
        this.checkOutAt = checkOutAt;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}