package com.safe.bike.infrastructure.persistence.biketype;

import com.safe.bike.domain.model.entity.BikeTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BikeTypeJpaRepository extends JpaRepository<BikeTypeEntity, Integer> {
    // Los métodos de persistencia son proporcionados por JpaRepository
    public BikeTypeEntity save(BikeTypeEntity bikeType);

    public List<BikeTypeEntity> findAll();
    public Optional<BikeTypeEntity> findById(Integer id);
}