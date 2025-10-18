package com.proyecto.ops.contacts.web;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateContactRequest(
        @NotNull UUID customerId,
        @NotBlank String name,
        String email,
        String phone,
        String role
) {}