package com.safe.user.infrastructure.persistence.repositories;

import com.safe.user.domain.model.entity.UserEntity;
import com.safe.user.infrastructure.persistence.mappers.UserPersistenceMapper;

import com.safe.user.domain.ports.UserRepositoryPort;
import com.safe.user.domain.model.entity.User;
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

    // ================================
    // MÉTODOS EXISTENTES - ✅ YA IMPLEMENTADOS
    // ================================

    @Override
    public List<UserEntity> findAll() {
        logger.info("Obteniendo todos los usuarios de la base de datos");

        try {
            List<UserEntity> entities = userJpaRepository.findAll();
            logger.info("Se encontraron {} usuarios en la base de datos", entities.size());
            logger.debug("Usuarios obtenidos: {}", entities.stream()
                    .map(UserEntity::getEmail)
                    .collect(Collectors.toList()));
            return entities;
        } catch (Exception e) {
            logger.error("Error al obtener todos los usuarios de la base de datos", e);
            throw new RuntimeException("Error al acceder a la base de datos", e);
        }
    }

    @Override
    public Optional<UserEntity> findById(Long id) {
        logger.info("Buscando usuario por ID en la base de datos: {}", id);

        if (id == null) {
            logger.warn("Se intentó buscar un usuario con ID null en la base de datos");
            return Optional.empty();
        }

        try {
            Optional<UserEntity> entityOpt = userJpaRepository.findById(id);

            if (entityOpt.isPresent()) {
                logger.info("Usuario encontrado en la base de datos con ID: {}", id);
                logger.debug("Detalles del usuario desde BD: {}", entityOpt.get());
            } else {
                logger.warn("No se encontró usuario en la base de datos con ID: {}", id);
            }

            return entityOpt;
        } catch (Exception e) {
            logger.error("Error al buscar usuario por ID en la base de datos: {}", id, e);
            throw new RuntimeException("Error al acceder a la base de datos", e);
        }
    }

    @Override
    public User findByEmail(String email) {
        logger.info("Buscando usuario por email en la base de datos: {}", email);

        if (email == null || email.trim().isEmpty()) {
            logger.warn("Se intentó buscar un usuario con email null o vacío en la base de datos");
            return null;
        }

        try {
            UserEntity userEntity = userJpaRepository.findByEmailIgnoreCase(email.trim().toLowerCase());

            if (userEntity != null) {
                User user = userMapper.toDomain(userEntity);
                logger.info("Usuario encontrado en la base de datos con email: {}", email);
                logger.debug("Detalles del usuario desde BD: {}", user);
                return user;
            } else {
                logger.warn("No se encontró usuario en la base de datos con email: {}", email);
                return null;
            }
        } catch (Exception e) {
            logger.error("Error al buscar usuario por email en la base de datos: {}", email, e);
            throw new RuntimeException("Error al acceder a la base de datos", e);
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
                // Usuario nuevo - usar método del mapper
                entityToSave = userMapper.toNewEntity(user);
                logger.debug("Preparando nuevo usuario para creación en BD");
            } else {
                // Usuario existente - usar método del mapper
                entityToSave = userMapper.toEntity(user);
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
            throw new RuntimeException("Error al acceder a la base de datos", e);
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
            throw new RuntimeException("Error al acceder a la base de datos", e);
        }
    }

    // ================================
    // MÉTODOS NUEVOS - ✅ IMPLEMENTACIONES FALTANTES
    // ================================

    @Override
    public boolean existsByEmail(String email) {
        logger.debug("Verificando existencia de email en BD: {}", email);

        if (email == null || email.trim().isEmpty()) {
            logger.debug("Email vacío, retornando false");
            return false;
        }

        try {
            String normalizedEmail = email.trim().toLowerCase();
            UserEntity userEntity = userJpaRepository.findByEmailIgnoreCase(normalizedEmail);
            boolean exists = userEntity != null;

            logger.debug("Email {} existe en BD: {}", email, exists);
            return exists;
        } catch (Exception e) {
            logger.error("Error al verificar existencia de email: {}", email, e);
            throw new RuntimeException("Error al acceder a la base de datos", e);
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        logger.debug("Verificando existencia de username en BD: {}", username);

        if (username == null || username.trim().isEmpty()) {
            logger.debug("Username vacío, retornando false");
            return false;
        }

        try {
            String normalizedUsername = username.trim();

            // ✅ IMPLEMENTACIÓN EFICIENTE: Buscar en todos los usuarios
            List<UserEntity> allUsers = userJpaRepository.findAll();
            boolean exists = allUsers.stream()
                    .anyMatch(user -> normalizedUsername.equalsIgnoreCase(user.getUsername()));

            logger.debug("Username {} existe en BD: {}", username, exists);
            return exists;
        } catch (Exception e) {
            logger.error("Error al verificar existencia de username: {}", username, e);
            throw new RuntimeException("Error al acceder a la base de datos", e);
        }
    }

    @Override
    public long count() {
        logger.debug("Contando total de usuarios en BD");

        try {
            long total = userJpaRepository.count();
            logger.debug("Total de usuarios en BD: {}", total);
            return total;
        } catch (Exception e) {
            logger.error("Error al contar usuarios en BD", e);
            throw new RuntimeException("Error al acceder a la base de datos", e);
        }
    }

    @Override
    public List<UserEntity> findActiveUsers() {
        logger.debug("Buscando usuarios activos en BD");

        try {
            List<UserEntity> allUsers = userJpaRepository.findAll();
            List<UserEntity> activeUsers = allUsers.stream()
                    .filter(user -> {
                        String role = user.getRole();
                        // Considerar activo si role es null, vacío, o diferente de "INACTIVE"
                        return role == null || role.trim().isEmpty() || !"INACTIVE".equalsIgnoreCase(role.trim());
                    })
                    .collect(Collectors.toList());

            logger.debug("Usuarios activos encontrados: {} de {} total", activeUsers.size(), allUsers.size());
            return activeUsers;
        } catch (Exception e) {
            logger.error("Error al buscar usuarios activos en BD", e);
            throw new RuntimeException("Error al acceder a la base de datos", e);
        }
    }

    @Override
    public List<UserEntity> findByRole(String role) {
        logger.debug("Buscando usuarios por rol en BD: {}", role);

        if (role == null) {
            logger.debug("Rol es null, retornando lista vacía");
            return List.of();
        }

        try {
            String normalizedRole = role.trim();
            List<UserEntity> allUsers = userJpaRepository.findAll();
            List<UserEntity> usersByRole;

            if (normalizedRole.isEmpty()) {
                // Buscar usuarios con rol null o vacío
                usersByRole = allUsers.stream()
                        .filter(user -> {
                            String userRole = user.getRole();
                            return userRole == null || userRole.trim().isEmpty();
                        })
                        .collect(Collectors.toList());
            } else {
                // Buscar usuarios con el rol específico
                usersByRole = allUsers.stream()
                        .filter(user -> {
                            String userRole = user.getRole();
                            return userRole != null && normalizedRole.equalsIgnoreCase(userRole.trim());
                        })
                        .collect(Collectors.toList());
            }

            logger.debug("Usuarios con rol '{}' encontrados: {} de {} total",
                    role, usersByRole.size(), allUsers.size());
            return usersByRole;
        } catch (Exception e) {
            logger.error("Error al buscar usuarios por rol '{}' en BD", role, e);
            throw new RuntimeException("Error al acceder a la base de datos", e);
        }
    }

    // ================================
    // MÉTODOS AUXILIARES - ✅ UTILIDADES ADICIONALES
    // ================================

    /**
     * ✅ NUEVO: Verificar si un usuario existe por ID
     */
    public boolean existsById(Long id) {
        logger.debug("Verificando existencia de usuario por ID: {}", id);

        if (id == null) {
            logger.debug("ID es null, retornando false");
            return false;
        }

        try {
            boolean exists = userJpaRepository.existsById(id);
            logger.debug("Usuario con ID {} existe: {}", id, exists);
            return exists;
        } catch (Exception e) {
            logger.error("Error al verificar existencia de usuario con ID: {}", id, e);
            throw new RuntimeException("Error al acceder a la base de datos", e);
        }
    }

    /**
     * ✅ NUEVO: Contar usuarios activos
     */
    public long countActiveUsers() {
        logger.debug("Contando usuarios activos en BD");

        try {
            List<UserEntity> activeUsers = findActiveUsers();
            long count = activeUsers.size();
            logger.debug("Total de usuarios activos: {}", count);
            return count;
        } catch (Exception e) {
            logger.error("Error al contar usuarios activos", e);
            throw new RuntimeException("Error al acceder a la base de datos", e);
        }
    }

    /**
     * ✅ NUEVO: Contar usuarios por rol
     */
    public long countByRole(String role) {
        logger.debug("Contando usuarios por rol: {}", role);

        try {
            List<UserEntity> usersByRole = findByRole(role);
            long count = usersByRole.size();
            logger.debug("Total de usuarios con rol '{}': {}", role, count);
            return count;
        } catch (Exception e) {
            logger.error("Error al contar usuarios por rol '{}'", role, e);
            throw new RuntimeException("Error al acceder a la base de datos", e);
        }
    }

    /**
     * ✅ NUEVO: Buscar usuarios por patrón en email
     */
    public List<UserEntity> findByEmailPattern(String emailPattern) {
        logger.debug("Buscando usuarios por patrón de email: {}", emailPattern);

        if (emailPattern == null || emailPattern.trim().isEmpty()) {
            logger.debug("Patrón de email vacío, retornando lista vacía");
            return List.of();
        }

        try {
            String pattern = emailPattern.trim().toLowerCase();
            List<UserEntity> allUsers = userJpaRepository.findAll();
            List<UserEntity> matchingUsers = allUsers.stream()
                    .filter(user -> {
                        String email = user.getEmail();
                        return email != null && email.toLowerCase().contains(pattern);
                    })
                    .collect(Collectors.toList());

            logger.debug("Usuarios con email que contiene '{}': {} encontrados",
                    emailPattern, matchingUsers.size());
            return matchingUsers;
        } catch (Exception e) {
            logger.error("Error al buscar usuarios por patrón de email '{}'", emailPattern, e);
            throw new RuntimeException("Error al acceder a la base de datos", e);
        }
    }

    /**
     * ✅ NUEVO: Validar integridad de datos del usuario
     */
    private void validateUserIntegrity(User user) {
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Usuario debe tener un email válido");
        }

        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("Usuario debe tener un nombre válido");
        }

        if (user.getLastName() == null || user.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Usuario debe tener un apellido válido");
        }

        logger.debug("Validación de integridad de usuario completada: {}", user.getEmail());
    }
}