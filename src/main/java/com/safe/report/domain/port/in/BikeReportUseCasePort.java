package com.safe.report.domain.port.in;

public interface BikeReportUseCasePort {
    byte[] generateBikeReport(Long userId);
}