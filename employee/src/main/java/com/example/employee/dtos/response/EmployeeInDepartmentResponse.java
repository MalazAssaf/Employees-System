package com.example.employee.dtos.response;

import java.util.UUID;

public record EmployeeInDepartmentResponse(
    UUID id,
    String name,
    String email) {

}
