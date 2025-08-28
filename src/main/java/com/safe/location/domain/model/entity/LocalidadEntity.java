package com.safe.location.domain.model.entity;

import com.drew.lang.annotations.NotNull;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "localidades", schema = "public",
        uniqueConstraints = @UniqueConstraint(name = "unique_localidad_municipio",
                columnNames = {"nombre", "municipio_id"}))
public class LocalidadEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "localidad_id")
    private Integer localidadId;

    @NotNull
    @Column(name = "municipio_id", nullable = false)
    private Integer municipioId;

    @NotBlank
    @Size(max = 150)
    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Size(max = 10)
    @Column(name = "codigo_postal", length = 10)
    private String codigoPostal;

    @Size(max = 20)
    @Column(name = "codigo_localidad", length = 20)
    private String codigoLocalidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", length = 20)
    private TipoLocalidad tipo = TipoLocalidad.CIUDAD;

    @Column(name = "latitud", precision = 10, scale = 8)
    private BigDecimal latitud;

    @Column(name = "longitud", precision = 11, scale = 8)
    private BigDecimal longitud;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "municipio_id", insertable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_localidad_municipio"))
    private MunicipioEntity municipioEntity;

    // Enum para tipos de localidad
    public enum TipoLocalidad {
        CIUDAD("Ciudad"),
        PUEBLO("Pueblo"),
        VILLA("Villa"),
        BARRIO("Barrio"),
        PARAJE("Paraje"),
        COLONIA("Colonia");

        private final String displayName;

        TipoLocalidad(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Constructores
    public LocalidadEntity() {}

    public LocalidadEntity(String nombre, Integer municipioId) {
        this.nombre = nombre;
        this.municipioId = municipioId;
    }

    // Getters y Setters
    public Integer getLocalidadId() {
        return localidadId;
    }

    public void setLocalidadId(Integer localidadId) {
        this.localidadId = localidadId;
    }

    public Integer getMunicipioId() {
        return municipioId;
    }

    public void setMunicipioId(Integer municipioId) {
        this.municipioId = municipioId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public String getCodigoLocalidad() {
        return codigoLocalidad;
    }

    public void setCodigoLocalidad(String codigoLocalidad) {
        this.codigoLocalidad = codigoLocalidad;
    }

    public TipoLocalidad getTipo() {
        return tipo;
    }

    public void setTipo(TipoLocalidad tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getLatitud() {
        return latitud;
    }

    public void setLatitud(BigDecimal latitud) {
        this.latitud = latitud;
    }

    public BigDecimal getLongitud() {
        return longitud;
    }

    public void setLongitud(BigDecimal longitud) {
        this.longitud = longitud;
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

    public MunicipioEntity getMunicipio() {
        return municipioEntity;
    }

    public void setMunicipio(MunicipioEntity municipioEntity) {
        this.municipioEntity = municipioEntity;
        if (municipioEntity != null) {
            this.municipioId = municipioEntity.getMunicipioId();
        }
    }

    // Métodos de utilidad para coordenadas
    public void setCoordenadas(BigDecimal latitud, BigDecimal longitud) {
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public boolean tieneCoordenadas() {
        return latitud != null && longitud != null;
    }

    @Override
    public String toString() {
        return "LocalidadEntity{" +
                "localidadId=" + localidadId +
                ", nombre='" + nombre + '\'' +
                ", tipo=" + tipo +
                ", codigoPostal='" + codigoPostal + '\'' +
                ", municipioId=" + municipioId +
                '}';
    }
}