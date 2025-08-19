package com.safe.bike.domain.port.out;

import com.safe.bike.domain.model.BikeEntity;

import java.util.Optional;

public interface BikeRepositoryPort {
    void save(BikeEntity bike);
    Optional<BikeEntity> findByBrand(String brand);

    Optional<BikeEntity> findById(Long bikeId);

}
