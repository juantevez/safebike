package com.safe.bike.config;

import com.safe.bike.infrastructure.persistence.bike.BikeRepository;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseConfig {

    private final BikeRepository bikeRepository;

    public DatabaseConfig(BikeRepository bikeRepository) {
        this.bikeRepository = bikeRepository;
    }

}