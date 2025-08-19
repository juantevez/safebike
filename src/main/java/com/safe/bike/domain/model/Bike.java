package com.safe.bike.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Bike {
        private String bikeId;
        private Integer brandId;        // Cambiado de String brand
        private String serialNumber;
        private Integer bikeTypeId;     // Cambiado de String type
        private Integer frameTypeId;    // Cambiado de String frameType
        private LocalDate purchaseDate;
        private double purchaseValue;
        private LocalDateTime createdAt;

        public Bike(Integer brandId, String serialNumber, Integer bikeTypeId, Integer frameTypeId, LocalDate purchaseDate, double purchaseValue) {
                this.brandId = brandId;
                this.serialNumber = serialNumber;
                this.bikeTypeId = bikeTypeId;
                this.frameTypeId = frameTypeId;
                this.purchaseDate = purchaseDate;
                this.purchaseValue = purchaseValue;
        }
}

