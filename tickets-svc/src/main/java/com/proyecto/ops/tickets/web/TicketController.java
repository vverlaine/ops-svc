/*
 * -----------------------------------------------------------------------------
 * TicketController.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Controlador REST principal del microservicio "tickets-svc".
 *   Gestiona la creación, consulta y actualización de tickets a través de endpoints HTTP.
 *
 * Contexto de uso:
 *   - Expone los endpoints bajo la ruta base `/tickets`.
 *   - Se comunica con los microservicios "customers-svc" y "contacts-svc" para
 *     validar clientes y obtener nombres de contactos.
 *   - Utiliza `TicketRepository` para interactuar con la base de datos.
 *
 * Diseño:
 *   - Anotado con @RestController y @RequestMapping("/tickets").
 *   - Incluye validaciones con @Validated y @Valid para asegurar la integridad de los datos.
 *   - Maneja excepciones HTTP mediante `ResponseStatusException`.
 *
 * Endpoints principales:
 *   • GET /tickets → Lista paginada de tickets con filtros opcionales.
 *   • GET /tickets/{id} → Obtiene un ticket específico por su UUID.
 *   • POST /tickets → Crea un nuevo ticket validando el cliente asociado.
 *   • PATCH /tickets/{id}/status → Actualiza el estado de un ticket.
 *   • PATCH /tickets/{id}/priority → Actualiza la prioridad de un ticket.
 *
 * Mantenibilidad:
 *   - Si se agregan nuevos campos o servicios relacionados, se debe actualizar
 *     el método `toResponse()` y las validaciones en `create()`.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.tickets.web;

import java.net.URI;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.proyecto.ops.tickets.model.Ticket;
import com.proyecto.ops.tickets.model.TicketPriority;
import com.proyecto.ops.tickets.repo.TicketRepository;
import com.proyecto.ops.tickets.web.CreateTicketRequest;
import com.proyecto.ops.tickets.web.TicketResponse;
import com.proyecto.ops.tickets.web.UpdateTicketStatusRequest;

import com.proyecto.ops.tickets.clients.CustomersClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import jakarta.validation.Valid;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import com.proyecto.ops.tickets.model.TicketStatus;

import com.proyecto.ops.tickets.clients.CustomersClient;
import com.proyecto.ops.tickets.clients.ContactsClient;
import com.proyecto.ops.tickets.model.Ticket;
import com.proyecto.ops.tickets.repo.TicketRepository;
import org.springframework.web.client.ResourceAccessException;

@RestController
@RequestMapping("/tickets")
@Validated
/**
 * Controlador REST para gestionar los tickets del sistema.
 *
 * Proporciona endpoints para crear, listar, consultar y actualizar tickets.
 */
public class TicketController {

    private final TicketRepository repo;
    private final CustomersClient customersClient;
    private final ContactsClient contactsClient;

    /**
     * Constructor que inyecta las dependencias principales del controlador.
     *
     * @param repo Repositorio para operaciones CRUD sobre tickets.
     * @param customersClient Cliente HTTP para validar y obtener información de clientes.
     * @param contactsClient Cliente HTTP para obtener información de contactos.
     */
    public TicketController(TicketRepository repo,
            CustomersClient customersClient,
            ContactsClient contactsClient) {
        this.repo = repo;
        this.customersClient = customersClient;
        this.contactsClient = contactsClient;
    }

    /**
     * Lista los tickets existentes con filtros opcionales y paginación.
     *
     * @param status Estado del ticket (opcional).
     * @param priority Prioridad del ticket (opcional).
     * @param customerId Identificador del cliente (opcional).
     * @param requestedBy Usuario que solicitó el ticket (opcional).
     * @param pageable Parámetros de paginación (página, tamaño, orden).
     * @return Página con los tickets que cumplen los filtros aplicados.
     */
    @GetMapping
    public Page<TicketResponse> list(
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) TicketPriority priority,
            @RequestParam(required = false) UUID customerId,
            @RequestParam(required = false) UUID requestedBy,
            Pageable pageable
    ) {
        // Realiza la búsqueda de tickets aplicando filtros dinámicos según los parámetros recibidos.
        return repo.search(status, priority, customerId, requestedBy, pageable)
                .map(this::toResponse);
    }

    /**
     * Obtiene un ticket por su identificador UUID.
     *
     * @param id Identificador único del ticket.
     * @return Ticket encontrado o 404 si no existe.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> get(@PathVariable UUID id) {
        // Busca el ticket en el repositorio y devuelve la respuesta si existe.
        return repo.findById(id)
                .map(t -> ResponseEntity.ok(toResponse(t)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Crea un nuevo ticket en el sistema.
     *
     * @param req Objeto de solicitud que contiene los datos del nuevo ticket.
     * @return Respuesta HTTP 201 con el ticket creado o error si la validación falla.
     */
    @PostMapping
    public ResponseEntity<TicketResponse> create(@Valid @RequestBody CreateTicketRequest req) {
        // Crea una nueva instancia de Ticket y asigna los valores recibidos en la solicitud.
        Ticket t = new Ticket();
        t.setTitle(req.title());
        t.setDescription(req.description());

        if (req.status() != null) {
            t.setStatus(req.status());       // <-- sin valueOf
        }
        if (req.priority() != null) {
            t.setPriority(req.priority());   // <-- sin valueOf
        }
        t.setCustomerId(req.customerId());
        t.setSiteId(req.siteId());           // si lo tienes en la entidad
        t.setAssetId(req.assetId());
        t.setRequestedBy(req.requestedBy()); // <-- importante
        t.setCreatedBy(req.createdBy());

        // Verifica que el identificador del cliente sea obligatorio.
        if (t.getCustomerId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CustomerId es requerido");
        }

        // Valida que el cliente exista en el microservicio customers-svc.
        try {
            if (!customersClient.exists(t.getCustomerId())) {
                // Si el cliente no existe, lanza un error 400 indicando que el CustomerId es inválido.
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "CustomerId inválido o no existe en customers-svc"
                );
            }
        } catch (ResourceAccessException e) {
            // Si el servicio de clientes no está disponible, lanza un error 503 (Service Unavailable).
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE, "customers-svc no disponible", e
            );
        }

        // Guarda el ticket en la base de datos una vez validado.
        Ticket saved = repo.save(t);
        return ResponseEntity.created(URI.create("/tickets/" + saved.getId()))
                .body(toResponse(saved));
    }

    /**
     * Actualiza el estado de un ticket existente.
     *
     * @param id Identificador del ticket a actualizar.
     * @param req Objeto con el nuevo estado solicitado.
     * @return Ticket actualizado o 404 si no se encuentra.
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<TicketResponse> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTicketStatusRequest req
    ) {
        // Busca el ticket por su ID, actualiza el estado y guarda los cambios.
        return repo.findById(id)
                .map(t -> {
                    t.setStatus(req.status());
                    Ticket updated = repo.save(t);
                    return ResponseEntity.ok(toResponse(updated));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * DTO interno utilizado para actualizar la prioridad de un ticket.
     *
     * Se define dentro del controlador para evitar crear un archivo adicional.
     */
    // ---- Update Priority DTO (local to controller to avoid creating a new file) ----
    public static record UpdatePriorityRequest(
            @jakarta.validation.constraints.NotNull TicketPriority priority
            ) {

    }

    /**
     * Actualiza la prioridad de un ticket existente.
     *
     * @param id Identificador del ticket.
     * @param req Objeto con la nueva prioridad solicitada.
     * @return Ticket actualizado o 404 si no se encuentra.
     */
    @PatchMapping("/{id}/priority")
    public ResponseEntity<TicketResponse> updatePriority(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePriorityRequest req
    ) {
        // Busca el ticket por su ID, actualiza la prioridad y persiste los cambios.
        return repo.findById(id)
                .map(t -> {
                    t.setPriority(req.priority());
                    Ticket updated = repo.save(t);
                    return ResponseEntity.ok(toResponse(updated));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Convierte una entidad Ticket en un objeto TicketResponse para enviar al cliente.
     *
     * @param t Entidad Ticket proveniente del repositorio.
     * @return Objeto TicketResponse con los datos formateados para respuesta HTTP.
     */
    private TicketResponse toResponse(Ticket t) {
        // Obtiene el nombre del cliente consultando al servicio customers-svc.
        String customerName = customersClient.getNameOrUnknown(t.getCustomerId());
        // Si existe un contacto solicitante, obtiene su nombre desde contacts-svc.
        String requestedByName = (t.getRequestedBy() != null)
                ? contactsClient.getNameOrUnknown(t.getRequestedBy())
                : null;

        // Construye y devuelve el objeto de respuesta con los datos completos del ticket.
        return new TicketResponse(
                t.getId(),
                t.getTitle(),
                t.getDescription(),
                t.getStatus().name(),
                t.getPriority().name(),
                t.getCustomerId(),
                customerName,
                t.getSiteId(),
                t.getAssetId(),
                t.getRequestedBy(),
                requestedByName,
                t.getCreatedBy(),
                t.getCreatedAt()
        );
    }
}
