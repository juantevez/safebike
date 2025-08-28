package com.safe.location.domain.port.out;

import com.safe.location.domain.model.entity.LocalidadEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocalidadRepositoryPort {
    List<LocalidadEntity> findAll();
    Optional<LocalidadEntity> findById(Integer id);
}
