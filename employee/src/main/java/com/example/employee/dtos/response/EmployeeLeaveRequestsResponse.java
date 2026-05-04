package com.example.employee.dtos.response;

import java.util.UUID;

public record EmployeeLeaveRequestsResponse(
    UUID employeeId,
    String name,
    PaginatedResponse<LeaveRequestResponse> leaveRequests) {
}
