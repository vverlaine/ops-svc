package com.proyecto.ops.tickets.repo;

import com.proyecto.ops.tickets.model.Ticket;
import com.proyecto.ops.tickets.model.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    Page<Ticket> findByStatus(TicketStatus status, Pageable pageable);
    Page<Ticket> findByCustomerId(UUID customerId, Pageable pageable);
    Page<Ticket> findByStatusAndCustomerId(TicketStatus status, UUID customerId, Pageable pageable);
}