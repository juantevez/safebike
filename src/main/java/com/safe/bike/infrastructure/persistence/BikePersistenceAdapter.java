package com.safe.bike.infrastructure.persistence;

import com.safe.bike.domain.model.BikeEntity;
import com.safe.bike.domain.port.out.BikeRepositoryPort;

import java.util.Optional;

public class BikePersistenceAdapter implements BikeRepositoryPort {

    private final BikeRepository bikeRepository;

    public BikePersistenceAdapter(BikeRepository bikeRepository) {
        this.bikeRepository = bikeRepository;
    }

    @Override
    public void save(BikeEntity bike) {
        bikeRepository.save(bike);
    }

    @Override
    public Optional<BikeEntity> findByBrand(Integer brand) {
        return bikeRepository.findByBrandId(brand);
    }

    @Override
    public Optional<BikeEntity> findById(Long bikeId) {
        return bikeRepository.findById(bikeId);
    }

}
