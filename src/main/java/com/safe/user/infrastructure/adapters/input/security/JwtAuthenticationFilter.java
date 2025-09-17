package com.safe.user.infrastructure.adapters.input.security;


import com.safe.user.config.JwtUtil;
import com.safe.user.infrastructure.adapters.output.external.TokenBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;

    // ✅ SOLUCIÓN: Usar @Lazy para romper el ciclo de dependencias
    public JwtAuthenticationFilter(JwtUtil jwtUtil,
                                   UserDetailsService userDetailsService,
                                   TokenBlacklistService tokenBlacklistService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String token = extractTokenFromRequest(request);

            if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                logger.debug("Procesando token JWT");

                // Verificar si el token está en blacklist
                if (tokenBlacklistService.isTokenBlacklisted(token)) {
                    logger.warn("Token está en blacklist");
                    filterChain.doFilter(request, response);
                    return;
                }

                // Validar token y establecer contexto de seguridad
                if (jwtUtil.validateToken(token)) {
                    String username = jwtUtil.extractUsername(token);
                    logger.debug("Token válido para usuario: {}", username);

                    // ✅ El UserDetailsService se carga de forma lazy
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    if (jwtUtil.validateToken(token, username)) {
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        logger.debug("Usuario autenticado establecido en SecurityContext: {}", username);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error procesando JWT: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        // 1. Intentar obtener desde header Authorization
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        // 2. Intentar obtener desde cookie
        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if ("authToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        // 3. Intentar obtener desde sesión (para integración con Vaadin)
        var session = request.getSession(false);
        if (session != null) {
            Object token = session.getAttribute("authToken");
            if (token instanceof String) {
                return (String) token;
            }
        }

        // 4. Intentar desde parámetro de query
        String queryToken = request.getParameter("token");
        if (queryToken != null && !queryToken.trim().isEmpty()) {
            return queryToken;
        }

        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        // No filtrar rutas públicas
        return path.equals("/") ||
                path.startsWith("/login") ||
                path.startsWith("/register") ||
                path.startsWith("/VAADIN/") ||
                path.startsWith("/frontend/") ||
                path.startsWith("/images/") ||
                path.contains("manifest.json") ||
                path.contains("sw.js") ||
                path.contains("favicon.ico");
    }
}