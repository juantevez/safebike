package com.safe.loadphoto.domain.port.out;


import com.safe.loadphoto.domain.model.PhotoExif;
import com.safe.loadphoto.domain.model.PhotoFile;

import java.util.Optional;

public interface PhotoFileRepositoryPort {
    PhotoFile save(PhotoFile photoFile);
    Optional<PhotoExif> getPhotoById(String id);
}