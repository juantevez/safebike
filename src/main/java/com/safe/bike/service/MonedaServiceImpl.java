package com.safe.bike.service;

import com.safe.bike.domain.model.entity.BikeTypeEntity;
import com.safe.bike.domain.model.entity.MonedaEntity;
import com.safe.bike.domain.port.in.MonedaServicePort;
import com.safe.bike.domain.port.out.MonedaRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MonedaServiceImpl implements MonedaServicePort {
    private static final Logger logger = LoggerFactory.getLogger(MonedaServiceImpl.class);

    private final MonedaRepositoryPort monedaRepositoryPort;

    public MonedaServiceImpl(MonedaRepositoryPort monedaRepositoryPort) {
        this.monedaRepositoryPort = monedaRepositoryPort;
    }

    @Override
    @Cacheable("allMonedas")
    public List<MonedaEntity> findAllMonedas() {
        logger.info("Obteniendo todos las monedas");

        try {
            List<MonedaEntity> monedas = monedaRepositoryPort.findAll();
            logger.info("Se encontraron {} monedas: ", monedas.size());
            logger.debug("Tipos de monedas obtenidas: {}", monedas);
            return monedas;
        } catch (Exception e) {
            logger.error("Error al obtener todos los tipos de bicicletas", e);
            throw e;
        }
    }
}
