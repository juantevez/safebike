package com.safe.bike.domain.port.in;

import com.safe.bike.domain.model.entity.SizeEntity;

import java.util.List;

public interface SizeServicePort {

    List<SizeEntity> findAllSizes();
}
