package com.safe.bike.infrastructure.persistence.bikemodel;


import com.safe.bike.domain.model.entity.BikeModelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface BikeModelJpaRepository extends JpaRepository<BikeModelEntity, Integer> {

    // Buscar modelos por marca ID, ordenados alfabéticamente
    @Query("SELECT bm FROM BikeModelEntity bm JOIN FETCH bm.brand WHERE bm.brand.brandId = :brandId ORDER BY bm.modelName ASC")
    List<BikeModelEntity> findByBrand_BrandIdOrderByModelNameAsc(@Param("brandId") Integer brandId);

    // Buscar todos los modelos ordenados por nombre
    List<BikeModelEntity> findAllByOrderByModelNameAsc();

    // Buscar modelo por marca y nombre específico
    @Query("SELECT bm FROM BikeModelEntity bm WHERE bm.brand.brandId = :brandId AND bm.modelName = :modelName")
    Optional<BikeModelEntity> findByBrandAndModelName(@Param("brandId") Integer brandId, @Param("modelName") String modelName);

    // Buscar modelos por año de lanzamiento
    List<BikeModelEntity> findByYearReleasedOrderByModelNameAsc(Integer yearReleased);

    // Contar modelos por marca
    @Query("SELECT COUNT(bm) FROM BikeModelEntity bm WHERE bm.brand.brandId = :brandId")
    Long countByBrandId(@Param("brandId") Integer brandId);
}