package com.proyecto.ops.assets.web;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;

public record UpdateAssetRequest(
        String type,
        String model,
        @NotBlank String serialNumber,
        UUID siteId,
        LocalDate installedAt,
        String notes
) {}