package com.safe.user.config;

import com.safe.user.application.service.UserServiceImpl;
import com.safe.user.domain.model.entity.User;
import com.vaadin.flow.server.VaadinSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuthenticationHelper {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationHelper.class);

    private final JwtUtil jwtUtil;
    private final UserServiceImpl userService;

    public AuthenticationHelper(JwtUtil jwtUtil, UserServiceImpl userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    /**
     * Verifica si el usuario actual está autenticado
     * Solo verifica la sesión de Vaadin y la validez del token
     */
    public boolean isAuthenticated() {
        try {
            VaadinSession session = VaadinSession.getCurrent();
            if (session == null) {
                logger.debug("No hay sesión de Vaadin activa");
                return false;
            }

            String token = (String) session.getAttribute("authToken");
            String email = (String) session.getAttribute("userEmail");

            if (token == null || token.trim().isEmpty()) {
                logger.debug("No hay token en la sesión de Vaadin");
                return false;
            }

            if (email == null || email.trim().isEmpty()) {
                logger.debug("No hay email en la sesión de Vaadin");
                return false;
            }

            // Validar que el token siga siendo válido
            if (!jwtUtil.validateToken(token)) {
                logger.warn("Token inválido o expirado para usuario: {}", email);
                clearAuthentication(); // Limpiar sesión inválida
                return false;
            }

            // Validar que el token corresponda al usuario correcto
            String tokenEmail = jwtUtil.extractUsername(token);
            if (!email.equalsIgnoreCase(tokenEmail)) {
                logger.warn("Email en sesión no coincide con email en token");
                clearAuthentication();
                return false;
            }

            logger.debug("Usuario autenticado: {}", email);
            return true;

        } catch (Exception e) {
            logger.error("Error verificando autenticación: {}", e.getMessage());
            clearAuthentication(); // Limpiar en caso de error
            return false;
        }
    }

    /**
     * Obtiene el email del usuario actual
     */
    public Optional<String> getCurrentUserEmail() {
        if (!isAuthenticated()) {
            return Optional.empty();
        }

        VaadinSession session = VaadinSession.getCurrent();
        if (session != null) {
            String email = (String) session.getAttribute("userEmail");
            return Optional.ofNullable(email);
        }

        return Optional.empty();
    }

    /**
     * Obtiene el token de la sesión de Vaadin
     */
    public Optional<String> getCurrentToken() {
        if (!isAuthenticated()) {
            return Optional.empty();
        }

        VaadinSession session = VaadinSession.getCurrent();
        if (session != null) {
            String token = (String) session.getAttribute("authToken");
            return Optional.ofNullable(token);
        }
        return Optional.empty();
    }

    /**
     * Limpia la autenticación
     */
    public void clearAuthentication() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session != null) {
            session.setAttribute("authToken", null);
            session.setAttribute("userEmail", null);
            logger.info("Sesión de usuario limpiada");
        }
    }

    /**
     * Establece la autenticación en la sesión de Vaadin
     */
    public void setAuthentication(String token, String email) {
        VaadinSession session = VaadinSession.getCurrent();
        if (session != null) {
            session.setAttribute("authToken", token);
            session.setAttribute("userEmail", email);
            logger.info("Autenticación establecida para usuario: {}", email);
        }
    }

    /**
     * Método de conveniencia para verificar si se requiere autenticación
     */
    public boolean requiresAuthentication() {
        return !isAuthenticated();
    }

    /**
     * Valida si un token específico es válido (útil para logout)
     */
    public boolean isValidToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }

        try {
            return jwtUtil.validateToken(token);
        } catch (Exception e) {
            logger.error("Error validando token: {}", e.getMessage());
            return false;
        }
    }
    public Long getCurrentUserId() {
        Optional<String> emailOpt = getCurrentUserEmail();
        if (emailOpt.isPresent()) {
            try {
                User user = userService.findByEmail(emailOpt.get());
                if (user != null) {
                    logger.debug("ID de usuario obtenido: {} para email: {}", user.getId(), emailOpt.get());
                    return user.getId();
                } else {
                    logger.warn("Usuario no encontrado en BD para email: {}", emailOpt.get());
                    // Si el usuario no se encuentra en BD, limpiar la sesión inválida
                    clearAuthentication();
                }
            } catch (Exception e) {
                logger.error("Error obteniendo ID de usuario actual: {}", e.getMessage());
                // En caso de error, limpiar la sesión por seguridad
                clearAuthentication();
            }
        }
        return null;
    }

    /**
     * Obtiene el usuario completo actual
     */
    public Optional<User> getCurrentUser() {
        Optional<String> emailOpt = getCurrentUserEmail();
        if (emailOpt.isPresent()) {
            try {
                User user = userService.findByEmail(emailOpt.get());
                if (user != null) {
                    logger.debug("Usuario completo obtenido: {}", user.getEmail());
                    return Optional.of(user);
                } else {
                    logger.warn("Usuario no encontrado en BD para email: {}", emailOpt.get());
                    clearAuthentication();
                }
            } catch (Exception e) {
                logger.error("Error obteniendo usuario actual: {}", e.getMessage());
                clearAuthentication();
            }
        }
        return Optional.empty();
    }
}