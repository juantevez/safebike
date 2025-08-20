package com.safe.user.application.service;


import com.safe.user.application.dto.UserDTO;
import com.safe.user.domain.User;
import com.safe.user.infrastructure.adapter.UserRepositoryPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepositoryPort userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDTO registrarUsuario(String email, String password, String firstName, String lastName, String userName) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("El correo ya está registrado.");
        }

        String hashedPassword = passwordEncoder.encode(password);
        User user = new User(email, hashedPassword, firstName, lastName, userName);
        user.setRole("USER"); // Asignar rol por defecto

        User saved = userRepository.save(user);
        return new UserDTO(saved.getEmail(), saved.getFirstName(), saved.getLastName());
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElse(null);
    }
}