/*
 * -----------------------------------------------------------------------------
 * SiteController.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Controlador REST del microservicio "sites-svc" encargado de gestionar
 *   los sitios o ubicaciones asociadas a clientes.
 *
 * Contexto de uso:
 *   - Expone endpoints para crear, consultar, actualizar, listar y eliminar
 *     sitios de clientes.
 *   - Utiliza el repositorio CustomerSiteRepository para acceder a la base de datos.
 *
 * Endpoints:
 *   POST   /sites         → Crea un nuevo sitio.
 *   GET    /sites/{id}    → Consulta un sitio por su ID.
 *   GET    /sites         → Lista sitios (filtrables por customerId).
 *   PUT    /sites/{id}    → Actualiza los datos de un sitio.
 *   DELETE /sites/{id}    → Elimina un sitio existente.
 *
 * Diseño:
 *   - Anotado con @RestController y @RequestMapping("/sites").
 *   - Usa ResponseEntity para devolver respuestas HTTP estandarizadas.
 *   - Incluye un manejador global de excepciones para errores de integridad.
 *
 * Mantenibilidad:
 *   - Si se agregan nuevos campos al modelo CustomerSite, deben incluirse
 *     en los métodos create(), update() y toResponse().
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.sites.web;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.ops.sites.model.CustomerSite;
import com.proyecto.ops.sites.repo.CustomerSiteRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * Controlador REST que gestiona las operaciones CRUD de sitios (CustomerSite).
 *
 * Proporciona endpoints para crear, listar, consultar, actualizar y eliminar
 * sitios de clientes.
 */
@RestController
@RequestMapping("/sites")
public class SiteController {

    private final CustomerSiteRepository repo;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param repo Repositorio JPA para acceder a los datos de los sitios.
     */
    public SiteController(CustomerSiteRepository repo) {
        this.repo = repo;
    }

    /**
     * Crea un nuevo sitio asociado a un cliente.
     *
     * @param req Objeto CreateSiteRequest con los datos del sitio a registrar.
     * @return Respuesta HTTP 201 Created con la información del sitio creado.
     */
    @PostMapping
    public ResponseEntity<SiteResponse> create(@Valid @RequestBody CreateSiteRequest req) {
        // Construye la entidad CustomerSite a partir de los datos recibidos en la solicitud.
        CustomerSite s = new CustomerSite();
        s.setCustomerId(req.customerId());
        s.setName(req.name());
        s.setAddress(req.address());
        s.setCity(req.city());
        s.setState(req.state());
        s.setCountry(req.country());

        CustomerSite saved = repo.save(s);
        return ResponseEntity.created(URI.create("/sites/" + saved.getId()))
                .body(toResponse(saved));
    }

    /**
     * Obtiene un sitio por su identificador UUID.
     *
     * @param id Identificador único del sitio.
     * @return Respuesta 200 OK si existe, o 404 Not Found si no se encuentra.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SiteResponse> getOne(@PathVariable UUID id) {
        return repo.findById(id)
                .map(s -> ResponseEntity.ok(toResponse(s)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Lista sitios existentes, con soporte de paginación.
     *
     * @param customerId (Opcional) Filtra los sitios por el identificador del cliente.
     * @param pageable   Parámetros de paginación (página, tamaño, orden).
     * @return Página de resultados con los sitios encontrados.
     */
    @GetMapping
    public Page<SiteResponse> list(@RequestParam(required = false) UUID customerId,
                                   Pageable pageable) {
        // Determina si se listan todos los sitios o solo los asociados a un cliente específico.
        Page<CustomerSite> page = (customerId == null)
                ? repo.findAll(pageable)
                : repo.findByCustomerId(customerId, pageable);

        return page.map(this::toResponse);
    }

    /**
     * Actualiza los datos de un sitio existente.
     *
     * @param id  Identificador del sitio a actualizar.
     * @param req Objeto UpdateSiteRequest con los nuevos datos del sitio.
     * @return Respuesta 200 OK si se actualiza, o 404 Not Found si no existe.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SiteResponse> update(@PathVariable UUID id,
                                               @Valid @RequestBody UpdateSiteRequest req) {
        return repo.findById(id).map(s -> {
            // Actualiza los campos del sitio con los valores recibidos.
            s.setName(req.name());
            s.setAddress(req.address());
            s.setCity(req.city());
            s.setState(req.state());
            s.setCountry(req.country());
            CustomerSite saved = repo.save(s);
            return ResponseEntity.ok(toResponse(saved));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Elimina un sitio existente por su identificador UUID.
     *
     * @param id Identificador único del sitio.
     * @return Respuesta 204 No Content si se elimina correctamente o 404 si no existe.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        return repo.findById(id).map(s -> {
            // Elimina el sitio encontrado del repositorio.
            repo.delete(s);
            return ResponseEntity.noContent().<Void>build();
        }).orElseGet(() -> ResponseEntity.notFound().<Void>build());
    }

    /**
     * Maneja excepciones de integridad referencial, como claves foráneas inválidas.
     *
     * @param ex  Excepción de integridad lanzada por la capa de persistencia.
     * @param req Objeto HttpServletRequest que contiene información de la solicitud.
     * @return Respuesta HTTP 409 Conflict con detalles del error.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleIntegrity(DataIntegrityViolationException ex,
                                                               HttpServletRequest req) {
        // Construye la respuesta con metadatos del conflicto detectado.
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", OffsetDateTime.now().toString());
        body.put("status", HttpStatus.CONFLICT.value());
        body.put("error", "Conflict");
        body.put("message", "FK inválida (customer_id) o conflicto de integridad");
        body.put("path", req.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    /**
     * Convierte una entidad CustomerSite en su representación de respuesta (SiteResponse).
     *
     * @param s Entidad CustomerSite obtenida desde la base de datos.
     * @return Objeto SiteResponse listo para ser devuelto en una respuesta HTTP.
     */
    private SiteResponse toResponse(CustomerSite s) {
        // Mapea los campos de la entidad al DTO de respuesta.
        return new SiteResponse(
                s.getId(),
                s.getCustomerId(),
                s.getName(),
                s.getAddress(),
                s.getCity(),
                s.getState(),
                s.getCountry(),
                s.getCreatedAt()
        );
    }
}