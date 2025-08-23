package com.safe.bike.service;


import com.safe.bike.domain.model.dto.BikeModelDto;
import com.safe.bike.domain.model.entity.BikeModelEntity;
import com.safe.bike.domain.port.in.BikeModelServicePort;
import com.safe.bike.domain.port.out.BikeModelRepositoryPort;
import com.safe.bike.infrastructure.web.BikeFormView;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BikeModelServiceImpl implements BikeModelServicePort {

    private static final Logger logger = LoggerFactory.getLogger(BikeModelServiceImpl.class);
    private final BikeModelRepositoryPort bikeModelRepositoryPort;

    public BikeModelServiceImpl(BikeModelRepositoryPort bikeModelRepositoryPort) {
        this.bikeModelRepositoryPort = bikeModelRepositoryPort;
    }

    @Override
    public List<BikeModelDto> getAllBikeModels() {
        return bikeModelRepositoryPort.findAllWithDetails().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BikeModelDto> getModelsByBrand(Long brandId) {
        return bikeModelRepositoryPort.findByBrandId(brandId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BikeModelDto> getModelsByType(Long typeId) {
        return bikeModelRepositoryPort.findByBikeTypeId(typeId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BikeModelDto> getModelsByBrandAndType(Long brandId, Long typeId) {
        return bikeModelRepositoryPort.findByBrandIdAndBikeTypeId(brandId, typeId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BikeModelEntity> findAllWithDetails() {
        logger.info("Obteniendo todos los modelos de bicicleta con detalles");
        try {
            List<BikeModelEntity> models = bikeModelRepositoryPort.findAllWithDetails();
            logger.info("Se obtuvieron {} modelos con detalles", models.size());
            return models;
        } catch (Exception e) {
            logger.error("Error al obtener modelos con detalles", e);
            throw new RuntimeException("Error al obtener modelos de bicicleta con detalles", e);
        }
    }

    private BikeModelDto toDto(BikeModelEntity entity) {
        return new BikeModelDto(
                entity.getIdBikeModel().longValue(),
                entity.getModelName(),
                entity.getBrand().getBrandId(),
                entity.getBrand().getName(),
                entity.getBikeType().getBikeTypeId().longValue(),
                entity.getBikeType().getName()
        );
    }
}
