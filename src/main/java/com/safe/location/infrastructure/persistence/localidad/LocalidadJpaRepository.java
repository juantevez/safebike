package com.safe.location.infrastructure.persistence.localidad;

import com.safe.bike.domain.model.entity.MonedaEntity;
import com.safe.location.domain.model.entity.LocalidadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocalidadJpaRepository extends JpaRepository<LocalidadEntity, Integer> {
    public List<LocalidadEntity> findAll();
    public Optional<LocalidadEntity> findById(Integer id);
}
