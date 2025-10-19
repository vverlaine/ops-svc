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

@Service
public class VisitEmailServiceImpl implements VisitEmailService {

    private static final Logger log = LoggerFactory.getLogger(VisitEmailServiceImpl.class);

    private final VisitEmailRepository emailRepository;
    private final JavaMailSender mailSender;

    public VisitEmailServiceImpl(VisitEmailRepository emailRepository, JavaMailSender mailSender) {
        this.emailRepository = emailRepository;
        this.mailSender = mailSender;
    }

    @Value("${visits.mail.default_to:devnull@example.com}")
    private String defaultTo;

    @Value("${visits.mail.from:no-reply@visits.local}")
    private String from;

    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @Override
    @Transactional
    public void onVisitCompleted(Visit visit) {

        log.info("Enviando correo por visita completada: {}", visit.getId());
        // 1) Construir registro PENDING
        VisitEmail email = new VisitEmail();
        email.setId(UUID.randomUUID());
        email.setVisitId(visit.getId());
        email.setToEmail(resolveToEmail(visit));
        email.setSubject(buildSubject(visit));
        email.setStatus("PENDING");
        emailRepository.save(email);

        // 2) Intentar enviar
        try {
            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    mime, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
            helper.setFrom(from);
            helper.setTo(email.getToEmail());
            helper.setSubject(email.getSubject());
            helper.setText(buildBodyHtml(visit), true);

            mailSender.send(mime);

            email.setStatus("SENT");
            emailRepository.save(email);
            log.info("Visit email SENT: {}", email.getId());
        } catch (MessagingException | MailException ex) {
            // 3) Guardar error (sin romper el checkout)
            log.error("Error sending visit email: {}", ex.getMessage(), ex);
            email.setStatus("ERROR");
            String msg = ex.getMessage();
            if (msg != null && msg.length() > 1000) {
                msg = msg.substring(0, 1000);
            }
            email.setErrorMessage(msg);
            emailRepository.save(email);
        }


    }

    private String resolveToEmail(Visit visit) {
        // TODO: si luego tienes email del cliente/sitio/técnico, reemplaza aquí
        return defaultTo;
    }

    private String buildSubject(Visit visit) {
        return "Visita completada – " + visit.getId();
    }

    private String buildBodyHtml(Visit v) {
        String in = v.getCheckInAt() != null ? ISO.format(v.getCheckInAt()) : "—";
        String out = v.getCheckOutAt() != null ? ISO.format(v.getCheckOutAt()) : "—";
        return """
                <html><body>
                <h3>Visita completada</h3>
                <ul>
                  <li><b>ID:</b> %s</li>
                  <li><b>Propósito:</b> %s</li>
                  <li><b>Inicio:</b> %s</li>
                  <li><b>Fin:</b> %s</li>
                </ul>
                </body></html>
                """.formatted(v.getId(), safe(v.getPurpose()), in, out);
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }
}
