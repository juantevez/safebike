package com.safe.bike.infrastructure.persistence.size;

import com.safe.bike.domain.model.entity.SizeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SizeJpaRepository  extends JpaRepository<SizeEntity, Integer> {

    public List<SizeEntity> findAll();
    public Optional<SizeEntity> findById(Integer id);
}
