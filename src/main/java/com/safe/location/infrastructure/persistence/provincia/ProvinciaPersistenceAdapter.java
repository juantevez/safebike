package com.safe.location.infrastructure.persistence.provincia;

import com.safe.location.domain.model.entity.ProvinciaEntity;
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
        logger.info("Consultando todas las provincias desde la base de datos");

        try {
            // ✅ USA EL MÉTODO CORREGIDO
            List<ProvinciaEntity> provincias = provinciaJpaRepository.findAllByOrderByNombreAsc();
            logger.info("Consulta exitosa: {} provincias encontradas en la base de datos", provincias.size());
            logger.debug("Provincias obtenidas de la BD: {}", provincias);
            return provincias;
        } catch (Exception e) {
            logger.error("Error al consultar todas las provincias desde la base de datos", e);
            throw e;
        }
    }

    @Override
    public Optional<ProvinciaEntity> findById(Integer id) {
        logger.info("Consultando provincia con ID: {} desde la base de datos", id);

        if (id == null) {
            logger.warn("Se intentó consultar una provincia con ID null");
            return Optional.empty();
        }

        try {
            Optional<ProvinciaEntity> provincia = provinciaJpaRepository.findById(id);

            if (provincia.isPresent()) {
                logger.info("Provincia encontrada en la BD con ID: {}", id);
                logger.debug("Provincia obtenida de la BD: {}", provincia.get());
            } else {
                logger.warn("No se encontró provincia en la BD con ID: {}", id);
            }

            return provincia;
        } catch (Exception e) {
            logger.error("Error al consultar provincia por ID: {} desde la base de datos", id, e);
            throw e;
        }
    }
}
