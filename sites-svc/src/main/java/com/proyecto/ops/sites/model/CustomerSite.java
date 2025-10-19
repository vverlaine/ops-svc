/*
 * -----------------------------------------------------------------------------
 * CustomerSite.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Representa la entidad JPA que almacena los sitios o ubicaciones asociadas
 *   a un cliente dentro del microservicio "sites-svc".
 *
 * Contexto de uso:
 *   - Cada sitio pertenece a un cliente (relación 1:N entre Customer y CustomerSite).
 *   - Se utiliza para registrar la dirección física, ciudad, estado y país
 *     de cada ubicación asociada.
 *
 * Diseño:
 *   - Mapeada a la tabla `app.customer_sites`.
 *   - Utiliza un identificador único generado automáticamente (UUID).
 *   - Incluye un campo `created_at` gestionado por la base de datos para
 *     registrar la fecha y hora de creación.
 *
 * Campos principales:
 *   id          → Identificador único del sitio.
 *   customerId  → Referencia al cliente propietario del sitio.
 *   name        → Nombre o descripción del sitio.
 *   address     → Dirección física del sitio.
 *   city        → Ciudad donde se ubica.
 *   state       → Estado o provincia.
 *   country     → País.
 *   createdAt   → Fecha de creación (establecida automáticamente por la BD).
 *
 * Mantenibilidad:
 *   - Si se agregan nuevas características (por ejemplo, coordenadas geográficas),
 *     deben añadirse como nuevos campos con sus respectivas anotaciones JPA.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.sites.model;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad JPA que representa un sitio o ubicación asociada a un cliente.
 *
 * Mapeada a la tabla `app.customer_sites`.
 */
@Entity
@Table(name = "customer_sites", schema = "app")
public class CustomerSite {

    // Identificador único del sitio (clave primaria).
    @Id
    @GeneratedValue
    private UUID id;

    // Identificador del cliente propietario del sitio (relación 1:N).
    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    // Nombre o descripción del sitio (obligatorio).
    @Column(nullable = false)
    private String name;

    // Dirección física del sitio.
    private String address;
    // Ciudad donde se ubica el sitio.
    private String city;
    // Estado o provincia del sitio.
    private String state;
    // País donde se ubica el sitio.
    private String country;

    // Fecha y hora de creación del registro (asignada automáticamente por la base de datos).
    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private OffsetDateTime createdAt;

    /**
     * Métodos getter y setter para acceder y modificar los atributos del sitio.
     */
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}