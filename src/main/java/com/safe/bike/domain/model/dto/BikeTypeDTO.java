package com.safe.bike.domain.model.dto;

import java.time.LocalDateTime;

public final class BikeTypeDTO {

    private final Long bikeTypeId;
    private final String name;
    private final String description;
    private final LocalDateTime createdAt;

    // Constructor privado: solo accesible desde el Builder
    private BikeTypeDTO(Builder builder) {
        this.bikeTypeId = builder.bikeTypeId;
        this.name = builder.name;
        this.description = builder.description;
        this.createdAt = builder.createdAt;
    }

    // Getters
    public Long getBikeTypeId() {
        return bikeTypeId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // toString
    @Override
    public String toString() {
        return "BikeTypeDTO{" +
                "bikeTypeId=" + bikeTypeId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }

    // ✅ Patrón Builder estático
    public static class Builder {
        private Long bikeTypeId;
        private String name;
        private String description;
        private LocalDateTime createdAt;

        public Builder bikeTypeId(Long bikeTypeId) {
            this.bikeTypeId = bikeTypeId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public BikeTypeDTO build() {
            return new BikeTypeDTO(this);
        }
    }
}