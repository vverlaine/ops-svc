package com.proyecto.ops.tickets.web;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.proyecto.ops.tickets.model.TicketPriority;
import com.proyecto.ops.tickets.model.TicketStatus;

public record TicketResponse(
        UUID id,
        String title,
        String description,
        TicketStatus status,
        TicketPriority priority,
        UUID customerId,
        String customerName,   // <- nuevo
        UUID assetId,
        String createdBy,
        OffsetDateTime createdAt
) {}