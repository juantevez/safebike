package com.safe.user.application.service;

import com.safe.user.domain.model.User;
import com.safe.user.domain.ports.UserRepository;
import com.safe.user.infrastructure.port.UserServicePort;
import com.vaadin.flow.server.VaadinSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service("userServiceImpl")
@Qualifier("original")
@Transactional
public class UserServiceImpl implements UserServicePort {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserServiceImpl() {}

    // ✅ IMPLEMENTAR MÉTODOS BÁSICOS CRUD QUE FALTAN
    @Override
    public List<User> getAllUsers() {
        logger.info("Obteniendo todos los usuarios");
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        logger.info("Buscando usuario por ID: {}", id);
        return userRepository.findById(id);
    }

    @Override
    public User save(User user) {
        logger.info("Guardando usuario: {}", user.getEmail());
        return userRepository.save(user);
    }

    @Override
    public void deleteById(Long id) {
        logger.info("Eliminando usuario con ID: {}", id);
        userRepository.deleteById(id);
    }

    @Override
    public User findByEmail(String email) {
        logger.info("Buscando usuario por email: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));
    }

    @Override
    public User findByUsername(String username) {
        logger.info("Buscando usuario por username: {}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con username: " + username));
    }

    @Override
    public User findById(Long id) {
        logger.info("Buscando usuario por ID: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    @Override
    public User registrarUsuario(String email, String password, String firstName, String lastName, String username) {
        logger.info("Registrando usuario: {}", email);

        // Validaciones
        validarDatosUsuario(email, password, firstName, lastName, username);

        // Verificar si ya existe
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Ya existe un usuario con el email: " + email);
        }

        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Ya existe un usuario con el username: " + username);
        }

        // Crear usuario
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setRole("USER");
        user.setCreatedAt(LocalDateTime.now());

        // Datos geográficos null
        user.setProvinciaId(null);
        user.setMunicipioId(null);
        user.setLocalidadId(null);

        User savedUser = userRepository.save(user);
        logger.info("Usuario registrado exitosamente con ID: {}", savedUser.getId());

        return savedUser;
    }

    @Override
    public User registrarUsuarioConDatosGeograficos(
            String email,
            String password,
            String firstName,
            String lastName,
            String username,
            Integer provinciaId,
            Integer municipioId,
            Integer localidadId) {

        logger.info("Registrando usuario con datos geográficos: {}", email);

        // Validaciones básicas
        validarDatosUsuario(email, password, firstName, lastName, username);

        // Validaciones geográficas opcionales
        validarDatosGeograficos(provinciaId, municipioId, localidadId);

        // Verificar si ya existe
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Ya existe un usuario con el email: " + email);
        }

        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Ya existe un usuario con el username: " + username);
        }

        // Crear usuario
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setRole("USER");
        user.setCreatedAt(LocalDateTime.now());

        // Asignar datos geográficos
        user.setProvinciaId(provinciaId);
        user.setMunicipioId(municipioId);
        user.setLocalidadId(localidadId);

        User savedUser = userRepository.save(user);

        logger.info("Usuario registrado exitosamente con ID: {} - Provincia: {}, Municipio: {}, Localidad: {}",
                savedUser.getId(), provinciaId, municipioId, localidadId);

        return savedUser;
    }

    @Override
    public User actualizarDatosGeograficos(Long userId, Integer provinciaId, Integer municipioId, Integer localidadId) {
        logger.info("Actualizando datos geográficos para usuario ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));

        // Validar datos geográficos
        validarDatosGeograficos(provinciaId, municipioId, localidadId);

        // Actualizar
        user.setProvinciaId(provinciaId);
        user.setMunicipioId(municipioId);
        user.setLocalidadId(localidadId);

        User updatedUser = userRepository.save(user);
        logger.info("Datos geográficos actualizados exitosamente para usuario ID: {}", userId);

        return updatedUser;
    }

    @Override
    public List<User> findUsersByProvincia(Integer provinciaId) {
        logger.info("Buscando usuarios por provincia ID: {}", provinciaId);

        if (provinciaId == null) {
            return Collections.emptyList();
        }

        return userRepository.findByProvinciaId(provinciaId);
    }

    @Override
    public List<User> findUsersByMunicipio(Integer municipioId) {
        logger.info("Buscando usuarios por municipio ID: {}", municipioId);

        if (municipioId == null) {
            return Collections.emptyList();
        }

        return userRepository.findByMunicipioId(municipioId);
    }

    @Override
    public List<User> findUsersByLocalidad(Integer localidadId) {
        logger.info("Buscando usuarios por localidad ID: {}", localidadId);

        if (localidadId == null) {
            return Collections.emptyList();
        }

        return userRepository.findByLocalidadId(localidadId);
    }



    // ✅ MÉTODOS PRIVADOS DE VALIDACIÓN (ya los tienes)
    private void validarDatosUsuario(String email, String password, String firstName, String lastName, String username) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El email es obligatorio");
        }

        if (!email.contains("@")) {
            throw new IllegalArgumentException("El email debe tener un formato válido");
        }

        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }

        if (password.length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
        }

        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }

        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido es obligatorio");
        }

        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("El username es obligatorio");
        }

        if (username.length() < 3) {
            throw new IllegalArgumentException("El username debe tener al menos 3 caracteres");
        }
    }

    private void validarDatosGeograficos(Integer provinciaId, Integer municipioId, Integer localidadId) {
        // Si hay municipio, debe haber provincia
        if (municipioId != null && provinciaId == null) {
            throw new IllegalArgumentException("Si seleccionas un municipio, debes seleccionar una provincia");
        }

        // Si hay localidad, debe haber municipio y provincia
        if (localidadId != null) {
            if (municipioId == null) {
                throw new IllegalArgumentException("Si seleccionas una localidad, debes seleccionar un municipio");
            }
            if (provinciaId == null) {
                throw new IllegalArgumentException("Si seleccionas una localidad, debes seleccionar una provincia");
            }
        }

        logger.debug("Validación de datos geográficos exitosa - Provincia: {}, Municipio: {}, Localidad: {}",
                provinciaId, municipioId, localidadId);
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

// ✅ TAMBIÉN NECESITAS ACTUALIZAR TU USER REPOSITORY
