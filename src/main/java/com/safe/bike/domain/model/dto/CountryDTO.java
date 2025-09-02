package com.safe.bike.domain.model.dto;

/**
 * DTO inmutable para Country (País), usando el patrón Builder.
 * Ideal para transferencia segura de datos sin exponer entidades JPA.
 */
public final class CountryDTO {

    private final Integer countryId;
    private final String name;

    // Constructor privado: solo accesible desde el Builder
    private CountryDTO(Builder builder) {
        this.countryId = builder.countryId;
        this.name = builder.name;
    }

    // Getters
    public Integer getCountryId() {
        return countryId;
    }

    public String getName() {
        return name;
    }

    // toString
    @Override
    public String toString() {
        return "CountryDTO{" +
                "countryId=" + countryId +
                ", name='" + name + '\'' +
                '}';
    }

    // ✅ Patrón Builder estático
    public static class Builder {
        private Integer countryId;
        private String name;

        public Builder countryId(Integer countryId) {
            this.countryId = countryId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public CountryDTO build() {
            return new CountryDTO(this);
        }
    }
}