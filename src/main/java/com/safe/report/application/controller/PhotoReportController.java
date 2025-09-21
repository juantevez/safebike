package com.safe.report.application.controller;

import com.safe.report.domain.usecase.PhotoReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/reports")
public class PhotoReportController {

    private final PhotoReportService photoReportService;

    public PhotoReportController(PhotoReportService photoReportService) {
        this.photoReportService = photoReportService;
    }

    @GetMapping("/bike/{bikeId}/photos/pdf")
    public ResponseEntity<byte[]> generatePhotoPdf(@PathVariable Long bikeId) {
        try {
            byte[] pdfBytes = photoReportService.generatePdfReportByBikeId(bikeId);

            String fileName = String.format("fotos_bici_%d_%s.pdf",
                    bikeId, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", fileName);
            headers.setContentLength(pdfBytes.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}