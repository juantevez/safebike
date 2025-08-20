package com.safe.bike.infrastructure.persistence.biketype;

import com.safe.bike.domain.model.entity.BikeTypeEntity;
import com.safe.bike.domain.port.out.BikeTypeRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class BikeTypePersistenceAdapter implements BikeTypeRepositoryPort {
    private static final Logger logger = LoggerFactory.getLogger(BikeTypePersistenceAdapter.class);

    // Inyecta tu repositorio de Spring Data JPA
    private final BikeTypeJpaRepository bikeTypeJpaRepository;

    public BikeTypePersistenceAdapter(BikeTypeJpaRepository bikeTypeJpaRepository) {
        this.bikeTypeJpaRepository = bikeTypeJpaRepository;
    }

    @Override
    public BikeTypeEntity save(BikeTypeEntity bikeType) {
        logger.info("Guardando tipo de bicicleta en la base de datos");

        if (bikeType == null) {
            logger.warn("Se intentó guardar un tipo de bicicleta null");
            throw new IllegalArgumentException("BikeType no puede ser null");
        }

        try {
            logger.debug("Datos del tipo de bicicleta a guardar: {}", bikeType);
            BikeTypeEntity savedBikeType = bikeTypeJpaRepository.save(bikeType);
            logger.info("Tipo de bicicleta guardado exitosamente con ID: {}", savedBikeType.getBikeTypeId());
            logger.debug("Tipo de bicicleta guardado: {}", savedBikeType);
            return savedBikeType;
        } catch (Exception e) {
            logger.error("Error al guardar el tipo de bicicleta: {}", bikeType, e);
            throw e;
        }
    }

    @Override
    public List<BikeTypeEntity> findAll() {
        logger.info("Consultando todos los tipos de bicicleta desde la base de datos");

        try {
            List<BikeTypeEntity> bikeTypes = bikeTypeJpaRepository.findAll();
            logger.info("Consulta exitosa: {} tipos de bicicleta encontrados en la base de datos", bikeTypes.size());
            logger.debug("Tipos de bicicleta obtenidos de la BD: {}", bikeTypes);
            return bikeTypes;
        } catch (Exception e) {
            logger.error("Error al consultar todos los tipos de bicicleta desde la base de datos", e);
            throw e;
        }
    }

    @Override
    public Optional<BikeTypeEntity> findById(Integer id) {
        logger.info("Consultando tipo de bicicleta por ID: {} desde la base de datos", id);

        if (id == null) {
            logger.warn("Se intentó consultar un tipo de bicicleta con ID null");
            return Optional.empty();
        }

        try {
            Optional<BikeTypeEntity> bikeType = bikeTypeJpaRepository.findById(id);

            if (bikeType.isPresent()) {
                logger.info("Tipo de bicicleta encontrado en la BD con ID: {}", id);
                logger.debug("Tipo de bicicleta obtenido de la BD: {}", bikeType.get());
            } else {
                logger.warn("No se encontró tipo de bicicleta en la BD con ID: {}", id);
            }

            return bikeType;
        } catch (Exception e) {
            logger.error("Error al consultar tipo de bicicleta por ID: {} desde la base de datos", id, e);
            throw e;
        }
    }
}