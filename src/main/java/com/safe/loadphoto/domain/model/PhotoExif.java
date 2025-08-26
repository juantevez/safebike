package com.safe.loadphoto.domain.model;

public class PhotoExif {
    private String id;
    private String fileName;
    private Double latitude;
    private Double longitude;
    private String dateTime;
    private String cameraModel;
    private String cameraMaker;

    // Constructor vacío
    public PhotoExif() {}

    // Constructor completo
    public PhotoExif(String id, String fileName, Double latitude, Double longitude, String dateTime, String cameraModel, String cameraMaker) {
        this.id = id;
        this.fileName = fileName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.dateTime = dateTime;
        this.cameraModel = cameraModel;
        this.cameraMaker = cameraMaker;
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

    public String getCameraMaker() { return cameraMaker; }
    public void setCameraMaker(String cameraMaker) { this.cameraMaker = cameraMaker; }
}