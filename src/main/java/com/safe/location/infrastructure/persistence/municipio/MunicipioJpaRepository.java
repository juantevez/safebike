package com.safe.location.infrastructure.persistence.municipio;

import com.safe.location.domain.model.entity.MunicipioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MunicipioJpaRepository extends JpaRepository<MunicipioEntity, Integer> {
    public List<MunicipioEntity> findAll();
    public Optional<MunicipioEntity> findById(Integer id);
}
