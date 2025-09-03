package com.safe.report.domain.port.out;

import com.safe.bike.domain.model.entity.BikeEntity;

import java.util.List;

public interface BikeReportPort {
    List<BikeEntity> findBikesByUserIdWithDetails(Long userId);
    //int countTotalUsers();
}