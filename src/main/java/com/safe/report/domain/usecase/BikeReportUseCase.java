package com.safe.report.domain.usecase;



import com.safe.report.domain.model.BikeReportDTO;
import com.safe.report.domain.port.in.BikeReportUseCasePort;
import com.safe.report.domain.port.out.BikeReportRepositoryPort;
import com.safe.report.domain.port.out.ReportServicePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BikeReportUseCase implements BikeReportUseCasePort {

    private static final Logger logger = LoggerFactory.getLogger(BikeReportUseCase.class);
    private final BikeReportRepositoryPort bikeReportRepositoryPort;
    private final ReportServicePort reportServicePort;

    public BikeReportUseCase(BikeReportRepositoryPort bikeRepositoryPort,
                             ReportServicePort reportServicePort) {
        this.bikeReportRepositoryPort = bikeRepositoryPort;
        this.reportServicePort = reportServicePort;
    }

    @Override
    public byte[] generateBikeReport(Long userId) {
        logger.debug("Iniciando generación de reporte para usuario ID: {}", userId);

        List<BikeReportDTO> bikeData = bikeReportRepositoryPort.findBikesByUserId(userId);
        logger.debug("Datos obtenidos del repositorio: {} bicicletas", bikeData.size());

        if (bikeData.isEmpty()) {
            logger.warn("No se encontraron bicicletas para el usuario ID: {}", userId);
            throw new IllegalArgumentException("No bikes found for user with ID: " + userId);
        }

        String userFullName = bikeData.get(0).getFullName();
        logger.debug("Generando PDF para usuario: {}", userFullName);

        try {
            byte[] pdfBytes = reportServicePort.generateBikeReportPdf(bikeData, userFullName);
            logger.debug("PDF generado exitosamente. Tamaño: {} bytes", pdfBytes.length);
            return pdfBytes;
        } catch (Exception e) {
            logger.error("Error generando PDF para usuario {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Error generating PDF report", e);
        }
    }
}