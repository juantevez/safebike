package com.safe.user.domain.ports;


import com.safe.user.domain.model.entity.User;
import com.safe.user.domain.model.entity.UserEntity;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para operaciones de persistencia de usuarios
 * ✅ COMPATIBLE con UserRepositoryAdapter existente y UserServicePort
 */
public interface UserRepositoryPort {

    /**
     * Buscar todos los usuarios
     * @return Lista de UserEntity (compatible con getAllUsers())
     */
    List<UserEntity> findAll();

    /**
     * Buscar usuario por ID
     * @param id ID del usuario
     * @return Optional de UserEntity (compatible con getUserById())
     */
    Optional<UserEntity> findById(Long id);

    /**
     * Buscar usuario por email
     * @param email Email del usuario
     * @return User de dominio (compatible con findByEmail())
     */
    User findByEmail(String email);

    /**
     * Guardar usuario
     * @param user Usuario de dominio
     * @return Usuario guardado (compatible con save())
     */
    User save(User user);

    /**
     * Eliminar usuario por ID
     * @param id ID del usuario a eliminar (compatible con deleteById())
     */
    void deleteById(Long id);

    /**
     * ✅ NUEVO: Verificar si existe usuario con email
     * @param email Email a verificar
     * @return true si existe, false si no
     */
    boolean existsByEmail(String email);

    /**
     * ✅ NUEVO: Verificar si existe usuario con username
     * @param username Username a verificar
     * @return true si existe, false si no
     */
    boolean existsByUsername(String username);

    /**
     * ✅ NUEVO: Contar total de usuarios
     * @return Número total de usuarios
     */
    long count();

    /**
     * ✅ NUEVO: Buscar usuarios activos (role != INACTIVE)
     * @return Lista de usuarios activos
     */
    List<UserEntity> findActiveUsers();

    /**
     * ✅ NUEVO: Buscar usuarios por rol
     * @param role Rol a buscar
     * @return Lista de usuarios con ese rol
     */
    List<UserEntity> findByRole(String role);
}
