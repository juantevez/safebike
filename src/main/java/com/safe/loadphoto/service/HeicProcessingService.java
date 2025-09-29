package com.safe.loadphoto.service;

import com.safe.loadphoto.domain.model.response.ExifDataResponse;
import com.safe.loadphoto.domain.model.response.HeicProcessingResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class HeicProcessingService {
    private static final Logger logger = LoggerFactory.getLogger(HeicProcessingService.class);

    private final RestTemplate restTemplate;
    private final String golangServiceUrl;

    public HeicProcessingService(@Value("${golang.heic.service.url:http://localhost:8090}") String golangServiceUrl) {
        this.golangServiceUrl = golangServiceUrl;
        this.restTemplate = createRestTemplate();
    }
    private RestTemplate createRestTemplate() {
        // Usar SimpleClientHttpRequestFactory (incluido en Spring Boot)
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(30000); // 30 segundos en milisegundos
        factory.setReadTimeout(60000);    // 60 segundos en milisegundos

        return new RestTemplate(factory);
    }

    public HeicProcessingResponse processHeicFile(byte[] heicData, String fileName, Long bikeId) {
        try {
            if (heicData == null || heicData.length == 0) {
                throw new IllegalArgumentException("HEIC data cannot be null or empty");
            }

            logger.info("Procesando archivo HEIC: {} ({} bytes) para bike_id: {}", fileName, heicData.length, bikeId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            // 1. Campo 'photo' - el archivo
            ByteArrayResource fileResource = new ByteArrayResource(heicData) {
                @Override
                public String getFilename() {
                    return fileName;
                }
            };
            body.add("photo", fileResource);

            // 2. Campo 'bike_id' - ID de la bicicleta
            body.add("bike_id", bikeId.toString());

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            String url = golangServiceUrl + "/api/v1/photos";

            ResponseEntity<HeicProcessingResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    HeicProcessingResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                HeicProcessingResponse responseBody = response.getBody();

                logger.info("✅ Archivo HEIC procesado exitosamente: {}", fileName);

                return responseBody;
            } else {
                throw new RuntimeException("Error en respuesta del servicio Golang: " + response.getStatusCode());
            }

        } catch (HttpClientErrorException e) {
            logger.error("❌ Error HTTP del servicio Golang: Status={}, Body={}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Error del servicio Golang: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            logger.error("❌ Error al procesar archivo HEIC: {}", fileName, e);
            throw new RuntimeException("Error al procesar archivo HEIC", e);
        }
    }

}