package com.safe.bike.application.service;

import com.safe.bike.domain.model.entity.BikeTypeEntity;
import com.safe.bike.domain.port.in.BikeTypeServicePort;
import com.safe.bike.domain.port.out.BikeTypeRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BikeTypeServiceImpl implements BikeTypeServicePort {
    private final BikeTypeRepositoryPort bikeTypeRepositoryPort;

    public BikeTypeServiceImpl(BikeTypeRepositoryPort bikeTypeRepositoryPort) {
        this.bikeTypeRepositoryPort = bikeTypeRepositoryPort;
    }

    @Override
    public List<BikeTypeEntity> getAllBikeTypes() {
        return bikeTypeRepositoryPort.findAll();
    }

    @Override
    public Optional<BikeTypeEntity> getBikeTypeById(Integer id) {
        return bikeTypeRepositoryPort.findById(id);
    }
}