package com.proyecto.ops.workorders.web;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record CreateWorkOrderRequest(
    @NotNull UUID ticketId,
    UUID technicianId,
    OffsetDateTime scheduledAt,
    String notes
) {}