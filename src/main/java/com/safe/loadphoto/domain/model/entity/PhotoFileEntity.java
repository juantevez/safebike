package com.safe.loadphoto.domain.model.entity;


import com.safe.loadphoto.domain.model.PhotoFile;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "photo_file")
public class PhotoFileEntity {

    @Id
    private String id;

    @Column(name = "id_exif", nullable = false)
    private String idExif;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_data", nullable = false)
    private byte[] fileData;

    public PhotoFileEntity() {}

    public PhotoFileEntity(PhotoFile photoFile) {
        this.id = photoFile.getId();
        this.idExif = photoFile.getIdExif();
        this.fileName = photoFile.getFileName();
        this.fileData = photoFile.getFileData();
    }

    public PhotoFile toDomain() {
        return new PhotoFile(id, idExif, fileName, fileData
        );
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