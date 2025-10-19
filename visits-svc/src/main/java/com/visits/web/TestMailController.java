package com.visits.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestMailController {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${visits.mail.from:no-reply@visits.local}")
    private String from;

    @Value("${visits.mail.default_to:test@demo.local}")
    private String defaultTo;

    @GetMapping("/test-mail")
    public String sendTestMail() {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(defaultTo);
            message.setSubject("Correo de prueba desde visits-svc 🚀");
            message.setText("Hola 👋,\n\nEste es un correo de prueba enviado desde visits-svc en Render usando Mailtrap Sandbox.\n\nSaludos!");

            mailSender.send(message);
            return "✅ Correo enviado correctamente. Revisa tu bandeja Mailtrap Sandbox.";
        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Error enviando correo: " + e.getMessage();
        }
    }
}