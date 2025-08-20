package com.safe.bike.domain.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class BrandEntity {
    @Id
    private Integer brandId;
    private String name; // Ejemplo de campo
}
