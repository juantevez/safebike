package com.safe.bike.domain.port.in;


import com.safe.bike.domain.model.entity.FrameTypeEntity;

import java.util.List;
import java.util.Optional;

public interface FrameTypeServicePort {

    List<FrameTypeEntity> getAllFrameTypes();
    Optional<FrameTypeEntity> getFrameTypeById(Integer id);
}