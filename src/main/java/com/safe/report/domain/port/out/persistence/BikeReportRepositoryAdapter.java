package com.safe.report.domain.port.out.persistence;


import com.safe.report.domain.model.BikeReportDTO;
import com.safe.report.domain.port.out.BikeReportRepositoryPort;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Repository
public class BikeReportRepositoryAdapter implements BikeReportRepositoryPort {
    private static final Logger log = LoggerFactory.getLogger(BikeReportRepositoryAdapter.class);
    private final EntityManager entityManager;

    public BikeReportRepositoryAdapter(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    @Override
    public List<BikeReportDTO> findBikesByUserId(Long userId) {

        log.info("findBikesByUserId {} userId: " + userId);

        String sql = """
            SELECT
                b.bike_id,
                u.first_name AS firstname,
                u.last_name as lastname,
                br.name as brand,
                bm.model_name as model,
                bt.name AS type,
                b.serial_number as "serial_number",
                b.purchase_date
            FROM
                bike b
            INNER JOIN users u ON b.user_id = u.id
            INNER JOIN brand br ON b.brand_id = br.brand_id
            INNER JOIN bike_type bt ON b.bike_type_id = bt.bike_type_id
            INNER JOIN moneda mo ON b.moneda_id = mo.id
            LEFT JOIN bike_model bm ON b.model_bike_id = bm.id_bike_model
            WHERE
                u.id = :userId
            ORDER BY
                b.created_at DESC
            """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("userId", userId);

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();

        log.info("Query results: {}", results.size());
        results.forEach(row -> log.info("Row: {}", Arrays.toString(row)));

        return results.stream()
                .map(this::mapToBikeReportDTO)
                .toList();
    }

    private BikeReportDTO mapToBikeReportDTO(Object[] result) {
        log.info(">>> Iniciando mapeo individual de: {}", java.util.Arrays.toString(result));

        try {
            Long bikeId = convertToLong(result[0]);          // <-- AQUÍ se invoca convertToLong
            log.debug(">>> bikeId: {}", bikeId);

            String firstName = (String) result[1];
            String lastName = (String) result[2];
            String brand = (String) result[3];
            String model = (String) result[4];
            String type = (String) result[5];
            String serialNumber = (String) result[6];

            LocalDate purchaseDate = convertToLocalDate(result[7]); // <-- AQUÍ se invoca convertToLocalDate
            log.debug(">>> purchaseDate: {}", purchaseDate);

            BikeReportDTO dto = new BikeReportDTO(
                    bikeId,           // Resultado de convertToLong(result[0])
                    firstName,
                    lastName,
                    brand,
                    model,
                    type,
                    serialNumber,
                    purchaseDate      // Resultado de convertToLocalDate(result[7])
            );

            log.info(">>> DTO creado: {}", dto);
            return dto;

        } catch (Exception e) {
            log.error(">>> ERROR en mapeo individual: {}", e.getMessage(), e);
            throw e;
        }
    }

    private Long convertToLong(Object value) {
        if (value == null) return null;
        if (value instanceof Long) return (Long) value;
        if (value instanceof Integer) return ((Integer) value).longValue();
        if (value instanceof BigInteger) return ((BigInteger) value).longValue();
        if (value instanceof Number) return ((Number) value).longValue();
        return Long.parseLong(value.toString());
    }

    private LocalDate convertToLocalDate(Object value) {
        if (value == null) return null;
        if (value instanceof LocalDate) return (LocalDate) value;
        if (value instanceof java.sql.Date) return ((java.sql.Date) value).toLocalDate();
        if (value instanceof java.sql.Timestamp) return ((java.sql.Timestamp) value).toLocalDateTime().toLocalDate();
        if (value instanceof java.util.Date) return new java.sql.Date(((java.util.Date) value).getTime()).toLocalDate();
        if (value instanceof String) return LocalDate.parse((String) value);
        return null;
    }
}