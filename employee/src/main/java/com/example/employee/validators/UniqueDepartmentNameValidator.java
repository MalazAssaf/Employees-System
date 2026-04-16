package com.example.employee.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.employee.repo.DepartmentRepo;

public class UniqueDepartmentNameValidator implements ConstraintValidator<UniqueDepartmentName, String> {

  @Autowired
  private DepartmentRepo departmentRepo;

  @Override
  public boolean isValid(String name, ConstraintValidatorContext context) {
    if (name == null || name.isEmpty()) {
      return true;
    }

    boolean exists = departmentRepo.existsByNameIgnoreCase(name);
    if (exists) {
      context.disableDefaultConstraintViolation();
      context.buildConstraintViolationWithTemplate(
          "Department " + name + " already exists").addConstraintViolation();
      return false;
    }
    return true;
  }
}
