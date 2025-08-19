package com.safe.bike.infrastructure.adapter.in.web.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidBikeValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidBike {

    String message() default "El tipo de bicicleta no es válido para carretera";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}