package com.visits.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "visit_notes",
    schema = "app",
    indexes = { @Index(name = "idx_notes_visit", columnList = "visit_id") }
)
public class VisitNote {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "visit_id", nullable = false)
    private UUID visitId;

    @Column(name = "author_id", nullable = false)
    private UUID authorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false, length = 20)
    private NoteVisibility visibility;

    @Column(name = "body", nullable = false, length = 4000)
    private String body;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    public VisitNote() {}

    public static VisitNote of(UUID visitId, UUID authorId, NoteVisibility visibility, String body) {
        VisitNote n = new VisitNote();
        n.id = UUID.randomUUID();
        n.visitId = visitId;
        n.authorId = authorId;
        n.visibility = visibility;
        n.body = body;
        return n;
    }

    // getters/setters
    public UUID getId() { return id; }
    public UUID getVisitId() { return visitId; }
    public UUID getAuthorId() { return authorId; }
    public NoteVisibility getVisibility() { return visibility; }
    public String getBody() { return body; }
    public OffsetDateTime getCreatedAt() { return createdAt; }

    public void setId(UUID id) { this.id = id; }
    public void setVisitId(UUID visitId) { this.visitId = visitId; }
    public void setAuthorId(UUID authorId) { this.authorId = authorId; }
    public void setVisibility(NoteVisibility visibility) { this.visibility = visibility; }
    public void setBody(String body) { this.body = body; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}