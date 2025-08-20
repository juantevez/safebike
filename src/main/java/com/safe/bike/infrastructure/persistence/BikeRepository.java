package com.safe.bike.infrastructure.persistence;

import com.safe.bike.domain.model.entity.BikeEntity;
import com.safe.bike.domain.model.entity.BrandEntity;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface BikeRepository extends Repository<BikeEntity, Long> {
    void save(BikeEntity bike);
    Optional<BikeEntity> findById(BrandEntity bikesId);
    Optional<BikeEntity> findByBrandId(Integer brand);
    List<BikeEntity> findAll();
}