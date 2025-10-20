package com.proyecto.ops.tickets.web.dto;

import com.proyecto.ops.tickets.model.TicketPriority;
import jakarta.validation.constraints.NotNull;

public record UpdatePriorityRequest(
        @NotNull TicketPriority priority
) {}