package com.safe.user.adapter.out.persistence.mapper;

import com.safe.user.model.User;
import com.safe.user.adapter.out.persistence.entity.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserPersistenceMapper {
    private static final Logger logger = LoggerFactory.getLogger(UserPersistenceMapper.class);
    /**
     * Convierte una entidad de dominio User a UserEntity para persistencia
     * @param user entidad de dominio
     * @return UserEntity para persistencia
     */
    public UserEntity toEntity(User user) {
        if (user == null) {
            return null;
        }

        UserEntity entity = new UserEntity();
        entity.setId(user.getId());
        entity.setUsername(user.getUsername());
        entity.setEmail(user.getEmail());
        entity.setPassword(user.getPassword());
        entity.setFirstName(user.getFirstName());
        entity.setLastName(user.getLastName());
        entity.setCreatedAt(user.getCreatedAt());

        return entity;
    }

    /**
     * Convierte una UserEntity de persistencia a entidad de dominio User
     * @param entity UserEntity de persistencia
     * @return User entidad de dominio
     */
    public User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }

        User user = new User();
        user.setId(entity.getId());
        user.setUsername(entity.getUsername());
        user.setEmail(entity.getEmail());
        user.setPassword(entity.getPassword());
        user.setFirstName(entity.getFirstName());
        user.setLastName(entity.getLastName());
        user.setCreatedAt(entity.getCreatedAt());

        return user;
    }

    /**
     * Convierte una lista de entidades User de dominio a lista de UserEntity
     * @param users lista de entidades de dominio
     * @return lista de UserEntity para persistencia
     */
    public List<UserEntity> toEntityList(List<User> users) {
        if (users == null) {
            return null;
        }

        return users.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    /**
     * Convierte una lista de UserEntity a lista de entidades User de dominio
     * @param entities lista de UserEntity de persistencia
     * @return lista de User entidades de dominio
     */
    public List<User> toDomainList(List<UserEntity> entities) {
        if (entities == null) {
            return null;
        }

        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * Actualiza una UserEntity existente con datos de User de dominio
     * Útil para operaciones de actualización donde queremos preservar ciertos campos
     * @param existingEntity UserEntity existente
     * @param user User de dominio con nuevos datos
     * @return UserEntity actualizada
     */
    public UserEntity updateEntity(UserEntity existingEntity, User user) {
        if (existingEntity == null || user == null) {
            return existingEntity;
        }

        // Preservamos el ID y fechas de creación si existen
        if (user.getUsername() != null) {
            existingEntity.setUsername(user.getUsername());
        }
        if (user.getEmail() != null) {
            existingEntity.setEmail(user.getEmail());
        }
        if (user.getPassword() != null) {
            existingEntity.setPassword(user.getPassword());
        }
        if (user.getFirstName() != null) {
            existingEntity.setFirstName(user.getFirstName());
        }
        if (user.getLastName() != null) {
            existingEntity.setLastName(user.getLastName());
        }

        return existingEntity;
    }

    /**
     * Crea una nueva UserEntity con valores por defecto para nuevos usuarios
     * @param user User de dominio
     * @return UserEntity con valores por defecto aplicados
     */
    public UserEntity toNewEntity(User user) {
        if (user == null) {
            return null;
        }

        UserEntity entity = toEntity(user);

        // Aplicar valores por defecto para nuevos usuarios
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(LocalDateTime.now());
        }

        return entity;
    }

    /**
     * Método de conveniencia para mapear User a UserEntity preparada para crear
     * @param user User de dominio
     * @return UserEntity lista para crear en BD
     */
    public UserEntity toEntityForCreation(User user) {
        UserEntity entity = toNewEntity(user);
        if (entity != null) {
            entity.setId(null); // Asegurar que el ID sea null para creación
        }
        return entity;
    }

    /**
     * Método de conveniencia para mapear User a UserEntity preparada para actualización
     * @param user User de dominio con ID existente
     * @return UserEntity lista para actualizar en BD
     */
    public UserEntity toEntityForUpdate(User user) {
        if (user == null) {
            logger.warn("Se intentó convertir un User null para actualización");
            return null;
        }

        if (user.getId() == null) {
            logger.warn("Se intentó actualizar un User sin ID. Use toEntityForCreation() en su lugar.");
            throw new IllegalArgumentException("Para actualización, el User debe tener un ID válido");
        }

        logger.debug("Convirtiendo User a UserEntity para actualización. ID: {}", user.getId());

        UserEntity entity = new UserEntity();
        entity.setId(user.getId());
        entity.setUsername(user.getUsername());
        entity.setEmail(user.getEmail());
        entity.setPassword(user.getPassword());
        entity.setFirstName(user.getFirstName());
        entity.setLastName(user.getLastName());

        // Preservar fecha de creación si existe, sino usar la actual
        if (user.getCreatedAt() != null) {
            entity.setCreatedAt(user.getCreatedAt());
        } else {
            logger.debug("User no tiene createdAt, se usará la fecha actual como fallback");
            entity.setCreatedAt(LocalDateTime.now());
        }

        logger.debug("UserEntity preparada para actualización: {}", entity);
        return entity;
    }
}
