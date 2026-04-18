package com.example.employee.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EnumValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEnum {

  Class<? extends Enum<?>> value();

  String message() default "Invalid enum value";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
