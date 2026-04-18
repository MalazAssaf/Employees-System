package com.example.employee.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class EnumValidator implements ConstraintValidator<ValidEnum, String> {

  private Class<? extends Enum<?>> enumClass;

  @Override
  public void initialize(ValidEnum annotation) {
    this.enumClass = annotation.value();
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {

    if (value == null)
      return true;

    boolean valid = Arrays.stream(enumClass.getEnumConstants())
        .anyMatch(e -> e.name().equalsIgnoreCase(value));

    if (!valid) {

      context.disableDefaultConstraintViolation();
      String allowedValues = Arrays.stream(enumClass.getEnumConstants())
          .map(Enum::name)
          .toList()
          .toString();
      context.buildConstraintViolationWithTemplate(
          "Invalid value '" + value + "'. Allowed: " + allowedValues).addConstraintViolation();
    }

    return valid;
  }
}
