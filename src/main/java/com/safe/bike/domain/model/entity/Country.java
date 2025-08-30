package com.safe.bike.domain.model.entity;


import jakarta.persistence.*;

@Entity
@Table(name = "country")
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "country_id", nullable = false)
    private Integer countryId;

    @Column(name = "name", length = 100, nullable = false, unique = true)
    private String name;

    // Constructores
    public Country() {
    }

    public Country(String name) {
        this.name = name;
    }

    public Country(Integer countryId, String name) {
        this.countryId = countryId;
        this.name = name;
    }

    // Getters y Setters
    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // toString
    @Override
    public String toString() {
        return "Country{" +
                "countryId=" + countryId +
                ", name='" + name + '\'' +
                '}';
    }

    // hashCode y equals (recomendado para colecciones)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Country)) return false;
        Country country = (Country) o;
        return countryId != null && countryId.equals(country.countryId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}