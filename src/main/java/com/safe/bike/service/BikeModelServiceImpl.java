package com.safe.bike.service;


import com.safe.bike.domain.model.entity.BikeModelEntity;
import com.safe.bike.domain.port.in.BikeModelServicePort;
import com.safe.bike.domain.port.out.BikeModelRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BikeModelServiceImpl implements BikeModelServicePort {

    private static final Logger logger = LoggerFactory.getLogger(BikeModelServiceImpl.class);

    private final BikeModelRepositoryPort bikeModelRepositoryPort;

    public BikeModelServiceImpl(BikeModelRepositoryPort bikeModelRepositoryPort) {
        this.bikeModelRepositoryPort = bikeModelRepositoryPort;
    }

    @Override
    public List<BikeModelEntity> getAllBikeModels() {
        logger.info("Obteniendo todos los modelos de bicicleta");

        try {
            List<BikeModelEntity> models = bikeModelRepositoryPort.findAll();
            logger.info("Se encontraron {} modelos de bicicleta", models.size());
            return models;
        } catch (Exception e) {
            logger.error("Error al obtener todos los modelos de bicicleta", e);
            throw e;
        }
    }

    @Override
    public Optional<BikeModelEntity> getBikeModelById(Integer id) {
        logger.info("Buscando modelo de bicicleta con ID: {}", id);

        if (id == null) {
            logger.warn("Se intentó buscar un modelo con ID null");
            return Optional.empty();
        }

        try {
            Optional<BikeModelEntity> model = bikeModelRepositoryPort.findById(id);

            if (model.isPresent()) {
                logger.info("Modelo de bicicleta encontrado con ID: {}", id);
                logger.debug("Detalles del modelo: {}", model.get());
            } else {
                logger.warn("No se encontró modelo de bicicleta con ID: {}", id);
            }

            return model;
        } catch (Exception e) {
            logger.error("Error al buscar modelo de bicicleta con ID: {}", id, e);
            throw e;
        }
    }

    @Override
    public List<BikeModelEntity> getBikeModelsByBrandId(Integer brandId) {
        logger.info("Obteniendo modelos de bicicleta para brand ID: {}", brandId);

        if (brandId == null) {
            logger.warn("Se intentó buscar modelos con brand ID null");
            return List.of();
        }

        try {
            List<BikeModelEntity> models = bikeModelRepositoryPort.findByBrandId(brandId);
            logger.info("Se encontraron {} modelos para brand ID: {}", models.size(), brandId);
            return models;
        } catch (Exception e) {
            logger.error("Error al obtener modelos por brand ID: {}", brandId, e);
            throw e;
        }
    }

    @Override
    public BikeModelEntity save(BikeModelEntity bikeModel) {
        logger.info("Guardando modelo de bicicleta");

        if (bikeModel == null) {
            logger.warn("Se intentó guardar un modelo null");
            throw new IllegalArgumentException("BikeModel no puede ser null");
        }

        try {
            logger.debug("Datos del modelo a guardar: {}", bikeModel);
            BikeModelEntity savedModel = bikeModelRepositoryPort.save(bikeModel);
            logger.info("Modelo guardado exitosamente con ID: {}", savedModel.getIdBikeModel());
            return savedModel;
        } catch (Exception e) {
            logger.error("Error al guardar modelo: {}", bikeModel, e);
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
            Optional<BikeModelEntity> existingModel = bikeModelRepositoryPort.findById(id);
            if (existingModel.isPresent()) {
                bikeModelRepositoryPort.deleteById(id);
                logger.info("Modelo eliminado exitosamente con ID: {}", id);
            } else {
                logger.warn("No se puede eliminar: Modelo con ID {} no existe", id);
                throw new IllegalArgumentException("Modelo con ID " + id + " no existe");
            }
        } catch (Exception e) {
            logger.error("Error al eliminar modelo con ID: {}", id, e);
            throw e;
        }
    }

    @Override
    public Long countModelsByBrandId(Integer brandId) {
        logger.info("Contando modelos para brand ID: {}", brandId);

        if (brandId == null) {
            logger.warn("Se intentó contar modelos con brand ID null");
            return 0L;
        }

        try {
            Long count = bikeModelRepositoryPort.countByBrandId(brandId);
            logger.debug("Brand ID {} tiene {} modelos", brandId, count);
            return count;
        } catch (Exception e) {
            logger.error("Error al contar modelos por brand ID: {}", brandId, e);
            return 0L;
        }
    }
}