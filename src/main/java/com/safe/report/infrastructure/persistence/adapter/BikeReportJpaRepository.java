package com.safe.report.infrastructure.persistence.adapter;

import com.safe.bike.domain.model.entity.BikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BikeReportJpaRepository extends JpaRepository<BikeEntity, Long> {

    @Query("SELECT b FROM Bike b " +
            "JOIN FETCH b.user u " +
            "JOIN FETCH b.brand " +
            "JOIN FETCH b.bikeModel " +
            "JOIN FETCH b.bikeType " +
            "JOIN FETCH b.sizeBike " +
            "WHERE b.user.id = :userId")
    List<BikeEntity> findBikesByUserIdWithDetails(@Param("userId") Long userId);

    /*@Query("SELECT COUNT(DISTINCT u.id) FROM User u")
    int countTotalUsers();*/
}