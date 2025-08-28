package com.safe.location.domain.port.in;

import com.safe.location.domain.model.entity.LocalidadEntity;
import com.safe.location.domain.model.entity.MunicipioEntity;

import java.util.List;

public interface LocalidadServicePort {

    List<LocalidadEntity> findAllLocalidades();
}
