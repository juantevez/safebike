package com.safe.bike.infrastructure.persistence.bikemodel;

import com.safe.bike.domain.model.dto.BikeModelDto;
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


    private final BikeModelJpaRepository jpaRepository;

    public BikeModelPersistenceAdapter(BikeModelJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<BikeModelEntity> findAll() {
        logger.info("Obteniendo todos los modelos de bicicleta desde la base de datos");
        try {
            List<BikeModelEntity> models = jpaRepository.findAllByOrderByModelNameAsc();
            logger.info("Se encontraron {} modelos", models.size());
            return models;
        } catch (Exception e) {
            logger.error("Error al obtener todos los modelos", e);
            throw e;
        }
    }

    @Override
    public Optional<BikeModelEntity> findById(Long id) {
        logger.info("Buscando modelo por ID: {}", id);
        if (id == null) {
            logger.warn("ID nulo proporcionado");
            return Optional.empty();
        }
        try {
            Optional<BikeModelEntity> result = jpaRepository.findById(id);
            if (result.isPresent()) {
                logger.debug("Modelo encontrado: {}", result.get());
            } else {
                logger.warn("No se encontró modelo con ID: {}", id);
            }
            return result;
        } catch (Exception e) {
            logger.error("Error al buscar modelo por ID: {}", id, e);
            throw e;
        }
    }

    @Override
    public boolean existsById(Long id) {
        logger.info("Verificando existencia de modelo con ID: {}", id);
        if (id == null) {
            logger.warn("ID nulo proporcionado");
            return false;
        }
        try {
            boolean exists = jpaRepository.existsById(id);
            logger.debug("Existencia del modelo con ID {}: {}", id, exists);
            return exists;
        } catch (Exception e) {
            logger.error("Error al verificar existencia del modelo con ID: {}", id, e);
            throw e;
        }
    }

    @Override
    public List<BikeModelEntity> findByBrandId(Long brandId) {
        logger.info("Buscando modelos por brandId: {}", brandId);
        if (brandId == null) {
            logger.warn("brandId nulo proporcionado");
            return List.of();
        }
        try {
            List<BikeModelEntity> models = jpaRepository.findWithFilters(brandId, null, null);
            logger.info("Se encontraron {} modelos para la marca {}", models.size(), brandId);
            return models;
        } catch (Exception e) {
            logger.error("Error al buscar modelos por brandId: {}", brandId, e);
            throw e;
        }
    }

    @Override
    public List<BikeModelEntity> findByBikeTypeId(Long bikeTypeId) {
        logger.info("Buscando modelos por bikeTypeId: {}", bikeTypeId);
        if (bikeTypeId == null) {
            logger.warn("bikeTypeId nulo proporcionado");
            return List.of();
        }
        try {
            List<BikeModelEntity> models = jpaRepository.findWithFilters(null, bikeTypeId, null);
            logger.info("Se encontraron {} modelos para el tipo {}", models.size(), bikeTypeId);
            return models;
        } catch (Exception e) {
            logger.error("Error al buscar modelos por bikeTypeId: {}", bikeTypeId, e);
            throw e;
        }
    }

    @Override
    public List<BikeModelEntity> findByBrandIdAndBikeTypeId(Long brandId, Long bikeTypeId) {
        logger.info("Buscando modelos por brandId: {} y bikeTypeId: {}", brandId, bikeTypeId);
        if (brandId == null || bikeTypeId == null) {
            logger.warn("brandId o bikeTypeId nulo");
            return List.of();
        }
        try {
            // USA findWithFilters en lugar de findByBrandIdAndBikeTypeIdOptional
            return jpaRepository.findWithFilters(brandId, bikeTypeId, null);
        } catch (Exception e) {
            logger.error("Error al buscar modelos por brandId y bikeTypeId: {}, {}", brandId, bikeTypeId, e);
            throw e;
        }
    }

    @Override
    public Optional<BikeModelEntity> findByName(String name) {
        logger.info("Buscando modelo por nombre exacto: {}", name);
        if (name == null || name.isBlank()) {
            logger.warn("Nombre nulo o vacío");
            return Optional.empty();
        }
        try {
            List<BikeModelEntity> models = jpaRepository.findWithFilters(null, null, name);
            return models.stream()
                    .filter(m -> m.getModelName().equalsIgnoreCase(name))
                    .findFirst();
        } catch (Exception e) {
            logger.error("Error al buscar modelo por nombre: {}", name, e);
            throw e;
        }
    }

    @Override
    public List<BikeModelEntity> findByNameContainingIgnoreCase(String name) {
        logger.info("Buscando modelos que contengan: {}", name);
        if (name == null || name.isBlank()) {
            return List.of();
        }
        try {
            String pattern = "%" + name + "%";
            return jpaRepository.findWithFilters(null, null, pattern);
        } catch (Exception e) {
            logger.error("Error al buscar modelos por nombre", e);
            throw e;
        }
    }

    @Override
    public long countByBrandId(Long brandId) {
        logger.info("Contando modelos para marca: {}", brandId);
        if (brandId == null) {
            return 0L;
        }
        try {
            Long count = jpaRepository.countByBrandId(brandId);
            logger.debug("Marca {} tiene {} modelos", brandId, count);
            return count != null ? count : 0L;
        } catch (Exception e) {
            logger.error("Error al contar modelos por marca: {}", brandId, e);
            throw e;
        }
    }

    @Override
    public long countByBikeTypeId(Long bikeTypeId) {
        logger.info("Contando modelos para tipo de bicicleta: {}", bikeTypeId);
        if (bikeTypeId == null) {
            return 0L;
        }
        try {
            // Usa findWithFilters y cuenta
            List<BikeModelEntity> models = jpaRepository.findWithFilters(null, bikeTypeId, null);
            return models.size();
        } catch (Exception e) {
            logger.error("Error al contar modelos por tipo: {}", bikeTypeId, e);
            throw e;
        }
    }

    @Override
    public boolean existsByName(String name) {
        logger.info("Verificando si existe modelo con nombre: {}", name);
        if (name == null || name.isBlank()) {
            return false;
        }
        try {
            return jpaRepository.findWithFilters(null, null, name).stream()
                    .anyMatch(m -> m.getModelName().equalsIgnoreCase(name));
        } catch (Exception e) {
            logger.error("Error al verificar existencia por nombre: {}", name, e);
            throw e;
        }
    }

    @Override
    public List<BikeModelEntity> findAllByOrderByNameAsc() {
        return findAll(); // ya está ordenado
    }

    @Override
    public List<BikeModelEntity> findWithFilters(Long brandId, Long typeId, String name) {
        logger.info("Buscando con filtros - brandId: {}, typeId: {}, name: {}", brandId, typeId, name);
        try {
            List<BikeModelEntity> models = jpaRepository.findWithFilters(brandId, typeId, name);
            logger.debug("Resultados encontrados: {}", models.size());
            return models;
        } catch (Exception e) {
            logger.error("Error en búsqueda con filtros", e);
            throw e;
        }
    }

    @Override
    public BikeModelEntity save(BikeModelEntity bikeModel) {
        logger.info("Guardando modelo: {}", bikeModel);
        if (bikeModel == null) {
            logger.warn("Intento de guardar modelo nulo");
            throw new IllegalArgumentException("El modelo no puede ser nulo");
        }
        try {
            BikeModelEntity saved = jpaRepository.save(bikeModel);
            logger.info("Modelo guardado con ID: {}", saved.getIdBikeModel());
            return saved;
        } catch (Exception e) {
            logger.error("Error al guardar modelo", e);
            throw e;
        }
    }

    @Override
    public void deleteById(Long id) {
        logger.info("Eliminando modelo con ID: {}", id);
        if (id == null) {
            logger.warn("ID nulo para eliminación");
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }
        if (!jpaRepository.existsById(id)) {
            logger.warn("No se puede eliminar: modelo con ID {} no existe", id);
            throw new IllegalArgumentException("Modelo con ID " + id + " no existe");
        }
        try {
            jpaRepository.deleteById(id);
            logger.info("Modelo eliminado correctamente");
        } catch (Exception e) {
            logger.error("Error al eliminar modelo con ID: {}", id, e);
            throw e;
        }
    }

    @Override
    public List<BikeModelDto> findDtoByFilters(Long brandId, Long typeId) {
        logger.info("Buscando modelos en DTO por marca: {} y tipo: {}", brandId, typeId);

        try {
            List<BikeModelDto> models = jpaRepository.findDtoByFilters(brandId, typeId);
            logger.info("Se encontraron {} modelos en DTO para marca {} y tipo {}",
                    models.size(), brandId, typeId);
            return models;
        } catch (Exception e) {
            logger.error("Error al buscar modelos en DTO por marca y tipo: {}, {}", brandId, typeId, e);
            throw e;
        }
    }

    @Override
    public List<BikeModelEntity> findAllWithDetails() {
        logger.info("Obteniendo todos los modelos con detalles (marca y tipo)");
        try {
            List<BikeModelEntity> models = jpaRepository.findAllWithDetails();
            logger.info("Se encontraron {} modelos con detalles", models.size());
            return models;
        } catch (Exception e) {
            logger.error("Error al obtener modelos con detalles", e);
            throw e;
        }
    }

}
