package com.pacific.volcano.campsitereservations.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ConsistentDateParameterValidator implements ConstraintValidator<ConsistentDateParameters, String> {
   public void initialize(ConsistentDateParameters constraint) {
   }

   public boolean isValid(String obj, ConstraintValidatorContext context) {
      return false;
   }
}
