package com.safe.bike.infrastructure.persistence.brand;

import com.safe.bike.domain.model.entity.BrandEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandJpaRepository extends JpaRepository<BrandEntity, Integer> {
    // Los métodos save(), findAll(), findById(), etc., son proporcionados automáticamente.
}