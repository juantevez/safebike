package com.safe.user.infrastructure.adapters.input.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // ✅ SOLUCIÓN: Usar @Lazy para romper el ciclo
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(@Lazy JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        // ✅ PERMITIR estas rutas SIN autenticación
                        .requestMatchers("/", "/login", "/register").permitAll()
                        // ✅ PERMITIR recursos estáticos de Vaadin
                        .requestMatchers("/VAADIN/**", "/vaadinServlet/**", "/frontend/**").permitAll()
                        // ✅ REQUERIR autenticación para rutas protegidas
                        .requestMatchers("/bike-form", "/photo-upload", "/reports").authenticated()
                        // ✅ Por defecto, todo lo demás requiere autenticación
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.disable()) // ✅ Deshabilitar CSRF para simplificar (solo para desarrollo)
                .httpBasic(httpBasic -> httpBasic.disable()) // ✅ Deshabilitar HTTP Basic Auth
                .formLogin(form -> form.disable()); // ✅ Deshabilitar el form login automático de Spring

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}