package com.safe.user.domain.model;

import com.safe.user.domain.exception.InvalidUserDataException;

public record PersonalInfo(String firstName, String lastName) {
    public PersonalInfo {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new InvalidUserDataException("Nombre es requerido");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new InvalidUserDataException("Apellido es requerido");
        }
        firstName = firstName.trim();
        lastName = lastName.trim();
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public static PersonalInfo of(String firstName, String lastName) {
        return new PersonalInfo(firstName, lastName);
    }
}