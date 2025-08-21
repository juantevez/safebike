package com.safe.user.domain.exception;

/**
 * Excepción lanzada cuando no se encuentra un usuario requerido
 */
public class UserNotFoundException extends RuntimeException {

    private final String email;
    private final Long userId;

    /**
     * Constructor con mensaje genérico
     */
    public UserNotFoundException(String message) {
        super(message);
        this.email = null;
        this.userId = null;
    }

    /**
     * Constructor con mensaje y causa
     */
    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.email = null;
        this.userId = null;
    }

    /**
     * Constructor específico para búsqueda por email
     */
    public UserNotFoundException(String message, String email) {
        super(message);
        this.email = email;
        this.userId = null;
    }

    /**
     * Constructor específico para búsqueda por ID
     */
    public UserNotFoundException(String message, Long userId) {
        super(message);
        this.email = null;
        this.userId = userId;
    }

    /**
     * Constructor con email y causa
     */
    public UserNotFoundException(String message, String email, Throwable cause) {
        super(message, cause);
        this.email = email;
        this.userId = null;
    }

    /**
     * Métodos de conveniencia para crear excepciones específicas
     */
    public static UserNotFoundException byEmail(String email) {
        return new UserNotFoundException("Usuario no encontrado con email: " + email, email);
    }

    public static UserNotFoundException byId(Long id) {
        return new UserNotFoundException("Usuario no encontrado con ID: " + id, id);
    }

    public static UserNotFoundException byEmailWithContext(String email, String context) {
        return new UserNotFoundException("Usuario no encontrado con email: " + email + " en contexto: " + context, email);
    }

    // Getters
    public String getEmail() {
        return email;
    }

    public Long getUserId() {
        return userId;
    }

    public boolean hasEmail() {
        return email != null && !email.trim().isEmpty();
    }

    public boolean hasUserId() {
        return userId != null;
    }
}