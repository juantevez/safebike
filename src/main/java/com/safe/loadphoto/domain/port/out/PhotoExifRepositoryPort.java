package com.safe.loadphoto.domain.port.out;


import com.safe.loadphoto.domain.model.PhotoExif;

public interface PhotoExifRepositoryPort {
    PhotoExif save(PhotoExif photoExif);
}