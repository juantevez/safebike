package com.safe.bike.service;

import com.safe.bike.domain.model.entity.FrameTypeEntity;
import com.safe.bike.domain.port.in.FrameTypeServicePort;
import com.safe.bike.domain.port.out.FrameTypeRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class FrameTypeServiceImpl implements FrameTypeServicePort {
    private static final Logger logger = LoggerFactory.getLogger(FrameTypeServiceImpl.class);

    private final FrameTypeRepositoryPort frameTypeRepositoryPort;

    public FrameTypeServiceImpl(FrameTypeRepositoryPort frameTypeRepositoryPort) {
        this.frameTypeRepositoryPort = frameTypeRepositoryPort;
    }

    @Override
    @Cacheable("allFrameTypes")
    public List<FrameTypeEntity> getAllFrameTypes() {
        logger.info("Obteniendo todos los tipos de marco");

        try {
            List<FrameTypeEntity> frameTypes = frameTypeRepositoryPort.findAll();
            logger.info("Se encontraron {} tipos de marco", frameTypes.size());
            logger.debug("Tipos de marco obtenidos: {}", frameTypes);
            return frameTypes;
        } catch (Exception e) {
            logger.error("Error al obtener todos los tipos de marco", e);
            throw e;
        }
    }

    @Override
    @Cacheable(value = "frameTypeById",  key = "#id")
    public Optional<FrameTypeEntity> getFrameTypeById(Integer id) {
        logger.info("Buscando tipo de marco con ID: {}", id);

        if (id == null) {
            logger.warn("Se intentó buscar un tipo de marco con ID null");
            return Optional.empty();
        }

        try {
            Optional<FrameTypeEntity> frameType = frameTypeRepositoryPort.findById(id);

            if (frameType.isPresent()) {
                logger.info("Tipo de marco encontrado con ID: {}", id);
                logger.debug("Detalles del tipo de marco: {}", frameType.get());
            } else {
                logger.warn("No se encontró tipo de marco con ID: {}", id);
            }

            return frameType;
        } catch (Exception e) {
            logger.error("Error al buscar tipo de marco con ID: {}", id, e);
            throw e;
        }
    }
}