package com.example.employee.dtos.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Size;

public record LeaveRequestPatchRequest(
        LocalDate startDate,
        LocalDate endDate,
        @Size(min = 3, max = 100, message = "Reason must be between 3 and 100 chars") String reason) {
}
