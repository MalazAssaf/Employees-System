package com.example.employee.validators;

import com.example.employee.dtos.request.LeaveRequestRequest;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, LeaveRequestRequest> {

  @Override
  public boolean isValid(LeaveRequestRequest value, ConstraintValidatorContext context) {
    if (value.getStartDate() == null || value.getEndDate() == null) {
      return true;
    }

    return value.getEndDate().isAfter(value.getStartDate());
  }

}
