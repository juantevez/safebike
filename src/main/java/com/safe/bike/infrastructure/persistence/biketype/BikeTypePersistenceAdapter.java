package com.safe.bike.infrastructure.persistence.biketype;

import com.safe.bike.domain.model.entity.BikeTypeEntity;
import com.safe.bike.domain.port.out.BikeTypeRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class BikeTypePersistenceAdapter implements BikeTypeRepositoryPort {

    // Inyecta tu repositorio de Spring Data JPA
    private final BikeTypeJpaRepository bikeTypeJpaRepository;

    public BikeTypePersistenceAdapter(BikeTypeJpaRepository bikeTypeJpaRepository) {
        this.bikeTypeJpaRepository = bikeTypeJpaRepository;
    }

    @Override
    public BikeTypeEntity save(BikeTypeEntity bikeType) {
        return bikeTypeJpaRepository.save(bikeType);
    }

    @Override
    public List<BikeTypeEntity> findAll() {
        return bikeTypeJpaRepository.findAll();
    }

    @Override
    public Optional<BikeTypeEntity> findById(Integer id) {
        return bikeTypeJpaRepository.findById(id);
    }
}