package com.app.portal.web;

import com.app.portal.session.CurrentUser;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final CurrentUser current;

    public WebConfig(CurrentUser current) {
        this.current = current;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor(current));
    }
}