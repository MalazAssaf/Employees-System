
package com.example.employee.dtos.response;

public record DepartmentWithEmployeesResponse(
                DepartmentResponse departmentInfo,
                PaginatedResponse<EmployeeSummaryResponse> employees) {
}