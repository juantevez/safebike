package com.safe.user.infrastructure.persistence;


import com.safe.user.adapter.out.persistence.entity.UserEntity;
import com.safe.user.adapter.out.persistence.repository.UserJpaRepository;
import com.safe.user.domain.ports.UserRepositoryPort;
import com.safe.user.infrastructure.mapper.UserMapper;
import com.safe.user.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;


@Component
public class UserPersistenceAdapter implements UserRepositoryPort {
    private static final Logger logger = LoggerFactory.getLogger(UserPersistenceAdapter.class);

    private final UserJpaRepository userJpaRepository;
    private final UserMapper userMapper;
    public UserPersistenceAdapter(UserJpaRepository userJpaRepository, UserMapper userMapper) {
        this.userJpaRepository = userJpaRepository;
        this.userMapper = userMapper;
    }

    @Override
    public User save(User user) {
        logger.info("Guardando usuario en la base de datos");

        if (user == null) {
            logger.warn("Se intentó guardar un usuario null");
            throw new IllegalArgumentException("Usuario no puede ser null");
        }

        try {
            logger.debug("Datos del usuario a guardar: {}", user);

            // ✅ Convertir de User (dominio) a UserEntity (JPA)
            UserEntity userEntity = userMapper.toEntity(user);

            // ✅ Guardar la entidad
            UserEntity savedEntity = userJpaRepository.save(userEntity);

            // ✅ Convertir de vuelta a User (dominio)
            User savedUser = userMapper.toDomain(savedEntity);

            logger.info("Usuario guardado exitosamente con ID: {}", savedUser.getId());
            logger.debug("Usuario guardado: {}", savedUser);

            return savedUser;

        } catch (Exception e) {
            logger.error("Error al guardar el usuario: {}", user, e);
            throw e;
        }
    }

    @Override
    public List<UserEntity> findAll() {
        logger.info("Consultando todos los usuarios desde la base de datos");

        try {
            List<UserEntity> users = userJpaRepository.findAll();
            logger.info("Consulta exitosa: {} usuarios encontrados en la base de datos", users.size());
            logger.debug("Usuarios obtenidos de la BD: {}", users);
            return users;
        } catch (Exception e) {
            logger.error("Error al consultar todos los usuarios desde la base de datos", e);
            throw e;
        }
    }

    @Override
    public Optional<UserEntity> findById(Long id) {
        logger.info("Consultando usuario por ID: {} desde la base de datos", id);

        if (id == null) {
            logger.warn("Se intentó consultar un usuario con ID null");
            return Optional.empty();
        }

        try {
            Optional<UserEntity> user = userJpaRepository.findById(id);

            if (user.isPresent()) {
                logger.info("Usuario encontrado en la BD con ID: {}", id);
                logger.debug("Usuario obtenido de la BD: {}", user.get());
            } else {
                logger.warn("No se encontró usuario en la BD con ID: {}", id);
            }

            return user;
        } catch (Exception e) {
            logger.error("Error al consultar usuario por ID: {} desde la base de datos", id, e);
            throw e;
        }
    }

    @Override
    public User findByEmail(String email) {
        logger.info("Buscando usuario por email en la base de datos: {}", email);

        try {
            // Validación de entrada
            if (email == null || email.trim().isEmpty()) {
                logger.warn("Email proporcionado es null o vacío");
                return null;
            }

            // El repositorio devuelve UserEntity directamente (puede ser null)
            UserEntity userEntity = userJpaRepository.findByEmailIgnoreCase(email.toLowerCase().trim());

            if (userEntity != null) {
                // Convertir la entidad a modelo de dominio
                User user = userMapper.toDomain(userEntity);
                logger.info("Usuario encontrado en la base de datos con email: {}", email);
                logger.debug("Detalles del usuario desde BD: ID={}, Name={}, Active={}",
                        user.getId(), user.getFirstName(), user.getLastName());
                return user;
            } else {
                logger.warn("No se encontró usuario en la base de datos con email: {}", email);
                return null;
            }

        } catch (Exception e) {
            logger.error("Error al buscar usuario por email en la base de datos: {}", email, e);
            throw new RuntimeException("Error al buscar usuario por email: " + email, e);
        }
    }

    @Override
    public void deleteById(Long id) {
        logger.info("Eliminando usuario por ID: {} desde la base de datos", id);

        if (id == null) {
            logger.warn("Se intentó eliminar un usuario con ID null");
            throw new IllegalArgumentException("ID no puede ser null");
        }

        try {
            userJpaRepository.deleteById(id);
            logger.info("Usuario eliminado exitosamente de la BD con ID: {}", id);
        } catch (Exception e) {
            logger.error("Error al eliminar usuario por ID: {} desde la base de datos", id, e);
            throw e;
        }
    }


}