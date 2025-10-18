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

@RestController
@RequestMapping("/assets")
public class AssetController {

    private final AssetRepository repo;

    public AssetController(AssetRepository repo) {
        this.repo = repo;
    }

    @PostMapping
    public ResponseEntity<AssetResponse> create(@Valid @RequestBody CreateAssetRequest req) {
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

    @GetMapping("/{id}")
    public ResponseEntity<AssetResponse> getOne(@PathVariable UUID id) {
        return repo.findById(id)
                .map(a -> ResponseEntity.ok(toResponse(a)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public Page<AssetResponse> list(
            @RequestParam(required = false) UUID customerId,
            Pageable pageable
    ) {
        Page<Asset> page = (customerId == null)
                ? repo.findAll(pageable)
                : repo.findByCustomerId(customerId, pageable);

        return page.map(this::toResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        return repo.findById(id)
                .map(a -> {
                    repo.delete(a);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.notFound().<Void>build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Asset> update(@PathVariable UUID id, @Valid @RequestBody UpdateAssetRequest req) {
        return repo.findById(id).map(a -> {
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

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleIntegrity(DataIntegrityViolationException ex,
            HttpServletRequest req) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", OffsetDateTime.now().toString());
        body.put("status", HttpStatus.CONFLICT.value());
        body.put("error", "Conflict");
        body.put("message", "serial_number ya existe");
        body.put("path", req.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex,
            HttpServletRequest req) {
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

    private AssetResponse toResponse(Asset a) {
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
