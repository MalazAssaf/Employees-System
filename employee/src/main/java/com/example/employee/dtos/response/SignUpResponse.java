package com.example.employee.dtos.response;

import java.util.UUID;

import com.example.employee.entity.UserRole;

public record SignUpResponse(
        UUID id,
        String username,
        UserRole role,
        UUID employeeId) {
}