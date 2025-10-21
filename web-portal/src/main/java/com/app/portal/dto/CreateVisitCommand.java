package com.app.portal.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import java.time.OffsetDateTime;

public class CreateVisitCommand {

    private UUID customerId;
    private UUID siteId;
    private UUID technicianId;
    private OffsetDateTime scheduledStartAt;
    private OffsetDateTime scheduledEndAt;
    private String purpose;
    private String priority;
    private String notesPlanned;


    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public UUID getSiteId() {
        return siteId;
    }

    public void setSiteId(UUID siteId) {
        this.siteId = siteId;
    }

    public UUID getTechnicianId() {
        return technicianId;
    }

    public void setTechnicianId(UUID technicianId) {
        this.technicianId = technicianId;
    }

    public OffsetDateTime getScheduledStartAt() {
        return scheduledStartAt;
    }

    public void setScheduledStartAt(OffsetDateTime scheduledStartAt) {
        this.scheduledStartAt = scheduledStartAt;
    }

    public OffsetDateTime getScheduledEndAt() {
        return scheduledEndAt;
    }

    public void setScheduledEndAt(OffsetDateTime scheduledEndAt) {
        this.scheduledEndAt = scheduledEndAt;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getNotesPlanned() {
        return notesPlanned;
    }

    public void setNotesPlanned(String notesPlanned) {
        this.notesPlanned = notesPlanned;
    }

    public CreateVisitCommand(UUID customerId, UUID siteId, UUID technicianId,
            OffsetDateTime scheduledStartAt, OffsetDateTime scheduledEndAt, String purpose, String priority, String notesPlanned) {
        this.customerId = customerId;
        this.siteId = siteId;
        this.technicianId = technicianId;
        this.scheduledStartAt = scheduledStartAt;
        this.scheduledEndAt = scheduledEndAt;
        this.purpose = purpose;
        this.priority = priority;
        this.notesPlanned = notesPlanned;
    }
}
