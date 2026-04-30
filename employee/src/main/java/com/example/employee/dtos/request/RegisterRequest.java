package com.example.employee.dtos.request;

import java.util.UUID;

import com.example.employee.entity.UserRole;
import com.example.employee.validators.ValidEnum;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(@NotBlank @Size(min = 3, max = 100) String username,
    @NotBlank String password,
    @NotBlank(message = "role is required") @ValidEnum(UserRole.class) String role,
    @NotNull UUID employeeId) {
}