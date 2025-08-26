package com.safe.loadphoto.domain.model;

public class PhotoFile {
    private String id;
    private String idExif;
    private String fileName;
    private byte[] fileData;

    public PhotoFile() {}

    public PhotoFile(String id, String idExif, String fileName, byte[] fileData) {
        this.id = id;
        this.idExif = idExif;
        this.fileName = fileName;
        this.fileData = fileData;
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
}