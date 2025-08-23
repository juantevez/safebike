package com.safe.bike.domain.port.out;

import com.safe.bike.domain.model.entity.BikeModelEntity;

import java.util.List;
import java.util.Optional;

public interface BikeModelRepositoryPort {

    List<BikeModelEntity> findAll();

    Optional<BikeModelEntity> findById(Integer id);

    List<BikeModelEntity> findByBrandId(Integer brandId);

    BikeModelEntity save(BikeModelEntity bikeModel);

    void deleteById(Integer id);

    Long countByBrandId(Integer brandId);
}