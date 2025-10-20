package com.app.portal.session;

import java.io.Serializable;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.app.portal.dto.UserDto;

@Component
@SessionScope
public class CurrentUser implements Serializable {

    private UserDto user;

    public boolean isLoggedIn() {
        return user != null;
    }

    public UserDto get() {
        return user;
    }

    public void set(UserDto user) {
        this.user = user;
    }

    public void clear() {
        this.user = null;
    }

    public String getRole() {
        return isLoggedIn() ? user.getRole() : null;
    }

    public String getEmail() {
        return isLoggedIn() ? user.getEmail() : null;
    }

    public String getName() {
        return isLoggedIn() ? user.getName() : null;
    }
}