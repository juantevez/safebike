package com.safe.location.infrastructure.persistence.provincia;

import com.safe.location.domain.model.entity.MunicipioEntity;
import com.safe.location.domain.model.entity.ProvinciaEntity;
import com.safe.location.domain.port.out.MunicipioRepositoryPort;
import com.safe.location.domain.port.out.ProvinciaRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ProvinciaPersistenceAdapter implements ProvinciaRepositoryPort {

    private static final Logger logger = LoggerFactory.getLogger(ProvinciaPersistenceAdapter.class);
    private final ProvinciaJpaRepository provinciaJpaRepository;

    public ProvinciaPersistenceAdapter(ProvinciaJpaRepository provinciaJpaRepository) {
        this.provinciaJpaRepository = provinciaJpaRepository;
    }

    @Override
    public List<ProvinciaEntity> findAll() {
        logger.info("Consultando todos los municipios desde la base de datos");

        try {
            List<ProvinciaEntity> provincias = provinciaJpaRepository.findAll();
            logger.info("Consulta exitosa: {} provincias encontrados en la base de datos", provincias.size());
            logger.debug("provincias obtenidos de la BD: {}", provincias);
            return provincias;
        } catch (Exception e) {
            logger.error("Error al consultar todos las provincias desde la base de datos", e);
            throw e;
        }
    }

    @Override
    public Optional<ProvinciaEntity> findById(Integer id) {
        logger.info("Consultando municipio con ID: {} desde la base de datos", id);

        if (id == null) {
            logger.warn("Se intentó consultar un municipio con ID null");
            return Optional.empty();
        }

        try {
            Optional<ProvinciaEntity> provincias = provinciaJpaRepository.findById(id);

            if (provincias.isPresent()) {
                logger.info("Provincias encontrados en la BD con ID: {}", id);
                logger.debug("Provincias obtenidos de la BD: {}", provincias.get());
            } else {
                logger.warn("No se encontró provincias en la BD con ID: {}", id);
            }

            return provincias;
        } catch (Exception e) {
            logger.error("Error al consultar provincias por ID: {} desde la base de datos", id, e);
            throw e;
        }
    }
}
