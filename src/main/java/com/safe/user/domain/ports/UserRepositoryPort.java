package com.safe.user.domain.ports;

import com.safe.user.adapter.out.persistence.entity.UserEntity;
import com.safe.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryPort {

    List<UserEntity> findAll();         // OK

    Optional<UserEntity> findById(Long id);     // OK

    User findByEmail(String email);

    User save(User user);               // OK

    void deleteById(Long id);            // OK

}