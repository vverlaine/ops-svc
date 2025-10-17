package com.proyecto.ops.tickets.web;

import com.proyecto.ops.tickets.model.TicketStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateTicketStatusRequest(@NotNull TicketStatus status) {}