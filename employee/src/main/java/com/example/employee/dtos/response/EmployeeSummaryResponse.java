package com.example.employee.dtos.response;

import java.util.UUID;

public record EmployeeSummaryResponse(
    UUID id,
    String name) {
}
