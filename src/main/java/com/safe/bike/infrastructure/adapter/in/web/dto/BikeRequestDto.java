package com.safe.bike.infrastructure.adapter.in.web.dto;

import com.safe.bike.infrastructure.adapter.in.web.validation.ValidBike;
import com.safe.bike.infrastructure.adapter.in.web.validation.ValidFrameMaterial;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BikeRequestDto {


    private String bikesId;

    private int brand;
    @NotNull(message = "El Numero de serie es obligatorio")
    private String serialNumber;
    @ValidBike(message = "Tipo de bicicleta no compatible")
    private int type;

    @ValidFrameMaterial(message = "Tipo de material de la bicicleta no compatible")
    private int frameType;

    @PastOrPresent(message = "La fecha debe ser hoy o anterior")
    @NotNull(message = "La fecha de compra es obligatoria")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate purchaseDate;

    @DecimalMin(value = "0.0", inclusive = false, message = "El valor debe ser mayor a 0")
    private double purchaseValue;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    // Getters y setters
}