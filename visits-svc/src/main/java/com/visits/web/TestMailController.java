/*
 * -----------------------------------------------------------------------------
 * TestMailController.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Controlador REST utilizado para probar la funcionalidad de envío de correos
 *   electrónicos dentro del microservicio "visits-svc".
 *
 * Contexto de uso:
 *   - Expone un endpoint público `/test-mail` que permite verificar que la configuración
 *     de correo (JavaMailSender) funciona correctamente en el entorno actual.
 *   - Utiliza valores configurados en el archivo `application.yml` o variables de entorno
 *     para definir el remitente y destinatario de prueba.
 *
 * Diseño:
 *   - Anotado con @RestController para exponer endpoints HTTP.
 *   - Usa JavaMailSender para enviar correos simples (texto plano).
 *   - El endpoint responde con un mensaje de éxito o error según el resultado del envío.
 *
 * Mantenibilidad:
 *   - Este controlador es principalmente de diagnóstico y no debe usarse en producción.
 *   - Puede eliminarse o reemplazarse por un servicio de correo más robusto.
 * -----------------------------------------------------------------------------
 */
package com.visits.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para probar el envío de correos electrónicos desde el microservicio "visits-svc".
 */
@RestController
public class TestMailController {

    // Inyección del servicio de correo de Spring utilizado para enviar emails.
    @Autowired
    private JavaMailSender mailSender;

    // Dirección de correo remitente. Se obtiene desde la configuración (application.yml).
    @Value("${visits.mail.from:no-reply@visits.local}")
    private String from;

    // Dirección de correo destino por defecto para las pruebas.
    @Value("${visits.mail.default_to:test@demo.local}")
    private String defaultTo;

    /**
     * Endpoint GET que envía un correo electrónico de prueba.
     *
     * @return Mensaje indicando si el correo se envió correctamente o si ocurrió un error.
     */
    @GetMapping("/test-mail")
    public String sendTestMail() {
        try {
            // Crea un nuevo mensaje de correo con asunto, destinatario y cuerpo en texto plano.
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(defaultTo);
            message.setSubject("Correo de prueba desde visits-svc 🚀");
            message.setText("Hola 👋,\n\nEste es un correo de prueba enviado desde visits-svc en Render usando Mailtrap Sandbox.\n\nSaludos!");

            // Envía el correo utilizando el servicio configurado (por ejemplo, Mailtrap Sandbox).
            mailSender.send(message);

            // Retorna un mensaje de confirmación si el correo fue enviado exitosamente.
            return "✅ Correo enviado correctamente. Revisa tu bandeja Mailtrap Sandbox.";
        } catch (Exception e) {
            // En caso de error, imprime el stack trace para depuración.
            e.printStackTrace();

            // Retorna un mensaje con el error ocurrido durante el envío del correo.
            return "❌ Error enviando correo: " + e.getMessage();
        }
    }
}