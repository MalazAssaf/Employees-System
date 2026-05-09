package com.example.employee.dtos.response;

public record EmployeeUnderManagerResponse(
    ManagerResponse managerInfo,
    PaginatedResponse<EmployeeSummaryResponse> team) {
}
