package com.safe.user.domain.exception;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Excepción para datos de usuario inválidos
 * Permite capturar múltiples errores de validación a la vez
 */
public class InvalidUserDataException extends RuntimeException {

    private final String field;
    private final Object invalidValue;
    private final Map<String, String> fieldErrors;
    private final List<String> generalErrors;

    /**
     * Constructor básico con mensaje
     */
    public InvalidUserDataException(String message) {
        super(message);
        this.field = null;
        this.invalidValue = null;
        this.fieldErrors = new HashMap<>();
        this.generalErrors = new ArrayList<>();
    }

    /**
     * Constructor con mensaje y causa
     */
    public InvalidUserDataException(String message, Throwable cause) {
        super(message, cause);
        this.field = null;
        this.invalidValue = null;
        this.fieldErrors = new HashMap<>();
        this.generalErrors = new ArrayList<>();
    }

    /**
     * Constructor para error específico de campo
     */
    public InvalidUserDataException(String message, String field, Object invalidValue) {
        super(message);
        this.field = field;
        this.invalidValue = invalidValue;
        this.fieldErrors = new HashMap<>();
        this.generalErrors = new ArrayList<>();

        if (field != null) {
            this.fieldErrors.put(field, message);
        }
    }

    /**
     * Constructor para múltiples errores de campo
     */
    public InvalidUserDataException(String message, Map<String, String> fieldErrors) {
        super(message);
        this.field = null;
        this.invalidValue = null;
        this.fieldErrors = fieldErrors != null ? new HashMap<>(fieldErrors) : new HashMap<>();
        this.generalErrors = new ArrayList<>();
    }

    /**
     * Constructor completo
     */
    public InvalidUserDataException(String message, String field, Object invalidValue,
                                    Map<String, String> fieldErrors, List<String> generalErrors) {
        super(message);
        this.field = field;
        this.invalidValue = invalidValue;
        this.fieldErrors = fieldErrors != null ? new HashMap<>(fieldErrors) : new HashMap<>();
        this.generalErrors = generalErrors != null ? new ArrayList<>(generalErrors) : new ArrayList<>();
    }

    // ================================
    // MÉTODOS DE CONVENIENCIA ESTÁTICOS
    // ================================

    /**
     * Crea excepción para email inválido
     */
    public static InvalidUserDataException invalidEmail(String email) {
        return new InvalidUserDataException(
                "Formato de email inválido: " + email,
                "email",
                email
        );
    }

    /**
     * Crea excepción para campo requerido vacío
     */
    public static InvalidUserDataException requiredField(String fieldName) {
        return new InvalidUserDataException(
                fieldName + " es requerido y no puede estar vacío",
                fieldName,
                null
        );
    }

    /**
     * Crea excepción para campo con longitud inválida
     */
    public static InvalidUserDataException invalidLength(String fieldName, Object value, int minLength, int maxLength) {
        String message = String.format("%s debe tener entre %d y %d caracteres", fieldName, minLength, maxLength);
        return new InvalidUserDataException(message, fieldName, value);
    }

    /**
     * Crea excepción para password débil
     */
    public static InvalidUserDataException weakPassword() {
        return new InvalidUserDataException(
                "La contraseña debe tener al menos 8 caracteres, incluir mayúsculas, minúsculas y números",
                "password",
                "[HIDDEN]"
        );
    }

    /**
     * Crea excepción para username inválido
     */
    public static InvalidUserDataException invalidUsername(String username) {
        return new InvalidUserDataException(
                "El username debe tener entre 3-50 caracteres, solo letras, números y guiones bajos",
                "username",
                username
        );
    }

    /**
     * Crea excepción para usuario ya existente
     */
    public static InvalidUserDataException userAlreadyExists(String email) {
        return new InvalidUserDataException(
                "Ya existe un usuario registrado con el email: " + email,
                "email",
                email
        );
    }

    // ================================
    // BUILDER PARA MÚLTIPLES ERRORES
    // ================================

    public static class Builder {
        private final Map<String, String> fieldErrors = new HashMap<>();
        private final List<String> generalErrors = new ArrayList<>();
        private String mainMessage = "Datos de usuario inválidos";

        public Builder withFieldError(String field, String error) {
            this.fieldErrors.put(field, error);
            return this;
        }

        public Builder withGeneralError(String error) {
            this.generalErrors.add(error);
            return this;
        }

        public Builder withMainMessage(String message) {
            this.mainMessage = message;
            return this;
        }

        public Builder emailError(String email) {
            return withFieldError("email", "Formato de email inválido: " + email);
        }

        public Builder passwordError(String reason) {
            return withFieldError("password", "Contraseña inválida: " + reason);
        }

        public Builder usernameError(String reason) {
            return withFieldError("username", "Username inválido: " + reason);
        }

        public Builder requiredFieldError(String field) {
            return withFieldError(field, field + " es requerido");
        }

        public InvalidUserDataException build() {
            if (fieldErrors.isEmpty() && generalErrors.isEmpty()) {
                throw new IllegalStateException("Debe especificar al menos un error");
            }

            return new InvalidUserDataException(
                    mainMessage,
                    null,
                    null,
                    fieldErrors,
                    generalErrors
            );
        }

        public boolean hasErrors() {
            return !fieldErrors.isEmpty() || !generalErrors.isEmpty();
        }
    }

    // ================================
    // GETTERS Y UTILIDADES
    // ================================

    public String getField() {
        return field;
    }

    public Object getInvalidValue() {
        return invalidValue;
    }

    public Map<String, String> getFieldErrors() {
        return new HashMap<>(fieldErrors);
    }

    public List<String> getGeneralErrors() {
        return new ArrayList<>(generalErrors);
    }

    public boolean hasFieldErrors() {
        return !fieldErrors.isEmpty();
    }

    public boolean hasGeneralErrors() {
        return !generalErrors.isEmpty();
    }

    public boolean hasMultipleErrors() {
        return fieldErrors.size() + generalErrors.size() > 1;
    }

    public String getFieldError(String field) {
        return fieldErrors.get(field);
    }

    /**
     * Obtiene un resumen detallado de todos los errores
     */
    public String getDetailedMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append(getMessage());

        if (hasFieldErrors()) {
            sb.append("\nErrores por campo:");
            fieldErrors.forEach((field, error) ->
                    sb.append("\n  - ").append(field).append(": ").append(error)
            );
        }

        if (hasGeneralErrors()) {
            sb.append("\nErrores generales:");
            generalErrors.forEach(error ->
                    sb.append("\n  - ").append(error)
            );
        }

        return sb.toString();
    }

    /**
     * Convierte a formato Map para APIs REST
     */
    public Map<String, Object> toMap() {
        Map<String, Object> errorMap = new HashMap<>();
        errorMap.put("message", getMessage());
        errorMap.put("type", "INVALID_USER_DATA");

        if (field != null) {
            errorMap.put("field", field);
        }

        if (invalidValue != null) {
            errorMap.put("invalidValue", invalidValue);
        }

        if (hasFieldErrors()) {
            errorMap.put("fieldErrors", getFieldErrors());
        }

        if (hasGeneralErrors()) {
            errorMap.put("generalErrors", getGeneralErrors());
        }

        return errorMap;
    }

    @Override
    public String toString() {
        if (hasMultipleErrors()) {
            return getDetailedMessage();
        }
        return super.toString();
    }
}

// ================================
// CLASE HELPER PARA VALIDACIONES
// ================================

/**
 * Clase utilitaria para validaciones comunes de usuarios
 */
class UserValidationHelper {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+\\..+)$";
    private static final String USERNAME_REGEX = "^[a-zA-Z0-9_]{3,50}$";

    /**
     * Valida email y lanza excepción si es inválido
     */
    public static void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw InvalidUserDataException.requiredField("email");
        }

        if (!email.matches(EMAIL_REGEX)) {
            throw InvalidUserDataException.invalidEmail(email);
        }
    }

    /**
     * Valida username y lanza excepción si es inválido
     */
    public static void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw InvalidUserDataException.requiredField("username");
        }

        if (!username.matches(USERNAME_REGEX)) {
            throw InvalidUserDataException.invalidUsername(username);
        }
    }

    /**
     * Valida password y lanza excepción si es inválido
     */
    public static void validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            throw InvalidUserDataException.requiredField("password");
        }

        if (password.length() < 8) {
            throw new InvalidUserDataException(
                    "La contraseña debe tener al menos 8 caracteres",
                    "password",
                    "[HIDDEN]"
            );
        }

        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);

        if (!hasUpper || !hasLower || !hasDigit) {
            throw InvalidUserDataException.weakPassword();
        }
    }

    /**
     * Valida nombre/apellido
     */
    public static void validateName(String name, String fieldName) {
        if (name == null || name.trim().isEmpty()) {
            throw InvalidUserDataException.requiredField(fieldName);
        }

        if (name.trim().length() < 2 || name.trim().length() > 50) {
            throw InvalidUserDataException.invalidLength(fieldName, name, 2, 50);
        }
    }

    /**
     * Validación completa de usuario para registro
     */
    public static void validateUserForRegistration(String email, String username,
                                                   String password, String firstName, String lastName) {
        InvalidUserDataException.Builder builder = new InvalidUserDataException.Builder()
                .withMainMessage("Error en datos de registro");

        try { validateEmail(email); }
        catch (InvalidUserDataException e) { builder.withFieldError("email", e.getMessage()); }

        try { validateUsername(username); }
        catch (InvalidUserDataException e) { builder.withFieldError("username", e.getMessage()); }

        try { validatePassword(password); }
        catch (InvalidUserDataException e) { builder.withFieldError("password", e.getMessage()); }

        try { validateName(firstName, "firstName"); }
        catch (InvalidUserDataException e) { builder.withFieldError("firstName", e.getMessage()); }

        try { validateName(lastName, "lastName"); }
        catch (InvalidUserDataException e) { builder.withFieldError("lastName", e.getMessage()); }

        if (builder.hasErrors()) {
            throw builder.build();
        }
    }
}