package com.safe.user.application.service;


import com.safe.user.config.JwtUtil;
import com.safe.user.infrastructure.adapters.output.external.TokenBlacklistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;

    public AuthService(AuthenticationManager authenticationManager, JwtUtil jwtUtil, TokenBlacklistService tokenBlacklistService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    public String login(String email, String password) {
        try {
            var token = new UsernamePasswordAuthenticationToken(email, password);
            Authentication auth = authenticationManager.authenticate(token);
            return jwtUtil.generateToken(email);

        } catch (BadCredentialsException | UsernameNotFoundException e) {
            throw new IllegalArgumentException("credenciales invalidas");
        } catch (DisabledException e) {
            throw new IllegalArgumentException("usuario deshabilitado");
        } catch (Exception e) {
            throw new IllegalArgumentException("error en autenticación");
        }
    }

    public void logout(String token) {
        try {
            // Solo validar que el token sea válido y extraer usuario para logging
            if (jwtUtil.validateToken(token)) {
                String email = jwtUtil.extractUsername(token);

                // Agregar a blacklist sin preocuparse por la expiración específica
                tokenBlacklistService.blacklistToken(token);

                logger.info("Usuario {} ha cerrado sesión exitosamente", email);
            } else {
                logger.warn("Intento de logout con token inválido o expirado");
            }
        } catch (Exception e) {
            logger.error("Error durante logout: {}", e.getMessage());
            // No lanzar excepción para logout, solo loggar
        }
    }

}