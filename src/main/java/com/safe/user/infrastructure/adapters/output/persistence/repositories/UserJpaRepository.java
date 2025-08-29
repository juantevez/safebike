package com.safe.user.infrastructure.adapters.output.persistence.repositories;


import com.safe.user.infrastructure.adapters.output.persistence.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByEmailIgnoreCase(String email);

}