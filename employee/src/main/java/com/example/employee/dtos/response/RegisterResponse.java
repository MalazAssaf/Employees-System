package com.example.employee.dtos.response;

import java.util.UUID;

import com.example.employee.entity.UserRole;

public record RegisterResponse(
    UUID id,
    String username,
    UserRole role,
    UUID employeeId) {
}