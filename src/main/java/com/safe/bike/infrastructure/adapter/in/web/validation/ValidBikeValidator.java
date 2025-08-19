package com.safe.bike.infrastructure.adapter.in.web.validation;

import com.safe.bike.infrastructure.adapter.in.web.validation.enums.BicycleType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ValidBikeValidator implements ConstraintValidator<ValidBike, String> {
    private static final Logger log = LoggerFactory.getLogger(ValidBikeValidator.class);
    @Override
    public void initialize(ValidBike constraintAnnotation) {
        // Puedes inicializar propiedades aquí si lo necesitas
    }
    private static final Set<String> TYPES = new HashSet<>();

    static {
        Arrays.stream(BicycleType.values())
                .forEach(types -> TYPES.add(types.name()));
    }
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (value == null ||  value.isEmpty()) {
            return true; // Si es opcional, puedes cambiar a false si es obligatorio
        }

        boolean isValid = TYPES.contains(value.toUpperCase());
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Tipo de Bicicleta no valido: " + value)
                    .addConstraintViolation();
        }
        return isValid;

    }
}