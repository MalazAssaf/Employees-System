
package com.example.employee.dtos.response;

import java.util.UUID;

public record DepartmentWithEmployeesResponse(
    UUID id,
    String name,
    PaginatedResponse<EmployeeInDepartmentResponse> employees) {
}