package com.proyecto.ops.tickets.web;

import com.proyecto.ops.tickets.model.TicketPriority;
import com.proyecto.ops.tickets.model.TicketStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateTicketRequest(
    @NotBlank String title,
    String description,
    TicketStatus status,
    TicketPriority priority,
    @NotNull UUID customerId,
    UUID siteId,
    UUID assetId,
    UUID requestedBy,
    @NotBlank String createdBy
) {}