package com.safe.location.infrastructure.persistence.municipio;

import com.safe.location.domain.model.entity.MunicipioEntity;
import com.safe.location.domain.port.out.MunicipioRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class MunicipioPersistenceAdapter implements MunicipioRepositoryPort {

    private static final Logger logger = LoggerFactory.getLogger(MunicipioPersistenceAdapter.class);
    private final MunicipioJpaRepository municipioJpaRepository;

    public MunicipioPersistenceAdapter(MunicipioJpaRepository municipioJpaRepository) {
        this.municipioJpaRepository = municipioJpaRepository;
    }

    @Override
    public List<MunicipioEntity> findAll() {
        logger.info("Consultando todos los municipios desde la base de datos");

        try {
            List<MunicipioEntity> municipios = municipioJpaRepository.findAll();
            logger.info("Consulta exitosa: {} municipios encontrados en la base de datos", municipios.size());
            logger.debug("municipios obtenidos de la BD: {}", municipios);
            return municipios;
        } catch (Exception e) {
            logger.error("Error al consultar todos los municipios desde la base de datos", e);
            throw e;
        }
    }

    @Override
    public Optional<MunicipioEntity> findById(Integer id) {
        logger.info("Consultando municipio con ID: {} desde la base de datos", id);

        if (id == null) {
            logger.warn("Se intentó consultar un municipio con ID null");
            return Optional.empty();
        }

        try {
            Optional<MunicipioEntity> municipio = municipioJpaRepository.findById(id);

            if (municipio.isPresent()) {
                logger.info("Municipios encontrados en la BD con ID: {}", id);
                logger.debug("Municipios obtenidos de la BD: {}", municipio.get());
            } else {
                logger.warn("No se encontró municipios en la BD con ID: {}", id);
            }

            return municipio;
        } catch (Exception e) {
            logger.error("Error al consultar Municipios por ID: {} desde la base de datos", id, e);
            throw e;
        }
    }
}
