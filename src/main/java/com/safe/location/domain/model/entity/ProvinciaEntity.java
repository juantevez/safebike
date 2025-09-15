package com.safe.location.domain.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "provincias", schema = "public")
public class ProvinciaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "provincia_id")
    private Integer id;

    @NotBlank
    @Size(max = 100)
    @Column(name = "nombre", nullable = false, unique = true, length = 100)
    private String nombre;

    @Size(max = 10)
    @Column(name = "codigo_provincia", unique = true, length = 10)
    private String codigoProvincia;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "provinciaEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MunicipioEntity> municipioEntities = new ArrayList<>();

    // Constructores
    public ProvinciaEntity() {}

    public ProvinciaEntity(String nombre) {
        this.nombre = nombre;
    }

    public ProvinciaEntity(String nombre, String codigoProvincia) {
        this.nombre = nombre;
        this.codigoProvincia = codigoProvincia;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer provinciaId) {
        this.id = provinciaId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodigoProvincia() {
        return codigoProvincia;
    }

    public void setCodigoProvincia(String codigoProvincia) {
        this.codigoProvincia = codigoProvincia;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<MunicipioEntity> getMunicipios() {
        return municipioEntities;
    }

    public void setMunicipios(List<MunicipioEntity> municipioEntities) {
        this.municipioEntities = municipioEntities;
    }

    // Métodos de utilidad
    public void addMunicipio(MunicipioEntity municipioEntity) {
        municipioEntities.add(municipioEntity);
        municipioEntity.setProvincia(this);
    }

    public void removeMunicipio(MunicipioEntity municipioEntity) {
        municipioEntities.remove(municipioEntity);
        municipioEntity.setProvincia(null);
    }

    @Override
    public String toString() {
        return "ProvinciaEntity{" +
                "provinciaId=" + id +
                ", nombre='" + nombre + '\'' +
                ", codigoProvincia='" + codigoProvincia + '\'' +
                '}';
    }
}