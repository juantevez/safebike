package com.safe.bike.infrastructure.persistence;

import com.safe.bike.domain.model.BikeEntity;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface BikeRepository extends Repository<BikeEntity, Long> {
    void save(BikeEntity bike);
    Optional<BikeEntity> findById(Long bikesId);
    Optional<BikeEntity> findByBrand(String brand);
}