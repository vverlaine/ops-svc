package com.proyecto.ops.assets.web;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record AssetResponse(
        UUID id,
        UUID customerId,
        UUID siteId,
        String serialNumber,
        String model,
        String type,
        LocalDate installedAt,
        String notes,
        Instant createdAt
) {}