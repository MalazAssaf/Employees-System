package com.example.employee.dtos.request;

import java.time.LocalDate;
import java.util.UUID;

import com.example.employee.validators.ValidDateRange;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@ValidDateRange
public class LeaveRequestRequest {
  @FutureOrPresent
  @NotNull
  private LocalDate startDate;

  @FutureOrPresent
  @NotNull
  private LocalDate endDate;

  @NotBlank(message = "reason is required")
  @Size(min = 3, max = 100, message = "Reason must be between 2 and 100 chars")
  private String reason;

  @NotNull(message = "employeeId Id is required")
  private UUID employeeId;
}
