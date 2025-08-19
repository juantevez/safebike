package com.safe.user.application.service;


import com.safe.user.config.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;


    public AuthService(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
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
}