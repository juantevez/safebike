package com.safe.location.infrastructure.persistence.localidad;

import com.safe.bike.domain.model.entity.MonedaEntity;
import com.safe.bike.domain.port.out.MonedaRepositoryPort;
import com.safe.location.domain.model.entity.LocalidadEntity;
import com.safe.location.domain.port.out.LocalidadRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class LocalidadPersistenceAdapter implements LocalidadRepositoryPort {

    private static final Logger logger = LoggerFactory.getLogger(LocalidadPersistenceAdapter.class);
    private final LocalidadJpaRepository localidadJpaRepository;

    public LocalidadPersistenceAdapter(LocalidadJpaRepository localidadJpaRepository) {
        this.localidadJpaRepository = localidadJpaRepository;
    }

    @Override
    public List<LocalidadEntity> findAll() {
        logger.info("Consultando todos las localidades desde la base de datos");

        try {
            List<LocalidadEntity> monedas = localidadJpaRepository.findAll();
            logger.info("Consulta exitosa: {} localidades encontradas en la base de datos", monedas.size());
            logger.debug("localidades obtenidas de la BD: {}", monedas);
            return monedas;
        } catch (Exception e) {
            logger.error("Error al consultar todos los tipos de bicicleta desde la base de datos", e);
            throw e;
        }
    }

    @Override
    public Optional<LocalidadEntity> findById(Integer id) {
        logger.info("Consultando localidad con ID: {} desde la base de datos", id);

        if (id == null) {
            logger.warn("Se intentó consultar un tipo de bicicleta con ID null");
            return Optional.empty();
        }

        try {
            Optional<LocalidadEntity> moneda = localidadJpaRepository.findById(id);

            if (moneda.isPresent()) {
                logger.info("Localidades encontradas en la BD con ID: {}", id);
                logger.debug("Localidades obtenidas de la BD: {}", moneda.get());
            } else {
                logger.warn("No se encontró localidades en la BD con ID: {}", id);
            }

            return moneda;
        } catch (Exception e) {
            logger.error("Error al consultar localidades por ID: {} desde la base de datos", id, e);
            throw e;
        }
    }
}
