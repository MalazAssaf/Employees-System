package com.example.employee.filter;

import java.time.LocalDate;
import com.example.employee.entity.UserRole;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeFilter {
  private String name;
  private String email;
  private String phoneNumber;
  private Boolean isActivated;
  private UserRole role;
  private String departmentId;
  private LocalDate hireDateFrom;
  private LocalDate hireDateTo;
}
