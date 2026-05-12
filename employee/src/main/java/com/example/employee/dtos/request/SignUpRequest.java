package com.example.employee.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignUpRequest(@NotBlank @Size(min = 3, max = 100) String username,
        @NotBlank String password) {
}