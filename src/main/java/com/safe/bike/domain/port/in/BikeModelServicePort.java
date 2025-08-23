package com.safe.bike.domain.port.in;


import com.safe.bike.domain.model.entity.BikeModelEntity;

import java.util.List;
import java.util.Optional;

public interface BikeModelServicePort {

    List<BikeModelEntity> getAllBikeModels();

    Optional<BikeModelEntity> getBikeModelById(Integer id);

    List<BikeModelEntity> getBikeModelsByBrandId(Integer brandId);

    BikeModelEntity save(BikeModelEntity bikeModel);

    void deleteById(Integer id);

    Long countModelsByBrandId(Integer brandId);
}