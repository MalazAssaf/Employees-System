package com.example.employee.dtos.request;

import com.example.employee.validators.UniqueDepartmentName;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentCreateRequest {
  @NotBlank(message = "Name is required")
  @Size(min = 2, max = 50)
  @UniqueDepartmentName
  private String name;
}
