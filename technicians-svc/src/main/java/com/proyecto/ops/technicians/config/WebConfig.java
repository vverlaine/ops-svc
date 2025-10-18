package com.proyecto.ops.technicians.config;

import java.util.List;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.proyecto.ops.technicians.security.AuthFilter;
import com.proyecto.ops.technicians.security.CurrentUserArgumentResolver;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  private final AuthFilter authFilter;
  private final CurrentUserArgumentResolver resolver;

  public WebConfig(AuthFilter authFilter, CurrentUserArgumentResolver resolver) {
    this.authFilter = authFilter;
    this.resolver = resolver;
  }

  @Bean
  public FilterRegistrationBean<AuthFilter> authFilterRegistration() {
    FilterRegistrationBean<AuthFilter> reg = new FilterRegistrationBean<>();
    reg.setFilter(authFilter);
    reg.addUrlPatterns("/*");
    reg.setOrder(1);
    return reg;
  }

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(resolver);
  }
}