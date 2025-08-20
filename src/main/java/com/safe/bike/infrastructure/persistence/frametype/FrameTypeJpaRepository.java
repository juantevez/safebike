package com.safe.bike.infrastructure.persistence.frametype;

import com.safe.bike.domain.model.entity.FrameTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FrameTypeJpaRepository extends JpaRepository<FrameTypeEntity, Integer> {
    // Spring Data JPA proporciona la implementación de los métodos
    // save(), findAll(), findById(), etc.
}
