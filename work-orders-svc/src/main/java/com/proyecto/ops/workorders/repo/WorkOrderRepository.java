package com.proyecto.ops.workorders.repo;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.proyecto.ops.workorders.model.WoStatus;
import com.proyecto.ops.workorders.model.WorkOrder;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, UUID> {

  @Query("""
         select w
         from WorkOrder w
         where w.ticketId = coalesce(:ticketId, w.ticketId)
           and w.status = coalesce(:status, w.status)
         order by w.createdAt desc
         """)
  Page<WorkOrder> search(
      @Param("ticketId") UUID ticketId,
      @Param("status") WoStatus status,
      Pageable pageable
  );
}