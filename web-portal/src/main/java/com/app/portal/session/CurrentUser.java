package com.app.portal.session;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;
import com.app.portal.service.AuthClient;

@Component
@SessionScope
public class CurrentUser {
    private String email;
    private String name;
    private String role;

    public boolean isLoggedIn() { return email != null; }

    public String getEmail() { return email; }
    public String getName()  { return name; }
    public String getRole()  { return role; }

    public void set(AuthClient.UserDto dto) {
        this.email = dto.email();
        this.name  = dto.name();
        this.role  = dto.role();
    }
    public void clear() { this.email = this.name = this.role = null; }
}