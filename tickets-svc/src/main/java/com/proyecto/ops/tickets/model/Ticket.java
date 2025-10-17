package com.proyecto.ops.tickets.model;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import jakarta.persistence.*;
import jakarta.persistence.PrePersist;

@Entity
@Table(schema = "app", name = "tickets")
public class Ticket {

    public Ticket() {
    }

    @Id
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @NotBlank
    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", columnDefinition = "app.ticket_status", nullable = false)
    private TicketStatus status;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "priority", columnDefinition = "app.ticket_priority", nullable = false)
    private TicketPriority priority;

    @NotNull
    @JdbcTypeCode(SqlTypes.UUID)
    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @JdbcTypeCode(SqlTypes.UUID)
    @Column(name = "asset_id")
    private UUID assetId;

    @NotBlank
    @Column(name = "created_by", nullable = false, length = 200)
    private String createdBy;

    @Column(name = "created_at", nullable = false, columnDefinition = "timestamptz")
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public TicketPriority getPriority() {
        return priority;
    }

    public void setPriority(TicketPriority priority) {
        this.priority = priority;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public UUID getAssetId() {
        return assetId;
    }

    public void setAssetId(UUID assetId) {
        this.assetId = assetId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @PrePersist
    void prePersistDefaults() {
        if (this.status == null) {
            this.status = TicketStatus.OPEN;
        }
        if (this.priority == null) {
            this.priority = TicketPriority.MEDIUM;
        }
        if (this.createdAt == null) {
            this.createdAt = OffsetDateTime.now();
        }
    }
}
