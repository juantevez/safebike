package com.safe.location.infrastructure.persistence.provincia;

import com.safe.location.domain.model.entity.MunicipioEntity;
import com.safe.location.domain.model.entity.ProvinciaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ProvinciaJpaRepository extends JpaRepository<ProvinciaEntity, Integer> {

    // ✅ CORRECTO - Si tu campo se llama "nombre"
    List<ProvinciaEntity> findAllByOrderByNombreAsc();

    // ✅ ALTERNATIVA - Usando @Query para ser explícito
    @Query("SELECT p FROM ProvinciaEntity p ORDER BY p.nombre ASC")
    List<ProvinciaEntity> getAllProvinciasSorted();

    // ✅ MÉTODOS ADICIONALES QUE PODRÍAS NECESITAR
    List<ProvinciaEntity> findByNombre(String nombre);
    List<ProvinciaEntity> findByNombreContaining(String nombre);
    boolean existsByNombre(String nombre);
}