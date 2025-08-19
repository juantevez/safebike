package com.safe.bike.config;

import com.safe.bike.infrastructure.persistence.BikePersistenceAdapter;
import com.safe.bike.infrastructure.persistence.BikeRepository;
import com.safe.bike.domain.port.out.BikeRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseConfig {

    private final BikeRepository bikeRepository;

    public DatabaseConfig(BikeRepository bikeRepository) {
        this.bikeRepository = bikeRepository;
    }

    @Bean
    public BikeRepositoryPort bikeRepositoryPort() {
        return new BikePersistenceAdapter(bikeRepository);
    }
}