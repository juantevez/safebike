package com.safe.bike.domain.model.entity;

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
    private String type; // Ejemplo de campo
}