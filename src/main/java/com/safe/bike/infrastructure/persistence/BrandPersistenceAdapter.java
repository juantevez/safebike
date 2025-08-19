package com.safe.bike.infrastructure.persistence;

import com.safe.bike.domain.model.Brand;
import com.safe.bike.domain.port.out.BrandRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BrandPersistenceAdapter implements BrandRepositoryPort {

    private final BrandRepository brandRepository;

    public BrandPersistenceAdapter(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    @Override
    public List<Brand> findAllBrands() {
        return brandRepository.findAllBrands().stream()
                .map(entity -> new Brand(
                        entity.getBrandId(),
                        entity.getBrandName(),
                        entity.getCreatedAt()))
                .toList();
    }

    @Override
    public List<String> getAllBrandNames() {
        return brandRepository.findAllBrands().stream()
                .map(BrandEntity::getBrandName)
                .sorted()
                .toList();
    }
}