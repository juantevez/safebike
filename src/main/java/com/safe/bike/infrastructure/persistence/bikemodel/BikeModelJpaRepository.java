package com.safe.bike.infrastructure.persistence.bikemodel;

import com.safe.bike.domain.model.dto.BikeModelDto;
import com.safe.bike.domain.model.entity.BikeModelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BikeModelJpaRepository extends JpaRepository<BikeModelEntity, Long> {

    // Buscar modelos por marca ID, ordenados alfabéticamente
    @Query("SELECT bm FROM BikeModelEntity bm JOIN FETCH bm.brand WHERE bm.brand.brandId = :brandId ORDER BY bm.modelName ASC")
    List<BikeModelEntity> findByBrand_BrandIdOrderByModelNameAsc(@Param("brandId") Long brandId);

    // Buscar todos los modelos ordenados por nombre
    List<BikeModelEntity> findAllByOrderByModelNameAsc();

    // Buscar modelo por marca y nombre específico
    @Query("SELECT bm FROM BikeModelEntity bm WHERE bm.brand.brandId = :brandId AND bm.modelName = :modelName")
    Optional<BikeModelEntity> findByBrandAndModelName(@Param("brandId") Long brandId, @Param("modelName") String modelName);


    @Query("SELECT new com.safe.bike.domain.model.dto.BikeModelDto(" +
            "m.idBikeModel, m.modelName, m.brand.brandId, m.brand.name, m.bikeType.bikeTypeId, m.bikeType.name) " +
            "FROM BikeModelEntity m " +
            "WHERE (:brandId IS NULL OR m.brand.brandId = :brandId) " +
            "AND (:typeId IS NULL OR m.bikeType.bikeTypeId = :typeId)")
    List<BikeModelDto> findDtoByFilters(
            @Param("brandId") Long brandId,
            @Param("typeId") Long typeId);

    // Buscar modelos por año de lanzamiento
    List<BikeModelEntity> findByYearReleasedOrderByModelNameAsc(Integer yearReleased);

    // Contar modelos por marca
    @Query("SELECT COUNT(bm) FROM BikeModelEntity bm WHERE bm.brand.brandId = :brandId")
    Long countByBrandId(@Param("brandId") Long brandId);

    @Query("SELECT m FROM BikeModelEntity m " +
            "JOIN FETCH m.brand " +
            "JOIN FETCH m.bikeType " +
            "WHERE (:brandId IS NULL OR m.brand.brandId = :brandId) " +
            "AND (:typeId IS NULL OR m.bikeType.bikeTypeId = :typeId) " +
            "AND (:namePattern IS NULL OR LOWER(m.modelName) LIKE LOWER(CAST(:namePattern AS string)))")
    List<BikeModelEntity> findWithFilters(
            @Param("brandId") Long brandId,
            @Param("typeId") Long typeId,
            @Param("namePattern") String namePattern);

    @Query("SELECT m FROM BikeModelEntity m " +
            "JOIN FETCH m.brand " +
            "JOIN FETCH m.bikeType " +
            "ORDER BY m.modelName ASC")
    List<BikeModelEntity> findAllWithDetails();
}