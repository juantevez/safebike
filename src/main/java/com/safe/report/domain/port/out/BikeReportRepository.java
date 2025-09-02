package com.safe.report.domain.port.out;

import com.safe.bike.domain.model.Bike;

import java.util.List;

public interface BikeReportRepository {
    List<Bike> findAllBikesWithUserInfo();
    int countTotalUsers();
}