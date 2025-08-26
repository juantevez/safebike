package com.safe.bike.infrastructure.persistence.size;

import com.safe.bike.domain.model.entity.SizeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SizeJpaRepository extends JpaRepository<SizeEntity, Integer> {

    List<SizeEntity> findAll();
    Optional<SizeEntity> findById(Integer id);


    @Query(value = """
    SELECT sb.id, sb.sigla, sb.description
    FROM size_bike sb
    INNER JOIN bike_model_size bms ON sb.id = bms.size_id
    WHERE bms.model_id = :modelId
    ORDER BY sb.sigla
    """,
            nativeQuery = true)
    List<SizeEntity> findByModelId(@Param("modelId") Long modelId);
}