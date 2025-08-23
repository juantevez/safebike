package com.safe.bike.domain.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.safe.user.adapter.out.persistence.entity.UserEntity;
import com.safe.user.model.User;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bike")
@Data
public class BikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bikeId;

    // Puedes tener un campo transitorio si necesitas el objeto User temporalmente
    @Transient
    private User userInMemory;  // ← Solo para uso en memoria, no se persiste

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id") // El nombre de la columna en la tabla `bike`
    private BrandEntity brand; // <--- Relación con Brand

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_bike_id")
    private BikeModelEntity bikeModel;
    @Column(name = "serial_number", nullable = false)
    private String serialNumber;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate purchaseDate;

    private double purchaseValue;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @CreationTimestamp // Hibernate genera automáticamente al crear
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
}
