package com.safe.bike.infrastructure.persistence.bike;

import com.safe.bike.domain.model.entity.BikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BikeRepository extends JpaRepository<BikeEntity, Long> {
    // Este método ya lo heredas de JpaRepository, no lo declares.
    // Optional<BikeEntity> findById(Long id);

    // Este método es personalizado para encontrar por el ID de la marca.
    // Usamos la convención de Spring Data JPA.
    Optional<BikeEntity> findByBrand_BrandId(Integer brandId);

    // Este método es personalizado para encontrar por el tipo de bicicleta.
    // Opcionalmente, si lo necesitas.
    Optional<BikeEntity> findByBikeType_BikeTypeId(Integer bikeTypeId);

    // Si necesitas un método para encontrar todas las bicicletas,
    // también lo heredas de JpaRepository.
    // List<BikeEntity> findAll();
}