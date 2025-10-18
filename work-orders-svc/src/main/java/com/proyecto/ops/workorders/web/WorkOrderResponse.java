package com.proyecto.ops.workorders.web;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.proyecto.ops.workorders.model.WoStatus;

public record WorkOrderResponse(
    UUID id,
    UUID ticketId,
    UUID technicianId,
    WoStatus status,
    OffsetDateTime scheduledAt,
    OffsetDateTime startedAt,
    OffsetDateTime endedAt,
    String notes,
    OffsetDateTime createdAt
) {}