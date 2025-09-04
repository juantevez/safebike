package com.safe.report.domain.model;

import java.time.LocalDate;

public record BikeReportDTO(
        Long bikeId,
        String firstName,
        String lastName,
        String brand,
        String model,
        String type,
        String serialNumber,
        LocalDate purchaseDate
) {
    public String getFullName() {
        return String.format("%s %s", firstName, lastName);
    }
}