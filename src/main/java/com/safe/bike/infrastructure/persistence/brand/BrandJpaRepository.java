package com.safe.bike.infrastructure.persistence.brand;

import com.safe.bike.domain.model.entity.BrandEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandJpaRepository extends JpaRepository<BrandEntity, Integer> {
    // Los métodos save(), findAll(), findById(), etc., son proporcionados automáticamente.
    @Query("SELECT b FROM BrandEntity b ORDER BY b.name ASC")
    List<BrandEntity> findAllOrderedByName();

}