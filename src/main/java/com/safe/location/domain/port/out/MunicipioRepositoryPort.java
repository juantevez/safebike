package com.safe.location.domain.port.out;

import com.safe.location.domain.model.entity.MunicipioEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MunicipioRepositoryPort {
    List<MunicipioEntity> findAll();
    Optional<MunicipioEntity> findById(Integer id);
}
