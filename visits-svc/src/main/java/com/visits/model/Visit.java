/*
 * -----------------------------------------------------------------------------
 * Visit.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Representa la entidad JPA correspondiente a la tabla `app.visits`, utilizada
 *   para registrar las visitas planificadas, en curso o completadas por técnicos.
 *
 * Contexto de uso:
 *   - Pertenece al microservicio "visits-svc".
 *   - Permite controlar el ciclo de vida de una visita (PLANNED → STARTED → DONE)
 *     así como sus validaciones y tiempos de ejecución.
 *
 * Diseño:
 *   - Anotada con @Entity y @Table(schema = "app", name = "visits").
 *   - Utiliza UUID como identificadores únicos para cada entidad.
 *   - Incluye anotaciones de validación (Jakarta Validation) y de auditoría
 *     (@CreationTimestamp, @UpdateTimestamp).
 *
 * Campos principales:
 *   • customerId, siteId, technicianId → Identificadores clave.
 *   • state, priority → Estado actual y prioridad de la visita.
 *   • scheduledStartAt / scheduledEndAt → Ventana de tiempo planificada.
 *   • checkInAt / checkOutAt → Tiempos reales de ejecución.
 *
 * Mantenibilidad:
 *   - Las reglas de transición se controlan mediante métodos específicos
 *     (start, complete, cancel, noShow).
 *   - validateDates() asegura coherencia temporal antes de persistir.
 * -----------------------------------------------------------------------------
 */
package com.visits.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Entidad JPA que representa una visita dentro del sistema.
 *
 * Incluye atributos planificados, tiempos de ejecución y reglas de transición.
 */
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
    // Identificador único de la visita (clave primaria).
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    // Identificador del cliente asociado a la visita.
    @NotNull
    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    // Identificador del sitio del cliente donde se realiza la visita.
    @NotNull
    @Column(name = "site_id", nullable = false)
    private UUID siteId;

    // Identificador del técnico asignado a la visita.
    @NotNull
    @Column(name = "technician_id", nullable = false)
    private UUID technicianId;

    // ---- Estado / prioridad ----
    // Estado actual de la visita (PLANNED, STARTED, DONE, CANCELLED, NO_SHOW).
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false, length = 20)
    private VisitState state;

    // Nivel de prioridad de la visita (LOW, MEDIUM, HIGH).
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 10)
    private VisitPriority priority;

    // ---- Datos planificados ----
    // Propósito o descripción breve de la visita planificada.
    @Size(max = 200)
    @Column(name = "purpose", length = 200)
    private String purpose;

    // Fecha y hora planificadas de inicio de la visita.
    @NotNull
    @Column(name = "scheduled_start_at", nullable = false)
    private OffsetDateTime scheduledStartAt;

    // Fecha y hora planificadas de finalización de la visita.
    @NotNull
    @Column(name = "scheduled_end_at", nullable = false)
    private OffsetDateTime scheduledEndAt;

    // Notas adicionales ingresadas al planificar la visita.
    @Size(max = 2000)
    @Column(name = "notes_planned", length = 2000)
    private String notesPlanned;

    // ---- Tiempos de ejecución ----
    // Marca de tiempo de inicio real (cuando el técnico hace check-in).
    @Column(name = "check_in_at")
    private OffsetDateTime checkInAt;

    // Marca de tiempo de finalización real (cuando el técnico completa la visita).
    @Column(name = "check_out_at")
    private OffsetDateTime checkOutAt;

    // ---- Auditoría ----
    // Fecha y hora en que se creó el registro (asignado automáticamente).
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    // Fecha y hora de la última actualización del registro (automática).
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    // ---- Constructores ----
    public Visit() {
        // JPA
    }

    // ---- Factories eficientes ----
    /**
     * Crea una instancia de visita planificada con valores seguros por defecto.
     *
     * @param customerId   Identificador del cliente.
     * @param siteId       Identificador del sitio asociado.
     * @param technicianId Identificador del técnico asignado.
     * @param start        Fecha y hora de inicio planificada.
     * @param end          Fecha y hora de finalización planificada.
     * @param priority     Prioridad asignada (por defecto: MEDIUM).
     * @param purpose      Propósito o descripción de la visita.
     * @param notesPlanned Notas adicionales.
     * @return Objeto Visit configurado en estado PLANNED.
     */
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
        // Crea una nueva instancia de visita y asigna valores iniciales por defecto.
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
    /**
     * Inicia la visita (cambia estado de PLANNED a STARTED).
     * Si no existe checkInAt, se asigna la marca de tiempo actual.
     *
     * @param when Fecha y hora en que se inicia la visita.
     */
    public void start(OffsetDateTime when) {
        // Verifica que la visita esté en estado PLANNED antes de iniciarla.
        if (this.state != VisitState.PLANNED) {
            throw new IllegalStateException("Solo se puede iniciar desde PLANNED.");
        }
        this.state = VisitState.STARTED;
        if (this.checkInAt == null) this.checkInAt = when;
    }

    /**
     * Completa la visita (cambia estado de STARTED a DONE).
     * Si no existe checkOutAt, se asigna la marca de tiempo actual.
     *
     * @param when Fecha y hora de finalización de la visita.
     */
    public void complete(OffsetDateTime when) {
        // Valida que solo las visitas en estado STARTED puedan completarse.
        if (this.state != VisitState.STARTED) {
            throw new IllegalStateException("Solo se puede completar desde STARTED.");
        }
        this.state = VisitState.DONE;
        if (this.checkOutAt == null) this.checkOutAt = when;
    }

    /**
     * Cancela la visita (solo puede hacerse desde el estado PLANNED).
     */
    public void cancel() {
        // Valida que la cancelación solo sea posible si la visita aún no ha iniciado.
        if (this.state != VisitState.PLANNED) {
            throw new IllegalStateException("Solo se puede cancelar desde PLANNED.");
        }
        this.state = VisitState.CANCELLED;
    }

    /**
     * Marca la visita como no presentada (PLANNED → NO_SHOW).
     * Usada por procesos automáticos (por ejemplo, un job nocturno).
     */
    public void noShow() {
        // Valida que solo las visitas planificadas puedan marcarse como no presentadas.
        if (this.state != VisitState.PLANNED) {
            throw new IllegalStateException("Solo se puede marcar NO_SHOW desde PLANNED.");
        }
        this.state = VisitState.NO_SHOW;
    }

    // ---- Validaciones ligeras (llamar desde el servicio antes de persistir) ----
    /**
     * Valida las fechas planificadas y ejecutadas para garantizar coherencia temporal.
     *
     * Lanza excepciones si:
     *   - Las fechas planificadas son nulas.
     *   - La fecha de fin no es posterior a la de inicio.
     *   - El check-out no es posterior al check-in (si ambos existen).
     */
    public void validateDates() {
        // Verifica que las fechas planificadas estén presentes y en orden cronológico correcto.
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