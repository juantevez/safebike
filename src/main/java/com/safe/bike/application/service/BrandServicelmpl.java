package com.safe.bike.application.service;

import com.safe.bike.domain.model.Brand;
import com.safe.bike.domain.port.in.BrandServicePort;
import com.safe.bike.domain.port.out.BrandRepositoryPort;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación del caso de uso para gestionar operaciones relacionadas con marcas.
 */
@Service
public class BrandServicelmpl implements BrandServicePort {

    private final BrandRepositoryPort brandRepositoryPort;

    public BrandServicelmpl(BrandRepositoryPort brandRepositoryPort) {
        this.brandRepositoryPort = brandRepositoryPort;
    }

    /**
     * Obtiene todas las marcas desde la base de datos.
     *
     * @return Lista de objetos Brand
     */
    @Override
    public List<Brand> getAllBrands() {
        return brandRepositoryPort.findAllBrands();
    }

    /**
     * Obtiene solo los nombres de todas las marcas.
     *
     * @return Lista de nombres de marcas
     */
    @Override
    @Cacheable(value = "brandNames", key = "'all'", cacheManager = "brandCacheManager")
    public List<String> getAllBrandNames() {
        return brandRepositoryPort.getAllBrandNames();
    }
}