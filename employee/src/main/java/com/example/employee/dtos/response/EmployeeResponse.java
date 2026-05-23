package com.example.employee.dtos.response;

import java.time.LocalDate;
import java.util.UUID;

import com.example.employee.entity.UserRole;

public record EmployeeResponse(
        UUID id,
        String name,
        String email,
        String phoneNumber,
        LocalDate hireDate,
        boolean isActivated,
        UserRole role,
        DepartmentResponse departmentInfo) {
}
