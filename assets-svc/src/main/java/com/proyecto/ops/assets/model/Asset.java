/*
 * -----------------------------------------------------------------------------
 * Asset.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Entidad JPA que representa un activo físico (asset) perteneciente a un cliente
 *   dentro del sistema. Se persiste en la tabla "assets" del esquema "app".
 *
 * Contexto de uso:
 *   - Esta clase es parte del dominio de assets dentro del servicio assets-svc.
 *   - Permite registrar y consultar información de equipos, dispositivos o
 *     componentes instalados en un sitio asociado a un cliente.
 *
 * Diseño:
 *   - Utiliza anotaciones JPA (jakarta.persistence) para mapear los campos a columnas.
 *   - Cada activo tiene un identificador único (UUID) generado automáticamente.
 *   - Incluye referencias a customer_id y site_id como llaves foráneas lógicas
 *     para relacionarlo con clientes y sitios.
 *   - La fecha de creación se inicializa automáticamente al momento de instanciación.
 *
 * Campos principales:
 *   id             → Identificador único del activo.
 *   customerId     → UUID del cliente propietario.
 *   siteId         → UUID del sitio donde está instalado (opcional).
 *   serialNumber   → Número de serie del equipo.
 *   model          → Modelo o versión del activo.
 *   type           → Tipo o categoría del activo.
 *   installedAt    → Fecha de instalación (LocalDate).
 *   notes          → Notas descriptivas u observaciones del activo.
 *   createdAt      → Fecha y hora en que se creó el registro (Instant).
 *
 * Mantenibilidad:
 *   - Los getters y setters permiten la manipulación controlada de los campos.
 *   - Si se añaden nuevas columnas a la tabla "assets", deben reflejarse aquí.
 *   - Puede ampliarse para incluir auditoría o relaciones JPA adicionales.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.assets.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad JPA que representa un activo físico en el sistema.
 *
 * Se almacena en la tabla `app.assets` y contiene información
 * básica del equipo, cliente y sitio asociado.
 */
@Entity
@Table(name = "assets", schema = "app")
public class Asset {
    // Identificador único del activo (UUID autogenerado).
    @Id
    @GeneratedValue
    private UUID id;

    // UUID del cliente al que pertenece el activo. No puede ser nulo.
    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    // UUID del sitio donde está instalado el activo (opcional).
    @Column(name = "site_id")
    private UUID siteId;

    // Número de serie del activo para identificación física.
    @Column(name = "serial_number")
    private String serialNumber;

    // Modelo del activo (por ejemplo: "HP LaserJet 2000").
    @Column(name = "model")
    private String model;

    // Tipo o categoría del activo (por ejemplo: "Impresora", "Escáner").
    @Column(name = "type")
    private String type;

    // Fecha de instalación o puesta en servicio del activo.
    @Column(name = "installed_at")
    private LocalDate installedAt;

    // Notas u observaciones adicionales sobre el activo.
    @Column(name = "notes")
    private String notes;

    // Fecha y hora de creación del registro. Se inicializa automáticamente.
    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt = Instant.now();

    // Getters & Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }

    public UUID getSiteId() { return siteId; }
    public void setSiteId(UUID siteId) { this.siteId = siteId; }

    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public LocalDate getInstalledAt() { return installedAt; }
    public void setInstalledAt(LocalDate installedAt) { this.installedAt = installedAt; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}