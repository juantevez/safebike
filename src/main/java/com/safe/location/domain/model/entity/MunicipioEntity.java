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
@Table(name = "municipios",
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
    private TipoMunicipio tipo = TipoMunicipio.Municipio;

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

    @OneToMany(mappedBy = "municipioEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LocalidadEntity> localidades = new ArrayList<>();

    // Enum para tipos de municipio
    public enum TipoMunicipio {
        Municipio,    // ✅ CAMBIAR A FORMATO DE TU BD
        Comuna,       // ✅ AGREGAR ESTE VALOR CON MAYÚSCULA INICIAL
        Ciudad,
        Villa,
        Pueblo,
        Distrito,
        Canton,
        Partido,
        Delegacion,
        Alcaldia;

        // ✅ MÉTODO PARA OBTENER DESCRIPCIÓN AMIGABLE
        public String getDescripcion() {
            switch (this) {
                case Municipio: return "Municipio";
                case Comuna: return "Comuna";
                case Ciudad: return "Ciudad";
                case Villa: return "Villa";
                case Pueblo: return "Pueblo";
                case Distrito: return "Distrito";
                case Canton: return "Cantón";
                case Partido: return "Partido";
                case Delegacion: return "Delegación";
                case Alcaldia: return "Alcaldía";
                default: return this.name();
            }
        }

        // ✅ MÉTODO PARA OBTENER ENUM DESDE STRING DE FORMA SEGURA
        public static TipoMunicipio fromString(String valor) {
            if (valor == null || valor.trim().isEmpty()) {
                return Municipio; // Valor por defecto
            }

            try {
                // Intentar coincidencia exacta primero
                return TipoMunicipio.valueOf(valor.trim());
            } catch (IllegalArgumentException e) {
                // Si no coincide exactamente, intentar case-insensitive
                for (TipoMunicipio tipo : TipoMunicipio.values()) {
                    if (tipo.name().equalsIgnoreCase(valor.trim())) {
                        return tipo;
                    }
                }
                // Si aún no encuentra, usar valor por defecto
                System.out.println("⚠️ Valor no reconocido para TipoMunicipio: '" + valor + "'. Usando 'Municipio' por defecto.");
                return Municipio;
            }
        }

        @Override
        public String toString() {
            return getDescripcion();
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