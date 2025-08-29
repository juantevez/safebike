package com.safe.user.domain.model;

import com.safe.user.domain.exception.InvalidUserDataException;

public record UserId(Long value) {
    public UserId {
        if (value != null && value <= 0) {
            throw new InvalidUserDataException("User ID debe ser mayor que 0");
        }
    }

    public static UserId of(Long value) {
        return new UserId(value);
    }
}