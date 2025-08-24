package com.safe.bike.service;

import com.safe.bike.domain.model.entity.BikeTypeEntity;
import com.safe.bike.domain.port.in.BikeTypeServicePort;
import com.safe.bike.domain.port.out.BikeTypeRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BikeTypeServiceImpl implements BikeTypeServicePort {
    private static final Logger logger = LoggerFactory.getLogger(BikeTypeServiceImpl.class);

    private final BikeTypeRepositoryPort bikeTypeRepositoryPort;

    public BikeTypeServiceImpl(BikeTypeRepositoryPort bikeTypeRepositoryPort) {
        this.bikeTypeRepositoryPort = bikeTypeRepositoryPort;
    }

    @Override
    @Cacheable("allBikesTypes")
    public List<BikeTypeEntity> getAllBikeTypes() {
        logger.info("Obteniendo todos los tipos de bicicletas");

        try {
            List<BikeTypeEntity> bikeTypes = bikeTypeRepositoryPort.findAll();
            logger.info("Se encontraron {} tipos de bicicletas", bikeTypes.size());
            logger.debug("Tipos de bicicletas obtenidos: {}", bikeTypes);
            return bikeTypes;
        } catch (Exception e) {
            logger.error("Error al obtener todos los tipos de bicicletas", e);
            throw e;
        }
    }

    @Override
    @Cacheable(value = "bikeTypeById",  key = "#id")
    public Optional<BikeTypeEntity> getBikeTypeById(Integer id) {
        logger.info("Buscando tipo de bicicleta con ID: {}", id);

        if (id == null) {
            logger.warn("Se intentó buscar un tipo de bicicleta con ID null");
            return Optional.empty();
        }

        try {
            Optional<BikeTypeEntity> bikeType = bikeTypeRepositoryPort.findById(id);

            if (bikeType.isPresent()) {
                logger.info("Tipo de bicicleta encontrado con ID: {}", id);
                logger.debug("Detalles del tipo de bicicleta: {}", bikeType.get());
            } else {
                logger.warn("No se encontró tipo de bicicleta con ID: {}", id);
            }

            return bikeType;
        } catch (Exception e) {
            logger.error("Error al buscar tipo de bicicleta con ID: {}", id, e);
            throw e;
        }
    }
}