package com.proyecto.ops.technicians.web;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
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
import com.proyecto.ops.technicians.security.AuthenticatedUser;
import com.proyecto.ops.technicians.security.CurrentUser;

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

    // ---------- Read ----------
    @GetMapping
    public Page<TechnicianResponse> list(
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String skill,
            Pageable pageable) {
        return repo.search(active, skill, pageable).map(this::toResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TechnicianResponse> get(@PathVariable UUID id) {
        return repo.findById(id)
                .map(t -> ResponseEntity.ok(toResponse(t)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ---------- Create ----------
    @PostMapping
    public ResponseEntity<TechnicianResponse> create(
            @Valid @RequestBody CreateTechnicianRequest req,
            @CurrentUser AuthenticatedUser me) {

        if (me == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Avoid duplicates by userId
        var existing = repo.findByUserId(me.id());
        if (existing.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(toResponse(existing.get()));
        }

        Technician t = new Technician();
        t.setUserId(me.id());
        t.setUserName(me.name());
        t.setActive(Boolean.TRUE.equals(req.getActive()));

        // de-dup skills preserving order
        if (req.getSkills() != null) {
            LinkedHashSet<String> set = new LinkedHashSet<>(req.getSkills());
            t.setSkills(new ArrayList<>(set));
        } else {
            t.setSkills(null);
        }

        Technician saved = repo.save(t);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    // ---------- Update ----------
    @PatchMapping("/{id}")
    public ResponseEntity<TechnicianResponse> update(
            @PathVariable UUID id,
            @RequestBody UpdateTechnicianRequest req) {

        return repo.findById(id).map(t -> {
            if (req.active() != null) {
                t.setActive(req.active());
            }
            if (req.skills() != null) {
                // de-dup and keep order if caller sends duplicates
                LinkedHashSet<String> set = new LinkedHashSet<>(req.skills());
                t.setSkills(new ArrayList<>(set));
            }
            Technician saved = repo.save(t);
            return ResponseEntity.ok(toResponse(saved));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ---------- Delete ----------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        return repo.findById(id).map(t -> {
            repo.delete(t);
            return ResponseEntity.noContent().<Void>build();
        }).orElseGet(() -> ResponseEntity.notFound().<Void>build());
    }

    // ---------- Manage skills ----------
    @PatchMapping("/{id}/skills/add")
    public TechnicianResponse addSkill(@PathVariable UUID id, @RequestBody SkillReq req) {
        Technician t = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        LinkedHashSet<String> set = new LinkedHashSet<>();
        if (t.getSkills() != null) {
            set.addAll(t.getSkills());
        }
        if (req.skill() != null && !req.skill().isBlank()) {
            set.add(req.skill());
        }

        t.setSkills(set.isEmpty() ? null : new ArrayList<>(set));
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

        LinkedHashSet<String> set = new LinkedHashSet<>(t.getSkills());
        set.remove(req.skill());

        t.setSkills(set.isEmpty() ? null : new ArrayList<>(set));
        Technician saved = repo.save(t);
        return toResponse(saved);
    }

    public static record SkillReq(String skill) { }

    // ---------- Mapper ----------
    private TechnicianResponse toResponse(Technician t) {
        String userName = (t.getUserName() != null && !t.getUserName().isBlank())
                ? t.getUserName()
                : usersClient.getNameOrUnknown(t.getUserId());

        List<String> skills = t.getSkills();
        return new TechnicianResponse(
                t.getId(),
                t.getUserId(),
                userName,
                t.isActive(),
                skills,
                t.getCreatedAt()
        );
    }
}
