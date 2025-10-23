package com.proyecto.ops.supervisors.web;

import java.time.OffsetDateTime;
import java.util.UUID;

public record SupervisorResponse(
    UUID userId,
    String name,
    Boolean active,
    UUID teamId,
    OffsetDateTime createdAt
) {}
