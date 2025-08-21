package com.safe.user.adapter.out.persistence;


import com.safe.user.adapter.out.persistence.entity.UserEntity;
import com.safe.user.adapter.out.persistence.repository.UserJpaRepository;
import com.safe.user.adapter.out.persistence.mapper.UserPersistenceMapper;

import com.safe.user.domain.ports.UserRepositoryPort;
import com.safe.user.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Primary
public class UserRepositoryAdapter implements UserRepositoryPort {
    private static final Logger logger = LoggerFactory.getLogger(UserRepositoryAdapter.class);

    private final UserJpaRepository userJpaRepository;
    private final UserPersistenceMapper userMapper;

    public UserRepositoryAdapter(UserJpaRepository userJpaRepository, UserPersistenceMapper userMapper) {
        this.userJpaRepository = userJpaRepository;
        this.userMapper = userMapper;
    }

    @Override
    public List<UserEntity> findAll() {
        logger.info("Obteniendo todos los usuarios de la base de datos");

        try {
            List<UserEntity> entities = userJpaRepository.findAll();
            List<User> users = entities.stream()
                    .map(userMapper::toDomain)
                    .collect(Collectors.toList());

            logger.info("Se encontraron {} usuarios en la base de datos", users.size());
            logger.debug("Usuarios obtenidos: {}", users);
            return entities;
        } catch (Exception e) {
            logger.error("Error al obtener todos los usuarios de la base de datos", e);
            throw e;
        }
    }

    public Optional<UserEntity> findById(Long id) {
        logger.info("Buscando usuario por ID en la base de datos: {}", id);

        if (id == null) {
            logger.warn("Se intentó buscar un usuario con ID null en la base de datos");
            return Optional.empty();
        }

        try {
            Optional<UserEntity> entityOpt = userJpaRepository.findById(id);
            Optional<User> userOpt = entityOpt.map(userMapper::toDomain);

            if (userOpt.isPresent()) {
                logger.info("Usuario encontrado en la base de datos con ID: {}", id);
                logger.debug("Detalles del usuario desde BD: {}", userOpt.get());
            } else {
                logger.warn("No se encontró usuario en la base de datos con ID: {}", id);
            }

            return entityOpt;
        } catch (Exception e) {
            logger.error("Error al buscar usuario por ID en la base de datos: {}", id, e);
            throw e;
        }
    }

    @Override
    public User findByEmail(String email) {
        logger.info("Buscando usuario por email en la base de datos: {}", email);

        if (email == null || email.trim().isEmpty()) {
            logger.warn("Se intentó buscar un usuario con email null o vacío en la base de datos");
        }

        try {
            UserEntity entityOpt = userJpaRepository.findByEmailIgnoreCase(email.toLowerCase());
            User userOpt =  userMapper.toDomain(entityOpt);  //.map(userMapper::toDomain);

            if (userOpt.getEmail().isEmpty()) {
                logger.info("Usuario encontrado en la base de datos con email: {}", email);
                logger.debug("Detalles del usuario desde BD: {}", userOpt.getId());
            } else {
                logger.warn("No se encontró usuario en la base de datos con email: {}", email);
            }

            return userOpt;
        } catch (Exception e) {
            logger.error("Error al buscar usuario por email en la base de datos: {}", email, e);
            throw e;
        }
    }


    @Override
    public User save(User user) {
        logger.info("Guardando usuario en la base de datos");

        if (user == null) {
            logger.warn("Se intentó guardar un usuario null en la base de datos");
            throw new IllegalArgumentException("Usuario no puede ser null");
        }

        try {
            logger.debug("Datos del usuario a guardar en BD: {}", user);

            // Convertir User (dominio) a UserEntity (persistencia)
            UserEntity entityToSave;
            if (user.getId() == null) {
                // Usuario nuevo - usar método específico para creación
                entityToSave = userMapper.toEntityForCreation(user);
                logger.debug("Preparando nuevo usuario para creación en BD");
            } else {
                // Usuario existente - usar método específico para actualización
                entityToSave = userMapper.toEntityForUpdate(user);
                logger.debug("Preparando usuario existente para actualización en BD con ID: {}", user.getId());
            }

            // Guardar en la base de datos
            UserEntity savedEntity = userJpaRepository.save(entityToSave);
            logger.debug("Usuario guardado en BD como entidad: {}", savedEntity);

            // Convertir de vuelta a User (dominio)
            User savedUser = userMapper.toDomain(savedEntity);

            logger.info("Usuario guardado exitosamente en BD con ID: {}", savedUser.getId());
            logger.debug("Usuario guardado convertido a dominio: {}", savedUser);

            return savedUser;
        } catch (Exception e) {
            logger.error("Error al guardar el usuario en la base de datos: {}", user, e);
            throw e;
        }
    }

    @Override
    public void deleteById(Long id) {
        logger.info("Eliminando usuario de la base de datos con ID: {}", id);

        if (id == null) {
            logger.warn("Se intentó eliminar un usuario con ID null de la base de datos");
            throw new IllegalArgumentException("ID no puede ser null");
        }

        try {
            // Verificar si existe antes de eliminar
            if (userJpaRepository.existsById(id)) {
                userJpaRepository.deleteById(id);
                logger.info("Usuario eliminado exitosamente de la base de datos con ID: {}", id);
            } else {
                logger.warn("No se puede eliminar: Usuario con ID {} no existe en la base de datos", id);
                throw new IllegalArgumentException("Usuario con ID " + id + " no existe en la base de datos");
            }
        } catch (Exception e) {
            logger.error("Error al eliminar usuario de la base de datos con ID: {}", id, e);
            throw e;
        }
    }


}