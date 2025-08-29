package com.safe.user.infrastructure.adapters.output.persistence.mappers;

import com.safe.user.domain.model.*;
import com.safe.user.infrastructure.adapters.output.persistence.entities.UserEntity;
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
     * ✅ COMPATIBLE: Convierte UserEntity a User (dominio) - SIN CAMBIOS ESTRUCTURALES
     */
    public User toDomain(UserEntity entity) {
        if (entity == null) {
            logger.debug("UserEntity es null, retornando null");
            return null;
        }

        logger.debug("Convirtiendo UserEntity a User: ID={}, Email={}",
                entity.getId(), entity.getEmail());

        // ✅ USAR CONSTRUCTOR EXISTENTE del User actual
        User user = new User();
        user.setId(entity.getId());
        user.setUsername(entity.getUsername());
        user.setEmail(entity.getEmail());
        user.setPassword(entity.getPassword());
        user.setFirstName(entity.getFirstName());
        user.setLastName(entity.getLastName());
        user.setRole(entity.getRole());
        user.setCreatedAt(entity.getCreatedAt());

        logger.debug("User convertido exitosamente: {}", user);
        return user;
    }

    /**
     * ✅ COMPATIBLE: Convierte User (dominio) a UserEntity - SIN CAMBIOS ESTRUCTURALES
     */
    public UserEntity toEntity(User user) {
        if (user == null) {
            logger.debug("User es null, retornando null");
            return null;
        }

        logger.debug("Convirtiendo User a UserEntity: ID={}, Email={}",
                user.getId(), user.getEmail());

        UserEntity entity = new UserEntity();
        entity.setId(user.getId());
        entity.setUsername(user.getUsername());
        entity.setEmail(user.getEmail());
        entity.setPassword(user.getPassword());
        entity.setFirstName(user.getFirstName());
        entity.setLastName(user.getLastName());
        entity.setRole(user.getRole());
        entity.setCreatedAt(user.getCreatedAt());

        logger.debug("UserEntity convertido exitosamente: {}", entity);
        return entity;
    }

    /**
     * ✅ MEJORADO: Lista de UserEntity a lista de User
     */
    public List<User> toDomainList(List<UserEntity> entities) {
        if (entities == null) {
            logger.debug("Lista de entities es null, retornando null");
            return null;
        }

        logger.debug("Convirtiendo lista de {} UserEntity a User", entities.size());

        List<User> users = entities.stream()
                .map(this::toDomain)
                .filter(user -> user != null) // ✅ Filtrar nulls por seguridad
                .collect(Collectors.toList());

        logger.debug("Lista convertida exitosamente: {} users", users.size());
        return users;
    }

    /**
     * ✅ MEJORADO: Lista de User a lista de UserEntity
     */
    public List<UserEntity> toEntityList(List<User> users) {
        if (users == null) {
            logger.debug("Lista de users es null, retornando null");
            return null;
        }

        logger.debug("Convirtiendo lista de {} User a UserEntity", users.size());

        List<UserEntity> entities = users.stream()
                .map(this::toEntity)
                .filter(entity -> entity != null) // ✅ Filtrar nulls por seguridad
                .collect(Collectors.toList());

        logger.debug("Lista convertida exitosamente: {} entities", entities.size());
        return entities;
    }

    /**
     * ✅ NUEVO: Actualizar UserEntity existente con datos de User
     * Útil para operaciones de actualización
     */
    public UserEntity updateEntity(UserEntity existingEntity, User user) {
        if (existingEntity == null) {
            logger.warn("UserEntity existente es null, no se puede actualizar");
            return null;
        }

        if (user == null) {
            logger.warn("User es null, no se puede actualizar UserEntity");
            return existingEntity;
        }

        logger.debug("Actualizando UserEntity ID={} con datos de User", existingEntity.getId());

        // ✅ Preservar ID y fechas de creación
        if (user.getUsername() != null && !user.getUsername().trim().isEmpty()) {
            existingEntity.setUsername(user.getUsername().trim());
        }

        if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
            existingEntity.setEmail(user.getEmail().trim().toLowerCase());
        }

        if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
            existingEntity.setPassword(user.getPassword());
        }

        if (user.getFirstName() != null && !user.getFirstName().trim().isEmpty()) {
            existingEntity.setFirstName(user.getFirstName().trim());
        }

        if (user.getLastName() != null && !user.getLastName().trim().isEmpty()) {
            existingEntity.setLastName(user.getLastName().trim());
        }

        if (user.getRole() != null && !user.getRole().trim().isEmpty()) {
            existingEntity.setRole(user.getRole().trim());
        }

        logger.debug("UserEntity actualizado exitosamente: {}", existingEntity);
        return existingEntity;
    }

    /**
     * ✅ NUEVO: Crear UserEntity para nuevos usuarios (sin ID)
     */
    public UserEntity toNewEntity(User user) {
        if (user == null) {
            logger.debug("User es null, retornando null");
            return null;
        }

        logger.debug("Creando nuevo UserEntity para User: Email={}", user.getEmail());

        UserEntity entity = new UserEntity();
        // ✅ Intencionalmente NO seteamos ID para que sea auto-generado
        entity.setUsername(user.getUsername());
        entity.setEmail(user.getEmail());
        entity.setPassword(user.getPassword());
        entity.setFirstName(user.getFirstName());
        entity.setLastName(user.getLastName());
        entity.setRole(user.getRole() != null ? user.getRole() : "USER"); // ✅ Valor por defecto

        // ✅ Si no tiene fecha de creación, usar la actual
        if (user.getCreatedAt() != null) {
            entity.setCreatedAt(user.getCreatedAt());
        } else {
            entity.setCreatedAt(LocalDateTime.now());
        }

        logger.debug("Nuevo UserEntity creado: {}", entity);
        return entity;
    }

    /**
     * ✅ NUEVO: Preparar UserEntity para creación (garantizar ID = null)
     */
    public UserEntity toEntityForCreation(User user) {
        UserEntity entity = toNewEntity(user);
        if (entity != null) {
            entity.setId(null); // ✅ Asegurar que el ID sea null para creación
            logger.debug("UserEntity preparado para creación (ID=null): {}", entity);
        }
        return entity;
    }

    /**
     * ✅ NUEVO: Preparar UserEntity para actualización (requiere ID válido)
     */
    public UserEntity toEntityForUpdate(User user) {
        if (user == null) {
            logger.warn("User es null, no se puede preparar para actualización");
            return null;
        }

        if (user.getId() == null) {
            logger.warn("User sin ID, no se puede actualizar. Use toEntityForCreation()");
            throw new IllegalArgumentException("Para actualización, el User debe tener un ID válido");
        }

        logger.debug("Preparando User para actualización. ID: {}", user.getId());

        UserEntity entity = new UserEntity();
        entity.setId(user.getId());
        entity.setUsername(user.getUsername());
        entity.setEmail(user.getEmail());
        entity.setPassword(user.getPassword());
        entity.setFirstName(user.getFirstName());
        entity.setLastName(user.getLastName());
        entity.setRole(user.getRole());

        // ✅ Preservar fecha de creación si existe
        if (user.getCreatedAt() != null) {
            entity.setCreatedAt(user.getCreatedAt());
        } else {
            logger.debug("User no tiene createdAt, usando fecha actual como fallback");
            entity.setCreatedAt(LocalDateTime.now());
        }

        logger.debug("UserEntity preparado para actualización: {}", entity);
        return entity;
    }

    /**
     * ✅ NUEVO: Validar que User tenga los campos mínimos requeridos
     */
    public boolean isValidUser(User user) {
        if (user == null) {
            logger.debug("User es null, no es válido");
            return false;
        }

        boolean isValid = user.getEmail() != null && !user.getEmail().trim().isEmpty() &&
                user.getFirstName() != null && !user.getFirstName().trim().isEmpty() &&
                user.getLastName() != null && !user.getLastName().trim().isEmpty();

        logger.debug("Validación de User {}: {}", user.getEmail(), isValid ? "VÁLIDO" : "INVÁLIDO");
        return isValid;
    }

    /**
     * ✅ NUEVO: Limpiar campos de texto (trim y null safety)
     */
    private String cleanString(String value) {
        return value != null ? value.trim() : null;
    }

    /**
     * ✅ NUEVO: Normalizar email (lowercase y trim)
     */
    public String normalizeEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return email;
        }
        String normalized = email.trim().toLowerCase();
        logger.debug("Email normalizado: '{}' -> '{}'", email, normalized);
        return normalized;
    }
}