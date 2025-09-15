package com.safe.location.domain.model.entity;

import com.drew.lang.annotations.NotNull;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "municipios", schema = "public",
        uniqueConstraints = @UniqueConstraint(columnNames = {"nombre", "provincia_id"}))
public class MunicipioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "municipio_id")
    private Integer id;

    @NotNull
    @Column(name = "provincia_id", nullable = false)
    private Integer provinciaId;

    @NotBlank
    @Size(max = 150)
    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Size(max = 20)
    @Column(name = "codigo_municipio", length = 20)
    private String codigoMunicipio;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", length = 20)
    private TipoMunicipio tipo = TipoMunicipio.MUNICIPIO;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provincia_id", insertable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_municipio_provincia"))
    private ProvinciaEntity provinciaEntity;

    /*@OneToMany(mappedBy = "municipio", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LocalidadEntity> localidades = new ArrayList<>();*/

    @OneToMany(mappedBy = "municipioEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LocalidadEntity> localidades = new ArrayList<>();

    // Enum para tipos de municipio
    public enum TipoMunicipio {
        MUNICIPIO("MunicipioEntity"),
        DEPARTAMENTO("Departamento"),
        PARTIDO("Partido"),
        COMUNA("Comuna");

        private final String displayName;

        TipoMunicipio(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Constructores
    public MunicipioEntity() {}

    public MunicipioEntity(String nombre, Integer provinciaId) {
        this.nombre = nombre;
        this.provinciaId = provinciaId;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer municipioId) {
        this.id = municipioId;
    }

    public Integer getProvinciaId() {
        return provinciaId;
    }

    public void setProvinciaId(Integer provinciaId) {
        this.provinciaId = provinciaId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodigoMunicipio() {
        return codigoMunicipio;
    }

    public void setCodigoMunicipio(String codigoMunicipio) {
        this.codigoMunicipio = codigoMunicipio;
    }

    public TipoMunicipio getTipo() {
        return tipo;
    }

    public void setTipo(TipoMunicipio tipo) {
        this.tipo = tipo;
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

    public ProvinciaEntity getProvincia() {
        return provinciaEntity;
    }

    public void setProvincia(ProvinciaEntity provinciaEntity) {
        this.provinciaEntity = provinciaEntity;
        if (provinciaEntity != null) {
            this.provinciaId = provinciaEntity.getId();
        }
    }

    public List<LocalidadEntity> getLocalidades() {
        return localidades;
    }

    public void setLocalidades(List<LocalidadEntity> localidades) {
        this.localidades = localidades;
    }

    // Métodos de utilidad
    public void addLocalidad(LocalidadEntity localidadEntity) {
        localidades.add(localidadEntity);
        localidadEntity.setMunicipio(this);
    }

    public void removeLocalidad(LocalidadEntity localidadEntity) {
        localidades.remove(localidadEntity);
        localidadEntity.setMunicipio(null);
    }

    @Override
    public String toString() {
        return "MunicipioEntity{" +
                "municipioId=" + id +
                ", nombre='" + nombre + '\'' +
                ", tipo=" + tipo +
                ", provinciaId=" + provinciaId +
                '}';
    }
}