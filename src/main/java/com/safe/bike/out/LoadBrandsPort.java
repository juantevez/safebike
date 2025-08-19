package com.safe.bike.out;

import com.safe.bike.domain.model.Brand;

import java.util.List;

public interface LoadBrandsPort {
    List<Brand> loadAllBrands();
}