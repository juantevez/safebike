package com.safe.bike.domain.port.out;

import com.safe.bike.domain.model.dto.BikeModelDto;
import com.safe.bike.domain.model.entity.BikeModelEntity;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para operaciones de persistencia de modelos de bicicleta.
 * Define únicamente las operaciones que el dominio necesita.
 */
public interface BikeModelRepositoryPort {

    List<BikeModelEntity> findAllWithDetails();
    /**
     * Obtiene todos los modelos ordenados por nombre.
     */
    List<BikeModelEntity> findAll();

    /**
     * Busca un modelo por su ID.
     */
    Optional<BikeModelEntity> findById(Long id);

    /**
     * Verifica si existe un modelo con el ID dado.
     */
    boolean existsById(Long id);

    /**
     * Busca todos los modelos de una marca específica.
     */
    List<BikeModelEntity> findByBrandId(Long brandId);

    /**
     * Busca todos los modelos de un tipo de bicicleta específico.
     */
    List<BikeModelEntity> findByBikeTypeId(Long bikeTypeId);

    /**
     * Busca modelos que coincidan con marca y tipo.
     */
    List<BikeModelEntity> findByBrandIdAndBikeTypeId(Long brandId, Long bikeTypeId);

    /**
     * Busca un modelo por nombre (búsqueda exacta, ignorando mayúsculas).
     */
    Optional<BikeModelEntity> findByName(String name);

    /**
     * Busca modelos cuyo nombre contenga el texto (ignorando mayúsculas).
     */
    List<BikeModelEntity> findByNameContainingIgnoreCase(String name);

    /**
     * Cuenta cuántos modelos hay de una marca.
     */
    long countByBrandId(Long brandId);

    /**
     * Cuenta cuántos modelos hay de un tipo.
     */
    long countByBikeTypeId(Long bikeTypeId);

    /**
     * Verifica si existe un modelo con el nombre dado.
     */
    boolean existsByName(String name);

    /**
     * Obtiene todos los modelos ordenados alfabéticamente por nombre.
     */
    List<BikeModelEntity> findAllByOrderByNameAsc();

    /**
     * Busca modelos aplicando filtros opcionales (marca, tipo, nombre).
     */
    List<BikeModelEntity> findWithFilters(Long brandId, Long typeId, String name);

    /**
     * Guarda o actualiza un modelo.
     */
    BikeModelEntity save(BikeModelEntity bikeModel);

    /**
     * Elimina un modelo por su ID.
     */
    void deleteById(Long id);
    List<BikeModelDto> findDtoByFilters(Long brandId, Long typeId);
}