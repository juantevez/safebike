package com.safe.bike.infrastructure.persistence.size;

import com.safe.bike.domain.model.entity.MonedaEntity;
import com.safe.bike.domain.model.entity.SizeEntity;
import com.safe.bike.domain.port.out.SizeRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class SizePersistenceAdapter implements SizeRepositoryPort {

    private static final Logger logger = LoggerFactory.getLogger(SizePersistenceAdapter.class);
    private final SizeJpaRepository sizeJpaRepository;

    public SizePersistenceAdapter(SizeJpaRepository sizeJpaRepository) {
        this.sizeJpaRepository = sizeJpaRepository;
    }


    @Override
    public List<SizeEntity> findAll() {
        logger.info("Consultando todos los tamanos de bicicleta desde la base de datos");

        try {
            List<SizeEntity> tamanos = sizeJpaRepository.findAll();
            logger.info("Consulta exitosa: {} tamanos de bicicleta encontrados en la base de datos", tamanos.size());
            logger.debug("Tamanos de bicicleta obtenidos de la BD: {}", tamanos);
            return tamanos;
        } catch (Exception e) {
            logger.error("Error al consultar todos los tamanos de bicicleta desde la base de datos", e);
            throw e;
        }
    }

    @Override
    public Optional<SizeEntity> findById(Integer id) {
        logger.info("Consultando tamano con ID: {} desde la base de datos", id);

        if (id == null) {
            logger.warn("Se intentó consultar un tamano de bicicleta con ID null");
            return Optional.empty();
        }

        try {
            Optional<SizeEntity> tamano = sizeJpaRepository.findById(id);

            if (tamano.isPresent()) {
                logger.info("Tamano de bicicleta encontrado en la BD con ID: {}", id);
                logger.debug("Tipo de bicicleta obtenido de la BD: {}", tamano.get());
            } else {
                logger.warn("No se encontró tipo de bicicleta en la BD con ID: {}", id);
            }

            return tamano;
        } catch (Exception e) {
            logger.error("Error al consultar tamano de bicicleta por ID: {} desde la base de datos", id, e);
            throw e;
        }
    }

    @Override
    public List<SizeEntity> findByModelId(Long modelId) {
        logger.info("Consultando tamaños disponibles para el modelo ID: {}", modelId);

        if (modelId == null) {
            logger.warn("No se puede buscar tamaños con modelId nulo");
            return List.of();
        }

        try {
            List<SizeEntity> sizes = sizeJpaRepository.findByModelId(modelId);
            logger.info("Se encontraron {} tamaños para el modelo {}", sizes.size(), modelId);
            return sizes;
        } catch (Exception e) {
            logger.error("Error al consultar tamaños por modelo ID: {}", modelId, e);
            throw e;
        }
    }
}
