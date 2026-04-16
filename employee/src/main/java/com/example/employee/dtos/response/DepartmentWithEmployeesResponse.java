
package com.example.employee.dtos.response;

import java.util.List;
import java.util.UUID;

public record DepartmentWithEmployeesResponse(
    UUID id,
    String name,
    List<EmployeeInDepartmentResponse> employees) {
}