package com.app.portal.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final AuthenticationProvider authProvider;

    public SecurityConfig(AuthenticationProvider authProvider) {
        this.authProvider = authProvider;
    }

    @Bean
    public AuthenticationSuccessHandler roleBasedSuccessHandler() {
        return new RoleBasedSuccessHandler();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authenticationProvider(authProvider)
            // Si tu login.html incluye campos de CSRF, habilita CSRF; si no, mantenlo deshabilitado.
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**","/js/**","/images/**","/favicon.ico").permitAll()
                .requestMatchers("/login","/logout","/error").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/supervisor/**").hasAnyRole("SUPERVISOR","ADMIN")
                .requestMatchers("/tech/**").hasAnyRole("TECH","ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(f -> f
                .loginPage("/login")
                .loginProcessingUrl("/login")
                // En vez de defaultSuccessUrl usamos el handler de roles:
                .successHandler(roleBasedSuccessHandler())
                .permitAll()
            )
            .logout(l -> l
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .permitAll()
            );

        return http.build();
    }
}