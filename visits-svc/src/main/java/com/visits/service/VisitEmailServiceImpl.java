/*
 * -----------------------------------------------------------------------------
 * VisitEmailServiceImpl.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Implementa la lógica del servicio de envío de correos electrónicos relacionados
 *   con las visitas dentro del microservicio "visits-svc".
 *
 * Contexto de uso:
 *   - Implementa la interfaz {@link com.visits.service.VisitEmailService}.
 *   - Envía un correo automático cuando una visita es marcada como completada.
 *   - Registra el resultado del envío en la entidad {@link com.visits.model.VisitEmail},
 *     almacenando tanto los casos exitosos como los fallidos.
 *
 * Diseño:
 *   - Anotada con @Service y @Transactional para que Spring maneje su ciclo de vida
 *     y la transaccionalidad de sus operaciones.
 *   - Usa JavaMailSender para enviar correos MIME con formato HTML.
 *   - Registra logs detallados sobre el flujo de envío, errores y persistencia.
 *
 * Flujo general:
 *   1. Crea un registro VisitEmail en estado "PENDING".
 *   2. Construye el mensaje de correo con HTML (asunto y cuerpo).
 *   3. Intenta enviar el correo mediante JavaMailSender.
 *   4. Actualiza el estado a "SENT" si fue exitoso, o a "ERROR" si ocurrió una excepción.
 *
 * Mantenibilidad:
 *   - El método `resolveToEmail()` puede adaptarse para obtener direcciones reales
 *     del cliente, sitio o técnico.
 *   - Los métodos `buildSubject()` y `buildBodyHtml()` pueden personalizarse
 *     para incorporar plantillas dinámicas.
 * -----------------------------------------------------------------------------
 */
package com.visits.service;

import com.visits.model.Visit;
import com.visits.model.VisitEmail;
import com.visits.repo.VisitEmailRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Implementación del servicio de envío de correos electrónicos asociados a visitas.
 *
 * Gestiona la creación de registros de correo, el envío real y la captura de errores.
 */
@Service
public class VisitEmailServiceImpl implements VisitEmailService {

    private static final Logger log = LoggerFactory.getLogger(VisitEmailServiceImpl.class);

    private final VisitEmailRepository emailRepository;
    private final JavaMailSender mailSender;

    /**
     * Constructor que inyecta las dependencias del repositorio y el servicio de correo.
     *
     * @param emailRepository Repositorio encargado de registrar el estado de los correos.
     * @param mailSender Servicio de envío de correos (JavaMailSender).
     */
    public VisitEmailServiceImpl(VisitEmailRepository emailRepository, JavaMailSender mailSender) {
        this.emailRepository = emailRepository;
        this.mailSender = mailSender;
    }

    @Value("${visits.mail.default_to:devnull@example.com}")
    private String defaultTo;

    @Value("${visits.mail.from:no-reply@visits.local}")
    private String from;

    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    /**
     * Maneja el evento de "visita completada" enviando un correo electrónico al destinatario correspondiente.
     * 
     * @param visit Objeto Visit con la información de la visita completada.
     */
    @Override
    @Transactional
    public void onVisitCompleted(Visit visit) {

        // Registra en logs el inicio del proceso de envío de correo.
        log.info("Enviando correo por visita completada: {}", visit.getId());
        // Crea un nuevo registro de correo con estado inicial "PENDING".
        VisitEmail email = new VisitEmail();
        email.setId(UUID.randomUUID());
        email.setVisitId(visit.getId());
        email.setToEmail(resolveToEmail(visit));
        email.setSubject(buildSubject(visit));
        email.setStatus("PENDING");
        emailRepository.save(email);

        // Intenta construir y enviar el correo electrónico con formato HTML.
        try {
            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    mime, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
            helper.setFrom(from);
            helper.setTo(email.getToEmail());
            helper.setSubject(email.getSubject());
            helper.setText(buildBodyHtml(visit), true);

            // Envía el mensaje preparado utilizando el servicio JavaMailSender.
            mailSender.send(mime);

            // Si el correo se envía correctamente, actualiza el estado a "SENT" y guarda los cambios.
            email.setStatus("SENT");
            emailRepository.save(email);
            log.info("Visit email SENT: {}", email.getId());
        } catch (MessagingException | MailException ex) {
            // Si ocurre un error durante el envío, captura la excepción y registra el fallo.
            log.error("Error sending visit email: {}", ex.getMessage(), ex);
            email.setStatus("ERROR");
            String msg = ex.getMessage();
            if (msg != null && msg.length() > 1000) {
                msg = msg.substring(0, 1000);
            }
            // Guarda el mensaje de error truncado (si es largo) y actualiza el estado a "ERROR".
            email.setErrorMessage(msg);
            emailRepository.save(email);
        }


    }

    /**
     * Determina la dirección de correo a la que se enviará el mensaje.
     *
     * @param visit Objeto Visit para el cual se enviará el correo.
     * @return Dirección de correo electrónico del destinatario.
     */
    private String resolveToEmail(Visit visit) {
        // TODO: si luego tienes email del cliente/sitio/técnico, reemplaza aquí
        return defaultTo;
    }

    /**
     * Construye el asunto del correo basándose en el propósito de la visita.
     *
     * @param visit Objeto Visit con los detalles de la visita.
     * @return Asunto del correo.
     */
    private String buildSubject(Visit visit) {
        String purpose = safe(visit.getPurpose());
        if (purpose.isBlank()) {
            return "Visita completada – " + visit.getId();
        }
        return "Visita completada – " + purpose;
    }

    /**
     * Construye el cuerpo HTML del correo electrónico con la información de la visita.
     *
     * @param v Objeto Visit con datos de la visita.
     * @return Cadena HTML que representa el contenido del correo.
     */
    private String buildBodyHtml(Visit v) {
        // Define las fechas de inicio y fin formateadas en ISO o usa "—" si son nulas.
        String in = v.getCheckInAt() != null ? ISO.format(v.getCheckInAt()) : "—";
        String out = v.getCheckOutAt() != null ? ISO.format(v.getCheckOutAt()) : "—";
        // Determina el subtítulo dinámico según si la visita tiene o no hora de cierre.
        String subtitle = out.equals("—") ? "Sin hora de cierre registrada" : "Finalizada el " + out;

        // Retorna el cuerpo HTML completo del correo con estilos y datos dinámicos.
        return """
                <html>
                  <body style=\"font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,Arial,sans-serif;background:#f6f8fa;padding:24px;\">
                    <div style=\"max-width:560px;margin:auto;background:#ffffff;border:1px solid #eaecef;border-radius:8px;overflow:hidden;\">
                      <div style=\"background:#0ea5e9;color:#ffffff;padding:16px 20px;\">
                        <h2 style=\"margin:0;font-weight:600;font-size:18px;\">Visita completada</h2>
                        <div style=\"opacity:.9;font-size:13px;\">%s</div>
                      </div>
                      <div style=\"padding:20px;\">
                        <table style=\"width:100%%;border-collapse:collapse;font-size:14px;\">
                          <tr>
                            <td style=\"color:#6b7280;padding:6px 0;\">ID</td>
                            <td style=\"padding:6px 0;\">%s</td>
                          </tr>
                          <tr>
                            <td style=\"color:#6b7280;padding:6px 0;\">Propósito</td>
                            <td style=\"padding:6px 0;\">%s</td>
                          </tr>
                          <tr>
                            <td style=\"color:#6b7280;padding:6px 0;\">Inicio</td>
                            <td style=\"padding:6px 0;\">%s</td>
                          </tr>
                          <tr>
                            <td style=\"color:#6b7280;padding:6px 0;\">Fin</td>
                            <td style=\"padding:6px 0;\">%s</td>
                          </tr>
                        </table>
                      </div>
                      <div style=\"padding:14px 20px;border-top:1px solid #eaecef;color:#6b7280;font-size:12px;\">
                        Este es un correo automático. Responder a %s no es monitoreado.
                      </div>
                    </div>
                  </body>
                </html>
                """.formatted(subtitle, v.getId(), safe(v.getPurpose()), in, out, from);
    }

    /**
     * Asegura que un valor de texto no sea nulo.
     *
     * @param s Cadena de texto a validar.
     * @return Cadena vacía si es nula, o el mismo valor original si no lo es.
     */
    private static String safe(String s) {
        return s == null ? "" : s;
    }
}
