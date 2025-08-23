package com.safe.bike.domain.port.out;

import com.safe.bike.domain.model.entity.MonedaEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MonedaRepositoryPort {
    List<MonedaEntity> findAll();
    Optional<MonedaEntity> findById(Integer id);
}
