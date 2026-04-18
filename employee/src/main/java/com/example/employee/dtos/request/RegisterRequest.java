package com.example.employee.dtos.request;

import java.util.UUID;

import com.example.employee.entity.UserRole;
import com.example.employee.validators.ValidEnum;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
  @NotBlank
  @Size(min = 3, max = 100)
  private String username;
  @NotBlank
  private String password;
  @NotBlank(message = "role is required")
  @ValidEnum(UserRole.class)
  private String role;
  @NotNull
  private UUID employeeId;
}