package com.safe.report.domain.model;

import com.safe.bike.domain.model.entity.BikeEntity;
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
    private List<BikeEntity> bikes;
    private int totalBikes;
    //private int totalUsers;
    private String reportGeneratedAt;
}