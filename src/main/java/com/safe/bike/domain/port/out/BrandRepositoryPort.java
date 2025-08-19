package com.safe.bike.domain.port.out;


import com.safe.bike.domain.model.Brand;

import java.util.List;

public interface BrandRepositoryPort {
    List<Brand> findAllBrands();
    List<String> getAllBrandNames();
}