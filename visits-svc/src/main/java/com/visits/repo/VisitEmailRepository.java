package com.visits.repo;

import com.visits.model.VisitEmail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VisitEmailRepository extends JpaRepository<VisitEmail, UUID> {
}