package com.example.employee.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueDepartmentNameValidator.class)
public @interface UniqueDepartmentName {
  String message() default "Department name already exists!";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
