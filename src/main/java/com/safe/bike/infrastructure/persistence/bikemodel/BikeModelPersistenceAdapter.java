package com.safe.bike.infrastructure.persistence.bikemodel;

import com.safe.bike.domain.model.entity.BikeModelEntity;
import com.safe.bike.domain.port.out.BikeModelRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class BikeModelPersistenceAdapter implements BikeModelRepositoryPort {

    private static final Logger logger = LoggerFactory.getLogger(BikeModelPersistenceAdapter.class);

    private final BikeModelJpaRepository bikeModelJpaRepository;

    public BikeModelPersistenceAdapter(BikeModelJpaRepository bikeModelJpaRepository) {
        this.bikeModelJpaRepository = bikeModelJpaRepository;
    }

    @Override
    public List<BikeModelEntity> findAll() {
        logger.info("Obteniendo todos los modelos de bicicleta desde la base de datos");

        try {
            List<BikeModelEntity> models = bikeModelJpaRepository.findAllByOrderByModelNameAsc();
            logger.info("Se encontraron {} modelos de bicicleta en la base de datos", models.size());
            return models;
        } catch (Exception e) {
            logger.error("Error al obtener todos los modelos de bicicleta desde la base de datos", e);
            throw e;
        }
    }

    @Override
    public Optional<BikeModelEntity> findById(Integer id) {
        logger.info("Buscando modelo de bicicleta por ID en la base de datos: {}", id);

        if (id == null) {
            logger.warn("Se intentó buscar un modelo de bicicleta con ID null");
            return Optional.empty();
        }

        try {
            Optional<BikeModelEntity> model = bikeModelJpaRepository.findById(id);

            if (model.isPresent()) {
                logger.info("Modelo de bicicleta encontrado en la base de datos con ID: {}", id);
                logger.debug("Detalles del modelo: {}", model.get());
            } else {
                logger.warn("No se encontró modelo de bicicleta en la base de datos con ID: {}", id);
            }

            return model;
        } catch (Exception e) {
            logger.error("Error al buscar modelo de bicicleta por ID en la base de datos: {}", id, e);
            throw e;
        }
    }

    @Override
    public List<BikeModelEntity> findByBrandId(Integer brandId) {
        logger.info("Buscando modelos de bicicleta por brand ID en la base de datos: {}", brandId);

        if (brandId == null) {
            logger.warn("Se intentó buscar modelos con brand ID null");
            return List.of();
        }

        try {
            List<BikeModelEntity> models = bikeModelJpaRepository.findByBrand_BrandIdOrderByModelNameAsc(brandId);
            logger.info("Se encontraron {} modelos para la marca ID: {}", models.size(), brandId);
            return models;
        } catch (Exception e) {
            logger.error("Error al buscar modelos por brand ID: {}", brandId, e);
            throw e;
        }
    }

    @Override
    public BikeModelEntity save(BikeModelEntity bikeModel) {
        logger.info("Guardando modelo de bicicleta en la base de datos");

        if (bikeModel == null) {
            logger.warn("Se intentó guardar un modelo de bicicleta null");
            throw new IllegalArgumentException("BikeModel no puede ser null");
        }

        try {
            logger.debug("Datos del modelo a guardar: {}", bikeModel);
            BikeModelEntity savedModel = bikeModelJpaRepository.save(bikeModel);
            logger.info("Modelo de bicicleta guardado exitosamente con ID: {}", savedModel.getIdBikeModel());
            logger.debug("Modelo guardado: {}", savedModel);
            return savedModel;
        } catch (Exception e) {
            logger.error("Error al guardar modelo de bicicleta: {}", bikeModel, e);
            throw e;
        }
    }

    @Override
    public void deleteById(Integer id) {
        logger.info("Eliminando modelo de bicicleta con ID: {}", id);

        if (id == null) {
            logger.warn("Se intentó eliminar un modelo con ID null");
            throw new IllegalArgumentException("ID no puede ser null");
        }

        try {
            if (bikeModelJpaRepository.existsById(id)) {
                bikeModelJpaRepository.deleteById(id);
                logger.info("Modelo de bicicleta eliminado exitosamente con ID: {}", id);
            } else {
                logger.warn("No se puede eliminar: Modelo con ID {} no existe", id);
                throw new IllegalArgumentException("Modelo con ID " + id + " no existe");
            }
        } catch (Exception e) {
            logger.error("Error al eliminar modelo de bicicleta con ID: {}", id, e);
            throw e;
        }
    }

    @Override
    public Long countByBrandId(Integer brandId) {
        logger.info("Contando modelos para brand ID: {}", brandId);

        try {
            Long count = bikeModelJpaRepository.countByBrandId(brandId);
            logger.debug("Brand ID {} tiene {} modelos", brandId, count);
            return count;
        } catch (Exception e) {
            logger.error("Error al contar modelos por brand ID: {}", brandId, e);
            throw e;
        }
    }
}