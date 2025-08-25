package com.safe.bike.domain.port.out;

import com.safe.bike.domain.model.entity.SizeEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface SizeRepositoryPort {

        List<SizeEntity> findAll();
        Optional<SizeEntity> findById(Integer id);

}

