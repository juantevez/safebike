package com.safe.bike.domain.port.out;

import com.safe.bike.domain.model.entity.FrameTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// No necesitas una clase de implementación. Spring la crea por ti.
@Repository
public interface FrameTypeRepositoryPort extends JpaRepository<FrameTypeEntity, Integer> {
    // Aquí puedes agregar métodos de consulta personalizados si los necesitas.
    // Por ejemplo: List<FrameTypeEntity> findByName(String name);
}