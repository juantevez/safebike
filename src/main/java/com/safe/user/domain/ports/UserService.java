package com.safe.user.domain.ports;

import com.safe.user.domain.model.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz que define los contratos para el servicio de usuarios
 * Siguiendo los principios de Clean Architecture (Puerto)
 */
public interface UserService {

    // ✅ MÉTODOS DE REGISTRO

    /**
     * Registra un nuevo usuario con datos geográficos completos
     * @param email Email del usuario (único)
     * @param password Contraseña en texto plano (será encriptada)
     * @param firstName Nombre del usuario
     * @param lastName Apellido del usuario
     * @param username Nombre de usuario (único)
     * @param provinciaId ID de la provincia
     * @param municipioId ID del municipio
     * @param localidadId ID de la localidad
     * @return Usuario creado y guardado
     * @throws IllegalArgumentException si los datos son inválidos
     * @throws RuntimeException si hay error en el registro
     */
    User registrarUsuarioConDatosGeograficos(String email,
                                             String password,
                                             String firstName,
                                             String lastName,
                                             String username,
                                             Integer provinciaId,
                                             Integer municipioId,
                                             Integer localidadId);

    /**
     * Registra un nuevo usuario sin datos geográficos
     * @param email Email del usuario (único)
     * @param password Contraseña en texto plano (será encriptada)
     * @param firstName Nombre del usuario
     * @param lastName Apellido del usuario
     * @param username Nombre de usuario (único)
     * @return Usuario creado y guardado
     * @throws IllegalArgumentException si los datos son inválidos
     * @throws RuntimeException si hay error en el registro
     */
    User registrarUsuario(String email,
                          String password,
                          String firstName,
                          String lastName,
                          String username);

    // ✅ MÉTODOS DE CONSULTA

    /**
     * Busca un usuario por email
     * @param email Email a buscar
     * @return Usuario encontrado o null si no existe
     */
    User findByEmail(String email);

    /**
     * Busca un usuario por username
     * @param username Username a buscar
     * @return Usuario encontrado o null si no existe
     */
    User findByUsername(String username);

    /**
     * Busca un usuario por ID
     * @param id ID del usuario
     * @return Optional con el usuario si existe
     */
    Optional<User> findById(Long id);

    /**
     * Obtiene todos los usuarios (con paginación si es necesario)
     * @return Lista de todos los usuarios
     */
    List<User> findAll();

    /**
     * Busca usuarios por nombre o apellido (búsqueda parcial)
     * @param nombre Texto a buscar en nombre o apellido
     * @return Lista de usuarios que coinciden
     */
    List<User> findByNombreContaining(String nombre);

    /**
     * Busca usuarios por provincia
     * @param provinciaId ID de la provincia
     * @return Lista de usuarios de esa provincia
     */
    List<User> findByProvinciaId(Integer provinciaId);

    /**
     * Busca usuarios por municipio
     * @param municipioId ID del municipio
     * @return Lista de usuarios de ese municipio
     */
    List<User> findByMunicipioId(Integer municipioId);

    /**
     * Busca usuarios por localidad
     * @param localidadId ID de la localidad
     * @return Lista de usuarios de esa localidad
     */
    List<User> findByLocalidadId(Integer localidadId);

    // ✅ MÉTODOS DE VERIFICACIÓN

    /**
     * Verifica si existe un usuario con el email dado
     * @param email Email a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByEmail(String email);

    /**
     * Verifica si existe un usuario con el username dado
     * @param username Username a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByUsername(String username);


    // ✅ MÉTODOS DE ACTUALIZACIÓN

    /**
     * Actualiza la información básica de un usuario
     * @param userId ID del usuario a actualizar
     * @param firstName Nuevo nombre
     * @param lastName Nuevo apellido
     * @param email Nuevo email
     * @return Usuario actualizado
     * @throws IllegalArgumentException si los datos son inválidos
     * @throws RuntimeException si hay error en la actualización
     */
    User actualizarInformacionBasica(Long userId,
                                     String firstName,
                                     String lastName,
                                     String email);

    /**
     * Actualiza los datos geográficos de un usuario
     * @param userId ID del usuario
     * @param provinciaId Nueva provincia
     * @param municipioId Nuevo municipio
     * @param localidadId Nueva localidad
     * @return Usuario actualizado
     * @throws IllegalArgumentException si la jerarquía geográfica es inválida
     */
    User actualizarDatosGeograficos(Long userId,
                                    Integer provinciaId,
                                    Integer municipioId,
                                    Integer localidadId);

    /**
     * Cambia la contraseña de un usuario
     * @param userId ID del usuario
     * @param currentPassword Contraseña actual
     * @param newPassword Nueva contraseña
     * @return true si el cambio fue exitoso
     * @throws IllegalArgumentException si la contraseña actual es incorrecta
     */
    boolean cambiarPassword(Long userId, String currentPassword, String newPassword);

    /**
     * Resetea la contraseña de un usuario (solo para administradores)
     * @param userId ID del usuario
     * @param newPassword Nueva contraseña
     * @return Usuario actualizado
     */
    User resetearPassword(Long userId, String newPassword);


    // ✅ MÉTODOS DE ELIMINACIÓN

    /**
     * Elimina un usuario por ID (eliminación física)
     * @param userId ID del usuario a eliminar
     * @throws RuntimeException si hay error en la eliminación
     */
    void eliminarUsuario(Long userId);

    /**
     * Elimina un usuario por email (eliminación física)
     * @param email Email del usuario a eliminar
     * @throws RuntimeException si hay error en la eliminación
     */
    void eliminarUsuarioPorEmail(String email);

    /**
     * Realiza eliminación lógica (marca como eliminado sin borrar)
     * @param userId ID del usuario
     * @return Usuario marcado como eliminado
     */
    User eliminarUsuarioLogico(Long userId);

    // ✅ MÉTODOS DE INFORMACIÓN GEOGRÁFICA

    /**
     * Obtiene la ubicación completa de un usuario (Provincia > Municipio > Localidad)
     * @param user Usuario del cual obtener la ubicación
     * @return String con la jerarquía geográfica completa
     */
    String getUbicacionCompleta(User user);

    /**
     * Obtiene la ubicación completa por ID de usuario
     * @param userId ID del usuario
     * @return String con la jerarquía geográfica completa
     */
    String getUbicacionCompletaPorId(Long userId);

    // ✅ MÉTODOS DE VALIDACIÓN

    /**
     * Valida si un email tiene formato correcto
     * @param email Email a validar
     * @return true si es válido, false en caso contrario
     */
    boolean validarFormatoEmail(String email);

    /**
     * Valida si una contraseña cumple los requisitos de seguridad
     * @param password Contraseña a validar
     * @return true si es válida, false en caso contrario
     */
    boolean validarFortalezaPassword(String password);

    /**
     * Valida si un username cumple los requisitos
     * @param username Username a validar
     * @return true si es válido, false en caso contrario
     */
    boolean validarFormatoUsername(String username);

    // ✅ MÉTODOS DE ESTADÍSTICAS

    /**
     * Cuenta el total de usuarios registrados
     * @return Número total de usuarios
     */
    long contarTotalUsuarios();

    /**
     * Cuenta usuarios activos
     * @return Número de usuarios habilitados
     */
    long contarUsuariosActivos();

    /**
     * Cuenta usuarios por provincia
     * @param provinciaId ID de la provincia
     * @return Número de usuarios en esa provincia
     */
    long contarUsuariosPorProvincia(Integer provinciaId);

    /**
     * Obtiene estadísticas de usuarios por ubicación geográfica
     * @return Mapa con estadísticas por provincia/municipio/localidad
     */
    java.util.Map<String, Long> obtenerEstadisticasPorUbicacion();

    // ✅ MÉTODOS DE AUTENTICACIÓN (SI SE NECESITAN)

    /**
     * Autentica un usuario por email y contraseña
     * @param email Email del usuario
     * @param password Contraseña en texto plano
     * @return Usuario autenticado o null si las credenciales son incorrectas
     */
    User autenticar(String email, String password);

    /**
     * Verifica si las credenciales son válidas
     * @param email Email del usuario
     * @param password Contraseña en texto plano
     * @return true si las credenciales son correctas
     */
    boolean verificarCredenciales(String email, String password);
}