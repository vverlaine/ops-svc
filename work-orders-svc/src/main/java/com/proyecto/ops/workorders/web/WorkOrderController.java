/*
 * -----------------------------------------------------------------------------
 * WorkOrderController.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Controlador REST responsable de exponer los endpoints para la gestión de
 *   órdenes de trabajo dentro del microservicio "work-orders-svc".
 *
 * Contexto de uso:
 *   - Permite crear, listar, actualizar, asignar técnicos, cambiar estados y eliminar
 *     órdenes de trabajo.
 *   - Se comunica con el repositorio {@link com.proyecto.ops.workorders.repo.WorkOrderRepository}
 *     y con el cliente {@link com.proyecto.ops.workorders.clients.TicketsClient}
 *     para validar la existencia de tickets en el microservicio "tickets-svc".
 *
 * Diseño:
 *   - Anotado con @RestController y @RequestMapping("/work-orders").
 *   - Utiliza ResponseEntity para manejar respuestas HTTP consistentes.
 *   - Maneja errores comunes como violaciones de integridad con un controlador de excepciones.
 *
 * Endpoints principales:
 *   • POST   /work-orders             → Crea una nueva orden de trabajo.
 *   • GET    /work-orders/{id}        → Obtiene una orden específica.
 *   • GET    /work-orders             → Lista todas las órdenes con filtros opcionales.
 *   • PATCH  /work-orders/{id}/assign → Asigna un técnico a una orden existente.
 *   • PATCH  /work-orders/{id}/status → Actualiza el estado de una orden.
 *   • DELETE /work-orders/{id}        → Elimina una orden de trabajo.
 *
 * Mantenibilidad:
 *   - Este controlador se encarga solo del flujo HTTP → Servicio → Repositorio.
 *   - La lógica de negocio adicional debe implementarse en la capa de servicio.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.workorders.web;

import java.net.URI;
import java.time.OffsetDateTime;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.proyecto.ops.workorders.clients.TicketsClient;
import com.proyecto.ops.workorders.model.WoStatus;
import com.proyecto.ops.workorders.model.WorkOrder;
import com.proyecto.ops.workorders.repo.WorkOrderRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * Controlador REST que gestiona las operaciones CRUD sobre las órdenes de trabajo.
 *
 * Permite crear, consultar, actualizar, asignar técnicos y eliminar órdenes,
 * integrándose con el servicio de tickets para validación.
 */
@RestController
@RequestMapping("/work-orders")
public class WorkOrderController {

    private final WorkOrderRepository repo;
    private final TicketsClient ticketsClient;

    /**
     * Constructor que inyecta el repositorio de órdenes y el cliente de tickets.
     *
     * @param repo Repositorio de acceso a datos para WorkOrder.
     * @param ticketsClient Cliente HTTP para validar la existencia de tickets en tickets-svc.
     */
    public WorkOrderController(WorkOrderRepository repo, TicketsClient ticketsClient) {
        this.repo = repo;
        this.ticketsClient = ticketsClient;
    }

    /**
     * Crea una nueva orden de trabajo.
     * 
     * Verifica la validez del ticket en el microservicio "tickets-svc" antes de crearla.
     *
     * @param req Solicitud con los datos de la nueva orden.
     * @return Respuesta HTTP 201 (Created) con la orden creada o error en caso de fallo.
     */
    @PostMapping
    public ResponseEntity<WorkOrderResponse> create(@Valid @RequestBody CreateWorkOrderRequest req) {
        // Validación: se comprueba que el ticket exista en el servicio remoto "tickets-svc".
        try {
            if (!ticketsClient.exists(req.ticketId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ticketId inválido o no existe en tickets-svc");
            }
        } catch (org.springframework.web.client.ResourceAccessException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "tickets-svc no disponible", e);
        }

        WorkOrder w = new WorkOrder();
        w.setTicketId(req.ticketId());
        w.setTechnicianId(req.technicianId());
        w.setScheduledAt(req.scheduledAt());
        w.setNotes(req.notes());
        w.setStatus(WoStatus.PENDING);

        // Se guarda la nueva orden en la base de datos.
        WorkOrder saved = repo.save(w);
        // Se retorna la orden creada junto con la ubicación del nuevo recurso.
        return ResponseEntity.created(URI.create("/work-orders/" + saved.getId()))
                .body(toResponse(saved));
    }

    /**
     * Obtiene una orden de trabajo específica por su identificador.
     *
     * @param id Identificador UUID de la orden.
     * @return Respuesta HTTP 200 (OK) si se encuentra, o 404 (Not Found) si no existe.
     */
    @GetMapping("/{id}")
    public ResponseEntity<WorkOrderResponse> getOne(@PathVariable UUID id) {
        return repo.findById(id)
                .map(w -> ResponseEntity.ok(toResponse(w)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Lista las órdenes de trabajo existentes aplicando filtros opcionales.
     *
     * @param ticketId Identificador del ticket asociado (opcional).
     * @param status Estado actual de la orden (opcional).
     * @param pageable Parámetros de paginación (número de página, tamaño, orden).
     * @return Página de resultados con las órdenes filtradas.
     */
    @GetMapping
    public Page<WorkOrderResponse> list(
            @RequestParam(required = false) UUID ticketId,
            @RequestParam(required = false) WoStatus status,
            Pageable pageable
    ) {
        // Consulta el repositorio aplicando los filtros de ticket y estado.
        Page<WorkOrder> page = repo.search(ticketId, status, pageable);
        return page.map(this::toResponse);
    }

    /**
     * Asigna un técnico a una orden de trabajo existente.
     *
     * @param id Identificador de la orden.
     * @param req Solicitud con el identificador del técnico a asignar.
     * @return Orden actualizada con el técnico asignado o 404 si no existe.
     */
    @PatchMapping("/{id}/assign")
    public ResponseEntity<WorkOrderResponse> assign(@PathVariable UUID id, @Valid @RequestBody AssignRequest req) {
        return repo.findById(id).map(w -> {
            // Asigna el identificador del técnico a la orden.
            w.setTechnicianId(req.technicianId());
            // Actualiza el estado de la orden a "ASSIGNED".
            w.setStatus(WoStatus.ASSIGNED);
            WorkOrder saved = repo.save(w);
            return ResponseEntity.ok(toResponse(saved));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Actualiza el estado de una orden de trabajo y registra las fechas relevantes.
     *
     * @param id Identificador de la orden.
     * @param req Solicitud con el nuevo estado y notas opcionales.
     * @return Orden actualizada o 404 si no se encuentra.
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<WorkOrderResponse> updateStatus(@PathVariable UUID id, @Valid @RequestBody UpdateStatusRequest req) {
        return repo.findById(id).map(w -> {
            WoStatus newStatus = req.status();
            // Si el nuevo estado es STARTED y no tiene fecha, se marca el inicio.
            if (newStatus == WoStatus.STARTED && w.getStartedAt() == null) {
                w.setStartedAt(OffsetDateTime.now());
            }
            // Si el nuevo estado es DONE y no tiene fecha, se marca la finalización.
            if (newStatus == WoStatus.DONE && w.getEndedAt() == null) {
                w.setEndedAt(OffsetDateTime.now());
            }
            w.setStatus(newStatus);
            // Si hay notas adicionales en la solicitud, se actualizan en la orden.
            if (req.notes() != null && !req.notes().isBlank()) {
                w.setNotes(req.notes());
            }
            WorkOrder saved = repo.save(w);
            return ResponseEntity.ok(toResponse(saved));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Elimina una orden de trabajo por su identificador.
     *
     * @param id Identificador UUID de la orden a eliminar.
     * @return Respuesta 204 (No Content) si se elimina correctamente o 404 si no se encuentra.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        return repo.findById(id)
                .map(w -> {
                    repo.delete(w);
                    return ResponseEntity.noContent().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Manejador global de errores de integridad referencial (por ejemplo, claves duplicadas).
     *
     * @param ex Excepción de violación de integridad.
     * @param req Objeto HttpServletRequest para obtener la ruta del error.
     * @return Respuesta con detalles del error (HTTP 409 Conflict).
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> integrity(DataIntegrityViolationException ex, HttpServletRequest req) {
        // Se construye un cuerpo JSON con detalles del error para la respuesta HTTP.
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", OffsetDateTime.now().toString());
        body.put("status", HttpStatus.CONFLICT.value());
        body.put("error", "Conflict");
        body.put("message", "Violación de integridad");
        body.put("path", req.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    /**
     * Convierte una entidad WorkOrder en un objeto de respuesta (DTO).
     *
     * @param w Entidad WorkOrder.
     * @return Objeto WorkOrderResponse listo para enviar en la respuesta HTTP.
     */
    private WorkOrderResponse toResponse(WorkOrder w) {
        return new WorkOrderResponse(
                w.getId(),
                w.getTicketId(),
                w.getTechnicianId(),
                w.getStatus(),
                w.getScheduledAt(),
                w.getStartedAt(),
                w.getEndedAt(),
                w.getNotes(),
                w.getCreatedAt()
        );
    }
}
