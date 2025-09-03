package com.safe.bike.domain.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.safe.location.domain.model.entity.LocalidadEntity;
import com.safe.location.domain.model.entity.MunicipioEntity;
import com.safe.location.domain.model.entity.ProvinciaEntity;
import com.safe.user.infrastructure.adapters.output.persistence.entities.UserEntity;
import com.safe.user.domain.model.User;
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
    private User userInMemory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private BrandEntity brand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bike_type_id")
    private BikeTypeEntity bikeType;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_bike_id")
    private BikeModelEntity bikeModel;
    @Column(name = "serial_number", nullable = false)
    private String serialNumber;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate purchaseDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "size_id")
    private SizeEntity sizeBike;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "moneda_id")
    private MonedaEntity moneda;
    private double purchaseValue;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "localidad_id")
    private LocalidadEntity localidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "municipio_id")
    private MunicipioEntity municipio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provincia_id")
    private ProvinciaEntity provincia;
}
