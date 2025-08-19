package com.safe.bike.infrastructure.persistence;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface BrandRepository extends Repository<BrandEntity, Long> {

    @Query("SELECT b FROM BrandEntity b")
    List<BrandEntity> findAllBrands();
}