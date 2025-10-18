package com.proyecto.ops.sites.web;

import jakarta.validation.constraints.NotBlank;

public record UpdateSiteRequest(
        @NotBlank String name,
        String address,
        String city,
        String state,
        String country
) {}