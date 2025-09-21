package com.safe.report.domain.usecase;

public interface PhotoReportService {
    byte[] generatePdfReportByBikeId(Long bikeId);
}
