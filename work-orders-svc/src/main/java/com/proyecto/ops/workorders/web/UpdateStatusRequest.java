package com.proyecto.ops.workorders.web;

import com.proyecto.ops.workorders.model.WoStatus;

import jakarta.validation.constraints.NotNull;

public record UpdateStatusRequest(@NotNull WoStatus status, String notes) {}