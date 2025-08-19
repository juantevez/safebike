package com.safe.bike.infrastructure.adapter.in.web.validation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidFrameMaterialValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFrameMaterial {

    String message() default "El tipo de material no es válido";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
