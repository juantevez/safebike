package com.safe.bike.domain.model.dto;

import java.time.LocalDateTime;

/**
 * DTO inmutable para BrandEntity, usando el patrón Builder.
 * Ideal para transferencia de datos sin exponer entidades JPA.
 */
public final class BrandDTO {

    private final Long brandId;
    private final String name;
    private final String countryName;        // Solo el nombre del país, no toda la entidad
    private final Integer countryId;         // ID del país (opcional, útil para UI)
    private final LocalDateTime createdAt;

    // Constructor privado: solo accesible desde el Builder
    private BrandDTO(Builder builder) {
        this.brandId = builder.brandId;
        this.name = builder.name;
        this.countryName = builder.countryName;
        this.countryId = builder.countryId;
        this.createdAt = builder.createdAt;
    }

    // Getters
    public Long getBrandId() {
        return brandId;
    }

    public String getName() {
        return name;
    }

    public String getCountryName() {
        return countryName;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // toString
    @Override
    public String toString() {
        return "BrandDTO{" +
                "brandId=" + brandId +
                ", name='" + name + '\'' +
                ", countryName='" + countryName + '\'' +
                ", countryId=" + countryId +
                ", createdAt=" + createdAt +
                '}';
    }

    // ✅ Patrón Builder estático
    public static class Builder {
        private Long brandId;
        private String name;
        private String countryName;
        private Integer countryId;
        private LocalDateTime createdAt;

        public Builder brandId(Long brandId) {
            this.brandId = brandId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder countryName(String countryName) {
            this.countryName = countryName;
            return this;
        }

        public Builder countryId(Integer countryId) {
            this.countryId = countryId;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public BrandDTO build() {
            return new BrandDTO(this);
        }
    }
}