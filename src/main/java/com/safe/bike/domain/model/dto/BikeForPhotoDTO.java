package com.safe.bike.domain.model.dto;

public class BikeForPhotoDTO {
    private final Long bikeId;
    private final String brandName;
    private final String modelName;
    private final String serialNumber;

    public BikeForPhotoDTO(Long bikeId, String brandName, String modelName, String serialNumber) {
        this.bikeId = bikeId;
        this.brandName = brandName;
        this.modelName = modelName;
        this.serialNumber = serialNumber;
    }

    // Getters
    public Long getBikeId() { return bikeId; }
    public String getBrandName() { return brandName; }
    public String getModelName() { return modelName; }
    public String getSerialNumber() { return serialNumber; }

    public String getDisplayLabel() {
        return brandName + " - " + modelName;
    }
}