package com.safe.bike.domain.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "size_bike")
public class SizeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "sigla", length = 20, nullable = false)
    private String sigla;

    @Column(name = "description", length = 100, nullable = false)
    private String description;

    // ❌ Eliminar: @ManyToOne a BikeModelEntity
    // ❌ Eliminar: Set<BikeModelEntity> bikeModels

    // Constructores
    public SizeEntity() { }

    public SizeEntity(Integer id, String sigla, String description) {
        this.id = id;
        this.sigla = sigla;
        this.description = description;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // toString, equals, hashCode (opcionales pero recomendados)

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SizeEntity)) return false;
        SizeEntity that = (SizeEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "SizeEntity{" +
                "id=" + id +
                ", sigla='" + sigla + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}