package com.proyecto.ops.contacts.web;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ContactResponse(
        UUID id,
        UUID customerId,
        String name,
        String email,
        String phone,
        String role,
        OffsetDateTime createdAt
) {}