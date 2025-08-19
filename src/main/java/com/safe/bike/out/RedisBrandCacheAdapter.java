package com.safe.bike.out;

import com.safe.bike.domain.model.Brand;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class RedisBrandCacheAdapter implements RedisBrandPort {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private static final String BRANDS_CACHE_KEY = "brands:all";

    public RedisBrandCacheAdapter(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void saveAll(List<Brand> brands) {
        try {
            String json = objectMapper.writeValueAsString(brands);
            redisTemplate.opsForValue().set(BRANDS_CACHE_KEY, json);
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar en Redis", e);
        }
    }

    @Override
    public Optional<List<Brand>> findAll() {
        String json = redisTemplate.opsForValue().get(BRANDS_CACHE_KEY);
        if (json == null) return Optional.empty();

        try {
            return Optional.of(objectMapper.readValue(json, new TypeReference<List<Brand>>() {}));
        } catch (Exception e) {
            throw new RuntimeException("Error al leer desde Redis", e);
        }
    }

    @Override
    public void deleteAll() {
        redisTemplate.delete(BRANDS_CACHE_KEY);
    }
}