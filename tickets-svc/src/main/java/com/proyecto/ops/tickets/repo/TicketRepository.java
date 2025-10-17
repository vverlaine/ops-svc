package com.proyecto.ops.tickets.repo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proyecto.ops.tickets.model.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, UUID> { }