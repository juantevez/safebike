package com.safe.bike.domain.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "brand")
public class BrandEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "brand_id", nullable = false)
    private Long brandId;

    @Column(name = "name", length = 255, nullable = false, unique = true)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "country_id", foreignKey = @ForeignKey(name = "brand_country_id_fkey"))
    private Country country;

    @Column(name = "created_at", nullable = true, updatable = false)
    private LocalDateTime createdAt;

    // Constructores
    public BrandEntity() {
    }

    public BrandEntity(String name, Country country) {
        this.name = name;
        this.country = country;
    }

    // Getters y Setters
    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // toString (opcional)
    @Override
    public String toString() {
        return "BrandEntity{" +
                "brandId=" + brandId +
                ", name='" + name + '\'' +
                ", countryId=" + (country != null ? country.getCountryId() : null) +
                ", createdAt=" + createdAt +
                '}';
    }
}