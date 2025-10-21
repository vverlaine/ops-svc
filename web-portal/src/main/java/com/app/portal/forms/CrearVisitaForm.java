package com.app.portal.forms;

import java.time.LocalDateTime;

public class CrearVisitaForm {
    private String customerId;
    private String siteId;
    private String technicianId;
    private LocalDateTime scheduledStartAt;
    private LocalDateTime scheduledEndAt;
    private String priority;
    private String purpose;
    private String notesPlanned;

    public String getNotesPlanned() {
        return notesPlanned;
    }

    public void setNotesPlanned(String notesPlanned) {
        this.notesPlanned = notesPlanned;
    }

    public String getPriority() {
        return priority;
    }
    public void setPriority(String priority) {
        this.priority = priority;
    }
    public String getPurpose() {
        return purpose;
    }
    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

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
}