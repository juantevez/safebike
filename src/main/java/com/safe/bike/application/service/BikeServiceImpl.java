package com.safe.bike.application.service;

import com.safe.bike.domain.model.entity.BikeEntity;
import com.safe.bike.domain.port.in.BikeServicePort;
import com.safe.bike.infrastructure.persistence.BikeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class BikeServiceImpl implements BikeServicePort {

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

    public Optional<BikeEntity> getBikesByBrand(Integer brand) {
        return bikeRepository.findByBrandId(brand);
    }
}