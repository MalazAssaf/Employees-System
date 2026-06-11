package com.example.employee.filter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepartmentFilter {
  String name;
  Boolean hasManager;
  Integer minEmployees;
  Integer maxEmployees;
}
