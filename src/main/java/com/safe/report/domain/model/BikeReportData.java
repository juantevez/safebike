package com.safe.report.domain.model;

import com.safe.bike.domain.model.Bike;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BikeReportData {
    private List<Bike> bikes;
    private int totalBikes;
    private int totalUsers;
    private String reportGeneratedAt;
}