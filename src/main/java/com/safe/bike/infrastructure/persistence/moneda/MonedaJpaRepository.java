package com.safe.bike.infrastructure.persistence.moneda;

import com.safe.bike.domain.model.entity.MonedaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MonedaJpaRepository extends JpaRepository<MonedaEntity, Integer> {
    public List<MonedaEntity> findAll();
    public Optional<MonedaEntity> findById(Integer id);
}
