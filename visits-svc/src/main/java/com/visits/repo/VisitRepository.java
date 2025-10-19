package com.visits.repo;

import com.visits.model.Visit;
import com.visits.model.VisitState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface VisitRepository extends JpaRepository<Visit, UUID> {
    Page<Visit> findByTechnicianIdAndScheduledStartAtBetween(
            UUID technicianId, OffsetDateTime from, OffsetDateTime to, Pageable pageable);

    Page<Visit> findByState(VisitState state, Pageable pageable);
}