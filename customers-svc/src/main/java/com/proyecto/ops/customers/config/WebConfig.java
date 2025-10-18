package com.proyecto.ops.customers.config;

import java.util.List;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.proyecto.ops.customers.security.AuthFilter;
import com.proyecto.ops.customers.security.CurrentUserArgumentResolver;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuthFilter authFilter;
    private final CurrentUserArgumentResolver currentUserArgumentResolver;

    public WebConfig(AuthFilter authFilter, CurrentUserArgumentResolver resolver) {
        this.authFilter = authFilter;
        this.currentUserArgumentResolver = resolver;
    }

    @Bean
    public FilterRegistrationBean<AuthFilter> authFilterRegistration() {
        FilterRegistrationBean<AuthFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(authFilter);
        reg.setOrder(1);
        reg.addUrlPatterns("/*");
        return reg;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(currentUserArgumentResolver);
    }
}