package com.example.employee.dtos.response;

public record EmployeeLeaveRequestsResponse(
    EmployeeSummaryResponse employeeInfo,
    PaginatedResponse<LeaveRequestResponse> leaveRequests) {
}
