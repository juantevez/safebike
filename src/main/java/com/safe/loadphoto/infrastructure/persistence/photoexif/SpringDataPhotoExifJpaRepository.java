package com.safe.loadphoto.infrastructure.persistence.photoexif;

import com.safe.loadphoto.domain.model.entity.PhotoExifEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataPhotoExifJpaRepository extends JpaRepository<PhotoExifEntity, String> {}
