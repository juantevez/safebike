package com.safe.bike.domain.port.in;

import com.safe.bike.domain.model.BikeEntity;

import java.util.Optional;

public interface BikeServicePort {
    void save(BikeEntity bike);

    Optional<BikeEntity> getBikeById(Long id);

    Optional<BikeEntity> getBikesByBrand(String brand);

}
