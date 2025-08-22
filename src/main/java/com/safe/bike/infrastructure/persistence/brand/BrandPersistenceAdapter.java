package com.safe.bike.infrastructure.persistence.brand;

import com.safe.bike.domain.model.entity.BrandEntity;
import com.safe.bike.domain.port.out.BrandRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class BrandPersistenceAdapter implements BrandRepositoryPort {

    private final BrandJpaRepository brandJpaRepository;

    public BrandPersistenceAdapter(BrandJpaRepository brandJpaRepository) {
        this.brandJpaRepository = brandJpaRepository;
    }

    @Override
    public BrandEntity save(BrandEntity brand) {
        return brandJpaRepository.save(brand);
    }

    @Override
    public List<BrandEntity> findAll() {
        return brandJpaRepository.findAll();
    }

    @Override
    public Optional<BrandEntity> findById(Integer id) {
        return brandJpaRepository.findById(id);
    }

    @Override
    public List<BrandEntity> findAllOrderedByName() {
        return brandJpaRepository.findAllOrderedByName();
    }
}