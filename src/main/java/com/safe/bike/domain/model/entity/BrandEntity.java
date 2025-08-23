package com.safe.bike.domain.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Table(name = "brand")
@Entity
@Data
public class BrandEntity {
    @Id
    private Long brandId;
    private String name; // Ejemplo de campo
}
