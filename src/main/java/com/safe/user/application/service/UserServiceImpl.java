package com.safe.user.application.service;

import com.safe.user.domain.model.entity.User;
import com.safe.user.infrastructure.adapters.output.persistence.repository.UserRepository;
import com.safe.user.infrastructure.persistence.port.UserService;
import com.vaadin.flow.server.VaadinSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Qualifier("original")
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final GeografiaService geografiaService;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           GeografiaService geografiaService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.geografiaService = geografiaService;
    }

    // ================================
    // MÉTODOS DE REGISTRO
    // ================================

    @Override
    @Transactional
    public User registrarUsuarioConDatosGeograficos(String email,
                                                    String password,
                                                    String firstName,
                                                    String lastName,
                                                    String username,
                                                    Integer provinciaId,
                                                    Integer municipioId,
                                                    Integer localidadId) {

        validateRegistrationInputs(email, password, firstName, lastName, username);

        try {
            if (userRepository.existsByEmail(email.trim())) {
                throw new IllegalArgumentException("Ya existe un usuario con este email: " + email);
            }

            if (userRepository.existsByUsername(username.trim())) {
                throw new IllegalArgumentException("Ya existe un usuario con este username: " + username);
            }

            if (provinciaId != null || municipioId != null || localidadId != null) {
                if (geografiaService != null && !validarJerarquiaGeografica(provinciaId, municipioId, localidadId)) {
                    throw new IllegalArgumentException("La combinación de Provincia, Municipio y Localidad no es válida - 1");
                }
            }

            User nuevoUsuario = new User();
            nuevoUsuario.setEmail(email.trim().toLowerCase());
            nuevoUsuario.setUsername(username.trim());
            nuevoUsuario.setFirstName(firstName.trim());
            nuevoUsuario.setLastName(lastName.trim());
            nuevoUsuario.setPassword(passwordEncoder.encode(password));
            nuevoUsuario.setProvinciaId(provinciaId);
            nuevoUsuario.setMunicipioId(municipioId);
            nuevoUsuario.setLocalidadId(localidadId);
            // Remover campos de Spring Security que no existen en tu entidad

            User usuarioGuardado = userRepository.save(nuevoUsuario);
            logger.info("Usuario registrado exitosamente: {}", usuarioGuardado.getEmail());

            return usuarioGuardado;

        } catch (Exception e) {
            logger.error("Error al registrar usuario: {}", e.getMessage(), e);
            throw new RuntimeException("Error al registrar usuario: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public User registrarUsuario(String email, String password, String firstName, String lastName, String username) {
        return registrarUsuarioConDatosGeograficos(email, password, firstName, lastName, username, null, null, null);
    }

    // ================================
    // MÉTODOS DE CONSULTA
    // ================================

    @Override
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        return userRepository.findByEmail(email.trim().toLowerCase()).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        return userRepository.findByUsername(username.trim()).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        try {
            return userRepository.findById(id);
        } catch (Exception e) {
            logger.error("Error al buscar usuario por ID: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        try {
            return userRepository.findAll();
        } catch (Exception e) {
            logger.error("Error al obtener todos los usuarios: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findByNombreContaining(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return userRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                    nombre.trim(), nombre.trim());
        } catch (Exception e) {
            logger.error("Error al buscar usuarios por nombre: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findByProvinciaId(Integer provinciaId) {
        if (provinciaId == null) {
            return new ArrayList<>();
        }
        try {
            return userRepository.findByProvinciaId(provinciaId);
        } catch (Exception e) {
            logger.error("Error al buscar usuarios por provincia: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findByMunicipioId(Integer municipioId) {
        if (municipioId == null) {
            return new ArrayList<>();
        }
        try {
            return userRepository.findByMunicipioId(municipioId);
        } catch (Exception e) {
            logger.error("Error al buscar usuarios por municipio: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findByLocalidadId(Integer localidadId) {
        if (localidadId == null) {
            return new ArrayList<>();
        }
        try {
            return userRepository.findByLocalidadId(localidadId);
        } catch (Exception e) {
            logger.error("Error al buscar usuarios por localidad: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    // ================================
    // MÉTODOS DE VERIFICACIÓN
    // ================================

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        try {
            return userRepository.existsByEmail(email.trim().toLowerCase());
        } catch (Exception e) {
            logger.error("Error al verificar existencia de email: {}", e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        try {
            return userRepository.existsByUsername(username.trim());
        } catch (Exception e) {
            logger.error("Error al verificar existencia de username: {}", e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUserEnabled(Long userId) {
        if (userId == null) {
            return false;
        }
        // Como no tienes campo enabled, asumimos que todos los usuarios están habilitados
        return findById(userId).isPresent();
    }

    // ================================
    // MÉTODOS DE ACTUALIZACIÓN
    // ================================

    @Override
    @Transactional
    public User actualizarInformacionBasica(Long userId, String firstName, String lastName, String email) {
        if (userId == null) {
            throw new IllegalArgumentException("ID de usuario es obligatorio");
        }

        User usuario = findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + userId));

        if (email != null && !email.equals(usuario.getEmail()) && existsByEmail(email)) {
            throw new IllegalArgumentException("El email ya está en uso por otro usuario");
        }

        if (firstName != null && !firstName.trim().isEmpty()) {
            usuario.setFirstName(firstName.trim());
        }
        if (lastName != null && !lastName.trim().isEmpty()) {
            usuario.setLastName(lastName.trim());
        }
        if (email != null && !email.trim().isEmpty()) {
            usuario.setEmail(email.trim().toLowerCase());
        }

        return userRepository.save(usuario);
    }

    @Override
    @Transactional
    public User actualizarDatosGeograficos(Long userId, Integer provinciaId, Integer municipioId, Integer localidadId) {
        if (userId == null) {
            throw new IllegalArgumentException("ID de usuario es obligatorio");
        }

        User usuario = findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + userId));

        if (!validarJerarquiaGeografica(provinciaId, municipioId, localidadId)) {
            throw new IllegalArgumentException("La combinación de Provincia, Municipio y Localidad no es válida - 2");
        }

        usuario.setProvinciaId(provinciaId);
        usuario.setMunicipioId(municipioId);
        usuario.setLocalidadId(localidadId);

        return userRepository.save(usuario);
    }

    @Override
    @Transactional
    public boolean cambiarPassword(Long userId, String currentPassword, String newPassword) {
        if (userId == null || currentPassword == null || newPassword == null) {
            return false;
        }

        User usuario = findById(userId).orElse(null);
        if (usuario == null) {
            return false;
        }

        if (!passwordEncoder.matches(currentPassword, usuario.getPassword())) {
            return false;
        }

        if (!validarFortalezaPassword(newPassword)) {
            throw new IllegalArgumentException("La nueva contraseña no cumple los requisitos de seguridad");
        }

        usuario.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(usuario);
        return true;
    }

    @Override
    @Transactional
    public User resetearPassword(Long userId, String newPassword) {
        if (userId == null || newPassword == null) {
            throw new IllegalArgumentException("ID de usuario y nueva contraseña son obligatorios");
        }

        User usuario = findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + userId));

        if (!validarFortalezaPassword(newPassword)) {
            throw new IllegalArgumentException("La nueva contraseña no cumple los requisitos de seguridad");
        }

        usuario.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(usuario);
    }

    // ================================
    // MÉTODOS DE ESTADO
    // ================================

    @Override
    @Transactional
    public User cambiarEstadoUsuario(Long userId, boolean enabled) {
        if (userId == null) {
            throw new IllegalArgumentException("ID de usuario es obligatorio");
        }

        User usuario = findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + userId));

        // Como no tienes campo enabled, este método no hace nada pero mantiene la interfaz
        logger.info("Método cambiarEstadoUsuario llamado para usuario {} - Sin campo enabled en entidad", userId);
        return usuario;
    }

    @Override
    @Transactional
    public User cambiarBloqueoUsuario(Long userId, boolean locked) {
        if (userId == null) {
            throw new IllegalArgumentException("ID de usuario es obligatorio");
        }

        User usuario = findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + userId));

        // Como no tienes campo accountNonLocked, este método no hace nada pero mantiene la interfaz
        logger.info("Método cambiarBloqueoUsuario llamado para usuario {} - Sin campo accountNonLocked en entidad", userId);
        return usuario;
    }

    // ================================
    // MÉTODOS DE ELIMINACIÓN
    // ================================

    @Override
    @Transactional
    public void eliminarUsuario(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("ID de usuario es obligatorio");
        }

        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("Usuario no encontrado con ID: " + userId);
        }

        try {
            userRepository.deleteById(userId);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar usuario: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void eliminarUsuarioPorEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email es obligatorio");
        }

        User usuario = findByEmail(email);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado con email: " + email);
        }

        eliminarUsuario(usuario.getId());
    }

    @Override
    @Transactional
    public User eliminarUsuarioLogico(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("ID de usuario es obligatorio");
        }

        User usuario = findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + userId));

        // Como no tienes campo enabled, simplemente logueamos la operación
        logger.info("Eliminación lógica solicitada para usuario {} - Sin campo enabled en entidad", userId);
        return usuario;
    }

    // ================================
    // MÉTODOS DE INFORMACIÓN GEOGRÁFICA
    // ================================

    @Override
    @Transactional(readOnly = true)
    public String getUbicacionCompleta(User user) {
        if (user == null || geografiaService == null) {
            return "Ubicación no disponible";
        }

        try {
            if (user.getLocalidadId() != null) {
                return geografiaService.getJerarquiaCompleta(user.getLocalidadId());
            } else if (user.getMunicipioId() != null) {
                return geografiaService.getMunicipioById(user.getMunicipioId())
                        .map(municipio -> {
                            return geografiaService.getProvinciaById(municipio.getProvinciaId())
                                    .map(provincia -> provincia.getNombre() + " > " + municipio.getNombre())
                                    .orElse(municipio.getNombre());
                        })
                        .orElse("Municipio no encontrado");
            } else if (user.getProvinciaId() != null) {
                return geografiaService.getProvinciaById(user.getProvinciaId())
                        .map(provincia -> provincia.getNombre())
                        .orElse("Provincia no encontrada");
            }

            return "Ubicación no especificada";

        } catch (Exception e) {
            logger.error("Error al obtener ubicación: {}", e.getMessage());
            return "Error al obtener ubicación";
        }
    }

    @Override
    @Transactional(readOnly = true)
    public String getUbicacionCompletaPorId(Long userId) {
        if (userId == null) {
            return "Usuario no especificado";
        }

        User usuario = findById(userId).orElse(null);
        return getUbicacionCompleta(usuario);
    }

    // ================================
    // MÉTODOS DE VALIDACIÓN
    // ================================

    @Override
    public boolean validarFormatoEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    @Override
    public boolean validarFortalezaPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);

        return hasUpper && hasLower && hasDigit;
    }

    @Override
    public boolean validarFormatoUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }

        String usernameRegex = "^[a-zA-Z0-9_]{3,20}$";
        return username.matches(usernameRegex);
    }

    // ================================
    // MÉTODOS DE ESTADÍSTICAS
    // ================================

    @Override
    @Transactional(readOnly = true)
    public long contarTotalUsuarios() {
        try {
            return userRepository.count();
        } catch (Exception e) {
            logger.error("Error al contar usuarios: {}", e.getMessage());
            return 0;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long contarUsuariosActivos() {
        try {
            // Como no tienes campo enabled, contamos todos los usuarios
            return userRepository.count();
        } catch (Exception e) {
            logger.error("Error al contar usuarios: {}", e.getMessage());
            return 0;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long contarUsuariosPorProvincia(Integer provinciaId) {
        if (provinciaId == null) {
            return 0;
        }
        try {
            return userRepository.countByProvinciaId(provinciaId);
        } catch (Exception e) {
            logger.error("Error al contar usuarios por provincia: {}", e.getMessage());
            return 0;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> obtenerEstadisticasPorUbicacion() {
        Map<String, Long> estadisticas = new HashMap<>();

        try {
            estadisticas.put("total_usuarios", contarTotalUsuarios());
            estadisticas.put("usuarios_activos", contarUsuariosActivos()); // Será igual a total_usuarios
            estadisticas.put("usuarios_con_ubicacion", userRepository.countByProvinciaIdIsNotNull());
        } catch (Exception e) {
            logger.error("Error al obtener estadísticas: {}", e.getMessage());
        }

        return estadisticas;
    }

    // ================================
    // MÉTODOS DE AUTENTICACIÓN
    // ================================

    @Override
    @Transactional(readOnly = true)
    public User autenticar(String email, String password) {
        if (email == null || password == null) {
            return null;
        }

        User usuario = findByEmail(email);
        if (usuario == null) {
            return null;
        }

        if (passwordEncoder.matches(password, usuario.getPassword())) {
            return usuario;
        }

        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean verificarCredenciales(String email, String password) {
        return autenticar(email, password) != null;
    }

    // ================================
    // MÉTODOS AUXILIARES
    // ================================

    private void validateRegistrationInputs(String email, String password, String firstName, String lastName, String username) {
        if (userRepository == null) {
            throw new RuntimeException("UserRepository no está disponible");
        }

        if (passwordEncoder == null) {
            throw new RuntimeException("PasswordEncoder no está disponible");
        }

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email es obligatorio");
        }

        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Contraseña es obligatoria");
        }

        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("Nombre es obligatorio");
        }

        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Apellido es obligatorio");
        }

        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username es obligatorio");
        }
    }

    private boolean validarJerarquiaGeografica(Integer provinciaId, Integer municipioId, Integer localidadId) {
        if (geografiaService == null || provinciaId == 1) {
            return true;
        }

        try {
            if (localidadId != null && municipioId == null) {
                return false;
            }

            if (municipioId != null && provinciaId == null) {
                return false;
            }

            return geografiaService.validarJerarquia(provinciaId, municipioId, localidadId);

        } catch (Exception e) {
            logger.error("Error al validar jerarquía geográfica: {}", e.getMessage());
            return false;
        }
    }

    // ================================
    // MÉTODOS DE SESIÓN
    // ================================

    public Long getCurrentUserIdFromSession() {
        try {
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

    public Long getCurrentUserId() {
        try {
            Long userIdFromSession = getCurrentUserIdFromSession();
            if (userIdFromSession != null) {
                return userIdFromSession;
            }

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
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

    // ================================
    // IMPLEMENTACIÓN DE UserServicePort
    // ================================


    public List<User> getAllUsers() {
        return findAll();
    }

    public Optional<User> getUserById(Long id) {
        return findById(id);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteById(Long id) {
        eliminarUsuario(id);
    }

    // Los métodos findByEmail, findByUsername ya están implementados arriba

    public User findUserById(Long id) {
        if (id == null) {
            return null;
        }
        try {
            return userRepository.findById(id).orElse(null);
        } catch (Exception e) {
            logger.error("Error al buscar usuario por ID: {}", e.getMessage());
            return null;
        }
    }

    // Los métodos de registro ya están implementados arriba


    public List<User> findUsersByProvincia(Integer provinciaId) {
        return findByProvinciaId(provinciaId);
    }

    public List<User> findUsersByMunicipio(Integer municipioId) {
        return findByMunicipioId(municipioId);
    }

    public List<User> findUsersByLocalidad(Integer localidadId) {
        return findByLocalidadId(localidadId);
    }
}