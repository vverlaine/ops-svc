package com.app.portal.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      .authorizeHttpRequests(auth -> auth
        .requestMatchers("/css/**","/js/**","/images/**","/favicon.ico","/h2/**").permitAll()
        .requestMatchers("/login").permitAll()
        .anyRequest().authenticated()
      )
      .formLogin(f -> f
        .loginPage("/login")
        .loginProcessingUrl("/login")
        .usernameParameter("username")
        .passwordParameter("password")
        .defaultSuccessUrl("/dashboard", true)
        .failureUrl("/login?error")
        .permitAll()
      )
      .logout(l -> l
        .logoutUrl("/logout")
        .logoutSuccessUrl("/login?logout")
        .permitAll()
      )
      .rememberMe(Customizer.withDefaults())
      .csrf(csrf -> csrf.ignoringRequestMatchers("/h2/**"))
      .headers(h -> h.frameOptions(f -> f.sameOrigin()));

    return http.build();
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  // Usuario de prueba: admin / admin123
  @Bean
  UserDetailsService userDetailsService(PasswordEncoder encoder) {
    return new InMemoryUserDetailsManager(
      User.withUsername("admin")
          .password(encoder.encode("admin123"))
          .roles("ADMIN")
          .build()
    );
  }
}