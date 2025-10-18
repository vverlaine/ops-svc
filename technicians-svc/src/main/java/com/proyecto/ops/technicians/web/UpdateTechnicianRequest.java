package com.proyecto.ops.technicians.web;

import java.util.List;

public record UpdateTechnicianRequest(
    Boolean active,
    List<String> skills
) {}