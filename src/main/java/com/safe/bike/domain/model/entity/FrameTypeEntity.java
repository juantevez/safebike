package com.safe.bike.domain.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Table(name = "frame_type")
@Entity
@Data
public class FrameTypeEntity {
    @Id
    private Integer frameTypeId;
    @Column(name = "name", nullable = false) // Asegúrate de que este campo no sea nulo
    private String name; // Ejemplo de campo
}