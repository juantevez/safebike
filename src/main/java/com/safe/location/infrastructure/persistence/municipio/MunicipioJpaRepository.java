package com.safe.location.infrastructure.persistence.municipio;

import com.safe.location.domain.model.entity.MunicipioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MunicipioJpaRepository extends JpaRepository<MunicipioEntity, Integer> {

    // ✅ MÉTODO PRINCIPAL: Obtener municipios por provincia
    List<MunicipioEntity> findByProvinciaIdOrderByNombreAsc(Integer provinciaId);

    // Contar municipios por provincia
    long countByProvinciaId(Integer provinciaId);

    // Buscar por nombre y provincia (opcional, para validaciones)
    Optional<MunicipioEntity> findByNombreAndProvinciaId(String nombre, Integer provinciaId);

    // Verificar si existe municipio en provincia
    boolean existsByIdAndProvinciaId(Integer municipioId, Integer provinciaId);
}