package com.example.employee.dtos.request;

import com.example.employee.entity.UserRole;
import com.example.employee.validators.ValidEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignUpRequest(@NotBlank @Size(min = 3, max = 100) String username,
    @NotBlank String password,
    @NotBlank(message = "role is required") @ValidEnum(UserRole.class) String role) {
}