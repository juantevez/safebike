package com.safe.user.application.service;

import com.safe.user.domain.model.entity.User;
import com.safe.user.domain.ports.UserRepository;
import com.safe.user.domain.ports.UserService;
import com.vaadin.flow.server.VaadinSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Qualifier("original")
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    // ✅ INYECCIÓN POR CONSTRUCTOR - DECLARAR COMO FINAL
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final GeografiaService geografiaService; // Si lo necesitas aquí

    // ✅ CONSTRUCTOR LIMPIO - Spring inyecta automáticamente
    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           GeografiaService geografiaService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.geografiaService = geografiaService;
    }

    // ✅ MÉTODO PRINCIPAL PARA REGISTRO CON DATOS GEOGRÁFICOS
    @Transactional
    public User registrarUsuarioConDatosGeograficos(String email,
                                                    String password,
                                                    String firstName,
                                                    String lastName,
                                                    String username,
                                                    Integer provinciaId,
                                                    Integer municipioId,
                                                    Integer localidadId) {

        // ✅ VERIFICACIONES DE NULL
        if (userRepository == null) {
            throw new RuntimeException("UserRepository no está disponible");
        }

        if (passwordEncoder == null) {
            throw new RuntimeException("PasswordEncoder no está disponible");
        }

        // ✅ VALIDACIONES DE ENTRADA
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

        try {
            // ✅ VERIFICAR QUE EL EMAIL NO EXISTA
            if (userRepository.existsByEmail(email.trim())) {
                throw new IllegalArgumentException("Ya existe un usuario con este email: " + email);
            }

            // ✅ VERIFICAR QUE EL USERNAME NO EXISTA
            if (userRepository.existsByUsername(username.trim())) {
                throw new IllegalArgumentException("Ya existe un usuario con este username: " + username);
            }

            // ✅ VALIDAR JERARQUÍA GEOGRÁFICA SI SE PROPORCIONAN LOS DATOS
            if (provinciaId != null || municipioId != null || localidadId != null) {
                if (geografiaService != null && !validarJerarquiaGeografica(provinciaId, municipioId, localidadId)) {
                    throw new IllegalArgumentException("La combinación de Provincia, Municipio y Localidad no es válida");
                }
            }

            // ✅ CREAR NUEVO USUARIO
            User nuevoUsuario = new User();
            nuevoUsuario.setEmail(email.trim().toLowerCase());
            nuevoUsuario.setUsername(username.trim());
            nuevoUsuario.setFirstName(firstName.trim());
            nuevoUsuario.setLastName(lastName.trim());

            // ✅ ENCRIPTAR CONTRASEÑA
            nuevoUsuario.setPassword(passwordEncoder.encode(password));

            // ✅ ASIGNAR DATOS GEOGRÁFICOS
            nuevoUsuario.setProvinciaId(provinciaId);
            nuevoUsuario.setMunicipioId(municipioId);
            nuevoUsuario.setLocalidadId(localidadId);

            // ✅ GUARDAR EN LA BASE DE DATOS
            User usuarioGuardado = userRepository.save(nuevoUsuario);

            // ✅ LOG DE ÉXITO
            System.out.println("✅ Usuario registrado exitosamente: " + usuarioGuardado.getEmail());

            return usuarioGuardado;

        } catch (Exception e) {
            // ✅ LOG DEL ERROR
            System.err.println("❌ Error al registrar usuario: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al registrar usuario: " + e.getMessage(), e);
        }
    }

    // ✅ MÉTODO AUXILIAR PARA VALIDAR JERARQUÍA GEOGRÁFICA
    private boolean validarJerarquiaGeografica(Integer provinciaId, Integer municipioId, Integer localidadId) {
        if (geografiaService == null) {
            // Si no hay servicio de geografía, no validamos
            return true;
        }

        try {
            // Si se proporciona localidad, debe haber municipio
            if (localidadId != null && municipioId == null) {
                return false;
            }

            // Si se proporciona municipio, debe haber provincia
            if (municipioId != null && provinciaId == null) {
                return false;
            }

            // Validar que las entidades existan y estén relacionadas correctamente
            return geografiaService.validarJerarquia(provinciaId, municipioId, localidadId);

        } catch (Exception e) {
            System.err.println("⚠️ Error al validar jerarquía geográfica: " + e.getMessage());
            return false;
        }
    }

    // ✅ MÉTODO ALTERNATIVO SIN DATOS GEOGRÁFICOS
    @Transactional
    public User registrarUsuario(String email, String password, String firstName, String lastName, String username) {
        return registrarUsuarioConDatosGeograficos(email, password, firstName, lastName, username, null, null, null);
    }

    // ✅ MÉTODOS DE CONSULTA
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        return userRepository.findByEmail(email.trim().toLowerCase()).orElse(null);
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        return userRepository.findByUsername(username.trim()).orElse(null);
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        if (userRepository == null) {
            throw new RuntimeException("UserRepository no está disponible");
        }

        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        try {
            return userRepository.existsByEmail(email.trim().toLowerCase());
        } catch (Exception e) {
            System.err.println("❌ Error al verificar existencia de email: " + e.getMessage());
            return false;
        }
    }

    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        if (userRepository == null) {
            throw new RuntimeException("UserRepository no está disponible");
        }

        if (username == null || username.trim().isEmpty()) {
            return false;
        }

        try {
            return userRepository.existsByUsername(username.trim());
        } catch (Exception e) {
            System.err.println("❌ Error al verificar existencia de username: " + e.getMessage());
            return false;
        }
    }

    // ✅ MÉTODO PARA OBTENER INFORMACIÓN GEOGRÁFICA COMPLETA DEL USUARIO
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
            System.err.println("⚠️ Error al obtener ubicación: " + e.getMessage());
            // ✅ IMPLEMENTACIÓN DE MÉTODOS DE CONSULTA
        }
        return "";
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
            System.err.println("❌ Error al buscar usuario por ID: " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        try {
            return userRepository.findAll();
        } catch (Exception e) {
            System.err.println("❌ Error al obtener todos los usuarios: " + e.getMessage());
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
            String searchTerm = "%" + nombre.trim().toLowerCase() + "%";
            return userRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                    nombre.trim(), nombre.trim());
        } catch (Exception e) {
            System.err.println("❌ Error al buscar usuarios por nombre: " + e.getMessage());
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
            System.err.println("❌ Error al buscar usuarios por provincia: " + e.getMessage());
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
            System.err.println("❌ Error al buscar usuarios por municipio: " + e.getMessage());
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
            System.err.println("❌ Error al buscar usuarios por localidad: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    public User actualizarInformacionBasica(Long userId, String firstName, String lastName, String email) {
        if (userId == null) {
            throw new IllegalArgumentException("ID de usuario es obligatorio");
        }

        User usuario = findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + userId));

        // Validar que el nuevo email no esté en uso por otro usuario
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

        // Validar jerarquía geográfica
        if (!validarJerarquiaGeografica(provinciaId, municipioId, localidadId)) {
            throw new IllegalArgumentException("La combinación de Provincia, Municipio y Localidad no es válida");
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

        // Verificar contraseña actual
        if (!passwordEncoder.matches(currentPassword, usuario.getPassword())) {
            return false;
        }

        // Validar nueva contraseña
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


    // ✅ MÉTODOS DE ELIMINACIÓN

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


        return userRepository.save(usuario);
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

    // ✅ MÉTODOS DE VALIDACIÓN

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

        // Al menos una mayúscula, una minúscula, un número
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

        // Solo letras, números y guiones bajos, entre 3 y 20 caracteres
        String usernameRegex = "^[a-zA-Z0-9_]{3,20}$";
        return username.matches(usernameRegex);
    }

    // ✅ MÉTODOS DE ESTADÍSTICAS

    @Override
    @Transactional(readOnly = true)
    public long contarTotalUsuarios() {
        try {
            return userRepository.count();
        } catch (Exception e) {
            System.err.println("❌ Error al contar usuarios: " + e.getMessage());
            return 0;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long contarUsuariosActivos() {
        try {
            return userRepository.countByEnabledTrue();
        } catch (Exception e) {
            System.err.println("❌ Error al contar usuarios activos: " + e.getMessage());
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
            System.err.println("❌ Error al contar usuarios por provincia: " + e.getMessage());
            return 0;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.Map<String, Long> obtenerEstadisticasPorUbicacion() {
        java.util.Map<String, Long> estadisticas = new java.util.HashMap<>();

        try {
            estadisticas.put("total_usuarios", contarTotalUsuarios());
            estadisticas.put("usuarios_activos", contarUsuariosActivos());
            estadisticas.put("usuarios_con_ubicacion", userRepository.countByProvinciaIdIsNotNull());

            // Agregar más estadísticas según necesites

        } catch (Exception e) {
            System.err.println("❌ Error al obtener estadísticas: " + e.getMessage());
        }

        return estadisticas;
    }

    // ✅ MÉTODOS DE AUTENTICACIÓN

    @Override
    @Transactional(readOnly = true)
    public User autenticar(String email, String password) {
        if (email == null || password == null) {
            return null;
        }

        User usuario = findByEmail(email);
        if (usuario == null ) {
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

