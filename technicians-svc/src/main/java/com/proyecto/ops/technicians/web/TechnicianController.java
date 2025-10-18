package com.proyecto.ops.technicians.web;

import java.net.URI;
import java.util.LinkedHashSet;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.proyecto.ops.technicians.clients.UsersClient;
import com.proyecto.ops.technicians.model.Technician;
import com.proyecto.ops.technicians.repo.TechnicianRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/technicians")
public class TechnicianController {

    private final TechnicianRepository repo;
    private final UsersClient usersClient;

    public TechnicianController(TechnicianRepository repo, UsersClient usersClient) {
        this.repo = repo;
        this.usersClient = usersClient;
    }

    @GetMapping
    public Page<TechnicianResponse> list(
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String skill,
            Pageable pageable
    ) {
        return repo.search(active, skill, pageable).map(this::toResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TechnicianResponse> get(@PathVariable UUID id) {
        return repo.findById(id)
                .map(t -> ResponseEntity.ok(toResponse(t)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TechnicianResponse> create(@Valid @RequestBody TechnicianRequest req) {
        Technician t = new Technician();
        t.setUserId(req.userId());
        if (req.active() != null) {
            t.setActive(req.active());
        }
        t.setSkills(req.skills());

        Technician saved = repo.save(t);
        return ResponseEntity
                .created(URI.create("/technicians/" + saved.getId()))
                .body(toResponse(saved));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TechnicianResponse> update(
            @PathVariable UUID id,
            @RequestBody UpdateTechnicianRequest req
    ) {
        return repo.findById(id).map(t -> {
            if (req.active() != null) {
                t.setActive(req.active());
            }
            if (req.skills() != null) {
                t.setSkills(req.skills());
            }
            Technician saved = repo.save(t);
            return ResponseEntity.ok(toResponse(saved));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        return repo.findById(id).map(t -> {
            repo.delete(t);
            return ResponseEntity.noContent().<Void>build();
        }).orElseGet(() -> ResponseEntity.notFound().<Void>build());
    }

    @PatchMapping("/{id}/skills/add")
    public TechnicianResponse addSkill(@PathVariable UUID id, @RequestBody SkillReq req) {
        Technician t = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        LinkedHashSet<String> set = new LinkedHashSet<>();
        if (t.getSkills() != null) {
            set.addAll(t.getSkills()); // getSkills() is a List<String>
        }
        if (req.skill() != null && !req.skill().isBlank()) {
            set.add(req.skill());
        }

        t.setSkills(set.isEmpty() ? null : new java.util.ArrayList<>(set));
        Technician saved = repo.save(t);
        return toResponse(saved);
    }

    @PatchMapping("/{id}/skills/remove")
    public TechnicianResponse removeSkill(@PathVariable UUID id, @RequestBody SkillReq req) {
        Technician t = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (t.getSkills() == null || req.skill() == null || req.skill().isBlank()) {
            return toResponse(t);
        }

        LinkedHashSet<String> set = new LinkedHashSet<>();
        set.addAll(t.getSkills());
        set.remove(req.skill());

        t.setSkills(set.isEmpty() ? null : new java.util.ArrayList<>(set));
        Technician saved = repo.save(t);
        return toResponse(saved);
    }

    public static record SkillReq(String skill) {

    }

    private TechnicianResponse toResponse(Technician t) {
        String userName = usersClient.getNameOrUnknown(t.getUserId());
        return new TechnicianResponse(
                t.getId(), t.getUserId(), userName, t.isActive(), t.getSkills(), t.getCreatedAt()
        );
    }
}
