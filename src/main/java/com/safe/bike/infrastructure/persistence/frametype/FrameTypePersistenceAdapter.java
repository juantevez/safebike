package com.safe.bike.infrastructure.persistence.frametype;

import com.safe.bike.domain.model.entity.FrameTypeEntity;
import com.safe.bike.domain.port.out.FrameTypeRepositoryPort;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;


@Component
public class FrameTypePersistenceAdapter implements FrameTypeRepositoryPort { // <-- Make sure it says FrameTypeRepositoryPort

    private final FrameTypeJpaRepository frameTypeJpaRepository;

    public FrameTypePersistenceAdapter(FrameTypeJpaRepository frameTypeJpaRepository) {
        this.frameTypeJpaRepository = frameTypeJpaRepository;
    }

    @Override
    public FrameTypeEntity save(FrameTypeEntity frameType) {
        return frameTypeJpaRepository.save(frameType);
    }

    @Override
    public List<FrameTypeEntity> findAll() {
        return frameTypeJpaRepository.findAll();
    }

    @Override
    public Optional<FrameTypeEntity> findById(Integer id) {
        return frameTypeJpaRepository.findById(id);
    }
}