package com.proyecto.ops.sites.web;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateSiteRequest(
        @NotNull UUID customerId,
        @NotBlank String name,
        String address,
        String city,
        String state,
        String country
) {}