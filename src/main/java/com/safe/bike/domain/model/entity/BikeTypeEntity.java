package com.safe.bike.domain.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Table(name = "bike_type")
@Entity
@Data
public class BikeTypeEntity {
    @Id
    private Integer bikeTypeId;
    private String type; // Ejemplo de campo
}
