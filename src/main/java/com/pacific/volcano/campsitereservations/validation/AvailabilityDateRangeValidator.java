package com.pacific.volcano.campsitereservations.validation;

import com.pacific.volcano.campsitereservations.api.DateRangeValidatable;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraintvalidation.SupportedValidationTarget;
import javax.validation.constraintvalidation.ValidationTarget;
import java.time.LocalDate;

@SupportedValidationTarget(ValidationTarget.ANNOTATED_ELEMENT)
public class AvailabilityDateRangeValidator implements ConstraintValidator<ValidAvailabilityRange, DateRangeValidatable> {

    public void initialize(ConsistentDateParameters constraint) {
    }

    public boolean isValid(DateRangeValidatable toValidate, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        if (toValidate.getFrom() == null && toValidate.getTo() == null) {
            return true;
        }

        if (toValidate.getFrom() == null || toValidate.getTo() == null) {
            context.buildConstraintViolationWithTemplate("both from and to date must be provided, or none")
                    .addConstraintViolation();
            return false;
        }

        if (!toValidate.getTo().isAfter(toValidate.getFrom())) {
            context.buildConstraintViolationWithTemplate("date to must be after date from")
                    .addConstraintViolation();
            return false;
        }

        if (!toValidate.getFrom().isAfter(LocalDate.now().minusDays(1))) {
            context.buildConstraintViolationWithTemplate("Date range must be in the future")
                    .addConstraintViolation();
            return false;
        }

        return true;

    }
}
