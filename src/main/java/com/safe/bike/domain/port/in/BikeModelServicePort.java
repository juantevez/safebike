package com.safe.bike.domain.port.in;


import com.safe.bike.domain.model.dto.BikeModelDto;
import com.safe.bike.domain.model.entity.BikeModelEntity;
import java.util.List;

// com.safe.bike.domain.port.in.BikeModelServicePort

public interface BikeModelServicePort {

    List<BikeModelDto> getAllBikeModels();

    List<BikeModelDto> getModelsByBrand(Long brandId);

    List<BikeModelDto> getModelsByType(Long typeId);

    List<BikeModelDto> getModelsByBrandAndType(Long brandId, Long typeId);

    // Agregar este método
    List<BikeModelEntity> findAllWithDetails();
}

