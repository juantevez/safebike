package com.safe.bike.domain.port.out;

import com.safe.bike.domain.model.entity.FrameTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// No necesitas una clase de implementación. Spring la crea por ti.
@Repository
public interface FrameTypeRepositoryPort{
    FrameTypeEntity save(FrameTypeEntity frameType);

    List<FrameTypeEntity> findAll();

    Optional<FrameTypeEntity> findById(Integer id);
}