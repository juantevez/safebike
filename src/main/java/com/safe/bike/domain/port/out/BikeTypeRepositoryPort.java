package com.safe.bike.domain.port.out;

import com.safe.bike.domain.model.entity.BikeTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// De nuevo, no se requiere una clase de implementación.
@Repository
public interface BikeTypeRepositoryPort extends JpaRepository<BikeTypeEntity, Integer> {
    // Métodos de consulta personalizados pueden ir aquí.
    // Por ejemplo: Optional<BikeTypeEntity> findByType(String type);
}