package com.safe.user.application.service;

import com.safe.location.domain.model.entity.LocalidadEntity;
import com.safe.location.domain.model.entity.MunicipioEntity;
import com.safe.location.domain.model.entity.ProvinciaEntity;
import com.safe.location.infrastructure.persistence.localidad.LocalidadJpaRepository;
import com.safe.location.infrastructure.persistence.municipio.MunicipioJpaRepository;
import com.safe.location.infrastructure.persistence.provincia.ProvinciaJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class GeografiaService {

    private final ProvinciaJpaRepository provinciaRepository;
    private final MunicipioJpaRepository municipioRepository;
    private final LocalidadJpaRepository localidadRepository;

    public GeografiaService(ProvinciaJpaRepository provinciaRepository,
                            MunicipioJpaRepository municipioRepository,
                            LocalidadJpaRepository localidadRepository) {
        this.provinciaRepository = provinciaRepository;
        this.municipioRepository = municipioRepository;
        this.localidadRepository = localidadRepository;
    }

    // ✅ MÉTODO PARA OBTENER TODAS LAS PROVINCIAS
    public List<ProvinciaEntity> getAllProvincias() {
        try {
            return provinciaRepository.findAllByOrderByNombreAsc();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener provincias: " + e.getMessage(), e);
        }
    }

    // ✅ MÉTODO PARA OBTENER PROVINCIA POR ID
    public Optional<ProvinciaEntity> getProvinciaById(Integer provinciaId) {
        if (provinciaId == null) {
            return Optional.empty();
        }
        try {
            return provinciaRepository.findById(provinciaId);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener provincia con ID " + provinciaId + ": " + e.getMessage(), e);
        }
    }

    // ✅ MÉTODO PARA OBTENER MUNICIPIOS POR PROVINCIA
    public List<MunicipioEntity> getMunicipiosByProvincia(Integer provinciaId) {
        if (provinciaId == null) {
            return new ArrayList<>();
        }
        try {
            return municipioRepository.findByProvinciaIdOrderByNombreAsc(provinciaId);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener municipios de provincia " + provinciaId + ": " + e.getMessage(), e);
        }
    }

    // ✅ MÉTODO PARA OBTENER MUNICIPIO POR ID
    public Optional<MunicipioEntity> getMunicipioById(Integer municipioId) {
        if (municipioId == null) {
            return Optional.empty();
        }
        try {
            return municipioRepository.findById(municipioId);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener municipio con ID " + municipioId + ": " + e.getMessage(), e);
        }
    }

    // ✅ MÉTODO PARA OBTENER LOCALIDADES POR MUNICIPIO
    public List<LocalidadEntity> getLocalidadesByMunicipio(Integer municipioId) {
        if (municipioId == null) {
            return new ArrayList<>();
        }
        try {
            return localidadRepository.findByMunicipioIdOrderByNombreAsc(municipioId);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener localidades de municipio " + municipioId + ": " + e.getMessage(), e);
        }
    }

    // ✅ MÉTODO PARA OBTENER LOCALIDAD POR ID
    public Optional<LocalidadEntity> getLocalidadById(Integer localidadId) {
        if (localidadId == null) {
            return Optional.empty();
        }
        try {
            return localidadRepository.findById(localidadId);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener localidad con ID " + localidadId + ": " + e.getMessage(), e);
        }
    }

    // ✅ MÉTODOS ADICIONALES ÚTILES

    /**
     * Obtiene la jerarquía completa: Provincia > Municipio > Localidad
     */
    public String getJerarquiaCompleta(Integer localidadId) {
        return getLocalidadById(localidadId)
                .map(localidad -> {
                    StringBuilder jerarquia = new StringBuilder();

                    // Obtener municipio
                    getMunicipioById(localidad.getMunicipioId())
                            .ifPresent(municipio -> {
                                // Obtener provincia
                                getProvinciaById(municipio.getProvinciaId())
                                        .ifPresent(provincia -> {
                                            jerarquia.append(provincia.getNombre())
                                                    .append(" > ")
                                                    .append(municipio.getNombre())
                                                    .append(" > ")
                                                    .append(localidad.getNombre());
                                        });
                            });

                    return jerarquia.toString();
                })
                .orElse("Jerarquía no encontrada");
    }

    /**
     * Valida que la combinación Provincia-Municipio-Localidad sea correcta
     */
    public boolean validarJerarquia(Integer provinciaId, Integer municipioId, Integer localidadId) {
        if (provinciaId == null || municipioId == null || localidadId == null) {
            return false;
        }

        try {
            // Verificar que el municipio pertenece a la provincia
            Optional<MunicipioEntity> municipio = getMunicipioById(municipioId);
            if (municipio.isEmpty() || !provinciaId.equals(municipio.get().getProvinciaId())) {
                return false;
            }

            // Verificar que la localidad pertenece al municipio
            Optional<LocalidadEntity> localidad = getLocalidadById(localidadId);
            return localidad.isPresent() && municipioId.equals(localidad.get().getMunicipioId());

        } catch (Exception e) {
            return false;
        }
    }
}