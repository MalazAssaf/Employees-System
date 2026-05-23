package com.example.employee.dtos.request;

import java.util.UUID;
import com.example.employee.entity.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record EmployeeUpdateRequest(
        @NotBlank(message = "Name is required") String name,
        @Pattern(regexp = "^0\\d{9}$", message = "Mobile must start with 0 and be 10 digits") @NotBlank(message = "Mobile phone is required") String phoneNumber,
        @NotNull(message = "Department Id is required") UUID departmentId,
        @NotNull(message = "Role is required") UserRole role) {
}
