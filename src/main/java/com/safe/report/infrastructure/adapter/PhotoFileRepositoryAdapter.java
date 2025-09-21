package com.safe.report.infrastructure.adapter;

import com.safe.loadphoto.domain.model.PhotoExif;
import com.safe.loadphoto.domain.model.PhotoFile;
import com.safe.loadphoto.domain.model.entity.PhotoExifEntity;
import com.safe.loadphoto.domain.model.entity.PhotoFileEntity;
import com.safe.loadphoto.infrastructure.persistence.photofile.PhotoFileJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PhotoFileRepositoryAdapter {

    private static final Logger logger = LoggerFactory.getLogger(PhotoFileRepositoryAdapter.class);
    private final PhotoFileJpaRepository jpaRepository;

    public PhotoFileRepositoryAdapter(PhotoFileJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    public List<PhotoFile> findByBikeId(Long bikeId) {
        logger.info("findByBikeId {}", bikeId);
        return jpaRepository.findByBikeId(bikeId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private PhotoFile toDomain(PhotoFileEntity entity) {
        logger.info("toDomain {}", entity);
        PhotoExif exif = null;
        if (entity.getExifEntity() != null) {
            PhotoExifEntity e = entity.getExifEntity();
            exif = new PhotoExif(e.getId(), e.getFileName(), e.getLatitude(), e.getLongitude(),
                    e.getDateTime(), e.getCameraModel(), e.getCameraMaker());
        }
        return new PhotoFile(entity.getId(), entity.getIdExif(), entity.getFileName(),
                entity.getFileData(), entity.getBikeId(), exif);
    }
}
