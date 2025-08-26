package com.safe.loadphoto.domain.port.in;



import com.safe.loadphoto.domain.model.PhotoExif;

import java.util.Optional;

public interface PhotoExifServicePort {
    PhotoExif extractAndSaveExif(String filePath, String fileName, byte []bytes);

    Optional<PhotoExif> getPhotoById(String id);
}