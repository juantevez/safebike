package com.safe.bike.infrastructure.persistence.frametype;

import com.safe.bike.domain.model.entity.FrameTypeEntity;
import com.safe.bike.domain.port.out.FrameTypeRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class FrameTypePersistenceAdapter implements FrameTypeRepositoryPort {
    private static final Logger logger = LoggerFactory.getLogger(FrameTypePersistenceAdapter.class);

    private final FrameTypeJpaRepository frameTypeJpaRepository;

    public FrameTypePersistenceAdapter(FrameTypeJpaRepository frameTypeJpaRepository) {
        this.frameTypeJpaRepository = frameTypeJpaRepository;
    }

    @Override
    public FrameTypeEntity save(FrameTypeEntity frameType) {
        logger.info("Guardando tipo de marco en la base de datos");

        if (frameType == null) {
            logger.warn("Se intentó guardar un tipo de marco null");
            throw new IllegalArgumentException("FrameType no puede ser null");
        }

        try {
            logger.debug("Datos del tipo de marco a guardar: {}", frameType);
            FrameTypeEntity savedFrameType = frameTypeJpaRepository.save(frameType);
            logger.info("Tipo de marco guardado exitosamente con ID: {}", savedFrameType.getFrameTypeId());
            logger.debug("Tipo de marco guardado: {}", savedFrameType);
            return savedFrameType;
        } catch (Exception e) {
            logger.error("Error al guardar el tipo de marco: {}", frameType, e);
            throw e;
        }
    }

    @Override
    public List<FrameTypeEntity> findAll() {
        logger.info("Consultando todos los tipos de marco desde la base de datos");

        try {
            List<FrameTypeEntity> frameTypes = frameTypeJpaRepository.findAll();
            logger.info("Consulta exitosa: {} tipos de marco encontrados en la base de datos", frameTypes.size());
            logger.debug("Tipos de marco obtenidos de la BD: {}", frameTypes);
            return frameTypes;
        } catch (Exception e) {
            logger.error("Error al consultar todos los tipos de marco desde la base de datos", e);
            throw e;
        }
    }

    @Override
    public Optional<FrameTypeEntity> findById(Integer id) {
        logger.info("Consultando tipo de marco por ID: {} desde la base de datos", id);

        if (id == null) {
            logger.warn("Se intentó consultar un tipo de marco con ID null");
            return Optional.empty();
        }

        try {
            Optional<FrameTypeEntity> frameType = frameTypeJpaRepository.findById(id);

            if (frameType.isPresent()) {
                logger.info("Tipo de marco encontrado en la BD con ID: {}", id);
                logger.debug("Tipo de marco obtenido de la BD: {}", frameType.get());
            } else {
                logger.warn("No se encontró tipo de marco en la BD con ID: {}", id);
            }

            return frameType;
        } catch (Exception e) {
            logger.error("Error al consultar tipo de marco por ID: {} desde la base de datos", id, e);
            throw e;
        }
    }
}