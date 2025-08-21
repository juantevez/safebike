package com.safe.user.infrastructure.port;

import com.safe.user.adapter.out.persistence.entity.UserEntity;
import com.safe.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserServicePort {
    List<UserEntity> getAllUsers();
    Optional<UserEntity> getUserById(Long id);
    User save(User user);
    void deleteById(Long id);

    User findByEmail(String email);
    // Método específico para registro de usuarios
    User registrarUsuario(String email, String password, String firstName, String lastName, String userName);
}