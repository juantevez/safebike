package com.safe.bike.domain.model.dto;

public record BikeModelDto(
        Long id,           // ← debe coincidir con m.idBikeModel
        String modelName,  // ← debe coincidir con m.modelName
        Long brandId,      // ← debe coincidir con m.brand.brandId
        String brandName,  // ← debe coincidir con m.brand.name
        Long bikeTypeId,   // ← debe coincidir con m.bikeType.bikeTypeId
        String bikeTypeName // ← debe coincidir con m.bikeType.name
) {}