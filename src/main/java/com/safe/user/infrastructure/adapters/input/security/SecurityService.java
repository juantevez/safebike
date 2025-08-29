package com.safe.user.infrastructure.adapters.input.security;

import com.safe.user.application.service.UserServiceImpl;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SecurityService {

    private final UserServiceImpl userService;

    public SecurityService(UserServiceImpl userService) {
        this.userService = userService;
    }

    /**
     * Obtiene el usuario autenticado actual desde el contexto de Spring Security
     * @return Usuario autenticado o null si no hay usuario autenticado
     */
    public com.safe.user.domain.model.User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }

        // El username en tu caso es el email
        String email = authentication.getName();

        try {
            return userService.findByEmail(email);
        } catch (Exception e) {
            // Log del error si es necesario
            return null;
        }
    }

    /**
     * Verifica si hay un usuario autenticado
     * @return true si hay usuario autenticado, false en caso contrario
     */
    public boolean isUserAuthenticated() {
        return getAuthenticatedUser() != null;
    }

    /**
     * Obtiene el email del usuario autenticado
     * @return email del usuario o null si no está autenticado
     */
    public String getAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }

        return authentication.getName();
    }

    /**
     * Obtiene los roles del usuario autenticado
     * @return Lista de roles o lista vacía si no está autenticado
     */
    public List<String> getAuthenticatedUserRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Collections.emptyList();
        }

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

    /**
     * Verifica si el usuario autenticado tiene un rol específico
     * @param role El rol a verificar (sin prefijo ROLE_)
     * @return true si tiene el rol, false en caso contrario
     */
    public boolean hasRole(String role) {
        return getAuthenticatedUserRoles().contains("ROLE_" + role);
    }

    /**
     * Logout programático del usuario
     */
    public void logout() {
        SecurityContextHolder.clearContext();
    }
}