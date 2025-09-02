package com.safe.bike.config;

import com.safe.bike.infrastructure.persistence.bike.BikeJpaRepository;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseConfig {

    private final BikeJpaRepository bikeJpaRepository;


    public DatabaseConfig(BikeJpaRepository bikeJpaRepository) {
        this.bikeJpaRepository = bikeJpaRepository;
    }

}