package com.safe.location.infrastructure.persistence.provincia;

import com.safe.location.domain.model.entity.MunicipioEntity;
import com.safe.location.domain.model.entity.ProvinciaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProvinciaJpaRepository extends JpaRepository<ProvinciaEntity, Integer> {
    public List<ProvinciaEntity> findAll();
    public Optional<ProvinciaEntity> findById(Integer id);
}
