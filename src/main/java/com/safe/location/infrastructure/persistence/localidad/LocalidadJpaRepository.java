package com.safe.location.infrastructure.persistence.localidad;

import com.safe.bike.domain.model.entity.MonedaEntity;
import com.safe.location.domain.model.entity.LocalidadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface LocalidadJpaRepository extends JpaRepository<LocalidadEntity, Integer> {

    // ✅ MÉTODO PRINCIPAL: Obtener localidades por municipio
    List<LocalidadEntity> findByMunicipioIdOrderByNombreAsc(Integer municipioId);

    // Contar localidades por municipio
    long countByMunicipioId(Integer municipioId);

    // Buscar por nombre y municipio (opcional, para validaciones)
    Optional<LocalidadEntity> findByNombreAndMunicipioId(String nombre, Integer municipioId);

    // Verificar si existe localidad en municipio
    boolean existsByIdAndMunicipioId(Integer localidadId, Integer municipioId);

    // Buscar por código postal (opcional)
    List<LocalidadEntity> findByCodigoPostal(String codigoPostal);

    // Buscar por tipo de localidad (opcional)
    List<LocalidadEntity> findByMunicipioIdAndTipoOrderByNombreAsc(Integer municipioId, LocalidadEntity.TipoLocalidad tipo);
}
/*
@Repository
public interface LocalidadJpaRepository extends JpaRepository<LocalidadEntity, Integer> {
    @Query("SELECT l FROM LocalidadEntity l WHERE l.municipioId = :municipioId ORDER BY l.nombre ASC")
    List<LocalidadEntity> getLocalidadesByMunicipio(@Param("municipioId") Integer municipioId);
}
*/