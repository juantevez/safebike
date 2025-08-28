package com.safe.location.service;

import com.safe.location.domain.model.entity.LocalidadEntity;
import com.safe.location.domain.port.in.LocalidadServicePort;
import com.safe.location.domain.port.out.LocalidadRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class LocalidadServiceImpl implements LocalidadServicePort {
    private static final Logger logger = LoggerFactory.getLogger(LocalidadServiceImpl.class);

    private final LocalidadRepositoryPort localidadRepositoryPort;

    public LocalidadServiceImpl(LocalidadRepositoryPort localidadRepositoryPort) {
        this.localidadRepositoryPort = localidadRepositoryPort;
    }

    @Override
    @Cacheable("allLocalidades")
    public List<LocalidadEntity> findAllLocalidades() {
        logger.info("Obteniendo todos las localidades");

        try {
            List<LocalidadEntity> localidades = localidadRepositoryPort.findAll();
            logger.info("Se encontraron {} localidades: ", localidades.size());
            logger.debug("Localidades obtenidas: {}", localidades);
            return localidades;
        } catch (Exception e) {
            logger.error("Error al obtener todas las localidades", e);
            throw e;
        }
    }
}
