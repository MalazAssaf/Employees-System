package com.example.employee.dtos.response;

import java.util.UUID;

public record EmployeeUnderManagerResponse(
        UUID managerId,
        String managerName,
        UUID departmentId,
        String departmentName,
        PaginatedResponse<EmployeeInDepartmentResponse> team) {
}
