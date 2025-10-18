package com.proyecto.ops.workorders.web;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record AssignRequest(@NotNull UUID technicianId) {}