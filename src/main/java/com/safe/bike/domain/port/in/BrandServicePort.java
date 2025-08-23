package com.safe.bike.domain.port.in;

import com.safe.bike.domain.model.entity.BrandEntity;

import java.util.List;
import java.util.Optional;

public interface BrandServicePort {
    List<BrandEntity> getAllBrands();
    Optional<BrandEntity> getBrandById(Long id);
}