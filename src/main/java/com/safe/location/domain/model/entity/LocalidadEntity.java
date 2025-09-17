package com.safe.location.domain.model.entity;

import com.drew.lang.annotations.NotNull;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "localidades",
        uniqueConstraints = @UniqueConstraint(name = "unique_localidad_municipio",
                columnNames = {"nombre", "municipio_id"}))
public class LocalidadEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "localidad_id")
    private Integer id;

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
    private TipoLocalidad tipo = TipoLocalidad.Ciudad;

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
        // ✅ VALORES PRINCIPALES
        Ciudad,
        Villa,
        Pueblo,
        Aldea,

        // ✅ TIPOS URBANOS
        Barrio,
        Colonia,
        Fraccionamiento,
        Zona,
        Sector,
        Urbanizacion,
        Conjunto,
        Residencial;

        // ✅ MÉTODO PARA OBTENER DESCRIPCIÓN AMIGABLE
        public String getDescripcion() {
            switch (this) {
                // Principales
                case Ciudad: return "Ciudad";
                case Villa: return "Villa";
                case Pueblo: return "Pueblo";
                case Aldea: return "Aldea";

                // Urbanos
                case Barrio: return "Barrio";
                case Colonia: return "Colonia";
                case Fraccionamiento: return "Fraccionamiento";
                case Zona: return "Zona";
                case Sector: return "Sector";
                case Urbanizacion: return "Urbanización";
                case Conjunto: return "Conjunto Habitacional";
                case Residencial: return "Zona Residencial";

                default: return this.name();
            }
        }

        // ✅ MÉTODO PARA OBTENER CATEGORÍA
        public CategoriaLocalidad getCategoria() {
            switch (this) {
                case Ciudad:
                case Villa:
                case Pueblo:
                case Aldea:
                    return CategoriaLocalidad.PRINCIPAL;

                case Barrio:
                case Colonia:
                case Fraccionamiento:
                case Zona:
                case Sector:
                case Urbanizacion:
                case Conjunto:
                case Residencial:
                    return CategoriaLocalidad.URBANA;

                default:
                    return CategoriaLocalidad.OTRA;
            }
        }

        // ✅ MÉTODO PARA OBTENER ENUM DESDE STRING DE FORMA SEGURA
        public static TipoLocalidad fromString(String valor) {
            if (valor == null || valor.trim().isEmpty()) {
                return Ciudad; // Valor por defecto
            }

            try {
                // Intentar coincidencia exacta primero
                return TipoLocalidad.valueOf(valor.trim());
            } catch (IllegalArgumentException e) {
                // Si no coincide exactamente, intentar case-insensitive
                for (TipoLocalidad tipo : TipoLocalidad.values()) {
                    if (tipo.name().equalsIgnoreCase(valor.trim())) {
                        return tipo;
                    }
                }
                // Si aún no encuentra, usar valor por defecto
                System.out.println("⚠️ Valor no reconocido para TipoLocalidad: '" + valor + "'. Usando 'Ciudad' por defecto.");
                return Ciudad;
            }
        }

        // ✅ OBTENER TODOS LOS TIPOS POR CATEGORÍA
        public static List<TipoLocalidad> getByCategoria(CategoriaLocalidad categoria) {
            return Arrays.stream(TipoLocalidad.values())
                    .filter(tipo -> tipo.getCategoria() == categoria)
                    .collect(Collectors.toList());
        }

        // ✅ VERIFICAR SI ES TIPO URBANO
        public boolean isUrbano() {
            return getCategoria() == CategoriaLocalidad.URBANA ||
                    getCategoria() == CategoriaLocalidad.PRINCIPAL;
        }


        @Override
        public String toString() {
            return getDescripcion();
        }

        // ✅ ENUM AUXILIAR PARA CATEGORIZAR
        public enum CategoriaLocalidad {
            PRINCIPAL("Localidades Principales"),
            URBANA("Zonas Urbanas"),
            OTRA("Otros Tipos");

            private final String descripcion;

            CategoriaLocalidad(String descripcion) {
                this.descripcion = descripcion;
            }

            public String getDescripcion() {
                return descripcion;
            }

            @Override
            public String toString() {
                return descripcion;
            }
        }
    }

    // Constructores
    public LocalidadEntity() {}

    public LocalidadEntity(String nombre, Integer municipioId) {
        this.nombre = nombre;
        this.municipioId = municipioId;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer localidadId) {
        this.id = localidadId;
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
            this.municipioId = municipioEntity.getId();
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
                "localidadId=" + id +
                ", nombre='" + nombre + '\'' +
                ", tipo=" + tipo +
                ", codigoPostal='" + codigoPostal + '\'' +
                ", municipioId=" + municipioId +
                '}';
    }
}