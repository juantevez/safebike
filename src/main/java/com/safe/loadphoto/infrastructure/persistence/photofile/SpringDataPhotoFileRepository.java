package com.safe.loadphoto.infrastructure.persistence.photofile;


import com.safe.loadphoto.domain.model.PhotoExif;
import com.safe.loadphoto.domain.model.PhotoFile;
import com.safe.loadphoto.domain.model.entity.PhotoFileEntity;
import com.safe.loadphoto.domain.port.out.PhotoFileRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class SpringDataPhotoFileRepository implements PhotoFileRepositoryPort {

    private final SpringDataPhotoFileJpaRepository jpaRepository;

    public SpringDataPhotoFileRepository(SpringDataPhotoFileJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public PhotoFile save(PhotoFile photoFile) {
        PhotoFileEntity entity = new PhotoFileEntity(photoFile);
        PhotoFileEntity savedEntity = jpaRepository.save(entity);
        return savedEntity.toDomain();
    }

    @Override
    public Optional<PhotoExif> getPhotoById(String id) {
        return Optional.empty();
    }
}


