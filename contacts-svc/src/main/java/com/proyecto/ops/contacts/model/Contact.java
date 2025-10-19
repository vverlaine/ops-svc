/*
 * -----------------------------------------------------------------------------
 * Contact.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Entidad JPA que representa un contacto asociado a un cliente dentro del sistema.
 *   Cada contacto puede tener un nombre, correo electrónico, teléfono y rol específico.
 *
 * Contexto de uso:
 *   - Pertenece al microservicio contacts-svc.
 *   - Se utiliza para registrar y consultar personas de contacto asociadas a clientes.
 *
 * Diseño:
 *   - Implementa las anotaciones JPA (@Entity, @Table, @Column, @Id, @GeneratedValue)
 *     para mapear la clase a la tabla `app.contacts`.
 *   - El identificador `id` es un UUID autogenerado.
 *   - Incluye los campos básicos de un contacto con sus respectivas restricciones.
 *
 * Campos principales:
 *   id          → Identificador único del contacto (UUID).
 *   customerId  → UUID del cliente asociado (no nulo).
 *   name        → Nombre completo del contacto.
 *   email       → Correo electrónico del contacto (opcional).
 *   phone       → Número telefónico del contacto (opcional).
 *   role        → Cargo o función del contacto dentro del cliente.
 *   createdAt   → Fecha y hora de creación del registro.
 *
 * Mantenibilidad:
 *   - Si se agregan nuevos campos en la tabla, deben reflejarse en esta clase.
 *   - Puede extenderse para incluir relaciones con otras entidades (ej. Customer).
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.contacts.model;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad JPA que modela un contacto de cliente.
 *
 * Se almacena en la tabla `app.contacts` y contiene información de identificación
 * y comunicación del contacto.
 */
@Entity
@Table(name = "contacts", schema = "app")
public class Contact {

    // Identificador único del contacto (UUID autogenerado).
    @Id
    @GeneratedValue
    private UUID id;

    // UUID del cliente asociado. No puede ser nulo.
    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    // Nombre completo del contacto (obligatorio).
    @Column(nullable = false)
    private String name;

    // Correo electrónico del contacto (opcional).
    private String email;
    // Número telefónico del contacto (opcional).
    private String phone;
    // Rol o cargo del contacto dentro de la organización del cliente.
    private String role;

    // Fecha de creación del registro, generada automáticamente por la base de datos.
    @Column(name = "created_at", updatable = false, insertable = false)
    private OffsetDateTime createdAt;

    // Getters & Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}