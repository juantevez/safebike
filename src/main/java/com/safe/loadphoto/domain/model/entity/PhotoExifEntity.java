package com.safe.loadphoto.domain.model.entity;

import com.safe.loadphoto.domain.model.PhotoExif;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "photo_exif")
public class PhotoExifEntity {

    @Id
    private String id;
    private String fileName;
    private Double latitude;
    private Double longitude;
    private String dateTime;
    @Column(name = "camera_model")
    private String cameraModel;

    @Column(name = "camera_maker")
    private String cameraMaker;

    // Constructor vacío
    public PhotoExifEntity() {}

    // Constructor desde entidad de dominio
    public PhotoExifEntity(PhotoExif photoExif) {
        this.id = photoExif.getId();
        this.fileName = photoExif.getFileName();
        this.latitude = photoExif.getLatitude();
        this.longitude = photoExif.getLongitude();
        this.dateTime = photoExif.getDateTime();
        this.cameraModel = photoExif.getCameraModel();
        this.cameraMaker = photoExif.getCameraMaker();
    }

    // Convertir a entidad de dominio
    public PhotoExif toDomain() {
        return new PhotoExif(
                id, fileName, latitude, longitude, dateTime, cameraModel, cameraMaker
        );
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public String getDateTime() { return dateTime; }
    public void setDateTime(String dateTime) { this.dateTime = dateTime; }
    public String getCameraModel() { return cameraModel; }
    public void setCameraModel(String cameraModel) { this.cameraModel = cameraModel; }

    public String getCameraMaker() { return cameraMaker;  }

    public void setCameraMaker(String cameraMaker) { this.cameraMaker = cameraMaker;  }
}