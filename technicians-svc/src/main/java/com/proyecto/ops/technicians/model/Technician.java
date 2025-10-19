/*
 * -----------------------------------------------------------------------------
 * Technician.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Representa la entidad JPA que modela a un técnico dentro del microservicio
 *   "technicians-svc". Contiene información relacionada con el usuario asignado,
 *   su estado y las habilidades que posee.
 *
 * Contexto de uso:
 *   - Cada técnico corresponde a un usuario existente en el sistema (userId).
 *   - Se utiliza para registrar y gestionar los técnicos disponibles en la base de datos.
 *
 * Diseño:
 *   - Mapeada a la tabla `app.technicians`.
 *   - Usa UUID como identificador único.
 *   - Incluye:
 *       • userId → Relación con el usuario del sistema.
 *       • userName → Nombre del usuario (solo informativo).
 *       • skills → Lista de habilidades técnicas (almacenadas como arreglo de texto).
 *       • active → Indica si el técnico está activo o no.
 *       • createdAt → Fecha de creación generada automáticamente.
 *
 * Mantenibilidad:
 *   - Si se agregan nuevas propiedades (por ejemplo, certificaciones o zonas de trabajo),
 *     deben incluirse en esta clase y reflejarse en la base de datos mediante una migración.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.technicians.model;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad JPA que representa un técnico registrado en el sistema.
 *
 * Mapeada a la tabla `app.technicians`.
 */
@Entity
@Table(schema = "app", name = "technicians")
public class Technician {

    // Identificador único del técnico (clave primaria).
    @Id
    @GeneratedValue
    private UUID id;

    // Nombre del usuario asociado al técnico (informativo).
    private String userName;

    // Identificador del usuario en el servicio de autenticación (obligatorio y único).
    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    // Indica si el técnico está activo o inactivo dentro del sistema.
    @Column(nullable = false)
    private boolean active = true;

    // Lista de habilidades del técnico (almacenadas como arreglo de texto en la base de datos).
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[]")
    private List<String> skills;

    // Fecha y hora de creación del registro (se genera automáticamente al insertar el registro).
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    /**
     * Métodos de acceso (getters y setters) para los campos del modelo Technician.
     */
    // Getters/Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
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
}
