/*
 * -----------------------------------------------------------------------------
 * VisitEmail.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Representa la entidad JPA correspondiente a la tabla `app.visit_emails`, 
 *   utilizada para registrar los correos electrónicos enviados o pendientes
 *   relacionados con una visita.
 *
 * Contexto de uso:
 *   - Pertenece al microservicio "visits-svc".
 *   - Cada registro representa un intento de notificación por correo
 *     asociado a una visita (por ejemplo, notificar al cliente al completar
 *     una visita técnica).
 *
 * Diseño:
 *   - Anotada con @Entity y @Table(schema = "app", name = "visit_emails").
 *   - Utiliza UUID como identificador único.
 *   - Incluye campos para destinatario, asunto, estado del envío y errores.
 *
 * Campos principales:
 *   • id             → Identificador único del correo.
 *   • visitId        → Referencia a la visita asociada.
 *   • toEmail        → Dirección de correo electrónico del destinatario.
 *   • subject        → Asunto del correo enviado.
 *   • status         → Estado del correo (PENDING, SENT, FAILED).
 *   • errorMessage   → Mensaje de error en caso de fallo.
 *   • createdAt      → Fecha y hora en que se registró el intento.
 *
 * Mantenibilidad:
 *   - Si se agregan nuevos estados o atributos (por ejemplo, CC, BCC, body),
 *     deben actualizarse tanto la entidad como el proceso que gestiona los envíos.
 * -----------------------------------------------------------------------------
 */
package com.visits.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Entidad JPA que registra los correos electrónicos asociados a las visitas.
 *
 * Permite almacenar el estado del envío y los errores ocurridos durante el proceso.
 */
@Entity
@Table(
    name = "visit_emails",
    schema = "app",
    indexes = { @Index(name = "idx_emails_visit", columnList = "visit_id") }
)
public class VisitEmail {

    // Identificador único del correo electrónico (clave primaria).
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    // Identificador de la visita asociada al correo.
    @Column(name = "visit_id", nullable = false)
    private UUID visitId;

    // Dirección de correo electrónico del destinatario (máximo 320 caracteres).
    @Column(name = "to_email", nullable = false, length = 320)
    private String toEmail;

    // Asunto del correo electrónico (máximo 300 caracteres).
    @Column(name = "subject", length = 300)
    private String subject;

    // Estado del correo: puede ser PENDING, SENT o FAILED.
    @Column(name = "status", length = 30)
    private String status; // SENT, FAILED, PENDING

    // Mensaje de error en caso de que el envío falle (opcional).
    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    // Fecha y hora de creación del registro (se asigna automáticamente).
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    public VisitEmail() {}

    /**
     * Crea una instancia de correo en estado "PENDING".
     *
     * @param visitId  Identificador de la visita asociada.
     * @param toEmail  Dirección de correo destino.
     * @param subject  Asunto del correo.
     * @return Instancia de VisitEmail con estado inicial PENDING.
     */
    public static VisitEmail pending(UUID visitId, String toEmail, String subject) {
        // Crea una nueva instancia de correo electrónico pendiente de envío.
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