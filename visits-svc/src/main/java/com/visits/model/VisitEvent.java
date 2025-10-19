/*
 * -----------------------------------------------------------------------------
 * VisitEvent.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Representa la entidad JPA correspondiente a la tabla `app.visit_events`,
 *   que almacena los eventos ocurridos durante el ciclo de vida de una visita.
 *
 * Contexto de uso:
 *   - Pertenece al microservicio "visits-svc".
 *   - Cada evento refleja una acción o cambio de estado dentro de una visita,
 *     como CHECK_IN, CHECK_OUT, COMPLETED, CANCELLED, etc.
 *
 * Diseño:
 *   - Anotada con @Entity y @Table(schema = "app", name = "visit_events").
 *   - Usa índices para optimizar las consultas por `visit_id`.
 *   - Incluye campos para geolocalización y un `payload` JSON con información adicional.
 *
 * Campos principales:
 *   • id          → Identificador único del evento.
 *   • visitId     → Identificador de la visita asociada.
 *   • type        → Tipo de evento (por ejemplo, STARTED, DONE, CANCELLED).
 *   • actorId     → Usuario o técnico que generó el evento.
 *   • geoLat/lng  → Coordenadas del evento (ubicación donde ocurrió).
 *   • payload     → Información adicional del evento en formato JSON.
 *   • createdAt   → Fecha y hora de creación (asignada automáticamente).
 *
 * Mantenibilidad:
 *   - Si se agregan nuevos tipos de evento o campos, deben reflejarse
 *     en los servicios que registran o consultan eventos.
 * -----------------------------------------------------------------------------
 */
package com.visits.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Entidad JPA que registra los eventos asociados a una visita.
 *
 * Permite almacenar acciones o cambios de estado junto con metadatos como
 * ubicación, actor y datos adicionales.
 */
@Entity
@Table(
    name = "visit_events",
    schema = "app",
    indexes = { @Index(name = "idx_events_visit", columnList = "visit_id") }
)
public class VisitEvent {

    // Identificador único del evento (clave primaria).
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    // Identificador de la visita asociada a este evento.
    @Column(name = "visit_id", nullable = false)
    private UUID visitId;

    // Tipo de evento registrado (por ejemplo, STARTED, CHECK_IN, DONE, etc.).
    @Column(name = "type", nullable = false, length = 50)
    private String type;

    // Identificador del usuario o técnico que ejecutó la acción.
    @Column(name = "actor_id")
    private UUID actorId;

    // Latitud registrada en el momento del evento (opcional).
    @Column(name = "geo_lat")
    private Double geoLat;

    // Longitud registrada en el momento del evento (opcional).
    @Column(name = "geo_lng")
    private Double geoLng;

    // Información adicional o datos contextuales del evento, almacenados como JSON (texto).
    @Column(name = "payload", columnDefinition = "text")
    private String payload;

    // Fecha y hora en que se creó el evento (asignado automáticamente por Hibernate).
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    public VisitEvent() {}

    /**
     * Crea una instancia de evento a partir de los datos proporcionados.
     *
     * @param visitId  Identificador de la visita asociada.
     * @param type     Tipo del evento (por ejemplo, CHECK_IN, CHECK_OUT).
     * @param actorId  Identificador del usuario que generó el evento.
     * @param lat      Latitud donde ocurrió el evento.
     * @param lng      Longitud donde ocurrió el evento.
     * @param payload  Información adicional en formato JSON.
     * @return Instancia de VisitEvent lista para persistir.
     */
    public static VisitEvent of(UUID visitId, String type, UUID actorId, Double lat, Double lng, String payload) {
        // Crea una nueva instancia de evento y asigna los valores iniciales.
        VisitEvent e = new VisitEvent();
        e.id = UUID.randomUUID();
        e.visitId = visitId;
        e.type = type;
        e.actorId = actorId;
        e.geoLat = lat;
        e.geoLng = lng;
        e.payload = payload;
        return e;
    }

    // getters/setters
    public UUID getId() { return id; }
    public UUID getVisitId() { return visitId; }
    public String getType() { return type; }
    public UUID getActorId() { return actorId; }
    public Double getGeoLat() { return geoLat; }
    public Double getGeoLng() { return geoLng; }
    public String getPayload() { return payload; }
    public OffsetDateTime getCreatedAt() { return createdAt; }

    public void setId(UUID id) { this.id = id; }
    public void setVisitId(UUID visitId) { this.visitId = visitId; }
    public void setType(String type) { this.type = type; }
    public void setActorId(UUID actorId) { this.actorId = actorId; }
    public void setGeoLat(Double geoLat) { this.geoLat = geoLat; }
    public void setGeoLng(Double geoLng) { this.geoLng = geoLng; }
    public void setPayload(String payload) { this.payload = payload; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}