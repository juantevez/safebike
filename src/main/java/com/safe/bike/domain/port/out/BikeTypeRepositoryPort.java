package com.safe.bike.domain.port.out;

import com.safe.bike.domain.model.entity.BikeTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// De nuevo, no se requiere una clase de implementación.
@Repository
public interface BikeTypeRepositoryPort {
    BikeTypeEntity save(BikeTypeEntity bikeType);
    List<BikeTypeEntity> findAll();
    Optional<BikeTypeEntity> findById(Integer id);
}