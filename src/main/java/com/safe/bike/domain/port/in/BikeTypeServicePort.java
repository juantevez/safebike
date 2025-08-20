package com.safe.bike.domain.port.in;

import com.safe.bike.domain.model.entity.BikeTypeEntity;

import java.util.List;
import java.util.Optional;

public interface BikeTypeServicePort {

    List<BikeTypeEntity> getAllBikeTypes();
    Optional<BikeTypeEntity> getBikeTypeById(Integer id);
}