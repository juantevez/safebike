package com.safe.bike.out;

import com.safe.bike.domain.model.Brand;

import java.util.List;
import java.util.Optional;

public interface RedisBrandPort {
    void saveAll(List<Brand> brands);
    Optional<List<Brand>> findAll();
    void deleteAll();
}