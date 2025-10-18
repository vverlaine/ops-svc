package com.proyecto.ops.customers.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCustomerRequest(
        @NotBlank @Size(max = 200) String name,
        @Size(max = 50) String taxId,
        @Size(max = 200) String email,
        @Size(max = 50) String phone
) {}