package com.safe.user.application.service;

import com.safe.user.infrastructure.adapters.output.persistence.entities.UserEntity;
import com.safe.user.domain.ports.UserRepositoryPort;
import com.safe.user.domain.model.User;
import com.safe.user.infrastructure.port.UserServicePort;
import com.vaadin.flow.server.VaadinSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("userServiceImpl") // ✅ Quitar @Primary, mantener como backup
@Qualifier("original")
public class UserServiceImpl implements UserServicePort {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;

    // Constructor con inyección de dependencias - Solo UserRepositoryPort es necesario
    public UserServiceImpl(UserRepositoryPort userRepositoryPort, PasswordEncoder passwordEncoder) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserEntity> getAllUsers() {
        logger.info("Obteniendo todos los usuarios");

        try {
            List<UserEntity> users = userRepositoryPort.findAll();
            logger.info("Se encontraron {} usuarios", users.size());
            logger.debug("Usuarios obtenidos: {}", users);
            return users;
        } catch (Exception e) {
            logger.error("Error al obtener todos los usuarios", e);
            throw e;
        }
    }

    @Override
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
                logger.debug("Detalles del usuario: {}", user.get());
            } else {
                logger.warn("No se encontró usuario con ID: {}", id);
            }

            return user;
        } catch (Exception e) {
            logger.error("Error al buscar usuario con ID: {}", id, e);
            throw e;
        }
    }

    @Override
    public User save(User user) {
        logger.info("Guardando usuario");

        if (user == null) {
            logger.warn("Se intentó guardar un usuario null");
            throw new IllegalArgumentException("Usuario no puede ser null");
        }

        try {
            logger.debug("Datos del usuario a guardar: {}", user);
            User savedUser = userRepositoryPort.save(user);
            logger.info("Usuario guardado exitosamente con ID: {}", savedUser.getId());
            logger.debug("Usuario guardado: {}", savedUser);
            return savedUser;
        } catch (Exception e) {
            logger.error("Error al guardar el usuario: {}", user, e);
            throw e;
        }
    }

    @Override
    public User findByEmail(String email) {
        logger.info("Buscando usuario por email: {}", email);

        if (email == null || email.trim().isEmpty()) {
            logger.warn("Se intentó buscar un usuario con email null o vacío");
            return null;
        }

        try {
            User user = userRepositoryPort.findByEmail(email.toLowerCase());

            if (user != null) {
                logger.info("Usuario encontrado con email: {}", email);
                logger.debug("Detalles del usuario: {}", user);
            } else {
                logger.warn("No se encontró usuario con email: {}", email);
            }

            return user; // ✅ CORREGIDO: Retornar el usuario encontrado o null
        } catch (Exception e) {
            logger.error("Error al buscar usuario por email: {}", email, e);
            throw e;
        }
    }

    @Override
    public void deleteById(Long id) {
        logger.info("Eliminando usuario con ID: {}", id);

        if (id == null) {
            logger.warn("Se intentó eliminar un usuario con ID null");
            throw new IllegalArgumentException("ID no puede ser null");
        }

        try {
            // Verificar si existe antes de eliminar
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
            throw e;
        }
    }

    @Override
    public User registrarUsuario(String email, String password, String firstName, String lastName, String userName) {
        logger.info("Registrando nuevo usuario: {}", email);

        // Validaciones de entrada
        if (email == null || email.trim().isEmpty()) {
            logger.warn("Intento de registro con email vacío");
            throw new IllegalArgumentException("Email es requerido");
        }

        if (password == null || password.trim().isEmpty()) {
            logger.warn("Intento de registro con password vacío");
            throw new IllegalArgumentException("Password es requerido");
        }

        if (firstName == null || firstName.trim().isEmpty()) {
            logger.warn("Intento de registro con firstName vacío");
            throw new IllegalArgumentException("Nombre es requerido");
        }

        if (lastName == null || lastName.trim().isEmpty()) {
            logger.warn("Intento de registro con lastName vacío");
            throw new IllegalArgumentException("Apellido es requerido");
        }

        try {
            // Verificar si el usuario ya existe
            User existingUser = userRepositoryPort.findByEmail(email.toLowerCase());
            if (existingUser != null) {  // ✅ CORREGIDO: Verificar null correctamente
                logger.warn("Intento de registro con email ya existente: {}", email);
                throw new IllegalArgumentException("Ya existe un usuario con el email: " + email);
            }
            String hashedPassword = passwordEncoder.encode(password);
            // Crear nuevo usuario
            User newUser = new User();
            newUser.setEmail(email.trim().toLowerCase());
            newUser.setFirstName(firstName.trim());
            newUser.setLastName(lastName.trim());
            newUser.setUsername(userName != null ? userName.trim() : email.trim());
            newUser.setPassword(hashedPassword); // En producción: hashear la contraseña

            logger.debug("Datos del nuevo usuario a registrar: email={}, firstName={}, lastName={}, username={}",
                    newUser.getEmail(), newUser.getFirstName(), newUser.getLastName(), newUser.getUsername());

            // Guardar usuario
            User savedUser = userRepositoryPort.save(newUser);
            logger.info("Usuario registrado exitosamente con ID: {} y email: {}",
                    savedUser.getId(), savedUser.getEmail());

            return savedUser;

        } catch (Exception e) {
            logger.error("Error al registrar usuario con email: {}", email, e);
            throw new RuntimeException("Error al registrar usuario: " + e.getMessage(), e);
        }
    }

    // Agregar este método alternativo a tu UserServiceImpl
    public Long getCurrentUserIdFromSession() {
        try {
            // Obtener desde la sesión de Vaadin
            if (VaadinSession.getCurrent() != null) {
                String userEmail = (String) VaadinSession.getCurrent().getAttribute("userEmail");

                if (userEmail != null) {
                    User currentUser = findByEmail(userEmail);
                    return currentUser != null ? currentUser.getId() : null;
                }
            }

            return null;
        } catch (Exception e) {
            logger.error("Error al obtener el usuario actual desde sesión Vaadin", e);
            return null;
        }
    }

    // Método mejorado que intenta ambos enfoques
    public Long getCurrentUserId() {
        try {
            // Primero intentar desde Vaadin Session
            Long userIdFromSession = getCurrentUserIdFromSession();
            if (userIdFromSession != null) {
                return userIdFromSession;
            }

            // Si falla, intentar desde Spring Security Context
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            logger.info("getCurrentUser");
            logger.info("authentication: " + (authentication != null ? authentication.getName() : "null"));

            if (authentication != null && authentication.isAuthenticated()
                    && !authentication.getName().equals("anonymousUser")) {
                String userEmail = authentication.getName();
                User currentUser = findByEmail(userEmail);
                return currentUser != null ? currentUser.getId() : null;
            }

            return null;
        } catch (Exception e) {
            logger.error("Error al obtener el usuario actual", e);
            return null;
        }
    }
}