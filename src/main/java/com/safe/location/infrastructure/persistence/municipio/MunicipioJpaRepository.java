package com.safe.location.infrastructure.persistence.municipio;

import com.safe.location.domain.model.entity.MunicipioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MunicipioJpaRepository extends JpaRepository<MunicipioEntity, Integer> {

    // ✅ CORRECTO - Si quieres buscar por provincia ID
    List<MunicipioEntity> findByProvinciaId(Integer provinciaId);

    // ✅ CORRECTO - Con ordenamiento
    List<MunicipioEntity> findByProvinciaIdOrderByNombreAsc(Integer provinciaId);

    // ✅ ADICIONALES - Si también necesitas buscar por nombre
    List<MunicipioEntity> findByNombre(String nombre);
    List<MunicipioEntity> findByNombreContaining(String nombre);

    // ✅ ALTERNATIVA - Usando @Query para ser más explícito
    @Query("SELECT m FROM MunicipioEntity m WHERE m.provinciaId = :provinciaId ORDER BY m.nombre ASC")
    List<MunicipioEntity> getMunicipiosByProvinciaId(@Param("provinciaId") Integer provinciaId);
}