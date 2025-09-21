package com.safe.loadphoto.domain.model;

public class PhotoFile {
    private String id;
    private String idExif;
    private String fileName;
    private byte[] fileData;
    private Long bikeId;
    private PhotoExif exif;
    public PhotoFile() {}

    public PhotoFile(String id, String idExif, String fileName, byte[] fileData, long bikeId, PhotoExif exif) {
        this.id = id;
        this.idExif = idExif;
        this.fileName = fileName;
        this.fileData = fileData;
        this.bikeId = bikeId;
        this.exif = exif;
    }
    public PhotoFile(String id, String idExif, String fileName, byte[] fileData, long bikeId) {
        this.id = id;
        this.idExif = idExif;
        this.fileName = fileName;
        this.fileData = fileData;
        this.bikeId = bikeId;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getIdExif() { return idExif; }
    public void setIdExif(String idExif) { this.idExif = idExif; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public byte[] getFileData() { return fileData; }
    public void setFileData(byte[] fileData) { this.fileData = fileData; }

    public Long getBikeId() {
        return bikeId;
    }

    public void setBikeId(Long bikeId) {
        this.bikeId = bikeId;
    }

    public PhotoExif getExif() {
        return exif;
    }

    public void setExif(PhotoExif exif) {
        this.exif = exif;
    }
}