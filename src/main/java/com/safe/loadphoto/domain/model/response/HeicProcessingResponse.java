package com.safe.loadphoto.domain.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.safe.loadphoto.domain.model.PhotoExif;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HeicProcessingResponse {
    private String id;

    @JsonProperty("file_name")
    private String fileName;

    private String message;

    @JsonProperty("exif_data")
    private ExifDataResponse exifData;

    // Constructor
    public HeicProcessingResponse() {}

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ExifDataResponse getExifData() {
        return exifData;
    }

    public void setExifData(ExifDataResponse exifData) {
        this.exifData = exifData;
    }

    // Método helper para verificar éxito
    public boolean isSuccess() {
        return message != null && message.contains("successfully");
    }

    // Para compatibilidad, no hay datos JPEG en la respuesta
    public byte[] getJpegData() {
        return null; // Tu servicio Golang no devuelve datos convertidos
    }

    public String getConvertedFileName() {
        return fileName; // Usar el mismo nombre
    }
}