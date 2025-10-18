package com.proyecto.ops.customers.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CustomerBasic(
    UUID id,
    String name,
    String taxId,
    String email,
    String phone,
    OffsetDateTime createdAt
) {}