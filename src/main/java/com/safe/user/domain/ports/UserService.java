package com.safe.user.domain.ports;

import com.safe.user.domain.model.Email;
import com.safe.user.domain.model.User;
import com.safe.user.domain.model.UserId;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(String email, String password, String firstName, String lastName, String username);
    Optional<User> findById(UserId id);
    Optional<User> findByEmail(Email email);
    List<User> getAllUsers();
    User updateUser(User user);
    void deleteUser(UserId id);
    User authenticateUser(String email, String password);
}