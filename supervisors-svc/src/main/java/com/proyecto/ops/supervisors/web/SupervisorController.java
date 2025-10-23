package com.proyecto.ops.supervisors.web;

import com.proyecto.ops.supervisors.clients.UsersClient;
import com.proyecto.ops.supervisors.model.Supervisor;
import com.proyecto.ops.supervisors.repo.SupervisorRepository;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/supervisors")
public class SupervisorController {

    private final SupervisorRepository repo;
    private final UsersClient usersClient;

    public SupervisorController(SupervisorRepository repo, UsersClient usersClient) {
        this.repo = repo;
        this.usersClient = usersClient;
    }

    @GetMapping
    public Page<SupervisorResponse> list(
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) UUID teamId,
            Pageable pageable) {
        return repo.search(active, teamId, pageable).map(this::toResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupervisorResponse> get(@PathVariable UUID id) {
        return repo.findById(id)
                .map(s -> ResponseEntity.ok(toResponse(s)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SupervisorResponse> update(
            @PathVariable UUID id,
            @RequestBody UpdateSupervisorRequest req) {

        return repo.findById(id).map(s -> {
            if (req.active() != null) {
                s.setActive(req.active());
            }
            if (req.teamId() != null) {
                s.setTeamId(req.teamId());
            }
            Supervisor saved = repo.save(s);
            return ResponseEntity.ok(toResponse(saved));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    private SupervisorResponse toResponse(Supervisor s) {
        String name = (s.getUserName() != null && !s.getUserName().isBlank())
                ? s.getUserName()
                : usersClient.getNameOrUnknown(s.getUserId());

        return new SupervisorResponse(
                s.getUserId(),
                name,
                s.isActive(),
                s.getTeamId(),
                s.getCreatedAt()
        );
    }
}
