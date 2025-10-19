/*
 * -----------------------------------------------------------------------------
 * AssetController.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Controlador REST principal del servicio "assets-svc".
 *   Gestiona las operaciones CRUD (crear, leer, actualizar y eliminar)
 *   sobre la entidad Asset mediante endpoints HTTP.
 *
 * Contexto de uso:
 *   - Expuesto bajo la ruta base `/assets`.
 *   - Utiliza el repositorio AssetRepository para interactuar con la base de datos.
 *   - Gestiona validaciones de entrada, manejo de excepciones y respuestas HTTP.
 *
 * Diseño:
 *   - Anotado con @RestController → Indica que es un controlador REST.
 *   - @RequestMapping("/assets") → Define la ruta raíz del recurso.
 *   - Los métodos devuelven objetos ResponseEntity, lo que permite controlar
 *     códigos de estado y encabezados HTTP.
 *
 * Manejadores principales:
 *   POST   /assets        → Crea un nuevo activo.
 *   GET    /assets/{id}   → Obtiene un activo por su ID.
 *   GET    /assets        → Lista activos (filtrando por cliente si se indica).
 *   PUT    /assets/{id}   → Actualiza un activo existente.
 *   DELETE /assets/{id}   → Elimina un activo.
 *
 * Manejo de errores:
 *   - DataIntegrityViolationException → Captura conflictos de datos (ej. serial duplicado).
 *   - MethodArgumentNotValidException → Captura validaciones fallidas en las peticiones.
 *
 * Mantenibilidad:
 *   - Separa claramente las responsabilidades de negocio y de presentación.
 *   - Los métodos son autocontenidos, facilitando pruebas unitarias y de integración.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.assets.web;

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
import org.springframework.web.bind.MethodArgumentNotValidException;
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

import com.proyecto.ops.assets.model.Asset;
import com.proyecto.ops.assets.repo.AssetRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * Controlador REST para operaciones relacionadas con los activos (assets).
 *
 * Expone endpoints para crear, listar, consultar, actualizar y eliminar activos.
 * Usa {@link com.proyecto.ops.assets.repo.AssetRepository} para las operaciones
 * de acceso a datos.
 */
@RestController
@RequestMapping("/assets")
public class AssetController {

    private final AssetRepository repo;

    /**
     * Constructor del controlador.
     *
     * @param repo Repositorio de activos inyectado automáticamente por Spring.
     */
    public AssetController(AssetRepository repo) {
        this.repo = repo;
    }

    /**
     * Crea un nuevo activo en el sistema.
     *
     * @param req Objeto de solicitud con los datos del nuevo activo.
     * @return ResponseEntity con el activo creado y código 201 (Created).
     */
    @PostMapping
    public ResponseEntity<AssetResponse> create(@Valid @RequestBody CreateAssetRequest req) {
        // Se construye un nuevo objeto Asset a partir de los datos de la solicitud.
        Asset a = new Asset();
        a.setCustomerId(req.customerId());
        a.setSiteId(req.siteId());
        a.setSerialNumber(req.serialNumber());
        a.setModel(req.model());
        a.setType(req.type());
        a.setInstalledAt(req.installedAt());
        a.setNotes(req.notes());

        Asset saved = repo.save(a);
        return ResponseEntity.created(URI.create("/assets/" + saved.getId()))
                .body(toResponse(saved));
    }

    /**
     * Recupera un activo específico por su ID.
     *
     * @param id Identificador UUID del activo.
     * @return 200 con el activo si existe, o 404 si no se encuentra.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AssetResponse> getOne(@PathVariable UUID id) {
        return repo.findById(id)
                .map(a -> ResponseEntity.ok(toResponse(a)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Lista los activos registrados.
     *
     * @param customerId (Opcional) Filtro por UUID de cliente.
     * @param pageable   Parámetros de paginación (página, tamaño, orden).
     * @return Página con los activos encontrados.
     */
    @GetMapping
    public Page<AssetResponse> list(
            @RequestParam(required = false) UUID customerId,
            Pageable pageable
    ) {
        // Determina si se listan todos los activos o solo los del cliente indicado.
        Page<Asset> page = (customerId == null)
                ? repo.findAll(pageable)
                : repo.findByCustomerId(customerId, pageable);
        return page.map(this::toResponse);
    }

    /**
     * Elimina un activo existente.
     *
     * @param id UUID del activo a eliminar.
     * @return 204 si se eliminó con éxito, o 404 si no existe.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        return repo.findById(id)
                .map(a -> {
                    // Elimina el activo del repositorio si existe.
                    repo.delete(a);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.notFound().<Void>build());
    }

    /**
     * Actualiza los datos de un activo existente.
     *
     * @param id  UUID del activo.
     * @param req Objeto de solicitud con los nuevos valores.
     * @return 200 con el activo actualizado, o 404 si no existe.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Asset> update(@PathVariable UUID id, @Valid @RequestBody UpdateAssetRequest req) {
        return repo.findById(id).map(a -> {
            // Actualiza los campos del activo con los datos del request.
            a.setType(req.type());
            a.setModel(req.model());
            a.setSerialNumber(req.serialNumber());
            a.setSiteId(req.siteId());
            a.setInstalledAt(req.installedAt());
            a.setNotes(req.notes());
            Asset saved = repo.save(a);
            return ResponseEntity.ok(saved);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Maneja excepciones de integridad referencial o duplicidad de datos.
     * 
     * @param ex  Excepción capturada.
     * @param req Información de la solicitud HTTP.
     * @return Respuesta con estado 409 (Conflict) y detalles del error.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleIntegrity(DataIntegrityViolationException ex,
            HttpServletRequest req) {
        // Construye un cuerpo JSON con detalles del conflicto (ej. número de serie duplicado).
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", OffsetDateTime.now().toString());
        body.put("status", HttpStatus.CONFLICT.value());
        body.put("error", "Conflict");
        body.put("message", "serial_number ya existe");
        body.put("path", req.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    /**
     * Maneja excepciones de validación de entrada.
     *
     * @param ex  Excepción lanzada por Spring Validation.
     * @param req Información de la solicitud HTTP.
     * @return Respuesta con estado 400 (Bad Request) y mensaje descriptivo.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex,
            HttpServletRequest req) {
        // Construye la respuesta con el detalle del primer error de validación.
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", OffsetDateTime.now().toString());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");

        // Mensaje simple (primera violación)
        var firstError = ex.getBindingResult().getFieldErrors().stream().findFirst();
        body.put("message", firstError
                .map(fe -> fe.getField() + " " + fe.getDefaultMessage())
                .orElse("Validation failed"));

        body.put("path", req.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Convierte una entidad Asset a su representación de respuesta (DTO).
     *
     * @param a Entidad Asset.
     * @return DTO con los datos listos para enviarse como respuesta.
     */
    private AssetResponse toResponse(Asset a) {
        // Mapea los campos del Asset a un objeto de tipo AssetResponse.
        return new AssetResponse(
                a.getId(),
                a.getCustomerId(),
                a.getSiteId(),
                a.getSerialNumber(),
                a.getModel(),
                a.getType(),
                a.getInstalledAt(),
                a.getNotes(),
                a.getCreatedAt()
        );
    }
}
