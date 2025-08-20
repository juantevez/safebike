package com.safe.bike.domain.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class BikeTypeEntity {
    @Id
    private Integer bikeTypeId;
    private String type; // Ejemplo de campo
}
