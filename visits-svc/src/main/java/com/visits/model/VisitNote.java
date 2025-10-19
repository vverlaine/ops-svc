/*
 * -----------------------------------------------------------------------------
 * VisitNote.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Representa la entidad JPA correspondiente a la tabla `app.visit_notes`,
 *   utilizada para almacenar las notas asociadas a una visita dentro del
 *   microservicio "visits-svc".
 *
 * Contexto de uso:
 *   - Cada nota está vinculada a una visita (visitId) y a un autor (authorId).
 *   - Las notas pueden tener diferentes niveles de visibilidad, controlados
 *     mediante el enum {@link com.visits.model.NoteVisibility}.
 *   - Generalmente son registradas por técnicos o supervisores como parte
 *     de la documentación de la visita.
 *
 * Diseño:
 *   - Anotada con @Entity y @Table(schema = "app", name = "visit_notes").
 *   - Incluye índice sobre `visit_id` para optimizar búsquedas por visita.
 *   - Utiliza UUID como identificador primario.
 *   - La fecha de creación se genera automáticamente mediante @CreationTimestamp.
 *
 * Campos principales:
 *   • id           → Identificador único de la nota.
 *   • visitId      → Referencia a la visita asociada.
 *   • authorId     → Identificador del autor de la nota.
 *   • visibility   → Nivel de visibilidad (INTERNAL o CUSTOMER).
 *   • body         → Contenido textual de la nota.
 *   • createdAt    → Fecha y hora en que se registró la nota.
 *
 * Mantenibilidad:
 *   - Si se agregan nuevos tipos de visibilidad o campos (por ejemplo, archivos adjuntos),
 *     deben reflejarse en esta entidad y en las consultas relacionadas.
 * -----------------------------------------------------------------------------
 */
package com.visits.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Entidad JPA que representa una nota asociada a una visita.
 *
 * Permite almacenar texto, visibilidad y autor de la nota, junto con su fecha de creación.
 */
@Entity
@Table(
    name = "visit_notes",
    schema = "app",
    indexes = { @Index(name = "idx_notes_visit", columnList = "visit_id") }
)
public class VisitNote {

    // Identificador único de la nota (clave primaria).
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    // Identificador de la visita a la que pertenece la nota.
    @Column(name = "visit_id", nullable = false)
    private UUID visitId;

    // Identificador del autor que creó la nota.
    @Column(name = "author_id", nullable = false)
    private UUID authorId;

    // Nivel de visibilidad de la nota (INTERNAL o CUSTOMER).
    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false, length = 20)
    private NoteVisibility visibility;

    // Contenido textual de la nota (máximo 4000 caracteres).
    @Column(name = "body", nullable = false, length = 4000)
    private String body;

    // Fecha y hora en que se creó la nota (asignada automáticamente por Hibernate).
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    public VisitNote() {}

    /**
     * Crea una nueva instancia de VisitNote con los valores proporcionados.
     *
     * @param visitId     Identificador de la visita asociada.
     * @param authorId    Identificador del autor de la nota.
     * @param visibility  Nivel de visibilidad (INTERNAL o CUSTOMER).
     * @param body        Contenido textual de la nota.
     * @return Nueva instancia de VisitNote lista para persistir.
     */
    public static VisitNote of(UUID visitId, UUID authorId, NoteVisibility visibility, String body) {
        // Crea una nueva nota y asigna los valores iniciales.
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