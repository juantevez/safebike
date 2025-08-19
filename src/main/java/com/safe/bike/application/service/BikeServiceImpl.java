package com.safe.bike.application.service;

import com.safe.bike.domain.model.BikeEntity;
import com.safe.bike.domain.port.in.BikeServicePort;
import com.safe.bike.domain.port.out.BikeRepositoryPort;
import com.safe.bike.domain.port.out.BrandRepositoryPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class BikeServiceImpl implements BikeServicePort {

    private final BikeRepositoryPort bikeRepositoryPort;

    private final BrandRepositoryPort brandRepositoryPort;

    public BikeServiceImpl(BikeRepositoryPort bikeRepositoryPort, BrandRepositoryPort brandRepositoryPort) {
        this.bikeRepositoryPort = bikeRepositoryPort;
        this.brandRepositoryPort = brandRepositoryPort;
    }

    @Override
    public void save(BikeEntity bike) {
        log.info("Guardando bicicleta con ID: {}", bike.getBikeId());

        try {
            //bikeRepositoryPort.save(bike);
            bikeRepositoryPort.save(bike);
            log.info("Bicicleta guardada exitosamente: {}", bike.getBikeId());
        } catch (Exception e) {
            log.error("Error al guardar bicicleta con ID: {}", bike.getBikeId(), e);
            throw e;
        }
    }

    @Cacheable(value = "bike", key = "#id")
    public Optional<BikeEntity> getBikeById(Long id) {
        return bikeRepositoryPort.findById(id);
    }
    public Optional<BikeEntity> getBikesByBrand(String brand) {
        return bikeRepositoryPort.findByBrand(brand);
    }

}
