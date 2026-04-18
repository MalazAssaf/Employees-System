package com.example.employee.dtos.request;

import java.time.LocalDate;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LeaveRequestUpdateRequest(

        @FutureOrPresent @NotNull(message = "Start date cannot be null") LocalDate startDate,

        @FutureOrPresent @NotNull(message = "End date cannot be null") LocalDate endDate,

        @Size(min = 3, max = 100, message = "Reason must be between 2 and 100 chars") @NotBlank(message = "Reason cannot be empty") String reason

) {

}
