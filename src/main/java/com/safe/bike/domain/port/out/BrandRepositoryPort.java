package com.safe.bike.domain.port.out;


import com.safe.bike.domain.model.Brand;
import com.safe.bike.domain.model.entity.BrandEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface BrandRepositoryPort {
    BrandEntity save(BrandEntity brand);

    List<BrandEntity> findAll();

    Optional<BrandEntity> findById(Integer id);

    List<BrandEntity> findAllOrderedByName();
}