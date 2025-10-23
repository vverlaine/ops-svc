/*
 * -----------------------------------------------------------------------------
 * Supervisor.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Representa la entidad JPA que modela a un supervisor dentro del microservicio
 *   "supervisors-svc". Contiene información relacionada con el usuario asignado,
 *   su estado y el equipo que administra.
 *
 * Contexto de uso:
 *   - Cada supervisor corresponde a un usuario existente en el sistema (userId).
 *   - Se sincroniza automáticamente desde la tabla app.users mediante un trigger de base de datos.
 *
 * Diseño:
 *   - Mapeada a la tabla `app.supervisors`.
 *   - Usa UUID como identificador único.
 *   - Incluye:
 *       • userId → Relación con el usuario del sistema.
 *       • userName → Nombre del usuario (solo informativo).
 *       • teamId → Identificador del equipo que lidera.
 *       • active → Indica si el técnico está activo o no.
 *       • createdAt → Fecha de creación generada automáticamente.
 *
 * Mantenibilidad:
 *   - Si se agregan nuevas propiedades,
 *     deben incluirse en esta clase y reflejarse en la base de datos mediante una migración.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.supervisors.model;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

/**
 * Entidad JPA que representa un supervisor registrado en el sistema.
 *
 * Mapeada a la tabla `app.supervisors`.
 */
@Entity
@Table(schema = "app", name = "supervisors")
public class Supervisor {

    // Nombre del usuario asociado al supervisor (informativo).
    @Column(name = "user_name")
    private String userName;

    // Identificador del usuario en el servicio de autenticación (obligatorio y único).
    @Column(name = "user_id", nullable = false, unique = true)
    @Id
    private UUID userId;

    // Indica si el supervisor está activo o inactivo dentro del sistema.
    @Column(nullable = false)
    private boolean active = true;

    // Identificador del equipo a cargo.
    @Column(name = "team_id", unique = true, nullable = false)
    private UUID teamId;

    // Fecha y hora de creación del registro (se genera automáticamente al insertar el registro).
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    /**
     * Métodos de acceso (getters y setters) para los campos del modelo Supervisor.
     */
    // Getters/Setters
    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public UUID getTeamId() {
        return teamId;
    }

    public void setTeamId(UUID teamId) {
        this.teamId = teamId;
    }
}
