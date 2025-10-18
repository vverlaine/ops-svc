package com.proyecto.ops.tickets.repo;

import com.proyecto.ops.tickets.model.Ticket;
import com.proyecto.ops.tickets.model.TicketPriority;
import com.proyecto.ops.tickets.model.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {

    @Query("""
           select t
           from Ticket t
           where t.status = coalesce(:status, t.status)
             and t.priority = coalesce(:priority, t.priority)
             and t.customerId = coalesce(:customerId, t.customerId)
           order by t.createdAt desc
           """)
    Page<Ticket> search(
            @Param("status") TicketStatus status,
            @Param("priority") TicketPriority priority,
            @Param("customerId") UUID customerId,
            Pageable pageable
    );
}
