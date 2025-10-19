package com.visits.repo;

import com.visits.model.VisitEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VisitEventRepository extends JpaRepository<VisitEvent, UUID> {
    List<VisitEvent> findByVisitIdOrderByCreatedAtAsc(UUID visitId);
}