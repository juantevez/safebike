package com.safe.user.domain.ports;

import com.safe.user.domain.model.Email;
import com.safe.user.domain.model.User;
import com.safe.user.domain.model.UserId;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(UserId id);
    Optional<User> findByEmail(Email email);
    List<User> findAll();
    User save(User user);
    void deleteById(UserId id);
    boolean existsByEmail(Email email);
}