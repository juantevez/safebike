package com.safe.bike.application.service;

import com.safe.bike.domain.model.entity.FrameTypeEntity;
import com.safe.bike.domain.port.in.FrameTypeServicePort;
import com.safe.bike.domain.port.out.FrameTypeRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FrameTypeServiceImpl implements FrameTypeServicePort {
    private final FrameTypeRepositoryPort frameTypeRepositoryPort;

    public FrameTypeServiceImpl(FrameTypeRepositoryPort frameTypeRepositoryPort) {
        this.frameTypeRepositoryPort = frameTypeRepositoryPort;
    }

    @Override
    public List<FrameTypeEntity> getAllFrameTypes() {
        return frameTypeRepositoryPort.findAll();
    }

    @Override
    public Optional<FrameTypeEntity> getFrameTypeById(Integer id) {
        return frameTypeRepositoryPort.findById(id);
    }
}