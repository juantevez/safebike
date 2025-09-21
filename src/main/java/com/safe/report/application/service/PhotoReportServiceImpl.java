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
        logger.info("generatePdfReportByBikeId {} bikeId ", bikeId);
        List<PhotoFile> photos = repository.findByBikeId(bikeId);
        if (photos.isEmpty()) {
            throw new RuntimeException("No se encontraron fotos para la bicicleta con ID: " + bikeId);
        }
        try {
            return pdfGenerator.generatePdf(photos);
        } catch (Exception e) {
            throw new RuntimeException("Error generando el PDF", e);
        }
    }
}
