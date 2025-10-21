package com.app.portal.dto;

import java.time.LocalDateTime;

public class CreateVisitCommand {

    private String customerId;
    private String siteId;
    private String technicianId;
    private LocalDateTime scheduledStartAt;
    private LocalDateTime scheduledEndAt;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getTechnicianId() {
        return technicianId;
    }

    public void setTechnicianId(String technicianId) {
        this.technicianId = technicianId;
    }

    public LocalDateTime getScheduledStartAt() {
        return scheduledStartAt;
    }

    public void setScheduledStartAt(LocalDateTime scheduledStartAt) {
        this.scheduledStartAt = scheduledStartAt;
    }

    public LocalDateTime getScheduledEndAt() {
        return scheduledEndAt;
    }

    public void setScheduledEndAt(LocalDateTime scheduledEndAt) {
        this.scheduledEndAt = scheduledEndAt;
    }

    public CreateVisitCommand(String customerId, String siteId, String technicianId,
            LocalDateTime scheduledStartAt, LocalDateTime scheduledEndAt) {
        this.customerId = customerId;
        this.siteId = siteId;
        this.technicianId = technicianId;
        this.scheduledStartAt = scheduledStartAt;
        this.scheduledEndAt = scheduledEndAt;
    }
}
