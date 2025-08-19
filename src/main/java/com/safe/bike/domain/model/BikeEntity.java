package com.safe.bike.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.safe.user.domain.model.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bike")
@Data
public class BikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Usa la secuencia asociada a la columna
    private String bikeId;
    private Integer brandId;
    @Column(name = "serial_number", nullable = false)
    private String serialNumber;
    private Integer bikeTypeId;
    private Integer frameTypeId;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate purchaseDate;
    private double purchaseValue;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Referencia al usuario
}
