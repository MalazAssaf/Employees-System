package com.example.employee.dtos.response;

import java.util.List;
import java.util.UUID;

public record EmployeeUnderManagerResponse(
    UUID managerId,
    String managerName,
    UUID departmentId,
    String departmentName,
    List<EmployeeInDepartmentResponse> employees) {

}
