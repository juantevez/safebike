package com.safe.loadphoto.infrastructure.persistence.photofile;

import com.safe.loadphoto.domain.model.entity.PhotoFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataPhotoFileJpaRepository extends JpaRepository<PhotoFileEntity, String> {}