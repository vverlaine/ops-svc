package com.visits.repo;

import com.visits.model.VisitEmail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VisitEmailRepository extends JpaRepository<VisitEmail, UUID> {
    List<VisitEmail> findByVisitIdOrderByCreatedAtAsc(UUID visitId);
}