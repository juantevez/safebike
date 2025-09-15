package com.safe.user.infrastructure.port;

import com.safe.user.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface UserServicePort {
    // Métodos básicos CRUD
    List<User> getAllUsers(); // ✅ Cambiado de UserEntity a User
    Optional<User> getUserById(Long id); // ✅ Cambiado de UserEntity a User
    User save(User user);
    void deleteById(Long id);

    // Métodos de búsqueda
    User findByEmail(String email);
    User findByUsername(String username); // ✅ NUEVO
    User findById(Long id); // ✅ NUEVO

    // Métodos de registro
    User registrarUsuario(String email, String password, String firstName, String lastName, String userName);

    // ✅ NUEVO MÉTODO CON DATOS GEOGRÁFICOS
    User registrarUsuarioConDatosGeograficos(
            String email,
            String password,
            String firstName,
            String lastName,
            String username,
            Integer provinciaId,
            Integer municipioId,
            Integer localidadId);

    // ✅ NUEVOS MÉTODOS GEOGRÁFICOS
    User actualizarDatosGeograficos(Long userId, Integer provinciaId, Integer municipioId, Integer localidadId);
    List<User> findUsersByProvincia(Integer provinciaId);
    List<User> findUsersByMunicipio(Integer municipioId);
    List<User> findUsersByLocalidad(Integer localidadId);

}