package com.safe.report.infrastructure.persistence.adapter;

import com.safe.bike.domain.model.Bike;
import com.safe.bike.domain.model.entity.BikeEntity;
import com.safe.report.domain.port.out.BikeReportPort;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BikeReportRepositoryAdapter implements BikeReportPort {

    private final BikeReportJpaRepository jpaRepository; // ✅ Inyecta el JPA repo

    public BikeReportRepositoryAdapter(BikeReportJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<BikeEntity> findBikesByUserIdWithDetails(Long userId) {
        return jpaRepository.findBikesByUserIdWithDetails(userId);
    }

    /*@Override
    public int countTotalUsers() {
        return jpaRepository.countTotalUsers();
    }*/
}