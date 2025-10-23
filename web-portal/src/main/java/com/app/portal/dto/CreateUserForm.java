package com.app.portal.dto;

public class CreateUserForm {
    private String email;
    private String name;
    private String role;
    private String password;
    private String supervisorId;

    public CreateUserForm() {}

    public CreateUserForm(String email, String name, String role, String password, String supervisorId) {
        this.email = email;
        this.name = name;
        this.role = role;
        this.password = password;
        this.supervisorId = supervisorId;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getSupervisorId() { return supervisorId; }
    public void setSupervisorId(String supervisorId) { this.supervisorId = supervisorId; }
}
