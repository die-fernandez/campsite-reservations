package com.pacific.volcano.campsitereservations.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = AvailabilityDateRangeValidator.class)
@Target({ TYPE })
@Retention(RUNTIME)
@Documented
public @interface ValidAvailabilityRange {
    String message() default
                    "The availability date range provided is invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
