package com.safe.bike.domain.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "moneda")
public class MonedaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotBlank(message = "El país es obligatorio")
    @Size(max = 50, message = "El país no puede exceder 50 caracteres")
    @Column(name = "pais", length = 50, nullable = false)
    private String pais;

    @NotBlank(message = "El nombre de la moneda es obligatorio")
    @Size(max = 50, message = "El nombre de la moneda no puede exceder 50 caracteres")
    @Column(name = "nombre_moneda", length = 50, nullable = false)
    private String nombreMoneda;

    @Column(name = "codigo_moneda", length = 3, nullable = false, columnDefinition = "bpchar(3)")
    private String codigoMoneda;


    // Constructor por defecto
    public MonedaEntity() {}

    // Constructor con parámetros
    public MonedaEntity(String pais, String nombreMoneda, String codigoMoneda) {
        this.pais = pais;
        this.nombreMoneda = nombreMoneda;
        this.codigoMoneda = codigoMoneda;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getNombreMoneda() {
        return nombreMoneda;
    }

    public void setNombreMoneda(String nombreMoneda) {
        this.nombreMoneda = nombreMoneda;
    }

    public String getCodigoMoneda() {
        return codigoMoneda;
    }

    public void setCodigoMoneda(String codigoMoneda) {
        this.codigoMoneda = codigoMoneda;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MonedaEntity monedaEntity = (MonedaEntity) o;

        if (id != null ? !id.equals(monedaEntity.id) : monedaEntity.id != null) return false;
        if (pais != null ? !pais.equals(monedaEntity.pais) : monedaEntity.pais != null) return false;
        if (nombreMoneda != null ? !nombreMoneda.equals(monedaEntity.nombreMoneda) : monedaEntity.nombreMoneda != null)
            return false;
        return codigoMoneda != null ? codigoMoneda.equals(monedaEntity.codigoMoneda) : monedaEntity.codigoMoneda == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (pais != null ? pais.hashCode() : 0);
        result = 31 * result + (nombreMoneda != null ? nombreMoneda.hashCode() : 0);
        result = 31 * result + (codigoMoneda != null ? codigoMoneda.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MonedaEntity{" +
                "id=" + id +
                ", pais='" + pais + '\'' +
                ", nombreMoneda='" + nombreMoneda + '\'' +
                ", codigoMoneda='" + codigoMoneda + '\'' +
                '}';
    }
}