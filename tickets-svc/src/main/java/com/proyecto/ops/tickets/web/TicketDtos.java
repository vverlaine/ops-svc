package com.proyecto.ops.tickets.web;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class TicketDtos {

    public record CreateReq(
            @NotBlank String title,
            String description,
            @Pattern(regexp = "OPEN|IN_PROGRESS|CLOSED") String status,
            @Pattern(regexp = "LOW|MEDIUM|HIGH") String priority,
            UUID customerId,
            UUID assetId,
            @NotBlank String createdBy
    ) {}

    public record UpdateStatusReq(
            @Pattern(regexp = "OPEN|IN_PROGRESS|CLOSED") String status
    ) {}
}