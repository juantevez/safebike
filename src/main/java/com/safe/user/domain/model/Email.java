package com.safe.user.domain.model;

import com.safe.user.domain.exception.InvalidUserDataException;

import java.util.regex.Pattern;

public record Email(String value) {
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    public Email {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidUserDataException("Email no puede estar vacío");
        }
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new InvalidUserDataException("Formato de email inválido");
        }
        value = value.toLowerCase().trim();
    }

    public static Email of(String value) {
        return new Email(value);
    }
}

// PersonalInfo.java
