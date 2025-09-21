package com.safe.report.application.service;

import com.safe.loadphoto.domain.model.PhotoFile;
import com.safe.report.domain.usecase.PhotoReportService;
import com.safe.report.infrastructure.adapter.PhotoFileRepositoryAdapter;
import com.safe.report.infrastructure.pdf.PdfGeneratorAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhotoReportServiceImpl implements PhotoReportService {
    private static final Logger logger = LoggerFactory.getLogger(PhotoReportServiceImpl.class);
    private final PhotoFileRepositoryAdapter repository;
    private final PdfGeneratorAdapter pdfGenerator;

    public PhotoReportServiceImpl(PhotoFileRepositoryAdapter repository, PdfGeneratorAdapter pdfGenerator) {
        this.repository = repository;
        this.pdfGenerator = pdfGenerator;
    }

    @Override
    public byte[] generatePdfReportByBikeId(Long bikeId) {
        logger.info("=== INICIO generatePdfReportByBikeId para bikeId: {} ===", bikeId);

        try {
            List<PhotoFile> photos = repository.findByBikeId(bikeId);
            logger.info("Fotos encontradas: {}", photos.size());

            if (photos.isEmpty()) {
                logger.warn("No se encontraron fotos para bikeId: {}", bikeId);
                throw new RuntimeException("No se encontraron fotos para la bicicleta con ID: " + bikeId);
            }

            // Log detalle de las fotos
            for (int i = 0; i < photos.size(); i++) {
                PhotoFile photo = photos.get(i);
                logger.info("Foto {}: id={}, fileName={}, dataSize={}",
                        i + 1, photo.getId(), photo.getFileName(),
                        photo.getFileData() != null ? photo.getFileData().length : "NULL");
            }

            logger.info("Llamando a pdfGenerator.generatePdf...");
            byte[] pdfBytes = pdfGenerator.generatePdf(photos);

            logger.info("PDF generado exitosamente. Tamaño: {} MegaBytes",
                    pdfBytes != null ? pdfBytes.length/1024/1024 : "NULL");

            if (pdfBytes == null || pdfBytes.length == 0) {
                throw new RuntimeException("El generador de PDF retornó datos vacíos");
            }

            logger.info("=== FIN generatePdfReportByBikeId EXITOSO ===");
            return pdfBytes;

        } catch (Exception e) {
            logger.error("=== ERROR en generatePdfReportByBikeId para bikeId: {} ===", bikeId, e);
            throw new RuntimeException("Error generando el PDF: " + e.getMessage(), e);
        }
    }
}
