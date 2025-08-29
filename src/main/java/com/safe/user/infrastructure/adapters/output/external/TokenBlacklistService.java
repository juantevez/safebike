package com.safe.user.infrastructure.adapters.output.external;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TokenBlacklistService {

    @Autowired
    private CacheManager cacheManager;

    // Método simplificado - solo recibe el token
    public void blacklistToken(String token) {
        Cache blacklistCache = cacheManager.getCache("tokenBlacklist");
        if (blacklistCache != null) {
            // El cache ya tiene configurado el TTL en la configuración (5 minutos)
            blacklistCache.put(token, true);
        }
    }

    public boolean isTokenBlacklisted(String token) {
        Cache blacklistCache = cacheManager.getCache("tokenBlacklist");
        return blacklistCache != null && blacklistCache.get(token) != null;
    }
}