package com.example.employee.dtos.response;

import java.util.UUID;

public record ManagerResponse(
                EmployeeSummaryResponse manager,
                UUID departmentId,
                String departmentName) {
}
