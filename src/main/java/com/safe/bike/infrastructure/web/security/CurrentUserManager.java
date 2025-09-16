package com.safe.bike.infrastructure.web.security;

import com.safe.user.config.JwtUtil;
import com.safe.user.infrastructure.port.UserServicePort;
import com.safe.user.domain.model.entity.User;
import com.vaadin.flow.server.VaadinSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CurrentUserManager {

    private static final Logger logger = LoggerFactory.getLogger(CurrentUserManager.class);

    private final JwtUtil jwtUtil;
    private final UserServicePort userService;

    public CurrentUserManager(JwtUtil jwtUtil, UserServicePort userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    public Optional<User> getCurrentUser() {
        logger.info("=== OBTENIENDO USUARIO ACTUAL ===");

        try {
            String token = getSessionToken();
            String email = getSessionEmail();

            if (token != null && email != null && jwtUtil.validateToken(token, email)) {
                logger.info("✅ Token válido para: {}", email);
                return loadUserFromEmail(email);
            }

            // Fallback: Spring Security
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
                logger.info("✅ Usando usuario de Spring Security: {}", auth.getName());
                return loadUserFromEmail(auth.getName());
            }

            logger.warn("❌ No se pudo obtener usuario válido");
            return Optional.empty();

        } catch (Exception e) {
            logger.error("💥 Error obteniendo usuario actual", e);
            return Optional.empty();
        }
    }

    public void clearSession() {
        VaadinSession.getCurrent().setAttribute("authToken", null);
        VaadinSession.getCurrent().setAttribute("userEmail", null);
        SecurityContextHolder.clearContext();
        VaadinSession.getCurrent().close();
    }

    private String getSessionToken() {
        return (String) VaadinSession.getCurrent().getAttribute("authToken");
    }

    private String getSessionEmail() {
        return (String) VaadinSession.getCurrent().getAttribute("userEmail");
    }

    private Optional<User> loadUserFromEmail(String email) {
        User user = userService.findByEmail(email);
        if (user != null) {
            logger.info("✅ Usuario cargado: {}", user.getEmail());
            return Optional.of(user);
        } else {
            logger.error("❌ Usuario no encontrado en BD: {}", email);
            return Optional.empty();
        }
    }
}