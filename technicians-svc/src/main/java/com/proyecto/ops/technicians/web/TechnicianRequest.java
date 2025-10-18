package com.proyecto.ops.technicians.web;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record TechnicianRequest(
    @NotNull UUID userId,
    Boolean active,
    List<String> skills
) {}