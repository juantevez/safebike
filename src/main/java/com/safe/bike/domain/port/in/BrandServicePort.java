package com.safe.bike.domain.port.in;

import com.safe.bike.domain.model.Brand;

import java.util.List;

/**
 * Puerto de entrada para definir los casos de uso relacionados con marcas.
 */
public interface BrandServicePort {

    /**
     * Obtiene todas las marcas disponibles.
     */
    List<Brand> getAllBrands();

    /**
     * Obtiene solo los nombres de las marcas.
     */
    List<String> getAllBrandNames();
}