package com.safe.report.domain.port.out;

import com.safe.bike.domain.model.Bike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para operaciones de persistencia relacionadas con el reporte de bicicletas.
 * Proporciona acceso a bicicletas con sus entidades relacionadas (usuario, marca, modelo, etc.).
 */
@Repository
public interface BikeReportRepository extends JpaRepository<Bike, Long> {

    /**
     * Obtiene todas las bicicletas de un usuario específico con sus relaciones completas cargadas.
     *
     * Relaciones cargadas:
     * - user
     * - brand
     * - bikeModel
     * - bikeType
     * - size
     *
     * @param userId ID del usuario
     * @return Lista de bicicletas con todas las relaciones inicializadas
     */
    @Query("SELECT b FROM Bike b " +
            "JOIN FETCH b.user u " +
            "JOIN FETCH b.brand " +
            "JOIN FETCH b.bikeModel " +
            "JOIN FETCH b.bikeType " +
            "JOIN FETCH b.size " +
            "WHERE b.user.id = :userId")
    List<Bike> findBikesByUserIdWithDetails(@Param("userId") Long userId);

    /**
     * Cuenta el número total de usuarios registrados en el sistema.
     * Útil para mostrar estadísticas en el reporte (ej: "Total de usuarios: X").
     *
     * @return cantidad de usuarios únicos
     */
    @Query("SELECT COUNT(DISTINCT u.id) FROM User u")
    int countTotalUsers();

    /**
     * (Opcional) Obtiene todas las bicicletas del sistema con relaciones completas.
     * Solo para uso de administradores.
     *
     * @return Lista de todas las bicicletas con sus relaciones
     */
    @Query("SELECT b FROM Bike b " +
            "JOIN FETCH b.user u " +
            "JOIN FETCH b.brand " +
            "JOIN FETCH b.bikeModel " +
            "JOIN FETCH b.bikeType " +
            "JOIN FETCH b.size " +
            "ORDER BY b.id")
    List<Bike> findAllBikesWithCompleteDetails();
}