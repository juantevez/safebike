package com.safe.bike.service;

import com.safe.bike.domain.model.dto.BikeForPhotoDTO;
import com.safe.bike.domain.model.entity.BikeEntity;
import com.safe.bike.domain.port.in.BikeServicePort;
import com.safe.bike.infrastructure.persistence.bike.BikeRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class BikeServiceImpl implements BikeServicePort {
    private static final Logger logger = LoggerFactory.getLogger(BikeServiceImpl.class);
    // Inyecta directamente la interfaz de Spring Data JPA
    private final BikeRepository bikeRepository;

    public BikeServiceImpl(BikeRepository bikeRepository) {
        this.bikeRepository = bikeRepository;
    }

    @Override
    public void save(BikeEntity bike) {
        log.info("Guardando bicicleta con ID: {}", bike.getBikeId());
        try {
            bikeRepository.save(bike);
            log.info("Bicicleta guardada exitosamente: {}", bike.getBikeId());
        } catch (Exception e) {
            log.error("Error al guardar bicicleta con ID: {}", bike.getBikeId(), e);
            throw e;
        }
    }

    @Override
    public Optional<BikeEntity> getBikeById(Long id) {
        return bikeRepository.findById(id);
    }


    @Override
    public List<BikeEntity> getAllBikes() {
        log.info("Obteniendo todas las bicicletas");
        try {
            return bikeRepository.findAll();
        } catch (Exception e) {
            log.error("Error al obtener todas las bicicletas", e);
            throw e;
        }
    }

    public Optional<BikeEntity> getBikesByBrand(Long brand) {
        return bikeRepository.findByBrand_BrandId(brand);
    }

    @Override
    public List<BikeEntity> getBikesByUserId(Long userId) {
        return bikeRepository.findByUser_Id(userId);
    }

    @Transactional(readOnly = true)
    public List<BikeForPhotoDTO> getBikesForPhotoUpload(Long userId) {
        try {
            logger.info("Obteniendo bicicletas para photo upload del usuario: {}", userId);

            // Obtener bicicletas del usuario
            List<BikeEntity> bikes = getBikesByUserId(userId);

            // Convertir a DTO para evitar problemas de lazy loading
            List<BikeForPhotoDTO> bikeDTOs = new ArrayList<>();

            for (BikeEntity bike : bikes) {
                try {
                    // ✅ ACCEDER A PROPIEDADES LAZY DENTRO DE LA TRANSACCIÓN
                    Long bikeId = bike.getBikeId();
                    String brandName = "Marca desconocida";
                    String modelName = "Modelo desconocido";
                    String serialNumber = bike.getSerialNumber() != null ? bike.getSerialNumber() : "Sin número de serie";

                    // Acceso seguro a BrandEntity
                    if (bike.getBrand() != null) {
                        brandName = bike.getBrand().getName(); // Hibernate carga la entidad aquí
                    }

                    // Acceso seguro a Model
                    if (bike.getBikeModel() != null) {
                        modelName = bike.getBikeModel().getModelName(); // Hibernate carga la entidad aquí
                    }

                    BikeForPhotoDTO dto = new BikeForPhotoDTO(bikeId, brandName, modelName, serialNumber);
                    bikeDTOs.add(dto);

                } catch (Exception e) {
                    logger.warn("Error procesando bicicleta ID {}: {}", bike.getBikeId(), e.getMessage());
                    // Continuar con la siguiente bicicleta
                }
            }

            logger.info("Convertidas {} bicicletas a DTO para photo upload", bikeDTOs.size());
            return bikeDTOs;

        } catch (Exception e) {
            logger.error("Error obteniendo bicicletas para photo upload: {}", e.getMessage(), e);
            throw new RuntimeException("Error obteniendo bicicletas para photo upload", e);
        }
    }

}

//// ✅ Correcto
//List<BikeEntity> findByUser_Id(Long id);