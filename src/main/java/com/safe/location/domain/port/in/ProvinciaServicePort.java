package com.safe.location.domain.port.in;

import com.safe.location.domain.model.entity.LocalidadEntity;
import com.safe.location.domain.model.entity.ProvinciaEntity;

import java.util.List;

public interface ProvinciaServicePort {

    List<ProvinciaEntity> findAllProvincias();
}
