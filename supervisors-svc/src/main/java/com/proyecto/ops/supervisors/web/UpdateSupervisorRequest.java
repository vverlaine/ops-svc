package com.proyecto.ops.supervisors.web;

import java.util.UUID;

public record UpdateSupervisorRequest(
    Boolean active,
    UUID teamId
) {}
