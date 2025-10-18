package com.proyecto.ops.assets.web;

import java.time.LocalDate;
import java.util.UUID;

public record UpdateAssetRequest(
        String type,
        String model,
        String serialNumber,
        UUID siteId,
        LocalDate installedAt,
        String notes
) {}