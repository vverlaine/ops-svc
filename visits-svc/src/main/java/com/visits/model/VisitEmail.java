package com.visits.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "visit_emails",
    schema = "app",
    indexes = { @Index(name = "idx_emails_visit", columnList = "visit_id") }
)
public class VisitEmail {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "visit_id", nullable = false)
    private UUID visitId;

    @Column(name = "to_email", nullable = false, length = 320)
    private String toEmail;

    @Column(name = "subject", length = 300)
    private String subject;

    @Column(name = "status", length = 30)
    private String status; // SENT, FAILED, PENDING

    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    public VisitEmail() {}

    public static VisitEmail pending(UUID visitId, String toEmail, String subject) {
        VisitEmail ve = new VisitEmail();
        ve.id = UUID.randomUUID();
        ve.visitId = visitId;
        ve.toEmail = toEmail;
        ve.subject = subject;
        ve.status = "PENDING";
        return ve;
    }

    // getters/setters
    public UUID getId() { return id; }
    public UUID getVisitId() { return visitId; }
    public String getToEmail() { return toEmail; }
    public String getSubject() { return subject; }
    public String getStatus() { return status; }
    public String getErrorMessage() { return errorMessage; }
    public OffsetDateTime getCreatedAt() { return createdAt; }

    public void setId(UUID id) { this.id = id; }
    public void setVisitId(UUID visitId) { this.visitId = visitId; }
    public void setToEmail(String toEmail) { this.toEmail = toEmail; }
    public void setSubject(String subject) { this.subject = subject; }
    public void setStatus(String status) { this.status = status; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}