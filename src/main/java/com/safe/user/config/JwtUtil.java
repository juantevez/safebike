package com.safe.user.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret:}") // Permite vacío para usar generación segura
    private String secret;

    @Value("${jwt.expiration:86400}")
    private int expiration;

    private SecretKey getSigningKey() {
        // Si no se proporciona una clave segura, genera una para HS512
        if (secret == null || secret.isEmpty()) {
            return Keys.secretKeyFor(SignatureAlgorithm.HS512);
        }
        // Asegura que la clave tenga al menos 512 bits (64 bytes)
        if (secret.getBytes().length < 64) {
            throw new IllegalArgumentException(
                    "La clave JWT debe tener al menos 64 caracteres (512 bits) para HS512"
            );
        }
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000L))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512) // Usa HS512 con clave segura
                .compact();
    }

    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return extractedUsername.equals(username) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .parseClaimsJws(token)
                .getBody();
    }
}