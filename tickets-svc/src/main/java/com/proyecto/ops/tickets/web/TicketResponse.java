// tickets-svc/src/main/java/com/proyecto/ops/tickets/web/TicketResponse.java
package com.proyecto.ops.tickets.web;

import java.time.OffsetDateTime;
import java.util.UUID;

public record TicketResponse(
        UUID id,
        String title,
        String description,
        String status,
        String priority,
        UUID customerId,
        String customerName,
        UUID siteId,
        UUID assetId,
        UUID requestedBy,
        String requestedByName,
        String createdBy,
        OffsetDateTime createdAt
) {}