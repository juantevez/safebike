package com.safe.bike.infrastructure.web.service;

import com.safe.bike.domain.model.entity.BikeEntity;
import com.safe.bike.domain.port.in.BikeServicePort;
import org.springframework.stereotype.Service;

@Service
public class BikeFormService {

    private final BikeServicePort bikeService;

    public BikeFormService(BikeServicePort bikeService) {
        this.bikeService = bikeService;
    }

    public void saveBike(BikeEntity bike) {
        bikeService.save(bike);
    }
}