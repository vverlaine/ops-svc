/*
 * -----------------------------------------------------------------------------
 * ContactController.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Controlador REST del microservicio "contacts-svc" para gestionar contactos
 *   asociados a clientes (crear, consultar, listar y eliminar).
 *
 * Contexto de uso:
 *   - Expone endpoints bajo la ruta base `/contacts`.
 *   - Delega acceso a datos en {@link com.proyecto.ops.contacts.repo.ContactRepository}.
 *   - Resuelve el nombre del cliente consultando el servicio externo "customers"
 *     a través de {@link com.proyecto.ops.contacts.clients.CustomersClient}.
 *
 * Diseño:
 *   - Anotado con @RestController y @RequestMapping para definir el recurso REST.
 *   - Devuelve `ResponseEntity` para controlar códigos de estado y encabezados.
 *   - Maneja errores de integridad con un método anotado con @ExceptionHandler.
 *
 * Endpoints:
 *   POST   /contacts        → Crea un nuevo contacto.
 *   GET    /contacts/{id}   → Consulta un contacto por su ID.
 *   GET    /contacts        → Lista contactos (con filtro opcional por customerId).
 *   DELETE /contacts/{id}   → Elimina un contacto existente.
 *
 * Mantenibilidad:
 *   - Métodos autocontenidos y de única responsabilidad.
 *   - Fácil de extender para agregar actualización (PUT/PATCH) u otros filtros de búsqueda.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.contacts.web;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.ops.contacts.clients.CustomersClient;
import com.proyecto.ops.contacts.model.Contact;
import com.proyecto.ops.contacts.repo.ContactRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * Controlador REST para operaciones sobre contactos.
 *
 * Expone endpoints para crear, consultar, listar y eliminar contactos
 * asociados a clientes. Usa el repositorio {@link com.proyecto.ops.contacts.repo.ContactRepository}
 * y el cliente externo {@link com.proyecto.ops.contacts.clients.CustomersClient}
 * para enriquecer la respuesta con el nombre del cliente.
 */
@RestController
@RequestMapping("/contacts")
public class ContactController {

    private final ContactRepository repo;
    private final CustomersClient customersClient;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param repo Repositorio JPA para acceso a datos de contactos.
     * @param customersClient Cliente HTTP para obtener datos del servicio "customers".
     */
    public ContactController(ContactRepository repo, CustomersClient customersClient) {
        this.repo = repo;
        this.customersClient = customersClient;
    }

    /**
     * Crea un nuevo contacto.
     *
     * @param req DTO con los datos del contacto a crear.
     * @return 201 Created con la ubicación del recurso y el cuerpo de respuesta.
     */
    @PostMapping
    public ResponseEntity<ContactResponse> create(@Valid @RequestBody CreateContactRequest req) {
        // Construye la entidad Contact a partir de los datos de la solicitud.
        Contact c = new Contact();
        c.setCustomerId(req.customerId());
        c.setName(req.name());
        c.setEmail(req.email());
        c.setPhone(req.phone());
        c.setRole(req.role());

        Contact saved = repo.save(c);
        return ResponseEntity.created(URI.create("/contacts/" + saved.getId()))
                .body(toResponse(saved));
    }

    /**
     * Obtiene un contacto por su identificador.
     *
     * @param id UUID del contacto.
     * @return 200 con el contacto si existe; 404 si no se encuentra.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ContactResponse> getOne(@PathVariable UUID id) {
        return repo.findById(id)
                .map(c -> ResponseEntity.ok(toResponse(c)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Lista contactos con soporte de paginación.
     *
     * @param customerId (Opcional) Filtra por UUID de cliente.
     * @param pageable   Parámetros de paginación (página, tamaño, orden).
     * @return Página de resultados mapeada a ContactResponse.
     */
    @GetMapping
    public Page<ContactResponse> list(@RequestParam(required = false) UUID customerId,
            Pageable pageable) {
        // Determina si se listan todos los contactos o solo los de un cliente específico.
        Page<Contact> page = (customerId == null)
                ? repo.findAll(pageable)
                : repo.findByCustomerId(customerId, pageable);
        return page.map(this::toResponse);
    }

    /**
     * Elimina un contacto existente por su ID.
     *
     * @param id UUID del contacto a eliminar.
     * @return 204 No Content si se elimina; 404 si no existe.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        return repo.findById(id)
                .map(c -> {
                    // Elimina el contacto del repositorio cuando se encuentra.
                    repo.delete(c);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.notFound().<Void>build());
    }

    /**
     * Convierte la entidad Contact en su DTO de respuesta, enriquecido con
     * el nombre del cliente obtenido desde el servicio externo.
     *
     * @param c Entidad Contact.
     * @return Objeto ContactResponse listo para serializarse a JSON.
     */
    private ContactResponse toResponse(Contact c) {
        // Resuelve el nombre del cliente (o "Unknown" si el servicio externo no responde).
        String customerName = customersClient.getNameOrUnknown(c.getCustomerId());
        return new ContactResponse(
                c.getId(),
                c.getCustomerId(),
                customerName,
                c.getName(),
                c.getEmail(),
                c.getPhone(),
                c.getRole(),
                c.getCreatedAt()
        );
    }

    /**
     * Maneja excepciones de integridad de datos (por ejemplo, violación de restricciones
     * únicas o llaves foráneas) y construye una respuesta HTTP 409 (Conflict).
     *
     * @param ex   Excepción lanzada por la capa de persistencia.
     * @param req  Información de la solicitud HTTP.
     * @return Respuesta con detalles básicos del conflicto.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleIntegrity(DataIntegrityViolationException ex,
            HttpServletRequest req) {
        // Construye un cuerpo de respuesta básico con meta-información del error.
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", HttpStatus.CONFLICT.value());
        body.put("error", "Conflict");
        body.put("message", "Violación de restricción");
        body.put("path", req.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }
}
