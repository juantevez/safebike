package com.safe.loadphoto.service;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.GpsDirectory;
import com.safe.loadphoto.domain.model.PhotoExif;
import com.safe.loadphoto.domain.model.PhotoFile;
import com.safe.loadphoto.domain.model.response.ExifDataResponse;
import com.safe.loadphoto.domain.model.response.HeicProcessingResponse;
import com.safe.loadphoto.domain.port.in.PhotoExifServicePort;
import com.safe.loadphoto.domain.port.out.PhotoExifRepositoryPort;
import com.safe.loadphoto.domain.port.out.PhotoFileRepositoryPort;
import com.safe.loadphoto.infrastructure.adapter.ExifAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.Optional;
import java.util.UUID;


@Service
public class PhotoExifService implements PhotoExifServicePort {
    private static final Logger logger = LoggerFactory.getLogger(PhotoExifService.class);
    private final ExifAdapter exifAdapter;
    private final PhotoExifRepositoryPort exifRepositoryPort;
    private final PhotoFileRepositoryPort fileRepositoryPort;
    private final HeicProcessingService heicProcessingService;

    public PhotoExifService(ExifAdapter exifAdapter, PhotoExifRepositoryPort exifRepositoryPort, PhotoFileRepositoryPort fileRepositoryPort, HeicProcessingService heicProcessingService) {
        this.exifAdapter = exifAdapter;
        this.exifRepositoryPort = exifRepositoryPort;
        this.fileRepositoryPort = fileRepositoryPort;
        this.heicProcessingService = heicProcessingService;
    }


    @Override
    public PhotoExif extractAndSaveExif(String filePath, String fileName, byte[] fileData, Long bikeId) {
        try {
            if (fileData == null || fileData.length == 0) {
                throw new IllegalArgumentException("File data cannot be null or empty");
            }

            PhotoExif photoExif;
            byte[] finalImageData;
            String finalFileName = fileName;

            // Verificar si es archivo HEIC
            if (isHeicFile(fileName)) {
                logger.info("Procesando archivo HEIC: {}", fileName);

                // Pasar el bikeId al servicio Golang
                HeicProcessingResponse heicResponse = heicProcessingService.processHeicFile(fileData, fileName, bikeId);

                if (!heicResponse.isSuccess()) {
                    throw new RuntimeException("Error al procesar archivo HEIC: " + heicResponse.getMessage());
                }

                // Usar datos originales (ya que Golang no devuelve archivo convertido)
                finalImageData = fileData;
                finalFileName = fileName;

                // Usar datos EXIF del servicio Golang
                photoExif = createPhotoExifFromHeicResponse(heicResponse);

            } else {
                // Procesamiento normal para JPG/PNG
                logger.info("Procesando archivo JPG/PNG: {}", fileName);
                photoExif = extractExif(fileData);
                finalImageData = fileData;
            }

            if (photoExif == null) {
                throw new IllegalStateException("Failed to extract EXIF data");
            }

            // Asigna ID y nombre de archivo
            photoExif.setId(UUID.randomUUID().toString());
            photoExif.setFileName(finalFileName);

            // Guarda EXIF
            PhotoExif savedExif = exifRepositoryPort.save(photoExif);

            // Guarda archivo (ahora puede ser JPG convertido desde HEIC)
            PhotoFile photoFile = new PhotoFile(
                    UUID.randomUUID().toString(),
                    savedExif.getId(),
                    finalFileName,
                    finalImageData,
                    bikeId
            );
            fileRepositoryPort.save(photoFile);

            return savedExif;

        } catch (Exception e) {
            logger.error("Error processing file: {}", fileName, e);
            throw new RuntimeException("Error processing EXIF data or saving to database", e);
        }
    }
    private boolean isHeicFile(String fileName) {
        if (fileName == null) return false;
        String extension = fileName.toLowerCase();
        return extension.endsWith(".heic") || extension.endsWith(".heif");
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

    private PhotoExif createPhotoExifFromHeicResponse(HeicProcessingResponse response) {
        PhotoExif photoExif = new PhotoExif();

        ExifDataResponse exifData = response.getExifData();
        if (exifData != null) {
            photoExif.setLatitude(exifData.getLatitude());
            photoExif.setLongitude(exifData.getLongitude());
            photoExif.setDateTime(exifData.getDateTime());
            //photoExif.setMake(exifData.getCameraMaker());
            //photoExif.setModel(exifData.getCameraModel());
            // photoExif.setOrientation(...); // Si lo necesitas
        }

        return photoExif;
    }
    @Override
    public Optional<PhotoExif> getPhotoById(String id) {
        return null;
    }
}