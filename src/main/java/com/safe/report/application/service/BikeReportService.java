package com.safe.report.application.service;


import com.safe.report.domain.model.BikeReportDTO;
import com.safe.report.domain.port.in.BikeReportUseCasePort;
import com.safe.report.domain.port.out.BikeReportRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class BikeReportService {
    private static final Logger log = LoggerFactory.getLogger(BikeReportService.class);
    private final BikeReportUseCasePort bikeReportUseCasePort;
    private final BikeReportRepositoryPort bikeReportRepositoryPort;

    public BikeReportService(BikeReportUseCasePort bikeReportUseCasePort,
                             BikeReportRepositoryPort bikeReportRepositoryPort) {
        this.bikeReportUseCasePort = bikeReportUseCasePort;
        this.bikeReportRepositoryPort = bikeReportRepositoryPort;
    }

    /**
     * Genera el reporte de bicicletas en formato PDF para un usuario específico
     *
     * @param userId ID del usuario
     * @return ByteArrayInputStream con el PDF generado
     * @throws IllegalArgumentException si no se encuentran bicicletas para el usuario
     * @throws RuntimeException si ocurre un error durante la generación del PDF
     */
    public ByteArrayInputStream generateBikeReportPdf(Long userId) {
        try {
            byte[] pdfBytes = bikeReportUseCasePort.generateBikeReport(userId);
            return new ByteArrayInputStream(pdfBytes);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("No se encontraron bicicletas para el usuario con ID: " + userId, e);
        } catch (Exception e) {
            throw new RuntimeException("Error generando el reporte PDF para el usuario " + userId, e);
        }
    }

    /**
     * Obtiene los datos de las bicicletas para previsualización
     *
     * @param userId ID del usuario
     * @return Lista de BikeReportDTO con los datos de las bicicletas
     */
    public List<BikeReportDTO> getBikeDataForUser(Long userId) {
        List<BikeReportDTO> bikeData = bikeReportRepositoryPort.findBikesByUserId(userId);
        if (bikeData.isEmpty()) {
            throw new IllegalArgumentException("No se encontraron bicicletas para el usuario con ID: " + userId);
        }
        return bikeData;
    }

    /**
     * Valida si un usuario tiene bicicletas registradas
     *
     * @param userId ID del usuario
     * @return true si el usuario tiene bicicletas, false en caso contrario
     */
    public boolean userHasBikes(Long userId) {
        log.info("userHasBikes {} userId " + userId);

        try {
            List<BikeReportDTO> bikeData = bikeReportRepositoryPort.findBikesByUserId(userId);
            log.info("boolean userHasBikes " + bikeData.isEmpty());
            return !bikeData.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Genera el nombre del archivo PDF basado en el usuario y la fecha actual
     *
     * @param userId ID del usuario
     * @return Nombre del archivo sugerido
     */
    public String generateFileName(Long userId) {
        return String.format("reporte_bicicletas_%d_%s.pdf",
                userId,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));
    }

    /**
     * Obtiene información resumida del reporte
     *
     * @param userId ID del usuario
     * @return ReportSummary con información del reporte
     */
    public ReportSummary getReportSummary(Long userId) {
        List<BikeReportDTO> bikeData = getBikeDataForUser(userId);

        if (bikeData.isEmpty()) {
            return new ReportSummary("", 0, LocalDateTime.now());
        }

        String userFullName = bikeData.get(0).getFullName();
        int totalBikes = bikeData.size();
        LocalDateTime generatedAt = LocalDateTime.now();

        return new ReportSummary(userFullName, totalBikes, generatedAt);
    }

    /**
     * Record que representa un resumen del reporte
     */
    public record ReportSummary(
            String userFullName,
            int totalBikes,
            LocalDateTime generatedAt
    ) {
        public String getFormattedDate() {
            return generatedAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        }

        public String getSummaryText() {
            return String.format("Reporte para %s - %d bicicleta(s) - Generado: %s",
                    userFullName, totalBikes, getFormattedDate());
        }
    }
}