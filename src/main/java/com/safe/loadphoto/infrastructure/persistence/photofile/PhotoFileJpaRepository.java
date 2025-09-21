package com.safe.loadphoto.infrastructure.persistence.photofile;

import com.safe.loadphoto.domain.model.entity.PhotoFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoFileJpaRepository extends JpaRepository<PhotoFileEntity, String> {
    @Query("SELECT pf FROM PhotoFileEntity pf LEFT JOIN FETCH pf.exifEntity WHERE pf.bikeId = :bikeId")
    List<PhotoFileEntity> findByBikeId(Long bikeId);
}

