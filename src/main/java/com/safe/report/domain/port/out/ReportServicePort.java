package com.safe.report.domain.port.out;

import com.safe.report.domain.model.BikeReportDTO;

import java.util.List;

public interface ReportServicePort {
    byte[] generateBikeReportPdf(List<BikeReportDTO> bikeData, String userFullName);
}