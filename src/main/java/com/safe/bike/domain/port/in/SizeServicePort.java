package com.safe.bike.domain.port.in;

import com.safe.bike.domain.model.entity.SizeEntity;

import java.util.List;
import java.util.Optional;

public interface SizeServicePort {

    List<SizeEntity> findAllSizes();

    Optional<SizeEntity> getSizeById(Integer id); // este está bien, busca por ID de tamaño

    // ✅ Cambia de Optional<SizeEntity> a List<SizeEntity>
    List<SizeEntity> getSizesByModelId(Long modelId);
}
