package com.safe.location.domain.port.out;

import com.safe.location.domain.model.entity.ProvinciaEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProvinciaRepositoryPort {
    List<ProvinciaEntity> findAll();
    Optional<ProvinciaEntity> findById(Integer id);
}
