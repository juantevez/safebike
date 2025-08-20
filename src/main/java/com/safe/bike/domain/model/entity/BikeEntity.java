package com.safe.bike.domain.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.safe.user.domain.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bike")
@Data
public class BikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bikeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id") // El nombre de la columna en la tabla `bike`
    private BrandEntity brand; // <--- Relación con Brand

    @Column(name = "serial_number", nullable = false)
    private String serialNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bike_type_id")
    private BikeTypeEntity bikeType; // <--- Relación con BikeTypeEntity

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "frame_type_id")
    private FrameTypeEntity frameType; // <--- Relación con FrameTypeEntity

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate purchaseDate;

    private double purchaseValue;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
