package com.example.employee.dtos.response;

import java.time.LocalDate;
import java.util.UUID;

import com.example.employee.entity.RequestStatus;

public record LeaveRequestResponse(UUID id,
        LocalDate startDate,
        LocalDate endDate,
        String reason,
        RequestStatus status) {

}
