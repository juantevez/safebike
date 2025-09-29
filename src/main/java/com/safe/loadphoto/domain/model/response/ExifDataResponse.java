package com.safe.loadphoto.domain.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ExifDataResponse {
    private String id;

    @JsonProperty("file_name")
    private String fileName;

    private Double latitude;
    private Double longitude;

    @JsonProperty("date_time")
    private String dateTime;

    @JsonProperty("camera_model")
    private String cameraModel;

    @JsonProperty("camera_maker")
    private String cameraMaker;

    @JsonProperty("created_at")
    private String createdAt;

    // Constructor
    public ExifDataResponse() {}

    // Getters y setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getCameraModel() {
        return cameraModel;
    }

    public void setCameraModel(String cameraModel) {
        this.cameraModel = cameraModel;
    }

    public String getCameraMaker() {
        return cameraMaker;
    }

    public void setCameraMaker(String cameraMaker) {
        this.cameraMaker = cameraMaker;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}