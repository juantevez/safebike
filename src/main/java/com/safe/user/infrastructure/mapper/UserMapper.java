package com.safe.user.infrastructure.mapper;


import com.safe.user.domain.model.entity.UserEntity;
import com.safe.user.domain.model.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    /**
     * Convierte una entidad JPA a modelo de dominio
     * @param entity La entidad JPA
     * @return El modelo de dominio User, null si entity es null
     */
    public User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }

        return User.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .password(entity.getPassword())
                .createdAt(entity.getCreatedAt())
                .role(entity.getRole())
                .build();
    }

    /**
     * Convierte un modelo de dominio a entidad JPA
     * @param domain El modelo de dominio
     * @return La entidad JPA UserEntity, null si domain es null
     */
    public UserEntity toEntity(User domain) {
        if (domain == null) {
            return null;
        }

        UserEntity entity = new UserEntity();
        entity.setId(domain.getId());
        entity.setEmail(domain.getEmail());
        entity.setFirstName(domain.getFirstName());
        entity.setLastName(domain.getLastName());
        entity.setPassword(domain.getPassword());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setRole(domain.getRole());

        return entity;
    }

    /**
     * Convierte una lista de entidades a una lista de modelos de dominio
     * @param entities Lista de entidades JPA
     * @return Lista de modelos de dominio
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
     * Convierte una lista de modelos de dominio a una lista de entidades
     * @param domains Lista de modelos de dominio
     * @return Lista de entidades JPA
     */
    public List<UserEntity> toEntityList(List<User> domains) {
        if (domains == null) {
            return null;
        }

        return domains.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    /**
     * Actualiza una entidad existente con los datos del modelo de dominio
     * Útil para operaciones de UPDATE donde queremos mantener la instancia JPA
     * @param entity La entidad existente a actualizar
     * @param domain El modelo de dominio con los nuevos datos
     */
    public void updateEntity(UserEntity entity, User domain) {
        if (entity == null || domain == null) {
            throw new IllegalArgumentException("Entity y Domain no pueden ser null");
        }

        // No actualizamos el ID ni las fechas de creación
        entity.setEmail(domain.getEmail());
        entity.setFirstName(domain.getFirstName());
        entity.setLastName(domain.getLastName());
        entity.setPassword(domain.getPassword());
        entity.setRole(domain.getRole());
    }

    /**
     * Crea una nueva entidad sin ID para operaciones de INSERT
     * @param domain El modelo de dominio
     * @return Nueva entidad sin ID
     */
    public UserEntity toNewEntity(User domain) {
        if (domain == null) {
            return null;
        }

        UserEntity entity = new UserEntity();
        // Intencionalmente no seteamos el ID para que sea auto-generado
        entity.setEmail(domain.getEmail());
        entity.setFirstName(domain.getFirstName());
        entity.setLastName(domain.getLastName());
        entity.setPassword(domain.getPassword());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setRole(domain.getRole());

        return entity;
    }
}

