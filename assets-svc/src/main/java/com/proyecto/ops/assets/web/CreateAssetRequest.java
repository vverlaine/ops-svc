package com.proyecto.ops.assets.web;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateAssetRequest(
        @NotNull UUID customerId,
        UUID siteId,
        @NotBlank String serialNumber,
        String model,
        String type,
        LocalDate installedAt,
        String notes
) {}