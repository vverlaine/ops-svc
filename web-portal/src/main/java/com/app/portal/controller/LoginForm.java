package com.app.portal.controller;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginForm(
        @Email String email,
        @NotBlank String password
) {}