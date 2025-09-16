package com.safe.user.application.usecases;

import com.safe.user.domain.model.entity.User;
import com.safe.user.domain.ports.UserRepositoryPort;
import com.safe.user.domain.model.entity.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * ✅ Caso de uso para buscar usuarios - implementación inline por compatibilidad
 */
public class FindUserUseCase {
    private static final Logger logger = LoggerFactory.getLogger(FindUserUseCase.class);
    private final UserRepositoryPort userRepository;

    public FindUserUseCase(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    public FindUserUseCase(UserRepositoryPort userRepositoryPort, UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<UserEntity> findById(Long id) {
        logger.debug("Ejecutando FindUserUseCase.findById para ID: {}", id);
        return userRepository.findById(id);
    }

    public User findByEmail(String emailValue) {
        logger.debug("Ejecutando FindUserUseCase.findByEmail para email: {}", emailValue);
        return userRepository.findByEmail(emailValue);
    }

    public List<UserEntity> findAll() {
        logger.debug("Ejecutando FindUserUseCase.findAll");
        return userRepository.findAll();
    }
}