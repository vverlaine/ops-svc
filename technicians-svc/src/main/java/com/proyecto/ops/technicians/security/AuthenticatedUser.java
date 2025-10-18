package com.proyecto.ops.technicians.security;

import java.util.UUID;

public record AuthenticatedUser(UUID id, String name, String email, String role) {}