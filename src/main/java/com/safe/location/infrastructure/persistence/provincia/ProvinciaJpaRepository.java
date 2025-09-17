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

    // Obtener todas las provincias ordenadas por nombre
    List<ProvinciaEntity> findAllByOrderByNombreAsc();

    // Buscar por nombre (opcional, para validaciones)
    Optional<ProvinciaEntity> findByNombre(String nombre);

    // Buscar por código (opcional)
    Optional<ProvinciaEntity> findByCodigoProvincia(String codigoProvincia);
}
