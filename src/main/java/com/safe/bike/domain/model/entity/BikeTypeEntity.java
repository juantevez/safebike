package com.safe.bike.domain.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Table(name = "bike_type")
@Entity
@Data
public class BikeTypeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bikeTypeId;

    @Column(name = "type", nullable = false) // Asegúrate de que este campo no sea nulo
    private String type;
}
