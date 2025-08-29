package com.safe.bike.domain.port.in;

import com.safe.bike.domain.model.entity.BikeEntity;
import com.safe.bike.domain.model.entity.BrandEntity;

import java.util.List;
import java.util.Optional;

public interface BikeServicePort {
    void save(BikeEntity bike);
    Optional<BikeEntity> getBikeById(Long id);
    List<BikeEntity> getAllBikes();
    Optional<BikeEntity> getBikesByBrand(Long brand); // Agregar este método
    // Agregar este método
    List<BikeEntity> getBikesByUserId(Long userId);
}