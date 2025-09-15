package com.safe.user.application.usecases;

import com.safe.user.domain.exception.InvalidUserDataException;
import com.safe.user.domain.model.User;
import com.safe.user.domain.ports.UserRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * ✅ Caso de uso para crear usuarios - implementación inline por compatibilidad
 */
public class CreateUserUseCase {
    private static final Logger logger = LoggerFactory.getLogger(CreateUserUseCase.class);

    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;

    public CreateUserUseCase(UserRepositoryPort userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User execute(String emailValue, String passwordValue, String firstName, String lastName, String usernameValue) {
        logger.info("Ejecutando CreateUserUseCase para email: {}", emailValue);

        // Validaciones completas
        validateInput(emailValue, passwordValue, firstName, lastName, usernameValue);

        // Verificar si el usuario ya existe
        User existingUser = userRepository.findByEmail(emailValue.toLowerCase());
        if (existingUser != null) {
            throw InvalidUserDataException.userAlreadyExists(emailValue);
        }

        // Crear nuevo usuario
        User newUser = new User();
        newUser.setEmail(emailValue.trim().toLowerCase());
        newUser.setFirstName(firstName.trim());
        newUser.setLastName(lastName.trim());
        newUser.setUsername(usernameValue != null ? usernameValue.trim() : emailValue.trim());
        newUser.setPassword(passwordEncoder.encode(passwordValue));
        newUser.setRole("USER");

        return userRepository.save(newUser);
    }

    private void validateInput(String email, String password, String firstName, String lastName, String username) {
        logger.info("Ejecutando validateInput: {}", email, firstName,lastName, username);

        InvalidUserDataException.Builder builder = new InvalidUserDataException.Builder()
                .withMainMessage("Error en datos de registro");

        // Validar email
        if (email == null || email.trim().isEmpty()) {
            builder.requiredFieldError("email");
        } else {
            String emailRegex = "^[A-Za-z0-9+_.-]+@(.+\\..+)$";
            if (!email.trim().matches(emailRegex)) {
                builder.emailError(email);
            }
        }

        // Validar password
        if (password == null || password.isEmpty()) {
            builder.requiredFieldError("password");
        } else if (password.length() < 6) {
            builder.passwordError("debe tener al menos 6 caracteres");
        }

        // Validar firstName
        if (firstName == null || firstName.trim().isEmpty()) {
            builder.requiredFieldError("firstName");
        } else if (firstName.trim().length() > 50) {
            builder.withFieldError("firstName", "Nombre no puede exceder 50 caracteres");
        }

        // Validar lastName
        if (lastName == null || lastName.trim().isEmpty()) {
            builder.requiredFieldError("lastName");
        } else if (lastName.trim().length() > 50) {
            builder.withFieldError("lastName", "Apellido no puede exceder 50 caracteres");
        }

        // Validar username si se proporciona
        if (username != null && !username.trim().isEmpty()) {
            if (username.trim().length() < 3 || username.trim().length() > 50) {
                builder.withFieldError("username", "Username debe tener entre 3 y 50 caracteres");
            }
        }

        if (builder.hasErrors()) {
            throw builder.build();
        }
    }
}