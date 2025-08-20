package com.safe.bike.domain.port.in;

import com.safe.bike.domain.model.entity.BikeEntity;
import com.safe.bike.domain.model.entity.BrandEntity;

import java.util.List;
import java.util.Optional;

public interface BikeServicePort {
    void save(BikeEntity bike);
    Optional<BikeEntity> getBikeById(BrandEntity id);
    // Agregar los métodos faltantes
    Optional<BikeEntity> getBikesByBrand(Integer brand);
    List<BikeEntity> getAllBikes();
}