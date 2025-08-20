package com.safe.bike.domain.port.out;

import com.safe.bike.domain.model.entity.BikeEntity;

import java.util.List;
import java.util.Optional;

public interface BikeRepositoryPort {
    void save(BikeEntity bike);
    Optional<BikeEntity> findByBrand(Integer brand);

    Optional<BikeEntity> findById(Long bikeId);
    List<BikeEntity> findAll();

    List<BikeEntity> findByBrandId(Integer brandId);
}
