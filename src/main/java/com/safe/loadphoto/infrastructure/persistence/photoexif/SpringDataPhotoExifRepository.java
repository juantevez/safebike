package com.safe.loadphoto.infrastructure.persistence.photoexif;


import com.safe.loadphoto.domain.model.PhotoExif;
import com.safe.loadphoto.domain.model.entity.PhotoExifEntity;
import com.safe.loadphoto.domain.port.out.PhotoExifRepositoryPort;
import org.springframework.stereotype.Repository;

@Repository
public class SpringDataPhotoExifRepository implements PhotoExifRepositoryPort {

    private final SpringDataPhotoExifJpaRepository jpaRepository;

    public SpringDataPhotoExifRepository(SpringDataPhotoExifJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public PhotoExif save(PhotoExif photoExif) {
        PhotoExifEntity entity = new PhotoExifEntity(photoExif);
        PhotoExifEntity savedEntity = jpaRepository.save(entity);
        return savedEntity.toDomain();
    }
}
