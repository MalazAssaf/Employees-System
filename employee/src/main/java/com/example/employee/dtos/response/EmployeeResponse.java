package com.example.employee.dtos.response;

import java.time.LocalDate;
import java.util.UUID;

public record EmployeeResponse(
        UUID id,
        String name,
        String email,
        String phoneNumber,
        LocalDate hireDate,
        UUID departmentId,
        String departmentName,
        UUID managerId,
        String managerName) {
}
