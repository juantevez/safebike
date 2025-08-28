package com.safe.location.service;

import com.safe.location.domain.model.entity.ProvinciaEntity;
import com.safe.location.domain.port.in.ProvinciaServicePort;
import com.safe.location.domain.port.out.ProvinciaRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProvinciaServiceImpl implements ProvinciaServicePort {
    private static final Logger logger = LoggerFactory.getLogger(ProvinciaServiceImpl.class);

    private final ProvinciaRepositoryPort provinciaRepositoryPort;

    public ProvinciaServiceImpl(ProvinciaRepositoryPort provinciaRepositoryPort) {
        this.provinciaRepositoryPort = provinciaRepositoryPort;
    }

    @Override
    @Cacheable("allProvincias")
    public List<ProvinciaEntity> findAllProvincias() {
        logger.info("Obteniendo todos las localidades");

        try {
            List<ProvinciaEntity> provincias = provinciaRepositoryPort.findAll();
            logger.info("Se encontraron {} provincias: ", provincias.size());
            logger.debug("Provincias obtenidas: {}", provincias);
            return provincias;
        } catch (Exception e) {
            logger.error("Error al obtener todas las provincias", e);
            throw e;
        }
    }
}
