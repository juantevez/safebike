package com.safe.bike.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager brandCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("brandNames");

        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(100)                 // Máximo 100 elementos (aunque aquí solo hay 1 lista)
                .expireAfterWrite(10, TimeUnit.MINUTES) // Invalida tras 10 min
                .recordStats()                   // Para monitoreo
        );

        return cacheManager;
    }
}