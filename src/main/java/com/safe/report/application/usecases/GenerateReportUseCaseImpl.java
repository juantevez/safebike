package com.safe.report.application.usecases;

import com.safe.bike.domain.model.Bike;
import com.safe.report.domain.port.in.GenerateReportUseCase;
import com.safe.report.domain.model.BikeReportData;
import com.safe.report.domain.port.out.BikeReportRepository;
import com.safe.report.domain.port.out.PdfGeneratorPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GenerateReportUseCaseImpl implements GenerateReportUseCase {

    private final BikeReportRepository bikeReportRepository;
    private final PdfGeneratorPort pdfGeneratorPort;

    @Override
    public byte[] generateBikeReport() {
        List<Bike> bikes = bikeReportRepository.findAllBikesWithUserInfo();
        int totalUsers = bikeReportRepository.countTotalUsers();

        BikeReportData reportData = BikeReportData.builder()
                .bikes(bikes)
                .totalBikes(bikes.size())
                .totalUsers(totalUsers)
                .reportGeneratedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
                .build();

        return pdfGeneratorPort.generateBikeReport(reportData);
    }
}