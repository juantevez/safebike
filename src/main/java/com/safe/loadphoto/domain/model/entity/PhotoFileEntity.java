package com.safe.loadphoto.domain.model.entity;


import com.safe.loadphoto.domain.model.PhotoExif;
import com.safe.loadphoto.domain.model.PhotoFile;
import jakarta.persistence.*;

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

    @Column(name = "bike_id")
    private Long bikeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_exif", referencedColumnName = "id", insertable = false, updatable = false)
    private PhotoExifEntity exifEntity;

    public PhotoFileEntity() {}


    public PhotoFileEntity(PhotoFile photoFile) {
        this.id = photoFile.getId();
        this.idExif = photoFile.getIdExif();
        this.fileName = photoFile.getFileName();
        this.fileData = photoFile.getFileData();
        this.bikeId = photoFile.getBikeId();
    }

    public PhotoFile toDomain() {
        PhotoExif photoExif = null;
        if (exifEntity != null) {
            photoExif = exifEntity.toDomain(); // Convertir la entidad a dominio
        }
        return new PhotoFile(id, idExif, fileName, fileData, bikeId, photoExif );
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

    public PhotoExifEntity getExifEntity() {
        return exifEntity;
    }

    public void setExifEntity(PhotoExifEntity exifEntity) {
        this.exifEntity = exifEntity;
    }
}