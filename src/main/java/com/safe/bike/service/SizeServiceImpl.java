package com.safe.bike.service;

import com.safe.bike.domain.model.entity.SizeEntity;
import com.safe.bike.domain.port.in.SizeServicePort;
import com.safe.bike.domain.port.out.SizeRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SizeServiceImpl implements SizeServicePort {

    private final SizeRepositoryPort sizeRepositoryPort;

    public SizeServiceImpl(SizeRepositoryPort sizeRepositoryPort) {
        this.sizeRepositoryPort = sizeRepositoryPort;
    }

    @Override
    public List<SizeEntity> findAllSizes() {
        return sizeRepositoryPort.findAll();
    }

    @Override
    public Optional<SizeEntity> getSizeById(Integer id) {
        return sizeRepositoryPort.findById(id);
    }

    @Override
    public List<SizeEntity> getSizesByModelId(Long modelId) {
        return sizeRepositoryPort.findByModelId(modelId);
    }
}
