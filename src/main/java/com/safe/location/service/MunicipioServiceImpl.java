package com.safe.location.service;

import com.safe.location.domain.model.entity.MunicipioEntity;
import com.safe.location.domain.port.in.MunicipalidadServicePort;
import com.safe.location.domain.port.out.MunicipioRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MunicipioServiceImpl implements MunicipalidadServicePort {
    private static final Logger logger = LoggerFactory.getLogger(MunicipioServiceImpl.class);

    private final MunicipioRepositoryPort municipioRepositoryPort  ;

    public MunicipioServiceImpl(MunicipioRepositoryPort municipioRepositoryPort) {
        this.municipioRepositoryPort = municipioRepositoryPort;
    }

    @Override
    @Cacheable("allMunicipios")
    public List<MunicipioEntity> findAllMunicipios() {
        logger.info("Obteniendo todos las municipalidades");

        try {
            List<MunicipioEntity> municipalidades = municipioRepositoryPort.findAll();
            logger.info("Se encontraron {} municipalidades: ", municipalidades.size());
            logger.debug("Municipalidades obtenidas: {}", municipalidades);
            return municipalidades;
        } catch (Exception e) {
            logger.error("Error al obtener todos las municipalidades", e);
            throw e;
        }
    }
}
