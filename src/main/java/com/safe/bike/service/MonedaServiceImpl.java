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

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

            // Ordenar alfabéticamente (asumiendo que tienes un campo 'nombre' o 'codigo')
            List<MonedaEntity> monedasOrdenadas = monedas.stream()
                    .sorted(Comparator.comparing(MonedaEntity::getCodigoMoneda))
                    .collect(Collectors.toList());

            logger.info("Se encontraron {} monedas ordenadas alfabéticamente", monedasOrdenadas.size());
            logger.debug("Tipos de monedas obtenidas: {}", monedasOrdenadas);
            return monedasOrdenadas;
        } catch (Exception e) {
            logger.error("Error al obtener todos los tipos de monedas", e);
            throw e;
        }
    }
}
