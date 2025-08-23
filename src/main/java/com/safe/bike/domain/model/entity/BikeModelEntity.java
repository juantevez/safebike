package com.safe.bike.domain.model.entity;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "bike_model")
@Data
public class BikeModelEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_bike_model")
    private Long  idBikeModel;

    @Column(name = "model_name", nullable = false, length = 255)
    private String modelName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "brand_id", nullable = false)
    private BrandEntity brand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bike_type_id")
    private BikeTypeEntity bikeType; //

    @Column(name = "year_released")
    private Integer yearReleased;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}