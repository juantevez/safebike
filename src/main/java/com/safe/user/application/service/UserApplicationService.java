package com.safe.user.application.service;


import com.safe.user.application.usecases.CreateUserUseCase;
import com.safe.user.application.usecases.FindUserUseCase;
import com.safe.user.domain.exception.InvalidUserDataException;
import com.safe.user.domain.model.User;
import com.safe.user.domain.ports.UserRepositoryPort;
import com.safe.user.infrastructure.adapters.output.persistence.entities.UserEntity;
import com.safe.user.infrastructure.port.UserServicePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * ✅ COMPATIBLE: Servicio de aplicación que implementa UserServicePort existente
 * Añade validaciones y casos de uso sin romper la interfaz actual
 */
@Service("userApplicationService") // ✅ Nombre específico para evitar conflictos
@Primary
public class UserApplicationService implements UserServicePort {

    private static final Logger logger = LoggerFactory.getLogger(UserApplicationService.class);

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;

    // ✅ Para migración futura - casos de uso opcionales por ahora
    private final CreateUserUseCase createUserUseCase;
    private final FindUserUseCase findUserUseCase;

    public UserApplicationService(
            UserRepositoryPort userRepositoryPort,
            PasswordEncoder passwordEncoder) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoder = passwordEncoder;
        // ✅ Por ahora crear casos de uso inline - migración futura
        this.createUserUseCase = new CreateUserUseCase(userRepositoryPort, passwordEncoder);
        this.findUserUseCase = new FindUserUseCase(userRepositoryPort);
    }

    // ================================
    // IMPLEMENTACIÓN DE UserServicePort - ✅ MISMOS MÉTODOS EXISTENTES
    // ================================

    @Override
    @Transactional(readOnly = true)
    public List<UserEntity> getAllUsers() {
        logger.info("Obteniendo todos los usuarios");
        try {
            List<UserEntity> users = userRepositoryPort.findAll();
            logger.info("Se encontraron {} usuarios", users.size());
            return users;
        } catch (Exception e) {
            logger.error("Error al obtener todos los usuarios", e);
            throw new RuntimeException("Error al obtener usuarios", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserEntity> getUserById(Long id) {
        logger.info("Buscando usuario con ID: {}", id);

        if (id == null) {
            logger.warn("Se intentó buscar un usuario con ID null");
            return Optional.empty();
        }

        try {
            Optional<UserEntity> user = userRepositoryPort.findById(id);
            if (user.isPresent()) {
                logger.info("Usuario encontrado con ID: {}", id);
            } else {
                logger.warn("No se encontró usuario con ID: {}", id);
            }
            return user;
        } catch (Exception e) {
            logger.error("Error al buscar usuario con ID: {}", id, e);
            throw new RuntimeException("Error al buscar usuario", e);
        }
    }

    @Override
    @Transactional
    public User save(User user) {
        logger.info("Guardando usuario");

        if (user == null) {
            logger.warn("Se intentó guardar un usuario null");
            throw new IllegalArgumentException("Usuario no puede ser null");
        }

        try {
            // ✅ NUEVA VALIDACIÓN: Usar las validaciones mejoradas
            validateUserData(user);

            logger.debug("Datos del usuario a guardar: {}", user);
            User savedUser = userRepositoryPort.save(user);
            logger.info("Usuario guardado exitosamente con ID: {}", savedUser.getId());
            return savedUser;
        } catch (InvalidUserDataException e) {
            logger.warn("Error de validación al guardar usuario: {}", e.getMessage());
            throw e; // Re-lanzar errores de validación
        } catch (Exception e) {
            logger.error("Error al guardar el usuario: {}", user, e);
            throw new RuntimeException("Error al guardar usuario", e);
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        logger.info("Eliminando usuario con ID: {}", id);

        if (id == null) {
            logger.warn("Se intentó eliminar un usuario con ID null");
            throw new IllegalArgumentException("ID no puede ser null");
        }

        try {
            Optional<UserEntity> existingUser = userRepositoryPort.findById(id);
            if (existingUser.isPresent()) {
                userRepositoryPort.deleteById(id);
                logger.info("Usuario eliminado exitosamente con ID: {}", id);
            } else {
                logger.warn("No se puede eliminar: Usuario con ID {} no existe", id);
                throw new IllegalArgumentException("Usuario con ID " + id + " no existe");
            }
        } catch (Exception e) {
            logger.error("Error al eliminar usuario con ID: {}", id, e);
            throw new RuntimeException("Error al eliminar usuario", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        logger.info("Buscando usuario por email: {}", email);

        if (email == null || email.trim().isEmpty()) {
            logger.warn("Se intentó buscar un usuario con email null o vacío");
            return null;
        }

        try {
            // ✅ NUEVA VALIDACIÓN: Validar formato de email
            validateEmailFormat(email);

            User user = userRepositoryPort.findByEmail(email.toLowerCase());
            if (user != null) {
                logger.info("Usuario encontrado con email: {}", email);
            } else {
                logger.warn("No se encontró usuario con email: {}", email);
            }
            return user;
        } catch (InvalidUserDataException e) {
            logger.warn("Email inválido: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error al buscar usuario por email: {}", email, e);
            throw new RuntimeException("Error al buscar usuario por email", e);
        }
    }

    @Override
    @Transactional
    public User registrarUsuario(String email, String password, String firstName, String lastName, String userName) {
        logger.info("Registrando nuevo usuario: {}", email);

        try {
            // ✅ MEJORADO: Usar caso de uso con validaciones robustas
            return createUserUseCase.execute(email, password, firstName, lastName, userName);

        } catch (InvalidUserDataException e) {
            logger.warn("Error de validación en registro: {}", e.getDetailedMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error al registrar usuario con email: {}", email, e);
            throw new RuntimeException("Error al registrar usuario: " + e.getMessage(), e);
        }
    }


    // ================================
    // MÉTODOS AUXILIARES DE VALIDACIÓN
    // ================================

    /**
     * Valida los datos básicos de un usuario
     */
    private void validateUserData(User user) {
        InvalidUserDataException.Builder builder = new InvalidUserDataException.Builder()
                .withMainMessage("Datos de usuario inválidos");

        // Validar email
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            builder.requiredFieldError("email");
        } else {
            try {
                validateEmailFormat(user.getEmail());
            } catch (InvalidUserDataException e) {
                builder.emailError(user.getEmail());
            }
        }

        // Validar nombres
        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
            builder.requiredFieldError("firstName");
        } else if (user.getFirstName().trim().length() > 50) {
            builder.withFieldError("firstName", "Nombre no puede exceder 50 caracteres");
        }

        if (user.getLastName() == null || user.getLastName().trim().isEmpty()) {
            builder.requiredFieldError("lastName");
        } else if (user.getLastName().trim().length() > 50) {
            builder.withFieldError("lastName", "Apellido no puede exceder 50 caracteres");
        }

        // Si hay errores, lanzar excepción
        if (builder.hasErrors()) {
            throw builder.build();
        }
    }

    /**
     * Valida formato de email
     */
    private void validateEmailFormat(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw InvalidUserDataException.requiredField("email");
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+\\..+)$";
        if (!email.trim().matches(emailRegex)) {
            throw InvalidUserDataException.invalidEmail(email);
        }
    }
}