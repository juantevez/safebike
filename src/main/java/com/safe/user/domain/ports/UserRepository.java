package com.safe.user.domain.ports;

import com.safe.user.domain.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // ✅ MÉTODOS BÁSICOS DE BÚSQUEDA

    /**
     * Busca usuario por email
     */
    Optional<User> findByEmail(String email);

    /**
     * Busca usuario por username
     */
    Optional<User> findByUsername(String username);

    /**
     * Verifica si existe usuario con el email dado
     */
    boolean existsByEmail(String email);

    /**
     * Verifica si existe usuario con el username dado
     */
    boolean existsByUsername(String username);

    // ✅ BÚSQUEDAS POR NOMBRE

    /**
     * Busca usuarios por nombre o apellido (case insensitive)
     */
    List<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String firstName, String lastName);

    /**
     * Busca usuarios por nombre exacto
     */
    List<User> findByFirstName(String firstName);

    /**
     * Busca usuarios por apellido exacto
     */
    List<User> findByLastName(String lastName);

    // ✅ BÚSQUEDAS GEOGRÁFICAS

    /**
     * Busca usuarios por provincia
     */
    List<User> findByProvinciaId(Integer provinciaId);

    /**
     * Busca usuarios por municipio
     */
    List<User> findByMunicipioId(Integer municipioId);

    /**
     * Busca usuarios por localidad
     */
    List<User> findByLocalidadId(Integer localidadId);

    /**
     * Busca usuarios por provincia y municipio
     */
    List<User> findByProvinciaIdAndMunicipioId(Integer provinciaId, Integer municipioId);

    /**
     * Busca usuarios por la jerarquía geográfica completa
     */
    List<User> findByProvinciaIdAndMunicipioIdAndLocalidadId(
            Integer provinciaId, Integer municipioId, Integer localidadId);

    // ✅ BÚSQUEDAS POR ESTADO

    /**
     * Busca usuarios habilitados
     */
    List<User> findByEnabledTrue();

    /**
     * Busca usuarios deshabilitados
     */
    List<User> findByEnabledFalse();

    /**
     * Busca usuarios con cuenta no bloqueada
     */
    List<User> findByAccountNonLockedTrue();

    /**
     * Busca usuarios con cuenta bloqueada
     */
    List<User> findByAccountNonLockedFalse();

    // ✅ MÉTODOS DE CONTEO

    /**
     * Cuenta usuarios habilitados
     */
    long countByEnabledTrue();

    /**
     * Cuenta usuarios deshabilitados
     */
    long countByEnabledFalse();

    /**
     * Cuenta usuarios por provincia
     */
    long countByProvinciaId(Integer provinciaId);

    /**
     * Cuenta usuarios por municipio
     */
    long countByMunicipioId(Integer municipioId);

    /**
     * Cuenta usuarios por localidad
     */
    long countByLocalidadId(Integer localidadId);

    /**
     * Cuenta usuarios que tienen ubicación definida
     */
    long countByProvinciaIdIsNotNull();

    /**
     * Cuenta usuarios sin ubicación
     */
    long countByProvinciaIdIsNull();

    // ✅ CONSULTAS PERSONALIZADAS CON @Query

    /**
     * Busca usuarios por texto en nombre, apellido o email
     */
    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    List<User> findBySearchText(@Param("searchText") String searchText);

    /**
     * Obtiene usuarios con ubicación completa (provincia, municipio y localidad)
     */
    @Query("SELECT u FROM User u WHERE u.provinciaId IS NOT NULL AND u.municipioId IS NOT NULL AND u.localidadId IS NOT NULL")
    List<User> findUsersWithCompleteLocation();

    /**
     * Obtiene usuarios sin ubicación definida
     */
    @Query("SELECT u FROM User u WHERE u.provinciaId IS NULL")
    List<User> findUsersWithoutLocation();

    /**
     * Busca usuarios por rango de IDs
     */
    @Query("SELECT u FROM User u WHERE u.id BETWEEN :startId AND :endId ORDER BY u.id")
    List<User> findByIdRange(@Param("startId") Long startId, @Param("endId") Long endId);

    /**
     * Obtiene estadísticas de usuarios por provincia
     */
    @Query("SELECT u.provinciaId, COUNT(u) FROM User u WHERE u.provinciaId IS NOT NULL GROUP BY u.provinciaId")
    List<Object[]> getUserStatsByProvincia();

    /**
     * Obtiene estadísticas de usuarios por municipio
     */
    @Query("SELECT u.municipioId, COUNT(u) FROM User u WHERE u.municipioId IS NOT NULL GROUP BY u.municipioId")
    List<Object[]> getUserStatsByMunicipio();

    /**
     * Verifica si existe usuario con email diferente al ID dado (para actualizaciones)
     */
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = :email AND u.id != :userId")
    boolean existsByEmailAndIdNot(@Param("email") String email, @Param("userId") Long userId);

    /**
     * Verifica si existe usuario con username diferente al ID dado (para actualizaciones)
     */
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.username = :username AND u.id != :userId")
    boolean existsByUsernameAndIdNot(@Param("username") String username, @Param("userId") Long userId);

    /**
     * Busca usuarios activos por ubicación geográfica
     */
    @Query("SELECT u FROM User u WHERE u.enabled = true AND " +
            "(:provinciaId IS NULL OR u.provinciaId = :provinciaId) AND " +
            "(:municipioId IS NULL OR u.municipioId = :municipioId) AND " +
            "(:localidadId IS NULL OR u.localidadId = :localidadId)")
    List<User> findActiveUsersByLocation(@Param("provinciaId") Integer provinciaId,
                                         @Param("municipioId") Integer municipioId,
                                         @Param("localidadId") Integer localidadId);

    /**
     * Obtiene el último usuario registrado
     */
    @Query("SELECT u FROM User u ORDER BY u.id DESC")
    Optional<User> findLastRegisteredUser();

    /**
     * Cuenta usuarios registrados en un rango de fechas
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate")
    long countUsersByDateRange(@Param("startDate") java.time.LocalDateTime startDate,
                               @Param("endDate") java.time.LocalDateTime endDate);

    // ✅ MÉTODOS ADICIONALES DE CONVENIENCIA

    /**
     * Busca usuarios por estado de cuenta
     */
    List<User> findByEnabledAndAccountNonLocked(boolean enabled, boolean accountNonLocked);

    /**
     * Busca usuarios habilitados por provincia
     */
    List<User> findByEnabledTrueAndProvinciaId(Integer provinciaId);

    /**
     * Busca usuarios habilitados por municipio
     */
    List<User> findByEnabledTrueAndMunicipioId(Integer municipioId);

    /**
     * Busca usuarios habilitados por localidad
     */
    List<User> findByEnabledTrueAndLocalidadId(Integer localidadId);

    /**
     * Obtiene usuarios ordenados por fecha de creación (más recientes primero)
     */
    List<User> findAllByOrderByCreatedAtDesc();

    /**
     * Obtiene usuarios ordenados por nombre y apellido
     */
    List<User> findAllByOrderByFirstNameAscLastNameAsc();

    /**
     * Busca usuarios por email que contenga el texto (búsqueda parcial)
     */
    List<User> findByEmailContainingIgnoreCase(String emailPart);

    /**
     * Busca usuarios por username que contenga el texto (búsqueda parcial)
     */
    List<User> findByUsernameContainingIgnoreCase(String usernamePart);

    // ✅ MÉTODOS DE CONTEO ADICIONALES

    /**
     * Cuenta usuarios habilitados por provincia
     */
    long countByEnabledTrueAndProvinciaId(Integer provinciaId);

    /**
     * Cuenta usuarios habilitados por municipio
     */
    long countByEnabledTrueAndMunicipioId(Integer municipioId);

    /**
     * Cuenta usuarios habilitados por localidad
     */
    long countByEnabledTrueAndLocalidadId(Integer localidadId);

    /**
     * Cuenta usuarios con cuentas bloqueadas
     */
    long countByAccountNonLockedFalse();

    /**
     * Cuenta usuarios con credenciales expiradas
     */
    long countByCredentialsNonExpiredFalse();

    /**
     * Cuenta usuarios con cuentas expiradas
     */
    long countByAccountNonExpiredFalse();
}