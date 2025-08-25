package com.safe.bike.service;

import com.safe.bike.domain.model.entity.SizeEntity;
import com.safe.bike.domain.port.in.SizeServicePort;
import com.safe.bike.domain.port.out.SizeRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SizeServiceImpl implements SizeServicePort {

    private static final Logger logger = LoggerFactory.getLogger(MonedaServiceImpl.class);

    private final SizeRepositoryPort sizeRepositoryPort;


    public SizeServiceImpl(SizeRepositoryPort sizeRepositoryPort) {
        this.sizeRepositoryPort = sizeRepositoryPort;
    }

    @Override
    @Cacheable("allSizes")
    public List<SizeEntity> findAllSizes() {
        logger.info("Obteniendo todos las monedas");

        try {
            List<SizeEntity> tamanos = sizeRepositoryPort.findAll();
            logger.info("Se encontraron {} monedas: ", tamanos.size());
            logger.debug("Tipos de monedas obtenidas: {}", tamanos);
            return tamanos;
        } catch (Exception e) {
            logger.error("Error al obtener todos los tipos de bicicletas", e);
            throw e;
        }
    }
}
