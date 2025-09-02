package com.safe.report.domain.port.out;


import com.safe.report.domain.model.BikeReportData;

public interface PdfGeneratorPort {
    byte[] generateBikeReport(BikeReportData reportData);
}
