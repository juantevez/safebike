package com.safe.user.domain.ports;

import com.safe.user.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository  extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);
    boolean existsByUsername(String username);


    // Métodos para búsqueda geográfica
    List<User> findByProvinciaId(Integer provinciaId);
    List<User> findByMunicipioId(Integer municipioId);
    List<User> findByLocalidadId(Integer localidadId);

    // Búsquedas combinadas
    List<User> findByProvinciaIdAndMunicipioId(Integer provinciaId, Integer municipioId);
    List<User> findByProvinciaIdAndMunicipioIdAndLocalidadId(Integer provinciaId, Integer municipioId, Integer localidadId);

    // Con paginación si la necesitas
    Page<User> findByProvinciaId(Integer provinciaId, Pageable pageable);
    Page<User> findByMunicipioId(Integer municipioId, Pageable pageable);

    // Con ordenamiento
    List<User> findByProvinciaIdOrderByLastNameAscFirstNameAsc(Integer provinciaId);

}