package com.safe.report.application.usecases;

import com.safe.bike.domain.model.Bike;
import com.safe.bike.domain.model.entity.BikeEntity;
import com.safe.report.domain.model.BikeReportData;
import com.safe.report.domain.port.in.GenerateReportUseCase;
import com.safe.report.domain.port.out.PdfGeneratorPort;
import com.safe.report.domain.port.out.BikeReportPort;
import com.safe.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GenerateReportUseCaseImpl implements GenerateReportUseCase {

    private final BikeReportPort bikeReportPort; // ✅ Inyecta el puerto, no el adaptador
    private final PdfGeneratorPort pdfGeneratorPort;

    @Override
    public byte[] generateBikeReport() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            throw new IllegalStateException("Usuario no autenticado");
        }

        List<BikeEntity> bikes = bikeReportPort.findBikesByUserIdWithDetails(userId);
        int totalBikes = bikes.size();
       // int totalUsers = bikeReportPort.countTotalUsers();

        BikeReportData reportData = BikeReportData.builder()
                .bikes(bikes)
                .totalBikes(totalBikes)
         //       .totalUsers(totalUsers)
                .reportGeneratedAt(LocalDateTime.now().toString())
                .build();

        return pdfGeneratorPort.generateBikeReport(reportData);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        User user = (User) authentication.getPrincipal();
        return user.getId();
    }
}