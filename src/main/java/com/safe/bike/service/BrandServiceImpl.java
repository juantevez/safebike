package com.safe.bike.service;

import com.safe.bike.domain.model.entity.BrandEntity;
import com.safe.bike.domain.port.in.BrandServicePort;
import com.safe.bike.domain.port.out.BrandRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BrandServiceImpl implements BrandServicePort {

    private final BrandRepositoryPort brandRepositoryPort;

    public BrandServiceImpl(BrandRepositoryPort brandRepositoryPort) {
        this.brandRepositoryPort = brandRepositoryPort;
    }

    @Override
    public List<BrandEntity> getAllBrands() {
        // Lógica de negocio para obtener todas las marcas
        return brandRepositoryPort.findAll();
    }

    @Override
    public Optional<BrandEntity> getBrandById(Integer id) {
        // Lógica de negocio para obtener una marca por su ID
        return brandRepositoryPort.findById(id);
    }
}