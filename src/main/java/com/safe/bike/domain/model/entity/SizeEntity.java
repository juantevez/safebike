package com.safe.bike.domain.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "size_bike")
public class SizeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "sigla", length = 20, nullable = false)
    private String sigla;

    @Size(max = 100, message = "El nombre de la moneda no puede exceder 50 caracteres")
    @Column(name = "description", length = 100, nullable = false)
    private String sizeDescription;

    public SizeEntity() {   }

    public SizeEntity(Integer id, String sigla, String sizeDescription) {
        this.id = id;
        this.sigla = sigla;
        this.sizeDescription = sizeDescription;
    }

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

    public String getSizeDescription() {
        return sizeDescription;
    }

    public void setSizeDescription(String sizeDescription) {
        this.sizeDescription = sizeDescription;
    }
}
