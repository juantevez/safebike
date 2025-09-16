package com.safe.user.infrastructure.adapters.output.persistence.repository;

import com.safe.user.domain.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // MÉTODOS BÁSICOS DE BÚSQUEDA
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    // BÚSQUEDAS POR NOMBRE
    List<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);
    List<User> findByFirstName(String firstName);
    List<User> findByLastName(String lastName);

    // BÚSQUEDAS GEOGRÁFICAS
    List<User> findByProvinciaId(Integer provinciaId);
    List<User> findByMunicipioId(Integer municipioId);
    List<User> findByLocalidadId(Integer localidadId);
    List<User> findByProvinciaIdAndMunicipioId(Integer provinciaId, Integer municipioId);
    List<User> findByProvinciaIdAndMunicipioIdAndLocalidadId(Integer provinciaId, Integer municipioId, Integer localidadId);

    // MÉTODOS DE CONTEO
    long countByProvinciaId(Integer provinciaId);
    long countByMunicipioId(Integer municipioId);
    long countByLocalidadId(Integer localidadId);
    long countByProvinciaIdIsNotNull();
    long countByProvinciaIdIsNull();

    // CONSULTAS PERSONALIZADAS CON @Query
    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    List<User> findBySearchText(@Param("searchText") String searchText);

    @Query("SELECT u FROM User u WHERE u.provinciaId IS NOT NULL AND u.municipioId IS NOT NULL AND u.localidadId IS NOT NULL")
    List<User> findUsersWithCompleteLocation();

    @Query("SELECT u FROM User u WHERE u.provinciaId IS NULL")
    List<User> findUsersWithoutLocation();

    @Query("SELECT u FROM User u WHERE u.id BETWEEN :startId AND :endId ORDER BY u.id")
    List<User> findByIdRange(@Param("startId") Long startId, @Param("endId") Long endId);

    @Query("SELECT u.provinciaId, COUNT(u) FROM User u WHERE u.provinciaId IS NOT NULL GROUP BY u.provinciaId")
    List<Object[]> getUserStatsByProvincia();

    @Query("SELECT u.municipioId, COUNT(u) FROM User u WHERE u.municipioId IS NOT NULL GROUP BY u.municipioId")
    List<Object[]> getUserStatsByMunicipio();

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = :email AND u.id != :userId")
    boolean existsByEmailAndIdNot(@Param("email") String email, @Param("userId") Long userId);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.username = :username AND u.id != :userId")
    boolean existsByUsernameAndIdNot(@Param("username") String username, @Param("userId") Long userId);

    @Query("SELECT u FROM User u WHERE " +
            "(:provinciaId IS NULL OR u.provinciaId = :provinciaId) AND " +
            "(:municipioId IS NULL OR u.municipioId = :municipioId) AND " +
            "(:localidadId IS NULL OR u.localidadId = :localidadId)")
    List<User> findUsersByLocation(@Param("provinciaId") Integer provinciaId,
                                   @Param("municipioId") Integer municipioId,
                                   @Param("localidadId") Integer localidadId);

    @Query("SELECT u FROM User u ORDER BY u.id DESC")
    Optional<User> findLastRegisteredUser();

    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate")
    long countUsersByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // MÉTODOS ADICIONALES DE CONVENIENCIA
    List<User> findAllByOrderByCreatedAtDesc();
    List<User> findAllByOrderByFirstNameAscLastNameAsc();
    List<User> findByEmailContainingIgnoreCase(String emailPart);
    List<User> findByUsernameContainingIgnoreCase(String usernamePart);
}