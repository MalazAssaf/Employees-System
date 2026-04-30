package com.example.employee.dtos.request;

import com.example.employee.entity.LeaveRequestStatus;
import com.example.employee.validators.ValidEnum;
import jakarta.validation.constraints.NotBlank;

public record LeaveRequestStatusUpdateRequest(
    @NotBlank(message = "Status is required") @ValidEnum(LeaveRequestStatus.class) String status) {
}
