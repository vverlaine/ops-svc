package com.app.portal.dto;

import java.util.UUID;

public class UserDto {

    private UUID id;
    private String name;
    private String email;
    private String role;
    private String supervisorId;
    private String teamId;

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getSupervisorId() { return supervisorId; }
    public String getTeamId() { return teamId; }

    public void setId(UUID id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setRole(String role) { this.role = role; }
    public void setSupervisorId(String supervisorId) { this.supervisorId = supervisorId; }
    public void setTeamId(String teamId) { this.teamId = teamId; }
}
