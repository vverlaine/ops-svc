package com.proyecto.ops.tickets.repo;

import com.proyecto.ops.tickets.model.Ticket;
import com.proyecto.ops.tickets.model.TicketPriority;
import com.proyecto.ops.tickets.model.TicketStatus;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface TicketRepository extends PagingAndSortingRepository<Ticket, UUID> {

    @Query("""
    select t
    from Ticket t
    where (:status is null or t.status = :status)
      and (:priority is null or t.priority = :priority)
      and (:customerId is null or t.customerId = :customerId)
      and (:requestedBy is null or t.requestedBy = :requestedBy)
    order by t.createdAt desc
    """)
    Page<Ticket> search(
            @Param("status") TicketStatus status,
            @Param("priority") TicketPriority priority,
            @Param("customerId") UUID customerId,
            @Param("requestedBy") UUID requestedBy,
            Pageable pageable
    );
}