package com.safe.bike.infrastructure.adapter.in.web.validation;

import com.safe.bike.infrastructure.adapter.in.web.validation.enums.FrameMaterial;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ValidFrameMaterialValidator implements ConstraintValidator<ValidFrameMaterial, String> {
    private static final Set<String> MATERIALS = new HashSet<>();

    static {
        Arrays.stream(FrameMaterial.values())
                .forEach(frame -> MATERIALS.add(frame.name()));
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // Puedes cambiar a false si es obligatorio
        }
        boolean isValid = MATERIALS.contains(value.toUpperCase());
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Tipo de material no permitida: " + value)
                    .addConstraintViolation();
        }
        return isValid;
    }
}
