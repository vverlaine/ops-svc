package com.proyecto.ops.tickets.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import com.proyecto.ops.tickets.model.TicketPriority;

public record CreateTicketRequest(
    @NotBlank String title,
    String description,
    TicketPriority priority,
    @NotNull UUID customerId,
    UUID assetId,
    @NotBlank String createdBy
) {}