package com.proyecto.ops.contacts.web;

import jakarta.validation.constraints.NotBlank;

public record UpdateContactRequest(
        @NotBlank String name,
        String email,
        String phone,
        String role
) {}