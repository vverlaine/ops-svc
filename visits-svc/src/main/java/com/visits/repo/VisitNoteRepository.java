package com.visits.repo;

import com.visits.model.VisitNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VisitNoteRepository extends JpaRepository<VisitNote, UUID> {
    List<VisitNote> findByVisitIdOrderByCreatedAtAsc(UUID visitId);
}