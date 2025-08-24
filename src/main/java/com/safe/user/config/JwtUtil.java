package com.safe.user.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    @Value("${jwt.secret:}") // Permite vacío para usar generación segura
    private String secret;

    @Value("${jwt.expiration:86400}")
    private int expiration;

    private SecretKey getSigningKey() {
        // Si no se proporciona una clave segura, genera una para HS256
        if (secret == null || secret.isEmpty()) {
            return Keys.secretKeyFor(SignatureAlgorithm.HS256);
        }
        // Asegura que la clave tenga al menos 512 bits (64 bytes)
        if (secret.getBytes().length < 32) {
            throw new IllegalArgumentException(
                    "La clave JWT debe tener al menos 32 caracteres (512 bits) para HS256"
            );
        }
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000L)) // ⚠️ ¿está en segundos?
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // ✅ Usa getSigningKey()
                .compact();
    }

    public String extractUsername(String token) {
        return getClaims(token).getSubject();
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

    public boolean validateToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (ExpiredJwtException e) {
            logger.warn("Token expirado: {}", e.getMessage());
            return false;
        } catch (UnsupportedJwtException e) {
            logger.warn("Token no soportado: {}", e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            logger.warn("Token malformado: {}", e.getMessage());
            return false;
        } catch (SignatureException e) {
            logger.warn("Firma del token inválida: {}", e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            logger.warn("Token vacío o nulo: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("Error inesperado validando token: {}", e.getMessage());
            return false;
        }
    }

    public boolean validateToken(String token, String username) {
        try {
            final String extractedUsername = extractUsername(token);
            return (extractedUsername.equals(username) && validateToken(token));
        } catch (Exception e) {
            logger.warn("Error validando token para usuario {}: {}", username, e.getMessage());
            return false;
        }
    }


    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey()) // ✅ Usa el mismo método
                .parseClaimsJws(token)
                .getBody();
    }


}