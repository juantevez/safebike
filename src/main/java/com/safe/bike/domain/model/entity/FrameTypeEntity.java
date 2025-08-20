package com.safe.bike.domain.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class FrameTypeEntity {
    @Id
    private Integer frameTypeId;
    private String type; // Ejemplo de campo
}