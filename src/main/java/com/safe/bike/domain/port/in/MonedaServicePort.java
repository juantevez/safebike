package com.safe.bike.domain.port.in;

import com.safe.bike.domain.model.entity.BikeTypeEntity;
import com.safe.bike.domain.model.entity.MonedaEntity;

import java.util.List;

public interface MonedaServicePort {

    List<MonedaEntity> findAllMonedas();
}
