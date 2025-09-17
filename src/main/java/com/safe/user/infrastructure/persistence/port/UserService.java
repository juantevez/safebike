package com.safe.user.infrastructure.persistence.port;

import com.safe.user.domain.model.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.Map;

/**
 * Interfaz unificada para el servicio de usuarios
 * Combina todas las operaciones necesarias en una sola interfaz
 */
public interface UserService {

    // ================================
    // MÉTODOS BÁSICOS CRUD
    // ================================

    List<User> findAll();
    Optional<User> findById(Long id);
    User save(User user);
    void deleteById(Long id);

    // ================================
    // MÉTODOS DE BÚSQUEDA
    // ================================

    User findByEmail(String email);
    User findByUsername(String username);
    List<User> findByNombreContaining(String nombre);

    // Búsquedas geográficas
    List<User> findByProvinciaId(Integer provinciaId);
    List<User> findByMunicipioId(Integer municipioId);
    List<User> findByLocalidadId(Integer localidadId);

    // ================================
    // MÉTODOS DE VERIFICACIÓN
    // ================================

    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    boolean isUserEnabled(Long userId);

    // ================================
    // MÉTODOS DE REGISTRO
    // ================================

    User registrarUsuario(String email, String password, String firstName, String lastName, String username);

    User registrarUsuarioConDatosGeograficos(String email,
                                             String password,
                                             String firstName,
                                             String lastName,
                                             String username,
                                             Integer provinciaId,
                                             Integer municipioId,
                                             Integer localidadId);

    // ================================
    // MÉTODOS DE ACTUALIZACIÓN
    // ================================

    User actualizarInformacionBasica(Long userId, String firstName, String lastName, String email);
    User actualizarDatosGeograficos(Long userId, Integer provinciaId, Integer municipioId, Integer localidadId);
    boolean cambiarPassword(Long userId, String currentPassword, String newPassword);
    User resetearPassword(Long userId, String newPassword);

    // ================================
    // MÉTODOS DE ESTADO
    // ================================

    User cambiarEstadoUsuario(Long userId, boolean enabled);
    User cambiarBloqueoUsuario(Long userId, boolean locked);

    // ================================
    // MÉTODOS DE ELIMINACIÓN
    // ================================

    void eliminarUsuario(Long userId);
    void eliminarUsuarioPorEmail(String email);
    User eliminarUsuarioLogico(Long userId);

    // ================================
    // MÉTODOS DE INFORMACIÓN GEOGRÁFICA
    // ================================

    String getUbicacionCompleta(User user);
    String getUbicacionCompletaPorId(Long userId);

    // ================================
    // MÉTODOS DE VALIDACIÓN
    // ================================

    boolean validarFormatoEmail(String email);
    boolean validarFortalezaPassword(String password);
    boolean validarFormatoUsername(String username);

    // ================================
    // MÉTODOS DE ESTADÍSTICAS
    // ================================

    long contarTotalUsuarios();
    long contarUsuariosActivos();
    long contarUsuariosPorProvincia(Integer provinciaId);
    Map<String, Long> obtenerEstadisticasPorUbicacion();

    // ================================
    // MÉTODOS DE AUTENTICACIÓN
    // ================================

    User autenticar(String email, String password);
    boolean verificarCredenciales(String email, String password);

    // ================================
    // MÉTODOS DE SESIÓN
    // ================================

    Long getCurrentUserId();
}