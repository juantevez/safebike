package com.safe.location.openstreet.service;

import com.safe.location.openstreet.model.dto.NominatimAddress;
import com.safe.location.openstreet.model.dto.NominatimResponse;
import com.safe.location.openstreet.model.dto.UbicacionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Optional;

@Service
public class NominatimGeocodingService {
    private static final Logger logger = LoggerFactory.getLogger(NominatimGeocodingService.class);
    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/reverse";

    private final WebClient webClient;

    public NominatimGeocodingService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl(NOMINATIM_URL)
                .defaultHeader("User-Agent", "MiApp/1.0")
                .build();
    }

    public UbicacionDTO obtenerUbicacion(double latitud, double longitud) {
        try {
            NominatimResponse response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("format", "json")
                            .queryParam("lat", latitud)
                            .queryParam("lon", longitud)
                            .queryParam("accept-language", "es")
                            .build())
                    .retrieve()
                    .bodyToMono(NominatimResponse.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();

            if (response != null && response.getAddress() != null) {
                return procesarResultadoNominatim(response);
            }

            return new UbicacionDTO("Ubicación no encontrada", "", "");

        } catch (Exception e) {
            logger.error("Error al obtener ubicación desde Nominatim: {}", e.getMessage());
            return new UbicacionDTO("Error al obtener ubicación", "", "");
        }
    }

    private UbicacionDTO procesarResultadoNominatim(NominatimResponse response) {
        NominatimAddress address = response.getAddress();

        String ciudad = Optional.ofNullable(address.getCity())
                .or(() -> Optional.ofNullable(address.getTown()))
                .or(() -> Optional.ofNullable(address.getVillage()))
                .or(() -> Optional.ofNullable(address.getSuburb()))
                .orElse("");

        String provincia = Optional.ofNullable(address.getState()).orElse("");
        String pais = Optional.ofNullable(address.getCountry()).orElse("");

        return new UbicacionDTO(ciudad, provincia, pais);
    }
}