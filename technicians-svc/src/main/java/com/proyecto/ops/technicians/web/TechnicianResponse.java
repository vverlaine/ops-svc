package com.proyecto.ops.technicians.web;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record TechnicianResponse(
    UUID id,
    UUID userId,
    String userName,
    Boolean active,
    List<String> skills,
    OffsetDateTime createdAt
) {}