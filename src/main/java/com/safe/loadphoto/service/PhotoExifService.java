package com.safe.loadphoto.service;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.GpsDirectory;
import com.safe.loadphoto.domain.model.PhotoExif;
import com.safe.loadphoto.domain.model.PhotoFile;
import com.safe.loadphoto.domain.port.in.PhotoExifServicePort;
import com.safe.loadphoto.domain.port.out.PhotoExifRepositoryPort;
import com.safe.loadphoto.domain.port.out.PhotoFileRepositoryPort;
import com.safe.loadphoto.infrastructure.adapter.ExifAdapter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.Optional;
import java.util.UUID;


@Service
public class PhotoExifService implements PhotoExifServicePort {

    private final ExifAdapter exifAdapter;
    private final PhotoExifRepositoryPort exifRepositoryPort;
    private final PhotoFileRepositoryPort fileRepositoryPort;

    public PhotoExifService(ExifAdapter exifAdapter, PhotoExifRepositoryPort exifRepositoryPort, PhotoFileRepositoryPort fileRepositoryPort) {
        this.exifAdapter = exifAdapter;
        this.exifRepositoryPort = exifRepositoryPort;
        this.fileRepositoryPort = fileRepositoryPort;
    }


    @Override
    public PhotoExif extractAndSaveExif(String filePath, String fileName, byte[] fileData) {
        try {
            if (fileData == null || fileData.length == 0) {
                throw new IllegalArgumentException("File data cannot be null or empty");
            }

            PhotoExif photoExif = extractExif(fileData);
            if (photoExif == null) {
                throw new IllegalStateException("Failed to extract EXIF data");
            }

            // Asigna ID y nombre de archivo
            photoExif.setId(UUID.randomUUID().toString());
            photoExif.setFileName(fileName);

            // Guarda EXIF
            PhotoExif savedExif = exifRepositoryPort.save(photoExif);

            // Guarda archivo
            PhotoFile photoFile = new PhotoFile(UUID.randomUUID().toString(), savedExif.getId(), fileName, fileData);
            fileRepositoryPort.save(photoFile);

            return savedExif;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error processing EXIF data or saving to database", e);
        }
    }


    public PhotoExif extractExif(byte[] fileData) {
        try {
            Metadata metadata = JpegMetadataReader.readMetadata(new ByteArrayInputStream(fileData));
            PhotoExif exif = new PhotoExif();

            ExifIFD0Directory directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);

            if (directory != null) {
                if (directory.containsTag(ExifIFD0Directory.TAG_MAKE)) {
                    String make = directory.getString(ExifIFD0Directory.TAG_MAKE);
                    exif.setCameraMaker(make.trim());
                    System.out.println("🏭 Fabricante extraído: " + make);
                }

                if (directory.containsTag(ExifIFD0Directory.TAG_MODEL)) {
                    String model = directory.getString(ExifIFD0Directory.TAG_MODEL);
                    exif.setCameraModel(model.trim());
                    System.out.println("📸 Modelo extraído: " + model);
                }

                if (directory.containsTag(ExifIFD0Directory.TAG_DATETIME)) {
                    String dateTime = directory.getString(ExifIFD0Directory.TAG_DATETIME);
                    exif.setDateTime(dateTime != null ? dateTime.trim() : null);
                    System.out.println("📅 Fecha y hora extraída: " + dateTime);
                }

            }

            GpsDirectory gpsDirectory = metadata.getFirstDirectoryOfType(GpsDirectory.class);
            if (gpsDirectory != null && gpsDirectory.getGeoLocation() != null) {
                GeoLocation geoLocation = gpsDirectory.getGeoLocation();
                exif.setLatitude(geoLocation.getLatitude());
                exif.setLongitude(geoLocation.getLongitude());
                System.out.println("📍 Latitud: " + geoLocation.getLatitude());
                System.out.println("📍 Longitud: " + geoLocation.getLongitude());
            }

            return exif;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Optional<PhotoExif> getPhotoById(String id) {
        return null;
    }
}