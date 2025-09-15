package com.safe.user.application.service;

import com.safe.location.domain.model.entity.LocalidadEntity;
import com.safe.location.domain.model.entity.MunicipioEntity;
import com.safe.location.domain.model.entity.ProvinciaEntity;
import com.safe.location.infrastructure.persistence.localidad.LocalidadJpaRepository;
import com.safe.location.infrastructure.persistence.municipio.MunicipioJpaRepository;
import com.safe.location.infrastructure.persistence.provincia.ProvinciaJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class GeografiaService {

    @Autowired
    private ProvinciaJpaRepository provinciaRepository;

    @Autowired
    private MunicipioJpaRepository municipioRepository;

    @Autowired
    private LocalidadJpaRepository localidadRepository;

    public List<ProvinciaEntity> getAllProvincias() {
        return provinciaRepository.findAllByOrderByNombreAsc();
    }

    public List<MunicipioEntity> getMunicipiosByProvincia(Integer provinciaId) {
        if (provinciaId == null) {
            return new ArrayList<>();
        }
        // ✅ AHORA USA EL MÉTODO CORRECTO
        return municipioRepository.findByProvinciaIdOrderByNombreAsc(provinciaId);
    }

    public List<LocalidadEntity> getLocalidadesByMunicipio(Integer municipioId) {
        if (municipioId == null) return new ArrayList<>();
        return localidadRepository.getLocalidadesByMunicipio(municipioId);
    }

    public Optional<ProvinciaEntity> getProvinciaById(Integer id) {
        return provinciaRepository.findById(id);
    }

    public Optional<MunicipioEntity> getMunicipioById(Integer id) {
        return municipioRepository.findById(id);
    }

    public Optional<LocalidadEntity> getLocalidadById(Integer id) {
        return localidadRepository.findById(id);
    }
}
