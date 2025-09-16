package com.safe.user.infrastructure.adapters.output.persistence.repository;


import com.safe.user.domain.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByEmailIgnoreCase(String email);

}