package com.visits.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "visit_events",
    schema = "app",
    indexes = { @Index(name = "idx_events_visit", columnList = "visit_id") }
)
public class VisitEvent {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "visit_id", nullable = false)
    private UUID visitId;

    @Column(name = "type", nullable = false, length = 50)
    private String type;

    @Column(name = "actor_id")
    private UUID actorId;

    @Column(name = "geo_lat")
    private Double geoLat;

    @Column(name = "geo_lng")
    private Double geoLng;

    @Column(name = "payload", columnDefinition = "text")
    private String payload;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    public VisitEvent() {}

    public static VisitEvent of(UUID visitId, String type, UUID actorId, Double lat, Double lng, String payload) {
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