package com.safe.bike.domain.model.dto;


import java.time.LocalDate;
import java.time.LocalDateTime;

public final class BikeDTO {

    // Campos del DTO
    private final Long bikeId;
    private final String brandName;
    private final String bikeTypeName;
    private final String modelName;
    private final String serialNumber;
    private final LocalDate purchaseDate;
    private final String sizeName;
    private final String monedaCodigo;
    private final Double purchaseValue;
    private final LocalDateTime createdAt;
    private final String userEmail;
    private final String localidadName;
    private final String municipioName;
    private final String provinciaName;

    // Constructor privado (solo accesible desde el Builder)
    private BikeDTO(Builder builder) {
        this.bikeId = builder.bikeId;
        this.brandName = builder.brandName;
        this.bikeTypeName = builder.bikeTypeName;
        this.modelName = builder.modelName;
        this.serialNumber = builder.serialNumber;
        this.purchaseDate = builder.purchaseDate;
        this.sizeName = builder.sizeName;
        this.monedaCodigo = builder.monedaCodigo;
        this.purchaseValue = builder.purchaseValue;
        this.createdAt = builder.createdAt;
        this.userEmail = builder.userEmail;
        this.localidadName = builder.localidadName;
        this.municipioName = builder.municipioName;
        this.provinciaName = builder.provinciaName;
    }

    // Getters
    public Long getBikeId() { return bikeId; }
    public String getBrandName() { return brandName; }
    public String getBikeTypeName() { return bikeTypeName; }
    public String getModelName() { return modelName; }
    public String getSerialNumber() { return serialNumber; }
    public LocalDate getPurchaseDate() { return purchaseDate; }
    public String getSizeName() { return sizeName; }
    public String getMonedaCodigo() { return monedaCodigo; }
    public Double getPurchaseValue() { return purchaseValue; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getUserEmail() { return userEmail; }
    public String getLocalidadName() { return localidadName; }
    public String getMunicipioName() { return municipioName; }
    public String getProvinciaName() { return provinciaName; }

    // toString (opcional)
    @Override
    public String toString() {
        return "BikeDTO{" +
                "bikeId=" + bikeId +
                ", brandName='" + brandName + '\'' +
                ", modelName='" + modelName + '\'' +
                ", serialNumber='" + serialNumber + '\'' +
                ", purchaseDate=" + purchaseDate +
                ", purchaseValue=" + purchaseValue +
                ", userEmail='" + userEmail + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }

    // ✅ Builder estático
    public static class Builder {
        private Long bikeId;
        private String brandName;
        private String bikeTypeName;
        private String modelName;
        private String serialNumber;
        private LocalDate purchaseDate;
        private String sizeName;
        private String monedaCodigo;
        private Double purchaseValue;
        private LocalDateTime createdAt;
        private String userEmail;
        private String localidadName;
        private String municipioName;
        private String provinciaName;

        public Builder bikeId(Long bikeId) {
            this.bikeId = bikeId;
            return this;
        }

        public Builder brandName(String brandName) {
            this.brandName = brandName;
            return this;
        }

        public Builder bikeTypeName(String bikeTypeName) {
            this.bikeTypeName = bikeTypeName;
            return this;
        }

        public Builder modelName(String modelName) {
            this.modelName = modelName;
            return this;
        }

        public Builder serialNumber(String serialNumber) {
            this.serialNumber = serialNumber;
            return this;
        }

        public Builder purchaseDate(LocalDate purchaseDate) {
            this.purchaseDate = purchaseDate;
            return this;
        }

        public Builder sizeName(String sizeName) {
            this.sizeName = sizeName;
            return this;
        }

        public Builder monedaCodigo(String monedaCodigo) {
            this.monedaCodigo = monedaCodigo;
            return this;
        }

        public Builder purchaseValue(Double purchaseValue) {
            this.purchaseValue = purchaseValue;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder userEmail(String userEmail) {
            this.userEmail = userEmail;
            return this;
        }

        public Builder localidadName(String localidadName) {
            this.localidadName = localidadName;
            return this;
        }

        public Builder municipioName(String municipioName) {
            this.municipioName = municipioName;
            return this;
        }

        public Builder provinciaName(String provinciaName) {
            this.provinciaName = provinciaName;
            return this;
        }

        public BikeDTO build() {
            return new BikeDTO(this);
        }
    }
}