package com.safe.bike.infrastructure.persistence.bike;

import com.safe.bike.domain.model.entity.BikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BikeRepository extends JpaRepository<BikeEntity, Long> {

    List<BikeEntity> findByUser_Id(Long userId);
    Optional<BikeEntity> findByBrand_BrandId(Integer brandId);

    // ✅ NUEVOS - para buscar por modelo
    List<BikeEntity> findByBikeModel_IdBikeModel(Integer modelId);
    List<BikeEntity> findByBikeModel_ModelNameContainingIgnoreCase(String modelName);

    Optional<BikeEntity> findBySerialNumber(String serialNumber);
}