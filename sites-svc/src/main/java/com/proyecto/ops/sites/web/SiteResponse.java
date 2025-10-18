package com.proyecto.ops.sites.web;

import java.time.OffsetDateTime;
import java.util.UUID;

public record SiteResponse(
        UUID id,
        UUID customerId,
        String name,
        String address,
        String city,
        String state,
        String country,
        OffsetDateTime createdAt
) {}