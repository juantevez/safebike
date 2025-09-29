package com.safe.location.openstreet.model.dto;

import java.util.ArrayList;
import java.util.List;

public class UbicacionDTO {
    private String ciudad;
    private String provincia;
    private String pais;

    public UbicacionDTO(String ciudad, String provincia, String pais) {
        this.ciudad = ciudad;
        this.provincia = provincia;
        this.pais = pais;
    }

    // Getters, setters, toString

    public String getUbicacionCompleta() {
        List<String> partes = new ArrayList<>();
        if (!ciudad.isEmpty()) partes.add(ciudad);
        if (!provincia.isEmpty()) partes.add(provincia);
        if (!pais.isEmpty()) partes.add(pais);

        return String.join(", ", partes);
    }
}