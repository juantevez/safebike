package com.safe.bike.infrastructure.persistence.bike;

import com.safe.bike.domain.model.dto.BikeForPhotoDTO;
import com.safe.bike.domain.model.entity.BikeEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BikeRepository extends JpaRepository<BikeEntity, Long> {

    @EntityGraph(attributePaths = {
            "brand",
            "bikeModel",
            "user",
            "bikeType",
            "size"
    })
    List<BikeEntity> findByUser_Id(Long userId);
    Optional<BikeEntity> findByBrand_BrandId(Long brandId);

    List<BikeEntity> findByUserId(Long userId);

    // ✅ NUEVOS - para buscar por modelo
    List<BikeEntity> findByBikeModel_IdBikeModel(Long modelId);
    List<BikeEntity> findByBikeModel_ModelNameContainingIgnoreCase(String modelName);
    Optional<BikeEntity> findBySerialNumber(String serialNumber);

    // ✅ MÉTODO ALTERNATIVO - si tu UserEntity tiene campo userId en lugar de id
// ✅ Correcto
    //List<BikeEntity> findByUser_Id(Long id);

    @Query("""
        SELECT new com.safe.bike.domain.model.dto.BikeForPhotoDTO(
            b.bikeId,
            br.name,
            bm.modelName,
            b.serialNumber
        )
        FROM BikeEntity b
        JOIN b.brand br
        JOIN b.bikeModel bm
        WHERE b.user.id = :userId
        """)
    List<BikeForPhotoDTO> findSummariesByUserId(@Param("userId") Long userId);


}