package com.example.employee.dtos.request;

import java.time.LocalDate;
import java.util.UUID;

import com.example.employee.entity.UserRole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRequest {
  @NotBlank(message = "Name is required")
  private String name;
  @Email(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "Email is invalid. PLease enter Valid Email")
  @NotBlank
  private String email;
  @Pattern(regexp = "^0\\d{9}$", message = "Mobile must start with 0 and be 10 digits")
  @NotBlank(message = "Mobile phone is required")
  private String phoneNumber;
  @FutureOrPresent
  private LocalDate hireDate;
  @NotNull(message = "Department Id is required")
  private UUID departmentId;
  @NotNull(message = "Role is required")
  private UserRole role;
}
