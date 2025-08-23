package com.safe.bike.infrastructure.persistence.moneda;

import com.safe.bike.domain.model.entity.MonedaEntity;
import com.safe.bike.domain.port.out.MonedaRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class MonedaPersistenceAdapter implements MonedaRepositoryPort {

    private static final Logger logger = LoggerFactory.getLogger(MonedaPersistenceAdapter.class);
    private final MonedaJpaRepository monedaJpaRepository;

    public MonedaPersistenceAdapter(MonedaJpaRepository monedaJpaRepository) {
        this.monedaJpaRepository = monedaJpaRepository;
    }

    @Override
    public List<MonedaEntity> findAll() {
        logger.info("Consultando todos los tipos de bicicleta desde la base de datos");

        try {
            List<MonedaEntity> monedas = monedaJpaRepository.findAll();
            logger.info("Consulta exitosa: {} tipos de bicicleta encontrados en la base de datos", monedas.size());
            logger.debug("Tipos de bicicleta obtenidos de la BD: {}", monedas);
            return monedas;
        } catch (Exception e) {
            logger.error("Error al consultar todos los tipos de bicicleta desde la base de datos", e);
            throw e;
        }
    }

    @Override
    public Optional<MonedaEntity> findById(Integer id) {
        logger.info("Consultando moneda con ID: {} desde la base de datos", id);

        if (id == null) {
            logger.warn("Se intentó consultar un tipo de bicicleta con ID null");
            return Optional.empty();
        }

        try {
            Optional<MonedaEntity> moneda = monedaJpaRepository.findById(id);

            if (moneda.isPresent()) {
                logger.info("Tipo de bicicleta encontrado en la BD con ID: {}", id);
                logger.debug("Tipo de bicicleta obtenido de la BD: {}", moneda.get());
            } else {
                logger.warn("No se encontró tipo de bicicleta en la BD con ID: {}", id);
            }

            return moneda;
        } catch (Exception e) {
            logger.error("Error al consultar tipo de bicicleta por ID: {} desde la base de datos", id, e);
            throw e;
        }
    }
}
