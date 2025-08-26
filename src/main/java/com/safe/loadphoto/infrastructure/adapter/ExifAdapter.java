package com.safe.loadphoto.infrastructure.adapter;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.safe.loadphoto.domain.model.PhotoExif;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;

@Component
public class ExifAdapter {

    public PhotoExif extractExif(byte[] fileData) {
        try {
            // Leer metadatos desde byte[]
            ByteArrayInputStream inputStream = new ByteArrayInputStream(fileData);
            Metadata metadata = ImageMetadataReader.readMetadata(inputStream);
            PhotoExif photoExif = new PhotoExif();

            // Extraer datos EXIF
            ExifSubIFDDirectory exifDirectory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            if (exifDirectory != null) {
                photoExif.setDateTime(exifDirectory.getString(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL));
                photoExif.setCameraModel(exifDirectory.getString(ExifSubIFDDirectory.TAG_MODEL));
            }

            // Extraer datos GPS
            GpsDirectory gpsDirectory = metadata.getFirstDirectoryOfType(GpsDirectory.class);
            if (gpsDirectory != null && gpsDirectory.getGeoLocation() != null) {
                photoExif.setLatitude(gpsDirectory.getGeoLocation().getLatitude());
                photoExif.setLongitude(gpsDirectory.getGeoLocation().getLongitude());
            }

            return photoExif;
        } catch (Exception e) {
            e.printStackTrace();
            return new PhotoExif(); // Devolver objeto vacío en caso de error
        }
    }
}