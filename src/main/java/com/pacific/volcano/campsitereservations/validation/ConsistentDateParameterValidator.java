package com.pacific.volcano.campsitereservations.validation;

import com.pacific.volcano.campsitereservations.api.DateRangeValidatable;
import com.pacific.volcano.campsitereservations.api.ReservationRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraintvalidation.SupportedValidationTarget;
import javax.validation.constraintvalidation.ValidationTarget;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static java.time.temporal.ChronoUnit.*;

@SupportedValidationTarget(ValidationTarget.ANNOTATED_ELEMENT)
public class ConsistentDateParameterValidator implements ConstraintValidator<ConsistentDateParameters, DateRangeValidatable> {
   private static final long MIN_STAY = 1;
   private static final long MAX_STAY = 3;
   private static final long MIN_ANTICIPATION = 1;
   private static final long MAX_ANTICIPATION = 30;

   public void initialize(ConsistentDateParameters constraint) {

   }

   public boolean isValid(DateRangeValidatable toValidate, ConstraintValidatorContext context) {
      context.disableDefaultConstraintViolation();
      long daysToStay = DAYS.between(toValidate.getArrivalDate(), toValidate.getDepartureDate());
      long anticipation = DAYS.between(LocalDate.now(),toValidate.getArrivalDate());

      if(toValidate.getArrivalDate() == null) {
         context.buildConstraintViolationWithTemplate("Arrival date should not be null")
                         .addConstraintViolation();
         return false;
      }
      if(toValidate.getDepartureDate() == null) {
         context.buildConstraintViolationWithTemplate("Departure date should not be null")
                         .addConstraintViolation();
         return false;
      }

      if( toValidate.getArrivalDate().isAfter(toValidate.getDepartureDate())) {
         context.buildConstraintViolationWithTemplate("Departure date should be after arrival date")
                         .addConstraintViolation();
         return false;
      }

      //validate stay between allowed range
      if(daysToStay < MIN_STAY) {
         context.buildConstraintViolationWithTemplate("minimum stay is 1 day")
                         .addConstraintViolation();
         return false;
      }

      if(daysToStay > MAX_STAY) {
         context.buildConstraintViolationWithTemplate("maximum stay is 30 days")
                         .addConstraintViolation();
         return false;
      }

      if(anticipation < MIN_ANTICIPATION) {
         context.buildConstraintViolationWithTemplate("Minimum anticipation for a reservation is 1 day before arrival")
                         .addConstraintViolation();
         return false;
      }

      if(anticipation > MAX_ANTICIPATION) {
         context.buildConstraintViolationWithTemplate("Maximum anticipation for a reservation cannot be more than 30 days ahead of arrival")
                         .addConstraintViolation();
         return false;
      }

      return true;
   }

}
