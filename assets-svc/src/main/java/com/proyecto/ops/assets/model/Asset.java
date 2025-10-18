package com.proyecto.ops.assets.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "assets", schema = "app")
public class Asset {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "site_id")
    private UUID siteId;

    @Column(name = "serial_number")
    private String serialNumber;

    @Column(name = "model")
    private String model;

    @Column(name = "type")
    private String type;

    @Column(name = "installed_at")
    private LocalDate installedAt;

    @Column(name = "notes")
    private String notes;

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